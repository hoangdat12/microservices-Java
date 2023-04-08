package com.microservices.cartservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservices.cartservice.dto.*;
import com.microservices.cartservice.model.Cart;
import com.microservices.cartservice.model.ProductCart;
import com.microservices.cartservice.repository.CartRepository;
import com.microservices.cartservice.response.errorResponse.InternalServerError;
import com.microservices.cartservice.response.errorResponse.NotFound;
import com.microservices.cartservice.response.successResponse.Created;
import com.microservices.cartservice.response.successResponse.Ok;
import com.microservices.productservice.dto.ListProductResponse;
import com.microservices.productservice.dto.ProductResponse;
import com.microservices.productservice.model.Product;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import com.mongodb.client.model.Updates;
import com.sun.jdi.InternalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartService {
    private final CartRepository cartRepository;
    private final MongoTemplate mongoTemplate;
    private final RedisTemplate<String, Object> redisTemplate;
    private ObjectMapper objectMapper = new ObjectMapper();
    private WebClient webClient = WebClient.builder().build();
    public ResponseEntity<?> createCart(String userId) {
        Cart isExistCart = cartRepository.findByUserId(userId).orElse(null);
        if (isExistCart != null) {
            throw new InternalException("Cart is Exits!");
        }
        Cart cart = Cart.builder()
                .userId(userId)
                .products(new ArrayList<>())
                .build();
        Cart newCart = cartRepository.save(cart);
        Created<Cart> response = new Created<>(newCart);
        return response.sender();
    }
    public ResponseEntity<?> deleteCart(CartRequest request) {
        Query query = new Query(Criteria.where("_id").is(request.getCartId())
                .and("userId").is(request.getUserId()));
        mongoTemplate.remove(query, Cart.class);
        Ok<String> response = new Ok<>("Deleted!");
        return response.sender();
    }
    public ResponseEntity<?> addProductToCart(AddProductToCartDto request) {
        Query query = new Query(Criteria.where("_id").is(request.getCartId())
                .and("userId").is(request.getUserId()));

        Cart cart = mongoTemplate.findOne(query, Cart.class);
        if (cart == null) {
            throw new NotFound("Cart not found!");
        }

//        // Check product isStock or not
//        boolean isStock = checkProductIsStock(request.getProductCart().getProductId());
//        log.info("isStock::: {}", isStock);
//        if (!isStock) {
//            return new Ok<>("Product sale out!").sender();
//        }

        boolean productIsExist = cart.getProducts().stream()
                .anyMatch(
                        product -> product.getProductId()
                                .equals(request.getProductCart().getProductId())
                );
        Update update;
        // Check product is Exist or not
        // If exist then increment quantity
        ProductCart productCart = request.getProductCart();
        if (productIsExist) {
            Document result = updateQuantity(
                    request.getUserId(),
                    productCart.getProductId(),
                    productCart.getQuantity()
            );

            log.info("Result increment quantity::: {}", result);
            if (result == null) {
                throw new InternalServerError("DB Error!");
            } else {
                redisTemplate.delete(cart.getId());
                Ok<Document> response = new Ok<>(result);
                return response.sender();
            }
        }
        // Else then add product
        else {
            update = new Update()
                    .push("products", productCart);
            redisTemplate.delete(cart.getId());
            return getResponseEntityWithFindAndModify(query, update);
        }
    }
    public ResponseEntity<?> addMultiProductToCart(AddMultiProductToCartDto request) {
        // Get cart
        Query query = new Query(Criteria.where("_id").is(request.getCartId())
                .and("userId").is(request.getUserId()));

        Cart cart = mongoTemplate.findOne(query, Cart.class);
        if (cart == null) {
            throw new NotFound("Cart not found!");
        }

        // Check product isStock or not
//        List<ProductCart> productSaleOut = new ArrayList<>();
//        for (ProductCart productCart: request.getProductCarts()) {
//            boolean isStock = checkProductIsStock(productCart.getProductId());
//            if (!isStock) {
//                productSaleOut.add(productCart);
//            }
//        }
//        if (!productSaleOut.isEmpty()) {
//            return new Ok<ProductSaleOut>
//                    (new ProductSaleOut("Product sale out!", productSaleOut))
//                    .sender();
//        }

        List<ProductCart> productCarts =  request.getProductCarts();

        // Get list product Id
        List<String> productIds = productCarts.stream()
                .map(ProductCart::getProductId)
                .collect(Collectors.toList());
        // Check Product is Exist in Cart
        List<ProductCart> productsExist = cart.getProducts().stream()
                .filter(productCart -> productIds.contains(productCart.getProductId()))
                .collect(Collectors.toList());
        // Update Cart
        for (ProductCart productCart : productCarts) {
            if (productsExist.stream().anyMatch(p -> p.getProductId().equals(productCart.getProductId()))) {
                // Increment quantity
                ProductCart existingProduct = productsExist.stream()
                        .filter(p -> p.getProductId().equals(productCart.getProductId()))
                        .findFirst().orElse(null);
                updateQuantity(request.getUserId(), existingProduct.getProductId(), productCart.getQuantity());
            } else {
                // Add Product To Cart
                addProductToCart(productCart, query);
            }
        }

        redisTemplate.delete(request.getCartId());
        return new Ok<String>("Add Product to Cart successfully!").sender();
    }

    public ResponseEntity<?> removeProductToCart(DeleteProductToCart request) {
        Query query = new Query(Criteria.where("_id").is(request.getCartId())
                .and("userId").is(request.getUserId()));
        Update update = new Update()
                .pull("products",  Query.query(Criteria.where("productId").is(request.getProductId())));
        FindAndModifyOptions options = getOptionsMongoUpdate();
        Cart result = mongoTemplate.findAndModify(query, update, options, Cart.class);
        if (result == null) {
            throw new IllegalArgumentException("Product not found in Cart!");
        } else {
            Ok<String> response = new Ok<>("Deleted!");
            redisTemplate.delete(request.getCartId());
            return response.sender();
        }
    }
    public ResponseEntity<?> removeMultipleProductToCart(DeleteMultipleProductToCart request) {
        Query query = new Query(Criteria.where("_id").is(request.getCartId())
                .and("userId").is(request.getUserId()));
        Cart cart = mongoTemplate.findOne(query, Cart.class);
        log.info("cart:::{}", cart);
        if (cart == null) {
            throw new NotFound("Cart not found!");
        }
        List<ProductCart> updatedCartItems = cart.getProducts().stream()
                .filter(cartItem -> !request.getProductIds().contains(cartItem.getProductId()))
                .collect(Collectors.toList());
        cart.setProducts(updatedCartItems);
        mongoTemplate.save(cart);
        return new Ok<Cart>(cart).sender();
    }
    //    public ResponseEntity<?> getDetailCartOfUser(String userId) throws IOException {
//        Query query = new Query(Criteria.where("userId").is(userId));
//        Cart cart = mongoTemplate.findOne(query, Cart.class);
//        if (cart == null) {
//            throw new IllegalArgumentException("User not found!");
//        } else {
//            double price = 0;
//            int quantityAllProduct = 0;
//            List<Product> products = new ArrayList<>();
//
//            if (redisTemplate.hasKey(cart.getId())) {
//                Map<Object, Object> dataJson = redisTemplate.opsForHash().entries(cart.getId());
//
//                String quantityAllProductString = (String) dataJson.get("quantityAllProduct");
//                quantityAllProduct = Integer.parseInt(quantityAllProductString);
//                String priceString = (String) dataJson.get("price");
//                price = Double.parseDouble(priceString);
//                dataJson.remove("price");
//                dataJson.remove("quantityAllProduct");
//
//                for (Object productJson : dataJson.values()) {
//                    Product product = objectMapper.readValue((String) productJson, Product.class);
//                    products.add(product);
//                }
//                DetailCartResponse response = DetailCartResponse.builder()
//                        .userId(cart.getUserId())
//                        .id(cart.getId())
//                        .products(products)
//                        .quantityAllProduct(quantityAllProduct)
//                        .price(price)
//                        .build();
//                return new Ok<>(response).sender();
//            } else {
//                log.info("Catch Empty!");
//                List<String> productIds = cart.getProducts().stream()
//                        .map(ProductCart::getProductId)
//                        .collect(Collectors.toList());
//
//                if (productIds.isEmpty()) {
//                    Ok<Cart> response = new Ok<>(cart);
//                    return response.sender();
//                }
//                Query queryProduct = Query.query(Criteria.where("_id").in(productIds));
//                products = mongoTemplate.find(queryProduct, Product.class);
//                price = products.stream()
//                        .mapToDouble(product -> product.getPrice() * cart.getProducts().stream()
//                                .filter(productCart -> productCart.getProductId().equals(product.getId()))
//                                .findFirst()
//                                .map(ProductCart::getQuantity)
//                                .orElse(0))
//                        .sum();
//                quantityAllProduct = cart.getProducts().stream()
//                        .mapToInt(ProductCart::getQuantity)
//                        .sum();
//
//                DetailCartResponse data = DetailCartResponse.builder()
//                        .userId(cart.getUserId())
//                        .id(cart.getId())
//                        .products(products)
//                        .quantityAllProduct(quantityAllProduct)
//                        .price(price)
//                        .build();
//                Map<String, String> productMap = new HashMap<>();
//                for (Product product : products) {
//                    productMap.put(product.getId(), objectMapper.writeValueAsString(product));
//                }
//                productMap.put("price", objectMapper.writeValueAsString(price));
//                productMap.put("quantityAllProduct", objectMapper.writeValueAsString(quantityAllProduct));
//                redisTemplate.opsForHash().putAll(
//                        cart.getId(),
//                        productMap
//                );
//                redisTemplate.expire(cart.getId(), 15, TimeUnit.SECONDS);
//                Ok<DetailCartResponse> response = new Ok<>(data);
//                return response.sender();
//            }
//        }
//    }
    public ResponseEntity<?> getDetailCartOfUserV2(String userId) throws IOException {
        Query query = new Query(Criteria.where("userId").is(userId));
        Cart cart = mongoTemplate.findOne(query, Cart.class);
        if (cart == null) {
            throw new NotFound("User not found!");
        }
        List<Product> products = new ArrayList<>();
        double price = 0;
        int quantityAllProduct = 0;

        if (redisTemplate.hasKey(cart.getId())) {
            Map<Object, Object> dataJson = redisTemplate.opsForHash().entries(cart.getId());
            log.info("Catch::: {}", dataJson);
            String quantityAllProductString = (String) dataJson.get("quantityAllProduct");
            quantityAllProduct = Integer.parseInt(quantityAllProductString);
            String priceString = (String) dataJson.get("price");
            price = Double.parseDouble(priceString);
            dataJson.remove("price");
            dataJson.remove("quantityAllProduct");

            for (Object productJson : dataJson.values()) {
                Product product = objectMapper.readValue((String) productJson, Product.class);
                products.add(product);
            }
        } else {
            log.info("Catch empty!");
            List<String> productIds = cart.getProducts().stream()
                    .map(ProductCart::getProductId)
                    .collect(Collectors.toList());
            if (!productIds.isEmpty()) {
//                Query queryProduct = Query.query(Criteria.where("_id").in(productIds));
                ProductResponse response = webClient.post()
                        .uri("http://localhost:8080/api/v1/product/productIds")
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(new ProductIds(productIds))
                        .retrieve()
                        .bodyToMono(ProductResponse.class)
                        .block();
                products = response.getMetaData().getProducts();
                Map<String, String> productMap = new HashMap<>();
                for (Product product : products) {
                    int quantity = cart.getProducts().stream()
                            .filter(productCart -> productCart.getProductId().equals(product.getId()))
                            .findFirst()
                            .map(ProductCart::getQuantity)
                            .orElse(0);
                    price += product.getPrice() * quantity;
                    quantityAllProduct += quantity;
                    productMap.put(product.getId(), objectMapper.writeValueAsString(product));
                }
                productMap.put("price", objectMapper.writeValueAsString(price));
                productMap.put("quantityAllProduct", objectMapper.writeValueAsString(quantityAllProduct));
                redisTemplate.opsForHash().putAll(cart.getId(), productMap);
                redisTemplate.expire(cart.getId(), 15, TimeUnit.SECONDS);
            }
        }

        DetailCartResponse response = DetailCartResponse.builder()
                .userId(cart.getUserId())
                .id(cart.getId())
                .products(products)
                .quantityAllProduct(quantityAllProduct)
                .price(price)
                .build();
        return new Ok<>(response).sender();
    }

    // PRIVATE
    private FindAndModifyOptions getOptionsMongoUpdate() {
        return new FindAndModifyOptions()
                .upsert(true)
                .returnNew(true);
    }
    private ResponseEntity<?> getResponseEntityWithFindAndModify(Query query, Update update) {
        FindAndModifyOptions options = getOptionsMongoUpdate();
        Cart result = mongoTemplate.findAndModify(query, update, options, Cart.class);
        if (result == null) {
            throw new InternalServerError("DB Error!");
        } else {
            Ok<Cart> response = new Ok<>(result);
            return response.sender();
        }
    }
    private Document updateQuantity(String userId, String productId, int quantity) {
        Bson filter = Filters.and(
                Filters.eq("userId", userId),
                Filters.elemMatch("products", Filters.eq("productId", productId))
        );
        Bson updateQuantity = Updates.inc("products.$.quantity", quantity);
        Document result = mongoTemplate.getCollection("carts")
                .findOneAndUpdate(filter, updateQuantity, new FindOneAndUpdateOptions()
                        .upsert(true)
                        .returnDocument(ReturnDocument.AFTER)
                );
        return result;
    }
    private Cart addProductToCart(ProductCart productCart, Query query) {
        Update update = new Update()
                .push("products", productCart);
        FindAndModifyOptions options = new FindAndModifyOptions()
                .upsert(true)
                .returnNew(true);
        return mongoTemplate.findAndModify(query, update, options, Cart.class);
    }
    private boolean checkProductIsStock(String productId) {
        return Boolean.TRUE.equals(webClient.get()
                .uri("http://localhost:8082/api/v1/inventory/isStock/{productId}", productId)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(boolean.class)
                .block());
    }
}

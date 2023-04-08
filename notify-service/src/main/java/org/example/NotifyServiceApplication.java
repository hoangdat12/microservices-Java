package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.OrderNotifyDto;
import org.example.service.MailSenderService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@SpringBootApplication
@RequiredArgsConstructor
@EnableKafka
@Slf4j
public class NotifyServiceApplication {
    private ObjectMapper objectMapper = new ObjectMapper();
//    private final MailSenderService mailSenderService;
    public static void main(String[] args) {
        SpringApplication.run(NotifyServiceApplication.class, args);
    }
    @KafkaListener(topics = "notificationOrderTopic", groupId = "notificationId")
    public void NotificationOrderSuccess(String data) throws JsonProcessingException {
        OrderNotifyDto orderNotifyDto = objectMapper.readValue(data, OrderNotifyDto.class);
        Date orderDate = new Date();
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        String formattedDate = dateFormat.format(orderDate);

        String shippingAddress = "";
        double amountPaid = orderNotifyDto.getProducts().stream()
                .mapToDouble(product -> product.getQuantity() * product.getPrice())
                .sum();
        String shippingTimeframe = "";

        String userEmail = orderNotifyDto.getUserEmail();
        String subject = "Thank you for your purchase. Here’s the confirmation of your order.";
        String body = "Dear Customer, <br><br>" +
                "We are writing to confirm that your order has been successfully placed on "
                + formattedDate + " and we would like to thank you for choosing to shop with us. " +
                "Your order details are as follows: <br><br>" +
                "Order Date: " + formattedDate + "<br>" +
                "Shipping Address: " + shippingAddress + "<br><br>" +
                "Here is a summary of the items you have purchased: <br>" + orderNotifyDto.getProducts() + "<br><br>" +
                "Your payment of " + amountPaid + " has been processed and we are now preparing your order for shipment. " +
                "Thank you for your business, and we look forward to serving you again in the future. <br><br>" +
                "Best regards, <br>" +
                "LAZYFY";
        // send out an email notification
//        mailSenderService.sendMailOrderSuccess(userEmail, subject, body);
        log.info("Send mail to {}", userEmail);
    }
}
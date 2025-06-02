//package com.example.Hdbank_project.kafka;
//
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.stereotype.Service;
//
//@Service
//public class KafkaLogProducer {
//
//    private final KafkaTemplate<String, String> kafkaTemplate;
//
//    public KafkaLogProducer(KafkaTemplate<String, String> kafkaTemplate) {
//        this.kafkaTemplate = kafkaTemplate;
//    }
//
//    public void sendLog (String messagse) {
//        kafkaTemplate.send("Nhật kí hoạt động của người dùng", messagse);
//
//    }
//}

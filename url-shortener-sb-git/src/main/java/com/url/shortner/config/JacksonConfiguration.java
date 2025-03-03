//package com.url.shortner.config;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.json.JsonMapper;
//import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
//import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
//import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.time.format.DateTimeFormatter;
//
//@Configuration
//public class JacksonConfiguration {
//
//
//    @Bean
//    public ObjectMapper objectMapper() {
//        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.registerModule(new JavaTimeModule());
//
//        return objectMapper;
//    }
//    @Bean
//    public JsonMapper jsonMapper(){
//            JsonMapper jsonMapper = new JsonMapper();
//        jsonMapper.registerModule(new JavaTimeModule());
//        return jsonMapper;
//    }
//
////    @Bean
////    @Primary
////    public ObjectMapper objectMapper() {
////        ObjectMapper mapper = new ObjectMapper();
////        mapper.registerModule(new JSR310Module());
////        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
////        return mapper;
////    }
//
//}
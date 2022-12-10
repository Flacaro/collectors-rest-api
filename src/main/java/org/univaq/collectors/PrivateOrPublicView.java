//package org.univaq.collectors;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.springframework.http.ResponseEntity;
//
//public class PrivateOrPublicView {
//
//    private final ObjectMapper objectMapper;
//
//    public PrivateOrPublicView(ObjectMapper objectMapper) {
//        this.objectMapper = objectMapper;
//
//
//     if ("private".equals(view)) {
//        return ResponseEntity.ok(
//                objectMapper.writerWithView(UserView.Private.class).writeValueAsString());
//    } else {
//        return ResponseEntity.ok(
//                objectMapper.writerWithView(UserView.Public.class).writeValueAsString()
//        );
//    }
//}

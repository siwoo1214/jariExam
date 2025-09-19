package com.parkingInfo.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000"}, allowCredentials = "true")
public class TestController {
    
    // CORS 테스트
    @GetMapping("/test")
    public ResponseEntity<Map<String, String>> test(){
        Map<String, String> response = new HashMap<>();
        response.put("message", "CORS 테스트 성공!");
        response.put("status", "success");
        return ResponseEntity.ok(response);
    }
    
    // 간단한 주차장 데이터 테스트
    @GetMapping("/test/parking")
    public ResponseEntity<Map<String, Object>> testParking(){
        Map<String, Object> response = new HashMap<>();
        response.put("parkingName", "테스트 주차장");
        response.put("address", "서울 강남구");
        response.put("totalParkingCount", 100);
        response.put("currentParkingCount", 50);
        return ResponseEntity.ok(response);
    }
}

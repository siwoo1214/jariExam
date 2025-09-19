package com.parkingInfo.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.parkingInfo.api.config.SeoulApiConfig;
import com.parkingInfo.api.dto.SeoulApiResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

@Service
@RequiredArgsConstructor
public class ApiService {

    private final SeoulApiConfig apiConfig;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 서울시 주차장 정보 API 호출 및 데이터 파싱
     * @return SeoulApiResponseDto 파싱된 주차장 정보
     * @throws Exception API 호출 또는 파싱 실패 시
     */
    public SeoulApiResponseDto getParkingInfo() throws Exception {
        // 1. API URL 생성
        StringBuilder urlBuilder = new StringBuilder(apiConfig.getBaseUrl());
        urlBuilder.append("/" + URLEncoder.encode(apiConfig.getKey(), "UTF-8"));
        urlBuilder.append("/" + URLEncoder.encode(apiConfig.getResponseType(), "UTF-8"));
        urlBuilder.append("/" + URLEncoder.encode(apiConfig.getServiceName(), "UTF-8"));
        urlBuilder.append("/" + URLEncoder.encode("1", "UTF-8"));
        urlBuilder.append("/" + URLEncoder.encode("178", "UTF-8")); // 178개빡에 없는듯?

        // 2. HTTP 연결 설정
        URL url = new URL(urlBuilder.toString());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");
        conn.setRequestProperty("Accept", "application/json");

        System.out.println("API 호출 URL: " + urlBuilder.toString());
        System.out.println("Response code: " + conn.getResponseCode());

        // 3. 응답 데이터 읽기
        BufferedReader rd;
        if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
        } else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "UTF-8"));
        }

        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        rd.close();
        conn.disconnect();

        // 4. JSON 파싱
        // jackson의 objectmapper이 json문자열을 파싱해서 객체형태로 변환해줌
        String jsonResponse = sb.toString();
        SeoulApiResponseDto responseDto = objectMapper.readValue(jsonResponse, SeoulApiResponseDto.class);

        // 5. 결과 출력
        printParkingDataSummary(responseDto);

        return responseDto;
    }

    /**
     * 주차장 데이터 요약 정보 출력
     */
    private void printParkingDataSummary(SeoulApiResponseDto responseDto) {
        SeoulApiResponseDto.GetParkingInfo parkingInfo = responseDto.getGetParkingInfo();

        System.out.println("\n=== 🚗 주차장 데이터 조회 결과 ===");
        System.out.println("📊 전체 주차장 수: " + parkingInfo.getListTotalCount() + "개");
        System.out.println("📋 조회된 주차장 수: " + (parkingInfo.getRow() != null ? parkingInfo.getRow().size() : 0) + "개");
        System.out.println("✅ API 응답 상태: " + parkingInfo.getResult().getMessage());
        System.out.println("🔍 응답 코드: " + parkingInfo.getResult().getCode());
        System.out.println("================================\n");

    }

    /**
     * 원본 JSON 문자열과 파싱된 객체를 함께 반환하는 메서드 (디버깅용)
     */
//    public String getParkingInfoAsJson() throws Exception {
//        SeoulApiResponseDto responseDto = getParkingInfo();
//        return objectMapper.writeValueAsString(responseDto);
//    }
}
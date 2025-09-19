package com.parkingInfo.api.controller;

import com.parkingInfo.api.dto.ParkingLotDto;
import com.parkingInfo.api.dto.SeoulApiResponseDto;
import com.parkingInfo.api.service.ApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/parking")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000"}, allowCredentials = "true")
public class ParkingController {

    private final ApiService apiService;

    /**
     * 주차장 정보 조회 (파싱된 객체 반환)
     */
    @GetMapping
    public ResponseEntity<SeoulApiResponseDto> getParkingInfo() throws Exception {
        SeoulApiResponseDto parkingData = apiService.getParkingInfo();
        return ResponseEntity.ok(parkingData);
    }

    /**
     * 주차장 목록만 반환
     */
    @GetMapping("/list")
    public ResponseEntity<List<ParkingLotDto>> getParkingList() throws Exception {
        SeoulApiResponseDto parkingData = apiService.getParkingInfo();
        List<ParkingLotDto> parkingList = parkingData.getGetParkingInfo().getRow();
        return ResponseEntity.ok(parkingList);
    }

    /**
     * 주차장 통계 정보
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getParkingStats() throws Exception {
        SeoulApiResponseDto parkingData = apiService.getParkingInfo();
        SeoulApiResponseDto.GetParkingInfo info = parkingData.getGetParkingInfo();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalCount(전체 주차장 수)", info.getListTotalCount());
        stats.put("retrievedCount(조회된 주차장 수)", info.getRow() != null ? info.getRow().size() : 0);
        stats.put("apiStatus", info.getResult().getMessage());
        stats.put("responseCode", info.getResult().getCode());

        // 추가 통계 계산
        if (info.getRow() != null && !info.getRow().isEmpty()) {
            List<ParkingLotDto> parkingLots = info.getRow();

            long paidParkingCount = parkingLots.stream()
                    .filter(lot -> "Y".equals(lot.getPaymentYn()))
                    .count();

            double totalCapacity = parkingLots.stream()
                    .filter(lot -> lot.getTotalParkingCount() != null)
                    .mapToDouble(ParkingLotDto::getTotalParkingCount)
                    .sum();

            double currentOccupancy = parkingLots.stream()
                    .filter(lot -> lot.getCurrentParkingCount() != null)
                    .mapToDouble(ParkingLotDto::getCurrentParkingCount)
                    .sum();

            // paidParkingCount - 유료 주차장 개수
            stats.put("paidParkingCount(유료 주차장 개수)", paidParkingCount);
            // freeParkingCount - 무료 주차장 개수
            stats.put("freeParkingCount(무료 주차장 개수)", parkingLots.size() - paidParkingCount);
            // totalCapacity - 전체 주차 가능 대수
            stats.put("totalCapacity(전체 주차 가능 대수)", (int) totalCapacity);
            // currentOccupancy - 현재 주차 중인 차량 수
            stats.put("currentOccupancy(현재 주차 중인 차량 수)", (int) currentOccupancy);
            // availableSpaces - 현재 주차 가능한 빈 공간
            stats.put("availableSpaces(현재 주차 가능한 빈 공간)", (int) (totalCapacity - currentOccupancy));
            // occupancyRate - 주차장 점유율 (%)
            stats.put("occupancyRate(주차장 점유율 (%))", totalCapacity > 0 ? Math.round((currentOccupancy / totalCapacity) * 100) : 0);
        }

        return ResponseEntity.ok(stats);
    }

    /**
     * 원본 JSON 형태로 반환 (디버깅/테스트용)
     */
//    @GetMapping("/raw")
//    public ResponseEntity<String> getParkingInfoRaw() throws Exception {
//        String jsonData = apiExplorer.getParkingInfoAsJson();
//        return ResponseEntity.ok(jsonData);
//    }
}

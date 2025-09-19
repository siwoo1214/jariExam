package com.parkingInfo.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * 서울시 주차장 정보 API 응답 DTO
 */
@Data
public class SeoulApiResponseDto {

    @JsonProperty("GetParkingInfo")
    private GetParkingInfo getParkingInfo;

    @Data
    public static class GetParkingInfo {
        /**
         * 전체 데이터 총 개수
         */
        @JsonProperty("list_total_count")
        private int listTotalCount;

        /**
         * API 응답 결과 정보
         */
        @JsonProperty("RESULT")
        private Result result;

        /**
         * 주차장 데이터 배열
         */
        @JsonProperty("row")
        private List<ParkingLotDto> row;

        @Data
        public static class Result {
            /**
             * 응답 코드 (INFO-000: 정상)
             */
            @JsonProperty("CODE")
            private String code;

            /**
             * 응답 메시지
             */
            @JsonProperty("MESSAGE")
            private String message;
        }
    }
}

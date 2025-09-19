package com.parkingInfo.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 주차장 상세 정보 DTO - 서울시 API 응답의 모든 필드 포함
 */
@Data
public class ParkingLotDto {

    /**
     * 주차장 코드 (고유 식별자)
     */
    @JsonProperty("PKLT_CD")
    private String parkingCode;

    /**
     * 주차장명
     */
    @JsonProperty("PKLT_NM")
    private String parkingName;

    /**
     * 주소
     */
    @JsonProperty("ADDR")
    private String address;

    /**
     * 주차장 유형 (예: "NW")
     */
    @JsonProperty("PKLT_TYPE")
    private String parkingType;

    /**
     * 주차장 유형명 (예: "노외 주차장")
     */
    @JsonProperty("PRK_TYPE_NM")
    private String parkingTypeName;

    /**
     * 운영 구분 코드
     */
    @JsonProperty("OPER_SE")
    private String operationCode;

    /**
     * 운영 구분명 (예: "시간제 주차장")
     */
    @JsonProperty("OPER_SE_NM")
    private String operationName;

    /**
     * 전화번호
     */
    @JsonProperty("TELNO")
    private String phoneNumber;

    /**
     * 주차현황 연계여부 (1: 연계, 0: 비연계)
     */
    @JsonProperty("PRK_STTS_YN")
    private String parkingStatusYn;

    /**
     * 주차현황 연계명
     */
    @JsonProperty("PRK_STTS_NM")
    private String parkingStatusName;

    /**
     * 총 주차대수
     */
    @JsonProperty("TPKCT")
    private Double totalParkingCount;

    /**
     * 현재 주차차량 수
     */
    @JsonProperty("NOW_PRK_VHCL_CNT")
    private Double currentParkingCount;

    /**
     * 현재 주차차량 수 업데이트 시간
     */
    @JsonProperty("NOW_PRK_VHCL_UPDT_TM")
    private String currentParkingUpdateTime;

    /**
     * 유/무료 구분 (Y: 유료, N: 무료)
     */
    @JsonProperty("PAY_YN")
    private String paymentYn;

    /**
     * 유/무료 구분명
     */
    @JsonProperty("PAY_YN_NM")
    private String paymentTypeName;

    /**
     * 야간개방여부 (Y: 개방, N: 미개방)
     */
    @JsonProperty("NGHT_PAY_YN")
    private String nightPaymentYn;

    /**
     * 야간개방여부명
     */
    @JsonProperty("NGHT_PAY_YN_NM")
    private String nightPaymentName;

    /**
     * 평일 운영 시작시간 (HHMM 형식)
     */
    @JsonProperty("WD_OPER_BGNG_TM")
    private String weekdayStartTime;

    /**
     * 평일 운영 종료시간 (HHMM 형식)
     */
    @JsonProperty("WD_OPER_END_TM")
    private String weekdayEndTime;

    /**
     * 주말 운영 시작시간 (HHMM 형식)
     */
    @JsonProperty("WE_OPER_BGNG_TM")
    private String weekendStartTime;

    /**
     * 주말 운영 종료시간 (HHMM 형식)
     */
    @JsonProperty("WE_OPER_END_TM")
    private String weekendEndTime;

    /**
     * 공휴일 운영 시작시간 (HHMM 형식)
     */
    @JsonProperty("LHLDY_OPER_BGNG_TM")
    private String holidayStartTime;

    /**
     * 공휴일 운영 종료시간 (HHMM 형식)
     */
    @JsonProperty("LHLDY_OPER_END_TM")
    private String holidayEndTime;

    /**
     * 토요일 유/무료 구분 (Y: 유료, N: 무료)
     */
    @JsonProperty("SAT_CHGD_FREE_SE")
    private String saturdayChargeYn;

    /**
     * 토요일 유/무료 구분명
     */
    @JsonProperty("SAT_CHGD_FREE_NM")
    private String saturdayChargeName;

    /**
     * 공휴일 유/무료 구분 (Y: 유료, N: 무료)
     */
    @JsonProperty("LHLDY_CHGD_FREE_SE")
    private String holidayChargeYn;

    /**
     * 공휴일 유/무료 구분명
     */
    @JsonProperty("LHLDY_CHGD_FREE_SE_NAME")
    private String holidayChargeName;

    /**
     * 월 정액권 금액
     */
    @JsonProperty("PRD_AMT")
    private String monthlyAmount;

    /**
     * 거리두기 주차장 관리번호
     */
    @JsonProperty("STRT_PKLT_MNG_NO")
    private String distancingManagementNumber;

    /**
     * 기본 주차요금 (분 단위)
     */
    @JsonProperty("BSC_PRK_CRG")
    private Double basicParkingFee;

    /**
     * 기본 주차시간 (분)
     */
    @JsonProperty("BSC_PRK_HR")
    private Double basicParkingTime;

    /**
     * 추가 주차요금 (분 단위)
     */
    @JsonProperty("ADD_PRK_CRG")
    private Double additionalParkingFee;

    /**
     * 추가 주차시간 (분)
     */
    @JsonProperty("ADD_PRK_HR")
    private Double additionalParkingTime;

    /**
     * 버스 기본 주차요금
     */
    @JsonProperty("BUS_BSC_PRK_CRG")
    private Double busBasicParkingFee;

    /**
     * 버스 기본 주차시간
     */
    @JsonProperty("BUS_BSC_PRK_HR")
    private Double busBasicParkingTime;

    /**
     * 버스 추가 주차시간
     */
    @JsonProperty("BUS_ADD_PRK_HR")
    private Double busAdditionalParkingTime;

    /**
     * 버스 추가 주차요금
     */
    @JsonProperty("BUS_ADD_PRK_CRG")
    private Double busAdditionalParkingFee;

    /**
     * 일일 최대요금
     */
    @JsonProperty("DAY_MAX_CRG")
    private Double dayMaxFee;

    /**
     * 공유 주차장 관리업체명
     */
    @JsonProperty("SHRN_PKLT_MNG_NM")
    private String sharedParkingManagerName;

    /**
     * 공유 주차장 관리업체 URL
     */
    @JsonProperty("SHRN_PKLT_MNG_URL")
    private String sharedParkingManagerUrl;

    /**
     * 공유 주차장 여부 (Y: 공유, N: 일반)
     */
    @JsonProperty("SHRN_PKLT_YN")
    private String sharedParkingYn;

    /**
     * 공유 주차장 기타사항
     */
    @JsonProperty("SHRN_PKLT_ETC")
    private String sharedParkingEtc;
}
package zypt.zyptapiserver.domain.dto;

/**
 * 한 달의 달력 UI에 집중데이터 존재 여부 마킹 데이터
 * 데이터가 있는 곳에만 마킹하면 되서 따로 boolean 타입 반환 필요 x
 * @param year
 * @param month
 * @param day
 */

public record FocusDayMarkDto(int year, int month, int day) {

}

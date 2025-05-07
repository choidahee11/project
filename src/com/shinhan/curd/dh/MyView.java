package com.shinhan.curd.dh;

import java.util.List;

public class MyView {
    
    // 1. 전체 공연 조회 보기 좋게 출력
    public static void display(List<ShowDTO> list) {
        String previousShow = "";
        System.out.println("list 사이즈 :" +list.size());
        for (ShowDTO dto : list) {
            if (!dto.getSHOW_NAME().equals(previousShow)) {
                if (!previousShow.isEmpty()) {
                    System.out.println("-------------");
                }
                System.out.println("공연명: " + dto.getSHOW_NAME());
                previousShow = dto.getSHOW_NAME();
            }
            
            System.out.println("날짜: " + dto.getSHOW_DATE()
                + "\tVIP 잔여좌석: " + dto.getVIP_SEAT()
                + "\tS 잔여좌석: " + dto.getS_REMAIN_SEAT()
                + "\tA 잔여좌석: " + dto.getA_REMAIN_SEAT());
        }
    }

 // 2. 객체를 받아서 콘솔에 출력하는 display 메서드
    public static void display(Object obj) {
        if (obj instanceof List) {
            List<?> list = (List<?>) obj;
            if (list.isEmpty()) {
                System.out.println("현재 예매된 티켓이 없습니다.");
            } else {
                for (Object item : list) {
                    System.out.println(item);
                }
            }
        } else {
            System.out.println(obj);
        }
    }

    // 3. 공연 정보 출력
    public static void displayPerformanceInfo(String showName, String showDate, String showPlace) {
        System.out.println("공연 정보:");
        System.out.println("공연명: " + showName);
        System.out.println("공연일자: " + showDate);
        System.out.println("공연장소: " + showPlace);
    }

    // 4. 예매된 티켓 정보 출력 (좌석번호 제거)
    public static void displayTicketInfo(String userName, String showName, String seatType) {
        System.out.println("예매된 티켓 정보:");
        System.out.println("고객명: " + userName);
        System.out.println("공연명: " + showName);
        System.out.println("좌석등급: " + seatType);
    }
}
package com.shinhan.curd.dh;
//
import java.time.LocalDate;
import java.util.List;
import java.util.Random;

public class MyService {
    MyDAO myDAO = new MyDAO();

    // 1.회원가입
    public int addUser(String id, String pw, String name, int phone) {
        return myDAO.addUser(id,pw,name,phone);
    }

    // 2.로그인
    public boolean checkLogin(String userId, String password) {
        return myDAO.checkLogin(userId, password);
    }

    // 3.공연전체 조회
    public List<ShowDTO> showAllShows() {
        return myDAO.showAllShows();
    }

    // 4. 공연 예매하기
    public String reserveShow(ReservDTO reserve) {
    	//int reservationNumber = generateReservationNumber();
    	//reserve.setTicket_number(reservationNumber);
    	
        return myDAO.saveReservation(reserve);
    }

    // 5. 예매내역 보기
    public List<ReservDTO> getMyTicket(String userId) {
        return myDAO.getMyTicket(userId);
    }
  //6. 예매취소 
  	public boolean cancelTicket(int ticket_Number) {
  		 return myDAO.cancelTicket(ticket_Number);
  	
  	}
	

 // 가격 높은것부터 출력하기
    public static int calculateTotalPrice(int seatClassChoice, int seatCount) {
        int pricePerSeat;
        switch (seatClassChoice) {
            case 1: pricePerSeat = 100; break;  // VIP
            case 2: pricePerSeat = 70; break;   // S
            case 3: pricePerSeat = 50; break;   // A
            default: pricePerSeat = 0; break;
        }
        return pricePerSeat * seatCount;
    }

    // 공연 예매 처리
    public String reserveShow(String userId, int theaterChoice, LocalDate dateChoice, int seatClassChoice, int seatCount, int totalPrice) {
        // 예매 로직 (예매번호 생성, 데이터베이스에 예매 정보 저장 등)
        String reservationNumber = generateReservationNumber();
        
        // 예매 정보를 DB에 저장하는 로직 필요 (DAO 호출)
        // 예: myDAO.saveReservation(userId, theaterChoice, dateChoice, seatClassChoice, seatCount, totalPrice, reservationNumber);
        
        //(String ticketNumber, String showId, String showName, String userId, LocalDate showDate,  String seatLevel, int price, String reservationYN)
        myDAO.insertReservation(reservationNumber, reservationNumber, reservationNumber, userId, dateChoice, userId, totalPrice, reservationNumber);
        // 좌석 갱신
        updateRemainingSeats(theaterChoice, dateChoice, seatClassChoice, seatCount);
        
        return reservationNumber;
    }

    // 예매 번호 생성
    private String generateReservationNumber() {
        return "RES" + new Random().nextInt(100000);  // 예매번호 예시 (RES12345)
    }

    // 잔여 좌석 갱신
    private void updateRemainingSeats(int theaterChoice, LocalDate dateChoice, int seatClassChoice, int seatCount) {
       
    }

	

}


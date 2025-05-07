package com.shinhan.curd.dh;
//
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;

public class MyController {
	static Scanner sc = new Scanner(System.in);
	static MyService myservice = new MyService();

	static String loggedInUserId;// 고르인 된 사용자 id 저장
	static String id;
	MyDAO dao = new MyDAO();
//	private LocalDate localDate;

	public void execute() {
		boolean isStop = false;
		while (!isStop) {
			menuDisplay();
			int menu = sc.nextInt();

			switch (menu) {
			case 1 -> {
				f_registerUser(); // 1.회원가입
			}
			case 2 -> {
				f_loginUser(); // 2.로그인
			}
			case 3 -> {
				f_showAllShows(); // 3. 전체공연 조회
			}
			case 4 -> {
				f_reserveShow(); // 4.공연 예매하기
			}
			case 5 -> {
				f_checkMyTicket(); // 5. 예매내역 조회
			}
			case 6 -> {
				f_cancelTicket();
			}
			case 7 -> {
				f_changeShowDate();
			}

			case 99 -> isStop = true;
			default -> System.out.println("잘못된 선택입니다.");
			}
		}
		System.out.println("시스템을 종료합니다.");
	}

	// 0. 메인 화면
	private void menuDisplay() {
		System.out.println("=============== Ticket Box ===============");
		System.out.println("                                          ");
		System.out.println("    1.회원 가입            5.예매 확인         ");
		System.out.println("    2.로그인              6.예매 취소         ");
		System.out.println("    3.공연 전체 조회        7.날짜 변경         ");
		System.out.println("    4.공연 예매                             ");
		System.out.println("                                          ");
		System.out.println("                 END                      ");
		System.out.println("==========================================");
		System.out.print("메뉴 선택 >> ");
	}

	// 1. 회원가입
	private void f_registerUser() {
		Scanner sc = new Scanner(System.in);

		System.out.print("아이디: ");
		String id = sc.nextLine();

		String pw;
		while (true) {
			System.out.print("비밀번호(숫자만 입력): ");
			String pwInput = sc.nextLine();
			if (pwInput.matches("\\d+")) {
				pw = pwInput;
				break;
			}
			System.out.println("비밀번호는 숫자만 입력하세요.");
		}

		System.out.print("이름: ");
		String name = sc.nextLine();

		int phone;
		while (true) {
			System.out.print("전화번호(숫자만 입력): ");
			String phoneInput = sc.nextLine();
			if (phoneInput.matches("\\d+")) {
				phone = Integer.parseInt(phoneInput);
				break;
			}
			System.out.println("전화번호는 숫자만 입력하세요.");
		}

//		UserDTO user = UserDTO.builder().userID(Integer.parseInt(id)).password(String.valueOf(pw)).userName(name)
//				.phoneNumber(phone).build();

		myservice.addUser(id,pw,name,phone);

	}

	// 2.로그인
	private void f_loginUser() {
		Scanner sc = new Scanner(System.in);
		System.out.print("아이디: ");
		id = sc.nextLine();

		System.out.print("비밀번호: ");
		String pw = sc.nextLine();

		if (myservice.checkLogin(id, pw)) {
			loggedInUserId = id; // 로그인 성공시 사용자 ID 저장
			System.out.println("로그인 성공");
		} else {
			System.out.println("로그인 실패");
		}
	}

	// 3.전체 공연 조회
	private void f_showAllShows() {
		List<ShowDTO> list = myservice.showAllShows();
		MyView.display(list);
	}

	// 4. 공연 예매
	public void f_reserveShow() {
		if (loggedInUserId == null) {
			System.out.println("먼저 로그인 해주세요.");
			return;
		}
		Scanner sc = new Scanner(System.in);

		// 1)공연 선택
		System.out.println("1. 오페라의 유령");
		System.out.println("2. 레미제라블");
		System.out.println("3. 시카고");
		System.out.print("→ ");
		int showChoice = Integer.parseInt(sc.nextLine());

		String showTitle = "";
		List<String> dates = new ArrayList<>();
		Map<String, Integer> seatPrices = new HashMap<>();

		switch (showChoice) {
		case 1:
			showTitle = "오페라의 유령";
			dates = Arrays.asList("2025-05-01", "2025-05-02", "2025-05-03", "2025-05-04", "2025-05-05");
			seatPrices.put("VIP", 200000);
			seatPrices.put("S", 150000);
			seatPrices.put("A", 100000);
			break;
		case 2:
			showTitle = "레미제라블";
			dates = Arrays.asList("2025-05-03", "2025-05-04", "2025-05-05", "2025-05-06", "2025-05-07");
			seatPrices.put("VIP", 200000);
			seatPrices.put("S", 150000);
			seatPrices.put("A", 100000);
			break;
		case 3:
			showTitle = "시카고";
			dates = Arrays.asList("2025-05-05", "2025-05-06", "2025-05-07", "2025-05-08", "2025-05-09");
			seatPrices.put("VIP", 150000);
			seatPrices.put("S", 100000);
			seatPrices.put("A", 50000);
			break;
		default:
			System.out.println("잘못된 선택입니다.");
			return;
		}

		// 2)날짜 선택
		for (int i = 0; i < dates.size(); i++) {
			System.out.println((i + 1) + ". " + dates.get(i));
		}
		System.out.print("→ ");
		int dateChoice = Integer.parseInt(sc.nextLine());
		Date selectedDate = Date.valueOf(dates.get(dateChoice - 1));

		// 3)좌석 등급 선택
		List<String> preferredOrder = Arrays.asList("VIP", "S", "A");
		int index = 1;
		for (String grade : preferredOrder) {
			if (seatPrices.containsKey(grade)) {
				System.out.println(index++ + ". " + grade + ": " + seatPrices.get(grade) + "원");
			}
		}
		System.out.print("→ ");
		String seatGrade = sc.nextLine().toUpperCase();

		// 좌석 등급이 seatPrices에 있는지 확인
		if (!seatPrices.containsKey(seatGrade)) {
			System.out.println("잘못된 좌석 등급입니다. 다시 선택하세요.");
			return; // 예외 처리 또는 반복문을 사용하여 재입력 받는 방법
		}

		// 4)좌석 수
		System.out.println("몇 좌석선택하십니까?");
		System.out.print("→ ");
		int seatCount = Integer.parseInt(sc.nextLine());

		// 5)금액 계산
		int pricePerSeat = seatPrices.get(seatGrade);
		int totalPrice = pricePerSeat * seatCount;
		System.out.println("총 금액: " + totalPrice + "원");

		// 6)결제 확인
		System.out.println("결제하시겠습니까? (yes/no)");
		System.out.print("→ ");
		String confirm = sc.nextLine();

		if (confirm.equalsIgnoreCase("yes")) {
			// 7. 예매 처리
			String reserveId = UUID.randomUUID().toString().substring(0, 8);
			System.out.println("\n결제가 완료되었습니다!");
			System.out.println("예매번호: " + reserveId);

			ReservDTO reserve = ReservDTO.builder().show_id(showChoice).show_name(showTitle)
					.user_id(Integer.parseInt(id)).show_date(selectedDate) 

					.seat_level(seatGrade).build();

			myservice.reserveShow(reserve);
			// 좌석 수 갱신 로직 (예: DB에서 해당 날짜, 좌석 등급의 잔여 좌석 수 - seatCount)

		} else {
			System.out.println("결제가 취소되었습니다.");
		}

	}

//5. 예매내역 확인 
	private void f_checkMyTicket() {
		if (loggedInUserId == null) {
			System.out.println("먼저 로그인 해주세요.");
			return;
		}

		List<ReservDTO> myTickets = myservice.getMyTicket(loggedInUserId);

		if (myTickets.isEmpty()) {
			System.out.println("예매 내역이 없습니다.");
		} else {
			System.out.println("=== 예매 내역 ===");
			for (ReservDTO dto : myTickets) {
				System.out.println("예매번호: " + dto.getTicket_number());
				System.out.println("공연명: " + dto.getShow_name());
				System.out.println("날짜: " + dto.getShow_date());
				System.out.println("좌석등급: " + dto.getSeat_level());
				System.out.println("가격: " + dto.getPrice() + "원");
				System.out.println("------------------------");
			}
		}
	}

// 6.예약 취소
	// 6. 예매 취소
	private void f_cancelTicket() {
		if (loggedInUserId == null) {
			System.out.println("먼저 로그인 해주세요.");
			return;
		}

		List<ReservDTO> myTickets = myservice.getMyTicket(loggedInUserId);

		if (myTickets.isEmpty()) {
			System.out.println("예매 내역이 없습니다.");
			return;
		}

		// 예매 내역 출력
		System.out.println("=== 예매 내역 ===");
		for (int i = 0; i < myTickets.size(); i++) {
			ReservDTO dto = myTickets.get(i);
			System.out.println((i + 1) + ". 예매번호: " + dto.getTicket_number());
			System.out.println("   공연명: " + dto.getShow_name());
			System.out.println("   날짜: " + dto.getShow_date());
			System.out.println("   좌석등급: " + dto.getSeat_level());
			System.out.println("   가격: " + dto.getPrice() + "원");
			System.out.println("------------------------");
		}

		// 사용자에게 취소할 번호 선택받기
		System.out.print("취소할 예매 번호를 선택하세요 (번호 입력): ");
		int cancelChoice = Integer.parseInt(sc.nextLine());

		if (cancelChoice < 1 || cancelChoice > myTickets.size()) {
			System.out.println("잘못된 선택입니다.");
			return;
		}

		ReservDTO selectedTicket = myTickets.get(cancelChoice - 1);

		// 취소 확인
		System.out.print("정말로 예매를 취소하시겠습니까? (yes/no): ");
		String confirm = sc.nextLine();

		if (confirm.equalsIgnoreCase("yes")) {
			boolean success = myservice.cancelTicket(selectedTicket.getTicket_number());
			if (success) {
				System.out.println("예매가 취소되었습니다.");
			} else {
				System.out.println("예매 취소에 실패했습니다.");
			}
		} else {

		}
	}

//7. 날짜 변경
	private void f_changeShowDate() {
		List<ReservDTO> reservations = dao.getReservationsByUser(loggedInUserId);
		if (loggedInUserId == null) {
			System.out.println("먼저 로그인 해주세요.");
			return;
		}

		// 1. 예매 내역 조회

		if (reservations != null && !reservations.isEmpty()) {
			// 비어있지 않다면 예약 변경 로직 진행
		} else {
			System.out.println("예약 내역이 없거나 리스트가 null입니다.");
		}

		// 2. 예매 내역 출력
		for (int i = 0; i < reservations.size(); i++) {
			ReservDTO dto = reservations.get(i);
			System.out.printf("[%d] 예매번호: %s | 공연명: %s | 날짜: %s | 등급: %s\n", i + 1, dto.getTicket_number(),
					dto.getShow_name(), dto.getShow_date(), dto.getSeat_level());
		}

		System.out.print("변경할 예매 번호 선택 (숫자 입력): ");
		int sel = Integer.parseInt(sc.nextLine());
		if (sel < 1 || sel > reservations.size()) {
			System.out.println("잘못된 선택입니다.");
			return;
		}
		ReservDTO selected = reservations.get(sel - 1);

		// 3. 해당 공연의 가능한 날짜 조회
		List<LocalDate> dateList = dao.getAvailableDatesForShow(selected.getShow_name(), ((Object) selected));
		if (dateList.isEmpty()) {
			System.out.println("변경 가능한 날짜가 없습니다.");
			return;
		}

		System.out.println("변경 가능한 날짜:");
		for (int i = 0; i < dateList.size(); i++) {
			System.out.printf("[%d] %s\n", i + 1, dateList.get(i));
		}

		System.out.print("변경할 날짜 선택 (숫자 입력): ");
		int dateSel = Integer.parseInt(sc.nextLine());
		LocalDate newDate = dateList.get(dateSel - 1);

		// 4. 좌석 등급 선택
		System.out.print("좌석 등급 선택 (VIP/S/A): ");
		String newSeatLevel = sc.nextLine().toUpperCase();

		// 5. 변경 처리
		boolean result = dao.updateReservationDate(selected.getTicket_number(), newDate);
		if (result) {
			System.out.println("예매 날짜가 변경되었습니다.");
		} else {
			System.out.println("변경 실패. 다시 시도해주세요.");
		}
	}

	public static void main(String[] args) {
		new MyController().execute();
	}

}

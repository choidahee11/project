package com.shinhan.curd.dh;
//
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.sql.DataSource;

import oracle.net.aso.d;

public class MyDAO {
	private List<UserDTO> dataList;

	public MyDAO() {
		this.dataList = new ArrayList<>(); // ArrayList로 초기화
	}

	public Connection getConnection() {
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			return DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "scott", "tiger");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String buildDebugSQL(String sqlTemplate, Object... params) {
		for (Object param : params) {
			String val = (param instanceof String) ? "'" + param + "'" : String.valueOf(param);
			sqlTemplate = sqlTemplate.replaceFirst("\\?", val);
		}
		return sqlTemplate;
	}

// 1.회원가입
	public int addUser(String id, String pw, String name, int phone) {
		Connection conn = DBUtil.getConnection();
		PreparedStatement pstmt = null;
		PreparedStatement checkStmt = null;
		ResultSet rs = null;

		try {
			// 중복된 아이디가 있는지 체크
			String checkSql = "SELECT COUNT(*) FROM User_Account WHERE id = ?";
			checkStmt = conn.prepareStatement(checkSql);
			checkStmt.setString(1, id);
			rs = checkStmt.executeQuery();

			if (rs.next() && rs.getInt(1) > 0) {
				System.out.println("이미 사용 중인 아이디입니다.");
				return 0; // 실패
			}

			// 사용자 정보 삽입
			String sql = "INSERT INTO User_Account (User_ID,id, Password, User_Name, Phone_Number) VALUES (id_seq.NEXTVAL,?, ?, ?, ?)";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, id);
			pstmt.setString(2, pw); // 수정: getPassword()는 String이어야 함
			pstmt.setString(3, name);
			pstmt.setInt(4, phone); // 수정: getPhoneNumber()도 String이어야 함

			int result = pstmt.executeUpdate();
			if (result > 0) {
				System.out.println("회원가입 성공");
				return 1;
			} else {
				System.out.println("회원가입 실패");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (checkStmt != null)
					checkStmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return 0;
	}

// 2. 로그인
	public boolean checkLogin(String id, String pw) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			conn = DBUtil.getConnection();
			String sql = "SELECT * FROM user_account WHERE id = ? AND password = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, id);
			pstmt.setString(2, pw);
			rs = pstmt.executeQuery();

			return rs.next(); // 결과가 있으면 true
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

// 3.전체 공연 조회
	public List<ShowDTO> showAllShows() {
		List<ShowDTO> list = new ArrayList<>(); 
		String sql = "select * from show";

		try (Connection conn = DBUtil.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql);
				ResultSet rs = pstmt.executeQuery()) {

			while (rs.next()) {
//				Date sqlDate = rs.getDate("Show_date");
//				Date showDate = (sqlDate != null) ? sqlDate : null;

				ShowDTO dto = ShowDTO.builder()
						.SHOW_ID(rs.getInt("show_id"))
						.TICKET_BOX_ID(rs.getInt("ticket_box_id"))
						//.User_ID(rs.getInt("user_id"))
						.SHOW_NAME(rs.getString("Show_name"))
						.SHOW_DATE(rs.getDate("show_date"))
						.TOTAL_SEAT(rs.getInt("Total_seat"))
						.VIP_SEAT(rs.getInt("VIP_seat"))
						.S_SEAT(rs.getInt("S_seat"))
						.A_SEAT(rs.getInt("A_seat"))
						.VIP_PRICE(rs.getInt("VIP_Price"))
						.S_PRICE(rs.getInt("S_Price"))
						.A_PRICE(rs.getInt("A_Price"))
						.VIP_REMAIN_SEAT(rs.getInt("VIP_Remain_seat"))
						.S_REMAIN_SEAT(rs.getInt("S_Remain_seat"))
						.A_REMAIN_SEAT(rs.getInt("A_Remain_seat")).build();
				System.out.println(dto);
				list.add(dto);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("mydao list 사이즈 :" + list.size());
		return list;
	}

	// 사용자 아이디가 존재하는지 확인하는 메소드
	public boolean isUserExists(String userId) {
		String sql = "SELECT COUNT(*) FROM User_Account WHERE User_ID = ?";
		try (Connection conn = DBUtil.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, userId);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				return rs.getInt(1) > 0; // 사용자 존재 여부 확인
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public String reserveShow(String userId, String showname, LocalDate showDate, String seatlevel,
			List<ReservDTO> list) {
		for (ReservDTO dto : list) {
			if (showname.equals(dto.getShow_id()) && seatlevel.equals(dto.getSeat_level())) {

				String ticketNum = UUID.randomUUID().toString().substring(0, 8);
				dto.setUser_id(0);
				//dto.setTicket_number(ticketNum);
				return ticketNum;
			}
		}
		return null;
	}

	// 잔여 좌석 DB에 연결 (잔여 좌석 조회)
	public int getRemainingSeats(int showId, String showDate, String grade) {
		int seats = 0;
		String sql = "SELECT remaining_seats FROM seat_info WHERE show_id = ? AND show_date = ? AND grade = ?";
		try (Connection conn = DBUtil.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, showId);
			pstmt.setString(2, showDate);
			pstmt.setString(3, grade);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				seats = rs.getInt("remaining_seats");
			} else {
				System.out.println("데이터가 없습니다.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return seats;
	}

//잔여 좌석 업그레이드
	public void updateRemainingSeats(int showId, String showDate, String grade, int newRemainingSeats) {
		String sql = "UPDATE seat_info SET remaining_seats = ? WHERE show_id = ? AND show_date = ? AND grade = ?";
		Connection conn = null; // Connection 객체를 try-with-resources 밖에서 선언
		try {
			conn = DBUtil.getConnection();
			conn.setAutoCommit(false); // 수동 커밋

			try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
				pstmt.setInt(1, newRemainingSeats);
				pstmt.setInt(2, showId);
				pstmt.setString(3, showDate);
				pstmt.setString(4, grade);
				pstmt.executeUpdate();

				// 커밋
				conn.commit();
				System.out.println("커밋 완료");
			} catch (SQLException e) {
				// 예외 발생 시 롤백
				if (conn != null) {
					conn.rollback(); // 트랜잭션 롤백
					System.out.println("롤백 완료");
				}
				e.printStackTrace();
			}

		} catch (SQLException e) {
			System.err.println("SQL 오류: " + e.getMessage());
			e.printStackTrace();
			try {
				if (conn != null) {
					conn.rollback(); // 롤백 처리
					System.out.println("롤백 완료");
				}
			} catch (SQLException rollbackEx) {
				System.err.println("롤백 오류: " + rollbackEx.getMessage());
				rollbackEx.printStackTrace();
			}
		} finally {
			try {
				if (conn != null) {
					conn.close(); // 커넥션 닫기
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	// 4. 예매 추가 (날짜 변경: LocalDate 사용)
	public class ReservationDAO {

		public String saveReservation(ReservDTO reserve) {
		    String sql = "INSERT INTO Reservation (Ticket_Number,Show_ID, Show_name, User_ID, Show_date, Seat_Level, Price) "
		               + "VALUES (ticket_seq.NEXTVAL,?, ?, ?, ?, ?, ?)";

		    try (Connection conn = DBUtil.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
		        pstmt.setInt(1, reserve.getShow_id());  // 공연 ID
		        pstmt.setString(2, reserve.getShow_name());  // 공연 이름
		        pstmt.setInt(3, reserve.getUser_id());  // 사용자 ID
		        pstmt.setObject(4, reserve.getShow_date());  // 예매 날짜
		        pstmt.setString(5, reserve.getSeat_level());  // 좌석 등급
		        pstmt.setInt(6, reserve.getPrice());  // 가격

		        pstmt.executeUpdate();  // 데이터베이스에 저장
		        return "예매 성공";
		    } catch (SQLException e) {
		        e.printStackTrace();
		        return "예매 실패";
		    }
		}



		// 예매 내역을 조회하는 메서드 (예시)
		public List<ReservDTO> getMyTicket(String userId) {
			List<ReservDTO> list = new ArrayList<>();
			String sql = "SELECT SHOW_NAME, SHOW_DATE, SEAT_LEVEL, PRICE FROM RESERVATION WHERE USER_ID = ?";

			try (Connection conn = DBUtil.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
				pstmt.setString(1, userId);
				ResultSet rs = pstmt.executeQuery();

				while (rs.next()) {
					ReservDTO dto = ReservDTO.builder().show_name(rs.getString("SHOW_NAME"))
							.show_date(rs.getDate("SHOW_DATE")) // sql.Date -> LocalDate
							.seat_level(rs.getString("SEAT_LEVEL")).price(rs.getInt("PRICE")).build();
					list.add(dto);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return list;
		}
	}



	// 예매 내역 저장
	// 4. 공연 예매
	public String saveReservation(ReservDTO reserve) {
		// String ticketNumber = UUID.randomUUID().toString();
		String sql = "INSERT INTO Reservation (Ticket_Number, Show_ID, Show_Name, User_ID, Show_Date, Seat_Level, Price) "
		           + "VALUES (ticket_seq.NEXTVAL, ?, ?, ?, ?, ?, ?)";


		Connection conn = null; // 바깥에 선언
		PreparedStatement pstmt = null;

		conn = DBUtil.getConnection();
		int rowsAffected = 0;
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, "show" + reserve.getShow_id());
			pstmt.setString(2, reserve.getShow_name());
			pstmt.setInt(3, reserve.getUser_id());
			pstmt.setObject(4, reserve.getShow_date()); // LocalDate로 바로 저장
			pstmt.setString(5, reserve.getSeat_level());
			pstmt.setInt(6, reserve.getPrice());

			//rowsAffected = pstmt.executeUpdate();

			if (rowsAffected > 0) {

				System.out.println("예매가 완료되었습니다.");
			} else {

				//System.out.println("예매 실패");
			}

		} catch (SQLException e) {

			e.printStackTrace();
		} finally {
			DBUtil.dbDisconnect(conn, pstmt, null);
		}
		return rowsAffected + "건";
	}

	// 잔여 좌석 DB에 연결 (잔여 좌석 조회)
	public int getRemainingSeats(String showId, String showDate, String grade) {
		int seats = 0;
		String sql = "SELECT remaining_seats FROM seat_info WHERE show_id = ? AND show_date = ? AND grade = ?";

		try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, showId);
			pstmt.setString(2, showDate);
			pstmt.setString(3, grade);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				seats = rs.getInt("remaining_seats");
			} else {
				System.out.println("데이터가 없습니다.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return seats;
	}
	// 5. 예매내역 확인

		// DAO 클래스 내부
		public List<ReservDTO> getMyTicket(String userId) {
			List<ReservDTO> list = new ArrayList<>();
			String sql = "SELECT Ticket_Number, Show_name, Show_date, Seat_Level, Price FROM Reservation WHERE User_ID = ?";

			try (Connection conn = DBUtil.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

				pstmt.setString(1, userId);
				ResultSet rs = pstmt.executeQuery();

				while (rs.next()) {
					ReservDTO dto = ReservDTO.builder()
							.ticket_number(rs.getInt("Ticket_Number"))
							.show_name(rs.getString("Show_name"))
							.show_date(rs.getDate("Show_date"))
							.seat_level(rs.getString("Seat_Level"))
							.price(rs.getInt("Price")).build();
					list.add(dto);
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}

			return list;
		}
//6.예매 취소 

	public boolean cancelTicket(int ticket_Number) {
		String sql = "DELETE FROM reservation WHERE ticket_number = ?";
		try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, ticket_Number);

			int rows = pstmt.executeUpdate();
			return rows > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	// 7.날짜 변경
	public boolean updateReservationDate(int i, LocalDate newDate) {
		String sql = "UPDATE Reservation SET Show_Date = ? WHERE Ticket_Number = ?";
		try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setObject(1, newDate);
			pstmt.setInt(2, i);

			int rows = pstmt.executeUpdate();
			return rows > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	private ReservDTO makeEmp(ResultSet rs) throws SQLException {
		return ReservDTO
				.builder()
				.show_name(rs.getString("Show_name"))
				.show_date(rs.getDate("Show_date")) // 문자열을\
				.VIP_Remain_seat(rs.getInt("VIP_Remain_seat"))
				.S_Remain_seat(rs.getInt("S_Remain_seat"))
				.A_Remain_seat(rs.getInt("A_Remain_seat"))
				.build();
	}

	public List<LocalDate> getAvailableDatesForShow(String show_name, Object selected) {
		// TODO Auto-generated method stub
		return null;
	}

	public static List<ReservDTO> getReservationsByUser(String loggedInUserId) {
		// TODO Auto-generated method stub
		return null;
	}

	public void insertReservation(String reservationNumber, String reservationNumber2, String reservationNumber3,
			String userId, LocalDate dateChoice, String userId2, int totalPrice, String reservationNumber4) {
		// TODO Auto-generated method stub

	}

}

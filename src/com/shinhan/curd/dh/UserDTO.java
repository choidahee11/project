package com.shinhan.curd.dh;

import java.sql.Date;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserDTO {
	private int userID;            // 얘는 데이터를 넣으면 안돼요 왜냐면 자동 생성이니까 근데 지금 로직을 보면 내가 입력하는 id 가 여기로 들어가서 문제 발생 
	private String Id;           // 얘가 로그인할 때 쓰는 아이디로 합시다
	private String password;
	private String userName;
	private int phoneNumber;
}
package com.shinhan.curd.dh;
//
import java.sql.Date;
import java.util.List;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ReservDTO {

	private int ticket_number;
	private String show_name;
	private Date show_date;
	
	private String seat_level;
	private int price;
	private int show_id;
	private int user_id;
	
	
	private int VIP_Remain_seat;
	private int S_Remain_seat;
	private int A_Remain_seat;


	
	

}

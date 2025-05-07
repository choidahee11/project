package com.shinhan.curd.dh;

//import java.util.Date;
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
public class ShowDTO {

	private int SHOW_ID;
	private int TICKET_BOX_ID;
	//private int User_ID;
	private String SHOW_NAME;
	private Date SHOW_DATE;
	
	private int TOTAL_SEAT;
	private int VIP_SEAT;
	private int S_SEAT;
	private int A_SEAT;
	
	private int VIP_PRICE;
	private int S_PRICE;
	private int A_PRICE;
	private int VIP_REMAIN_SEAT;
	private int S_REMAIN_SEAT;
	private int A_REMAIN_SEAT;
}

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
public class Ticket_BoxDTO {
	private int ticketBoxID;     
    private String showName;
    private Date showDate;
    private String showPlace;
}

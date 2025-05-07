package com.shinhan.curd.dh;

import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class DateUtil {

	public static String converToString(Date d1) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyy-MM-dd HH:nmm:ss");
		String str = sdf.format(d1);
		return str;

	}

	public static Date convertToDate(String str2) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyy-MM-dd");
		Date d2 = null;
		try {
			d2 = (Date) sdf.parse(str2);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return d2;
	}
//public static java.sql.Date convertToSQLDate(Date d){
	//return new java.sql.DAte(

	public static java.sql.Date convertToSQLDate(Date convertToDate) {
		
		java.sql.Date d = new java.sql.Date(convertToDate.getTime());
		return d;
	}

	
}


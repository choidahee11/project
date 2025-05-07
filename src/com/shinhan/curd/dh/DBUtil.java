package com.shinhan.curd.dh;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class DBUtil {

 
 

    public static Connection getConnection(){
    	
        Connection conn = null;
        
 
        try {
            // Oracle 드라이버 로딩
            Class.forName("oracle.jdbc.OracleDriver");

            // DB 연결 (주소, 사용자명, 비밀번호)
            conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", 
                                                "scott", // DB 사용자명
                                                "tiger"  // DB 비밀번호
            );
            if (conn != null) {
    
            }
        } catch (ClassNotFoundException e) {
            System.out.println("JDBC 드라이버를 찾을 수 없습니다: " + e.getMessage());
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("DB 연결 오류: " + e.getMessage());
            e.printStackTrace();
        }
        return conn;
    }

    public static void dbDisconnect(Connection conn, Statement st, ResultSet rs) {
    	try {
    		if(rs!=null) rs.close();
    		if(st!=null) st.close();
			if(conn!=null) conn.close();
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}


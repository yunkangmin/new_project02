package org.project.web;

import java.sql.Connection;
import java.sql.DriverManager;

import org.junit.Test;

public class MySQLConnectionTest {

	private static final String DRIVER = "com.mysql.jdbc.Driver";
	//MySQL Driver 6.0 이상"com.mysql.cj.jdbc.Driver";
	
	private static final String URL = "jdbc:mysql://127.0.0.1:3306/book_ex?useSSL=false";
	//MySQL 6.1 이상 "jdbc:mysql://127.0.0.1:3306/book_ex?useSSL=false&serverTimezone=Asia/Seoul";
	
	private static final String USER = 
			"admin";
	private static final String PW = 
			"121314";
			 
	
	@Test
	public void testConnection() throws Exception{
		
		Class.forName(DRIVER);
		
		try(Connection con = DriverManager.getConnection(URL, USER, PW)){
			
			System.out.println(con);
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}

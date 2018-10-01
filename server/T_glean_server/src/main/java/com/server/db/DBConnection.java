package com.server.db;

import java.sql.*;

public class DBConnection {
	public Connection getConnection() throws Exception {
		System.out.println("getConnection called");
		String driverName = "com.mysql.jdbc.Driver";
		String dbURL = "jdbc:mysql://localhost:3306/t_glean?serverTimezone=UTC";
		
		Class.forName(driverName);
		Connection con = DriverManager.getConnection(dbURL, "root", "korea");
		//System.out.println("DB 접속 완료");
		return con;
	}
	
	//close overloading
		public void close(Connection con, PreparedStatement pstmt, Statement stmt, ResultSet rs) {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {e.printStackTrace();}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {e.printStackTrace();}
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {e.printStackTrace();}
			}
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {e.printStackTrace();}
			}
		}
		
		public void close(Connection con, PreparedStatement pstmt, ResultSet rs) {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {e.printStackTrace();}
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {e.printStackTrace();}
			}
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {e.printStackTrace();}
			}
		}
		
		public void close(Connection con, Statement stmt, ResultSet rs) {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {e.printStackTrace();}
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {e.printStackTrace();}
			}
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {e.printStackTrace();}
			}
		}
		
		public void close(Connection con, Statement stmt) {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {e.printStackTrace();}
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {e.printStackTrace();}
			}
		}
		
		public void close(Connection con, PreparedStatement pstmt) {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {e.printStackTrace();}
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {e.printStackTrace();}
			}
		}
		
		public void close(PreparedStatement pstmt, ResultSet rs) {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {e.printStackTrace();}
			}
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {e.printStackTrace();}
			}
		}
}
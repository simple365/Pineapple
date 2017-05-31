package com.tom.utils.factory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class DButil {
	Logger logger = LoggerFactory.getLogger(DButil.class);
	private Connection conn = null;
	private java.sql.PreparedStatement ps;
	private ResultSet rs;
	private int rowsAffected;
	private String url;
	private String password;
	private String userName;

	public void init(String url, String userName, String passWord) {
		logger.info(String.format("数据库连接%s-%s-%s", url,userName,passWord));
		this.url = url;
		this.password = passWord;
		this.userName = userName;
	}

	public Connection openConnection() throws SQLException {

		try {
			conn = DriverManager.getConnection(url, userName, password);
			if (conn != null) {
				System.out.println("connect successful!");
			}
		} catch (SQLException e) {
			throw e;
		}
		return conn;
	}

	public void closeConnection() {
		try {
			if (conn != null)
				conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		conn = null;
	}

	public ResultSet querySql(String sql, Connection conn) {
		// List<String> list = new ArrayList<String>();
		try {
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rs;
	}

	public int queryResultNums(String sql) throws SQLException {
		// List<String> list = new ArrayList<String>();
		int count = 0;
		try {
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery(sql);
			while (rs.next()) {
				count = count + 1;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}
		return count;
	}

	public List<String> queryResults(String sql) {
		// List<String> list = new ArrayList<String>();
		List<String> result = new ArrayList<>();
		try {
			rs = querySql(sql, conn);
			for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
				result.add(rs.getString(i));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	public int updateSql(String sql, Connection conn) {
		try {
			ps = conn.prepareStatement(sql);
			rowsAffected = ps.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("rowsAffected: " + rowsAffected);
		return rowsAffected;
	}

	public void closeResult(PreparedStatement pStatement, ResultSet rs) {
		// TODO Auto-generated method stub
		try {
			pStatement.close();
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void closeResult(ResultSet rs) {
		// TODO Auto-generated method stub
		try {
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public List<String> queryInStrings(String sql) {
		List<String> values = new ArrayList<>();
		try {
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery(sql);
			int cnum = rs.getMetaData().getColumnCount();
			while (rs.next()) {
				for (int i = 1; i <= cnum; i++) {
					String value=rs.getString(i);
					//null 验证
					values.add(value==null?"null":value);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return values;
	}
}

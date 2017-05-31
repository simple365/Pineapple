package com.tom.utils.factory.impl;

import com.tom.utils.factory.DButil;

public class MysqlDButil extends DButil {

	static {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

}

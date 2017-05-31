package com.tom.utils.factory.impl;

import com.tom.utils.factory.DButil;

public class OracleDButil extends DButil {

	static {
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}

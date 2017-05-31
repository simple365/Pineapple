package com.tom.utils.factory;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.tom.utils.PropertiesUtil;
import com.tom.utils.TestContext;
import com.tom.utils.factory.impl.MysqlDButil;
import com.tom.utils.factory.impl.OracleDButil;

public class DBFactory {
	public static final String ORACLE = "oracle";
	public static final String MYSQL = "mysql";
	public static final String DDB = "ddb";
	private static final Map<String, DButil> dbs = new HashMap<>(3);

	public static boolean mysqlChanged = false;
	public static boolean ddbChanged = false;
	public static boolean oracleChanged = false;

	private static final String PROPERTY_FILE = Thread.currentThread().getContextClassLoader()
			.getResource("db.properties").getPath();

	public static DButil mysqlDButil() {
		String fileExt = TestContext.getInstance().getFileHandle().getFileExt();
		DButil dButil = dbs.get(MYSQL);
		if (dButil == null) {
			String url = PropertiesUtil.getProperty(fileExt + ".mysql_url", PROPERTY_FILE);
			String userName = PropertiesUtil.getProperty(fileExt + ".username", PROPERTY_FILE);
			String passWord = PropertiesUtil.getProperty(fileExt + ".password", PROPERTY_FILE);
			dButil = new MysqlDButil();
			dButil.init(url, userName, passWord);
			dbs.put(MYSQL, dButil);
		}
		return dButil;
	}

	public static DButil ddbDButil() {
		String fileExt = TestContext.getInstance().getFileHandle().getFileExt();
		DButil dButil = dbs.get(DDB);
		if (dButil == null ) {
			String url = PropertiesUtil.getProperty(fileExt + ".ddb_url", PROPERTY_FILE);
			String userName = PropertiesUtil.getProperty(fileExt + ".ddb_username", PROPERTY_FILE);
			String passWord = PropertiesUtil.getProperty(fileExt + ".ddb_password", PROPERTY_FILE);
			dButil = ddbDButil(url, userName, passWord);
			dbs.put(DDB, dButil);
		}
		return dButil;
	}

	public static DButil oracleDButil() {
		String fileExt = TestContext.getInstance().getFileHandle().getFileExt();
		DButil dButil = dbs.get(ORACLE);
		if (dButil == null) {
			String url = PropertiesUtil.getProperty(fileExt + ".oracle_url", PROPERTY_FILE);
			String userName = PropertiesUtil.getProperty(fileExt + ".oracle_username", PROPERTY_FILE);
			String passWord = PropertiesUtil.getProperty(fileExt + ".oracle_password", PROPERTY_FILE);
			dButil = new OracleDButil();
			dButil.init(url, userName, passWord);
			dbs.put(ORACLE, dButil);
		}
		return dButil;
	}

	/**
	 * 使用自定义的ddb
	 * 
	 * @param url
	 * @param userName
	 * @param passWord
	 * @return
	 */
	public static DButil ddbDButil(String url, String userName, String passWord) {
		DButil dButil = new MysqlDButil();
		dButil.init(url, userName, passWord);
		dbs.put(DDB, dButil);
		ddbChanged = true;
		return dButil;

	}

	/**
	 * 使用自定义的oracle
	 * 
	 * @param url
	 * @param userName
	 * @param passWord
	 * @return
	 */
	public static DButil oracleDButil(String url, String userName, String passWord) {
		DButil dButil = new OracleDButil();
		dButil.init(url, userName, passWord);
		dbs.put(ORACLE, dButil);
		oracleChanged = true;
		return dButil;
	}

	/**
	 * 使用自定义的mysql
	 * 
	 * @param url
	 * @param userName
	 * @param passWord
	 * @return
	 */
	public static DButil mysqlDButil(String url, String userName, String passWord) {
		DButil dButil = new MysqlDButil();
		dButil.init(url, userName, passWord);
		dbs.put(MYSQL, dButil);
		mysqlChanged = true;
		return dButil;

	}

	public static void closeAll() {
		if (dbs.get(MYSQL) != null)
			dbs.get(MYSQL).closeConnection();
		if (dbs.get(DDB) != null)
			dbs.get(DDB).closeConnection();
		if (dbs.get(ORACLE) != null)
			dbs.get(ORACLE).closeConnection();
	}

	public static void cleanse() {
		if (mysqlChanged){
			dbs.put(MYSQL, null);
			mysqlChanged=false;
		}
		if (ddbChanged){
			dbs.put(DDB, null);
			ddbChanged=false;
		}
		if (oracleChanged){
			dbs.put(ORACLE, null);
			oracleChanged=false;
		}
	}
}

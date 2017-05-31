package com.tom.test.logic;

import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tom.utils.ParameterUtil;
import com.tom.utils.TestContext;
import com.tom.utils.factory.DBFactory;
import com.tom.utils.factory.DButil;

import cucumber.api.java.en.And;

public class DatabaseSteps {
	ParameterUtil paraUtil = new ParameterUtil();
	Logger logger = LoggerFactory.getLogger(DatabaseSteps.class);
	// 数据库部分*********************************

	@And("^mysql连接,验证返回结果条数等于(\\d+):(.+)$")
	public void connectMysql(int count, String sql) throws Exception {
		verifyCount(DBFactory.mysqlDButil(), count, sql);
	}

	@And("^oracle连接,验证返回结果条数等于(\\d+):(.+)$")
	public void connectOracle(int count, String sql) throws Exception {
		verifyCount(DBFactory.oracleDButil(), count, sql);
	}

	@And("^ddb连接,验证返回结果条数等于(\\d+):(.+)$")
	public void connectddb(int count, String sql) throws Exception {
		verifyCount(DBFactory.ddbDButil(), count, sql);
	}

	@And("^ddb连接:(.+)用户名:(.+)密码:(.+)$")
	public void linkddb(String connect, String username, String password) {
		connect=paraUtil.parseParameter(connect);
		username=paraUtil.parseParameter(username);
		password=paraUtil.parseParameter(password);
		DBFactory.ddbDButil("jdbc:mysql://" + connect.trim(), username, password);
	}

	@And("^mysql连接:(.+)用户名:(.+)密码:(.+)$")
	public void linkmysql(String connect, String username, String password) {
		connect=paraUtil.parseParameter(connect);
		username=paraUtil.parseParameter(username);
		password=paraUtil.parseParameter(password);
		DBFactory.mysqlDButil("jdbc:mysql://" + connect.trim(), username, password);
	}

	@And("^oracle连接:(.+)用户名:(.+)密码:(.+)$")
	public void linkOracle(String connect, String username, String password) {
		connect=paraUtil.parseParameter(connect);
		username=paraUtil.parseParameter(username);
		password=paraUtil.parseParameter(password);
//		DBFactory.oracleDButil("jdbc:oracle:thin:@" + connect.trim(), username, password);
		DBFactory.oracleDButil(connect.trim(), username, password);
	}

	public void verifyCount(DButil dButil, int count, String sql) throws Exception {
		sql = paraUtil.parseParameter(sql);
		dButil.openConnection();
		int resl = dButil.queryResultNums(sql);
		dButil.closeConnection();
		if (resl != count) {
			throw new RuntimeException("返回数据库记录数是:" + resl + " 不等于:" + count);
		}
	}

	@And("^mysql连接,验证返回结果等于(.+?):(.+)$")
	public void connectMysql(List<String> expected, String sql) throws Exception {
		DButil dButil = DBFactory.mysqlDButil();
		verifyResult(dButil, sql, expected);
	}

	@And("^ddb连接,验证返回结果等于(.+?):(.+)$")
	public void connectddb(List<String> expected, String sql) throws Exception {
		DButil dButil = DBFactory.ddbDButil();
		verifyResult(dButil, sql, expected);
	}

	@And("^oracle连接,验证返回结果等于(.+?):(.+)$")
	public void notNullOracle(List<String> expected, String sql) throws Exception {
		DButil dButil = DBFactory.oracleDButil();
		verifyResult(dButil, sql, expected);
	}

	@And("^mysql验证结果不等于(.+?):(.+)$")
	public void notNullMysql(List<String> expected, String sql) throws Exception {
		DButil dButil = DBFactory.mysqlDButil();
		notEqual(dButil, sql, expected);
	}

	@And("^ddb验证结果不等于(.+?):(.+)$")
	public void notNullDDb(List<String> expected, String sql) throws Exception {
		DButil dButil = DBFactory.ddbDButil();
		notEqual(dButil, sql, expected);
	}

	@And("^oracle验证结果不等于(.+?):(.+)$")
	public void connectOracle(List<String> expected, String sql) throws Exception {
		DButil dButil = DBFactory.oracleDButil();
		notEqual(dButil, sql, expected);
	}

	/**
	 * 传入的值考虑前后空格的情况
	 * 
	 * @param dButil
	 * @param sql
	 * @param expected
	 * @throws SQLException
	 */
	private void notEqual(DButil dButil, String sql, List<String> expected) throws SQLException {
		// TODO Auto-generated method stub
		sql = paraUtil.parseParameter(sql);
		dButil.openConnection();
		List<String> resl = dButil.queryInStrings(sql);
		dButil.closeConnection();
		for (int i = 0; i < expected.size(); i++) {
			if (expected.get(i).equals(resl.get(i))) {
				throw new RuntimeException("期望与返回结果相同:" + resl.get(i));
			}
		}
	}

	@And("^ddb运行:(.+?)$")
	public void executeDdb(String sql) throws Exception {
		DButil dButil = DBFactory.ddbDButil();
		execute(dButil, sql);
	}

	@And("^oracle运行:(.+?)$")
	public void oracleUpdateExecute(String sql) throws Exception {
		DButil dButil = DBFactory.oracleDButil();
		execute(dButil, sql);
	}

	private void execute(DButil dButil, String sql) throws Exception {
		sql = paraUtil.parseParameter(sql);
		logger.info("sql 执行:" + sql);
		dButil.updateSql(sql, dButil.openConnection());
	}

	public void verifyResult(DButil dButil, String sql, List<String> expected) throws Exception {
		sql = paraUtil.parseParameter(sql);
		dButil.openConnection();
		List<String> resl = dButil.queryInStrings(sql);
		dButil.closeConnection();
		for (int i = 0; i < expected.size(); i++) {
			String expect=paraUtil.parseParameter(expected.get(i));
			if (!expect.equals(resl.get(i))) {
				throw new RuntimeException("期望值：" + expect + "返回结果值:" + resl.get(i));
			}
		}
	}

	@And("^mysql执行:(.+?)结果保存为场景参数:(.+)$")
	public void mysqlExecute(String sql, List<String> paras) throws Exception {
		sql = paraUtil.parseParameter(sql);
		DBFactory.mysqlDButil().openConnection();
		List<String> values = DBFactory.mysqlDButil().queryInStrings(sql);
		putDbResults(paras, values);
		DBFactory.mysqlDButil().closeConnection();
	}

	private void putDbResults(List<String> paras, List<String> values) {
		for (int i = 0; i < paras.size(); i++) {
			String value = null;
			// 此时出现了没有返回值的情况
			if (i < values.size()) {
				value = values.get(i);
			}
			TestContext.getInstance().putScenarioParameter(paras.get(i), value);
		}
	}

	@And("^ddb执行:(.+?)结果保存为场景参数:(.+)$")
	public void ddbExecute(String sql, List<String> paras) throws Exception {
		sql = paraUtil.parseParameter(sql);
		DBFactory.ddbDButil().openConnection();
		List<String> values = DBFactory.ddbDButil().queryInStrings(sql);
		putDbResults(paras, values);
		DBFactory.ddbDButil().closeConnection();
	}

	@And("^oracle执行:(.+?)结果保存为场景参数:(.+)$")
	public void oracleExecute(String sql, List<String> paras) throws Exception {
		sql = paraUtil.parseParameter(sql);
		DBFactory.oracleDButil().openConnection();
		List<String> values = DBFactory.oracleDButil().queryInStrings(sql);
		putDbResults(paras, values);
		DBFactory.oracleDButil().closeConnection();
	}

}

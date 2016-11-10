// (c) 2016 uchicom
package com.uchicom.nanokvs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.logging.Logger;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

/**
 * コネクション.
 *
 * @author uchicom: Shigeki Uchiyama
 *
 */
public class MailConnection implements Connection {

	/** ロガー */
	private static final Logger logger = Logger.getLogger(MailConnection.class.getCanonicalName());

	private Properties info;
	//POP3情報
	private String pop3Host;
	private int pop3Port = 110;
	private String pop3Db;
	private Socket pop3Socket;
	private PrintStream pop3Ps;
	private BufferedReader pop3Br;

	//SMTP情報
	private String smtpHost;
	private int smtpPort = 25;
	private String smtpDb;
	private Socket smtpSocket;
	private PrintStream smtpPs;
	private BufferedReader smtpBr;
	/** key:box, value:メッセージ番号のリスト */
	private Map<String, List<Msg>> boxMap = new HashMap<>();

	/**
	 * コンストラクタ.
	 */
	public MailConnection(String url, Properties info) {
		if (url.startsWith(MailDriver.URL_PREFIX)) {
			String hosts = url.substring(MailDriver.URL_PREFIX.length());
			String[] hostArray = hosts.split(",");
			String[] pop3Dbs = hostArray[0].split("/", 2);
			if (pop3Dbs.length > 1) {
				pop3Db = pop3Dbs[1];
			}
			String[] pop3s = pop3Dbs[0].split(":", 2);
			pop3Host = pop3s[0];
			if (pop3s.length > 1) {
				pop3Port = Integer.parseInt(pop3s[1]);
			}
			if (hostArray.length > 1) {
				String[] smtpDbs = hostArray[1].split("/", 2);
				if (smtpDbs.length > 1) {
					smtpDb = smtpDbs[1];
				}
				String[] smtps = smtpDbs[0].split(":", 2);
				smtpHost = smtps[0];
				if (smtps.length > 1) {
					smtpPort = Integer.parseInt(smtps[1]);
				}
			}
		}
		this.info = info;
	}

	/* (非 Javadoc)
	 * @see java.sql.Wrapper#unwrap(java.lang.Class)
	 */
	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.Wrapper#isWrapperFor(java.lang.Class)
	 */
	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#createStatement()
	 */
	@Override
	public Statement createStatement() throws SQLException {
		return new MailStatement(this);
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#prepareStatement(java.lang.String)
	 */
	@Override
	public PreparedStatement prepareStatement(String sql) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#prepareCall(java.lang.String)
	 */
	@Override
	public CallableStatement prepareCall(String sql) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#nativeSQL(java.lang.String)
	 */
	@Override
	public String nativeSQL(String sql) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#setAutoCommit(boolean)
	 */
	@Override
	public void setAutoCommit(boolean autoCommit) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#getAutoCommit()
	 */
	@Override
	public boolean getAutoCommit() throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#commit()
	 */
	@Override
	public void commit() throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#rollback()
	 */
	@Override
	public void rollback() throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#close()
	 */
	@Override
	public void close() throws SQLException {
		if (pop3Socket != null) {
			try {
				if (!pop3Socket.isOutputShutdown()) {
					writeclf(pop3Ps, "QUIT\r\n");
					logger.info("QUIT POP3!");
				}
				pop3Socket.close();
			} catch (IOException e) {
				throw new SQLException(e);
			}
		}
		if (smtpSocket != null) {
			try {
				if (!smtpSocket.isOutputShutdown()) {
					writeclf(smtpPs, "QUIT\r\n");
					logger.info("QUIT SMTP!");
				}
				smtpSocket.close();
			} catch (IOException e) {
				throw new SQLException(e);
			}
		}

		logger.info("CLOSE CONNECTION!");
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#isClosed()
	 */
	@Override
	public boolean isClosed() throws SQLException {
		return (pop3Socket == null || pop3Socket.isClosed()) && (smtpSocket == null || smtpSocket.isClosed());
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#getMetaData()
	 */
	@Override
	public DatabaseMetaData getMetaData() throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#setReadOnly(boolean)
	 */
	@Override
	public void setReadOnly(boolean readOnly) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#isReadOnly()
	 */
	@Override
	public boolean isReadOnly() throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#setCatalog(java.lang.String)
	 */
	@Override
	public void setCatalog(String catalog) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#getCatalog()
	 */
	@Override
	public String getCatalog() throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#setTransactionIsolation(int)
	 */
	@Override
	public void setTransactionIsolation(int level) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#getTransactionIsolation()
	 */
	@Override
	public int getTransactionIsolation() throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#getWarnings()
	 */
	@Override
	public SQLWarning getWarnings() throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#clearWarnings()
	 */
	@Override
	public void clearWarnings() throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#createStatement(int, int)
	 */
	@Override
	public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#prepareStatement(java.lang.String, int, int)
	 */
	@Override
	public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency)
			throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#prepareCall(java.lang.String, int, int)
	 */
	@Override
	public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#getTypeMap()
	 */
	@Override
	public Map<String, Class<?>> getTypeMap() throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#setTypeMap(java.util.Map)
	 */
	@Override
	public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#setHoldability(int)
	 */
	@Override
	public void setHoldability(int holdability) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#getHoldability()
	 */
	@Override
	public int getHoldability() throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#setSavepoint()
	 */
	@Override
	public Savepoint setSavepoint() throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#setSavepoint(java.lang.String)
	 */
	@Override
	public Savepoint setSavepoint(String name) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#rollback(java.sql.Savepoint)
	 */
	@Override
	public void rollback(Savepoint savepoint) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#releaseSavepoint(java.sql.Savepoint)
	 */
	@Override
	public void releaseSavepoint(Savepoint savepoint) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#createStatement(int, int, int)
	 */
	@Override
	public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability)
			throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#prepareStatement(java.lang.String, int, int, int)
	 */
	@Override
	public PreparedStatement prepareStatement(String sql,
			int resultSetType,
			int resultSetConcurrency,
			int resultSetHoldability) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#prepareCall(java.lang.String, int, int, int)
	 */
	@Override
	public CallableStatement prepareCall(String sql,
			int resultSetType,
			int resultSetConcurrency,
			int resultSetHoldability) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#prepareStatement(java.lang.String, int)
	 */
	@Override
	public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#prepareStatement(java.lang.String, int[])
	 */
	@Override
	public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#prepareStatement(java.lang.String, java.lang.String[])
	 */
	@Override
	public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#createClob()
	 */
	@Override
	public Clob createClob() throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#createBlob()
	 */
	@Override
	public Blob createBlob() throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#createNClob()
	 */
	@Override
	public NClob createNClob() throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#createSQLXML()
	 */
	@Override
	public SQLXML createSQLXML() throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#isValid(int)
	 */
	@Override
	public boolean isValid(int timeout) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#setClientInfo(java.lang.String, java.lang.String)
	 */
	@Override
	public void setClientInfo(String name, String value) throws SQLClientInfoException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#setClientInfo(java.util.Properties)
	 */
	@Override
	public void setClientInfo(Properties properties) throws SQLClientInfoException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#getClientInfo(java.lang.String)
	 */
	@Override
	public String getClientInfo(String name) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#getClientInfo()
	 */
	@Override
	public Properties getClientInfo() throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#createArrayOf(java.lang.String, java.lang.Object[])
	 */
	@Override
	public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#createStruct(java.lang.String, java.lang.Object[])
	 */
	@Override
	public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#setSchema(java.lang.String)
	 */
	@Override
	public void setSchema(String schema) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#getSchema()
	 */
	@Override
	public String getSchema() throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#abort(java.util.concurrent.Executor)
	 */
	@Override
	public void abort(Executor executor) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#setNetworkTimeout(java.util.concurrent.Executor, int)
	 */
	@Override
	public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#getNetworkTimeout()
	 */
	@Override
	public int getNetworkTimeout() throws SQLException {
		throw new UnsupportedOperationException();
	}

	/**
	 * 挿入処理.
	 *
	 * @param box
	 * @param json
	 */
	public int insert(String box, String json) throws SQLException {
		logger.info("insert " + box + " " + json);
		try {
			String value = null;
			if (smtpSocket == null || !smtpSocket.isConnected() || smtpSocket.isClosed()) {
				smtpSocket = createSocket(smtpHost, smtpPort, "true".equals(info.getProperty("smtp.ssl")));
				smtpPs = new PrintStream(smtpSocket.getOutputStream());
				smtpBr = new BufferedReader(new InputStreamReader(
						smtpSocket.getInputStream()));

				value = smtpBr.readLine();
				logger.info(value);
				writeclf(smtpPs, "HELO " + info.getProperty("smtp.helo"));
				value = smtpBr.readLine();
				logger.info(value);

				//SSL接続の場合
				if (info.containsKey("smtp.auth")) {
					switch (info.getProperty("smtp.auth")) {
					case "plain":
						writeclf(smtpPs, "AUTH PLAIN");
						value = smtpBr.readLine();
						logger.info(value);
						String smtpUser = info.getProperty("smtp.user");
						String smtpPass = info.getProperty("smtp.pass");
						writeclf(smtpPs,
								Base64.getEncoder()
										.encodeToString((smtpUser + "\0" + smtpUser + "\0" + smtpPass).getBytes()));
						break;
					}
					value = smtpBr.readLine();
					logger.info(value);
				}
			}

			writeclf(smtpPs, "MAIL FROM: " + info.getProperty("smtp.mail_from"));
			value = smtpBr.readLine();
			logger.info(value);
			writeclf(smtpPs, "RCPT TO: " + info.getProperty("smtp.rcpt_to"));
			value = smtpBr.readLine();
			logger.info(value);
			writeclf(smtpPs, "DATA");
			value = smtpBr.readLine();
			logger.info(value);
			writecl(smtpPs, "From: " + info.getProperty("smtp.from"));
			writecl(smtpPs, "To: " + info.getProperty("smtp.to"));
			if (smtpDb != null && !"".equals(smtpDb)) {
				writecl(smtpPs, "Subject: " + box + " " + smtpDb);
			} else {
				writecl(smtpPs, "Subject: " + box);
			}
			ZonedDateTime datetime = ZonedDateTime.now();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE, d MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
			writecl(smtpPs, "Date: " + datetime.format(formatter));
			writecl(smtpPs, "Message-ID: <" + System.currentTimeMillis() + "@" + info.getProperty("smtp.domain") + ">");
			writecl(smtpPs, "");
			for (String keyValue : json.split(",")) {
				writecl(smtpPs, keyValue);
			}
			writeclf(smtpPs, ".");
			value = smtpBr.readLine();
			logger.info(value);
		} catch (IOException e) {
			throw new SQLException(e);
		} catch (Exception e) {
			throw new SQLException(e);
		}
		return 1;
	}

	/**
	 * 削除処理.
	 *
	 * @param box
	 * @param json
	 * @return
	 * @throws SQLException
	 */
	public int delete(String box, String json) throws SQLException {
		logger.info("delete " + box + " " + json);
		int deleteCount = 0;
		//削除
		try {
			String value = null;
			if (pop3Socket == null || !pop3Socket.isConnected() || pop3Socket.isClosed()) {
				initPop3();
				List<Msg> msgList = createMsgList();

				if (msgList.isEmpty())
					return 0;
				//headerを調べてboxを抽出
				createBoxMap(msgList);
			}

			logger.info(boxMap.toString());
			if (boxMap.containsKey(box)) {
				String[] keyValues = json.split(",");
				//boxが存在する
				List<Msg> retrList = boxMap.get(box);
				for (Msg msg : retrList) {
					Map<String, String> keyValueMap = createKeyValueMap(msg.getNum());
					if (equalsKeyValues(keyValues, keyValueMap)) {
						//一致する場合は削除
						writeclf(pop3Ps, "DELE " + msg.getNum());
						value = pop3Br.readLine();
						logger.info(value); //+OK
						if (value.startsWith("+OK")) {
							deleteCount++;
						}
					}
				}
			}
			logger.info("削除件数" + deleteCount);
		} catch (IOException e) {
			throw new SQLException(e);
		} catch (Exception e) {
			throw new SQLException(e);
		}
		return deleteCount;
	}

	/**
	 * 検索処理.
	 *
	 * @param box
	 * @param json
	 * @return
	 * @throws SQLException
	 */
	public List<Map<String, String>> select(String box, String json) throws SQLException {
		logger.info(json);
		List<Map<String, String>> resultList = null;
		try {
			if (pop3Socket == null || !pop3Socket.isConnected() || pop3Socket.isClosed()) {
				initPop3();
				List<Msg> msgList = createMsgList();
				if (msgList.isEmpty())
					return new ArrayList<>();
				//headerを調べてboxを抽出
				createBoxMap(msgList);
			}
			logger.info(boxMap.toString());
			if (boxMap.containsKey(box)) {
				String[] keyValues = json.split(",");
				//boxが存在する
				List<Msg> retrList = boxMap.get(box);
				resultList = new ArrayList<>(retrList.size());
				for (Msg msg : retrList) {
					Map<String, String> keyValueMap = createKeyValueMap(msg.getNum());
					if (equalsKeyValues(keyValues, keyValueMap)) {
						resultList.add(keyValueMap);
					}
				}
			}
			if (resultList == null) {
				//存在しない
				logger.info("データなし");
				resultList = Collections.emptyList();
			}
		} catch (IOException e) {
			throw new SQLException(e);
		} catch (Exception e) {
			throw new SQLException(e);
		}
		return resultList;
	}

	/**
	 * \r\nの改行付きで出力します.
	 * @param ps
	 * @param value
	 * @throws IOException
	 */
	private void writecl(PrintStream ps, String value) throws IOException {
		ps.write(value.getBytes());
		ps.write("\r\n".getBytes());
	}

	/**
	 * \r\nの改行付きで出力し、フラッシュします.
	 * @param ps
	 * @param value
	 * @throws IOException
	 */
	private void writeclf(PrintStream ps, String value) throws IOException {
		writecl(ps, value);
		ps.flush();
	}

	/**
	 * POP3接続の初期化.
	 * @throws Exception
	 */
	public void initPop3() throws Exception {
		pop3Socket = createSocket(pop3Host, pop3Port, "true".equals(info.getProperty("pop3.ssl")));
		pop3Ps = new PrintStream(pop3Socket.getOutputStream());
		pop3Br = new BufferedReader(new InputStreamReader(
				pop3Socket.getInputStream()));
		String value = pop3Br.readLine();
		logger.info(value);
		pop3Login();
		boxMap.clear();
	}

	/**
	 * POP3のログイン処理.
	 * @throws IOException
	 */
	public void pop3Login() throws IOException {
		writeclf(pop3Ps, "USER " + info.getProperty("pop3.user"));
		String value = pop3Br.readLine();
		logger.info(value);
		writeclf(pop3Ps, "PASS " + info.getProperty("pop3.pass"));
		value = pop3Br.readLine();
		logger.info(value);
	}

	/**
	 * UIDLを使用してメッセージのリストを作成します.
	 * @return
	 * @throws IOException
	 */
	public List<Msg> createMsgList() throws IOException {
		writeclf(pop3Ps, "UIDL");
		String value = pop3Br.readLine();
		logger.info(value); //+OK
		List<Msg> msgList = new ArrayList<>();
		while (!".".equals(value = pop3Br.readLine())) {
			String[] values = value.split(" ");
			msgList.add(new Msg(Integer.parseInt(value.split(" ")[0]), values[1]));
		}
		return msgList;
	}

	/**
	 * メッセージのリストをもとにヘッダーを調べてBOXマップを作成します.
	 *
	 * @param msgList
	 * @throws IOException
	 */
	public void createBoxMap(List<Msg> msgList) throws IOException {
		//headerを調べてboxを抽出
		for (Msg msg : msgList) {
			writeclf(pop3Ps, "TOP " + msg.getNum() + " 0");
			String value = null;
			while (!(value = pop3Br.readLine()).equals(".")) {
				if (value.startsWith("Subject: ")) {
					String[] keyBoxs = value.substring(9).split(" ", 2);
					if (pop3Db == null || "".equals(pop3Db)) {
						if (keyBoxs.length > 1)
							continue;
					} else {
						if (keyBoxs.length < 2)
							continue;
						if (!pop3Db.equals(keyBoxs[1]))
							continue;
					}
					String keyBox = keyBoxs[0];
					List<Msg> objectList = null;
					if (boxMap.containsKey(keyBox)) {
						objectList = boxMap.get(keyBox);
					} else {
						objectList = new ArrayList<>();
						boxMap.put(keyBox, objectList);
					}
					objectList.add(msg);
				}
			}
		}
	}

	/**
	 * RETRを使用してKeyValueマップを作成します.
	 *
	 * @param msgNum
	 * @return
	 * @throws IOException
	 */
	public Map<String, String> createKeyValueMap(Integer msgNum) throws IOException {
		writeclf(pop3Ps, "RETR " + msgNum);
		boolean body = false;
		String value = null;
		Map<String, String> keyValueMap = new LinkedHashMap<>();
		while (!".".equals(value = pop3Br.readLine())) {
			if ("".equals(value)) {
				body = true;
			} else if (body) {
				String[] keyValue = value.split(":", 2);
				if (keyValue.length > 1) {
					keyValueMap.put(keyValue[0], keyValue[1]);
				} else {
					keyValueMap.put(keyValue[0], null);
				}
			}
		}
		return keyValueMap;
	}

	/**
	 * keyValueの値を比較します.
	 *
	 * @param keyValues
	 * @param keyValueMap
	 * @return
	 */
	public boolean equalsKeyValues(String[] keyValues, Map<String, String> keyValueMap) {
		boolean check = true;
		for (String keyValue : keyValues) {
			String[] keyValueArray = keyValue.split(":");
			if (keyValueArray.length > 1) {
				String conditionValue = keyValueMap.get(keyValueArray[0]);
				if (!keyValueArray[1].equals(conditionValue)) {
					logger.info(keyValueArray[1] + "!=" + conditionValue);
					check = false;
					break;
				}
			}
		}
		return check;
	}

	/**
	 * ソケットを生成します.
	 *
	 * @param host
	 * @param port
	 * @return
	 * @throws Exception
	 */
	private Socket createSocket(String host, int port, boolean ssl) throws Exception {
		logger.info("createSocket");
		Socket socket = null;
		if (ssl) {
			// SSLソケットを生成する
			SSLContext context = SSLContext.getDefault();
			SSLSocketFactory sf = context.getSocketFactory();
			SSLSocket soc = (SSLSocket) sf.createSocket(host, port);
			soc.startHandshake();
			socket = soc;
		} else {
			// ソケットを生成する
			socket = new Socket(host, port);
		}
		return socket;
	}
}

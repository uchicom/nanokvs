// (c) 2017 uchicom
package com.uchicom.nanokvs;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * @author uchicom: Shigeki Uchiyama
 *
 */
public class Pop3 implements Closeable {

	/** ロガー */
	private static final Logger logger = Logger.getLogger(Pop3.class.getCanonicalName());

	protected Properties info;
	//POP3情報
	protected String pop3Host;
	protected int pop3Port = 110;
	protected String pop3Db;
	protected Socket pop3Socket;
	protected PrintStream pop3Ps;
	protected BufferedReader pop3Br;
	/** key:box, value:メッセージ番号のリスト */
	protected Map<String, List<Msg>> boxMap = new HashMap<>();

	/** cache用のデータ box.xx:value uidl.yy:value */
	protected Map<String, String> boxUidlMap;

	public Pop3(String url, Properties info) {
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
		}
		this.info = info;
		if (info.containsKey("uidl.cache")) {
			File cacheFile = new File(info.getProperty("uidl.cashe"));
			if (cacheFile.exists()) {
				try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(cacheFile))) {
					boxUidlMap = (Map<String ,String>)ois.readObject();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
				if (boxUidlMap == null) {
					boxUidlMap = new HashMap<>();
				}
			}
		}
	}
	/**
	 * 削除処理.
	 *
	 * @param box
	 * @param json
	 * @return
	 * @throws SQLException
	 */
	protected int delete(String box, String json) throws SQLException {
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
					if (ConnectionUtil.equalsKeyValues(keyValues, keyValueMap)) {
						//一致する場合は削除
						ConnectionUtil.writeclf(pop3Ps, "DELE " + msg.getNum());
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
	protected List<Map<String, String>> select(String box, String json) throws SQLException {
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
					if (ConnectionUtil.equalsKeyValues(keyValues, keyValueMap)) {
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
	 * POP3接続の初期化.
	 * @throws Exception
	 */
	protected void initPop3() throws Exception {
		pop3Socket = ConnectionUtil.createSocket(pop3Host, pop3Port, "true".equals(info.getProperty("pop3.ssl")));
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
	protected void pop3Login() throws IOException {
		ConnectionUtil.writeclf(pop3Ps, "USER " + info.getProperty("pop3.user"));
		String value = pop3Br.readLine();
		logger.info(value);
		ConnectionUtil.writeclf(pop3Ps, "PASS " + info.getProperty("pop3.pass"));
		value = pop3Br.readLine();
		logger.info(value);
	}

	/**
	 * UIDLを使用してメッセージのリストを作成します.
	 * @return
	 * @throws IOException
	 */
	protected List<Msg> createMsgList() throws IOException {
		ConnectionUtil.writeclf(pop3Ps, "UIDL");

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
	protected void createBoxMap(List<Msg> msgList) throws IOException {
		//headerを調べてboxを抽出
		for (Msg msg : msgList) {
			ConnectionUtil.writeclf(pop3Ps, "TOP " + msg.getNum() + " 0");
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
	/* (非 Javadoc)
	 * @see java.io.Closeable#close()
	 */
	@Override
	public void close() throws IOException {
		if (pop3Socket != null) {
			if (!pop3Socket.isOutputShutdown()) {
				ConnectionUtil.writeclf(pop3Ps, "QUIT\r\n");
				logger.info("QUIT POP3!");
			}
			pop3Socket.close();
		}
	}

	public boolean isClosed() {
		return pop3Socket == null || pop3Socket.isClosed();
	}

}

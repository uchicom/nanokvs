// (c) 2017 uchicom
package com.uchicom.nanokvs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * パイプラインでPOP3処理する.
 * @author uchicom: Shigeki Uchiyama
 *
 */
public class PipeliningPop3 extends Pop3{

	/** ロガー */
	private static final Logger logger = Logger.getLogger(PipeliningPop3.class.getCanonicalName());

	public PipeliningPop3(String url, Properties info) {
		super(url, info);
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
		int result = 0;
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
						ConnectionUtil.writecl(pop3Ps, "DELE " + msg.getNum());
						deleteCount++;
					}
				}
				pop3Ps.flush();
				for (int i = 0; i < deleteCount; i++) {
					value = pop3Br.readLine();
					logger.info(value); //+OK
					if (value.startsWith("+OK")) {
						result++;
					}
				}
			}
			logger.info("削除件数" + result);
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
		long start = System.currentTimeMillis();
		List<Map<String, String>> resultList = null;
		try {
			if (pop3Socket == null || !pop3Socket.isConnected() || pop3Socket.isClosed()) {
				initPop3();
				System.out.println("initPop3" + (System.currentTimeMillis() - start) / 1000d + "[s]" );

				List<Msg> msgList = createMsgList();
				System.out.println("createMsgList" + (System.currentTimeMillis() - start) / 1000d + "[s]" );
				if (msgList.isEmpty())
					return new ArrayList<>();
				//headerを調べてboxを抽出
				createBoxMap(msgList);
				System.out.println("createBoxMap" + (System.currentTimeMillis() - start) / 1000d + "[s]" );
			}
			logger.info(boxMap.toString());
			if (boxMap.containsKey(box)) {
				String[] keyValues = json.split(",");
				//boxが存在する
				List<Msg> retrList = boxMap.get(box);
				resultList = new ArrayList<>(retrList.size());
				for (Msg msg : retrList) {
					ConnectionUtil.writecl(pop3Ps, "RETR " + msg.getNum());
				}
				System.out.println("RETR" + (System.currentTimeMillis() - start) / 1000d + "[s]" );

				pop3Ps.flush();
				for (Msg msg : retrList) {
					Map<String, String> keyValueMap = createKeyValueMap(msg.getNum());
					if (ConnectionUtil.equalsKeyValues(keyValues, keyValueMap)) {
						resultList.add(keyValueMap);
					}
				}
				System.out.println("RETR read" + (System.currentTimeMillis() - start) / 1000d + "[s]" );
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
	public void initPop3() throws Exception {
		long start = System.currentTimeMillis();
		pop3Socket = ConnectionUtil.createSocket(pop3Host, pop3Port, "true".equals(info.getProperty("pop3.ssl")));
		System.out.println("CONNECT" + (System.currentTimeMillis() - start) / 1000d + "[s]" );

		pop3Ps = new PrintStream(pop3Socket.getOutputStream());
		System.out.println("OUT"+ (System.currentTimeMillis() - start) / 1000d + "[s]" );

		pop3Br = new BufferedReader(new InputStreamReader(
				pop3Socket.getInputStream()));
		System.out.println("READER" + (System.currentTimeMillis() - start) / 1000d + "[s]" );

		String value = pop3Br.readLine();
		System.out.println("READ" + (System.currentTimeMillis() - start) / 1000d + "[s]" );

		logger.info(value);
		pop3Login();System.out.println("LOGIN" + (System.currentTimeMillis() - start) / 1000d + "[s]" );

		boxMap.clear();
	}

	/**
	 * POP3のログイン処理.
	 * @throws IOException
	 */
	public void pop3Login() throws IOException {
		ConnectionUtil.writecl(pop3Ps, "USER " + info.getProperty("pop3.user"));
		ConnectionUtil.writeclf(pop3Ps, "PASS " + info.getProperty("pop3.pass"));
		String value = pop3Br.readLine();
		logger.info(value);
		value = pop3Br.readLine();
		logger.info(value);
	}

	/**
	 * UIDLを使用してメッセージのリストを作成します.
	 * @return
	 * @throws IOException
	 */
	public List<Msg> createMsgList() throws IOException {
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
	public void createBoxMap(List<Msg> msgList) throws IOException {
		//headerを調べてboxを抽出
		for (Msg msg : msgList) {
			ConnectionUtil.writecl(pop3Ps, "TOP " + msg.getNum() + " 0");
		}
		pop3Ps.flush();

		for (Msg msg : msgList) {
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

}

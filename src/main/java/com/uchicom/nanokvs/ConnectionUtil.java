// (c) 2017 uchicom
package com.uchicom.nanokvs;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Map;
import java.util.logging.Logger;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

/**
 * @author uchicom: Shigeki Uchiyama
 *
 */
public class ConnectionUtil {

	/** ロガー */
	private static final Logger logger = Logger.getLogger(ConnectionUtil.class.getCanonicalName());

	/**
	 * \r\nの改行付きで出力します.
	 * @param ps
	 * @param value
	 * @throws IOException
	 */
	protected static void writecl(PrintStream ps, String value) throws IOException {
		ps.write(value.getBytes());
		ps.write("\r\n".getBytes());
	}

	/**
	 * \r\nの改行付きで出力し、フラッシュします.
	 * @param ps
	 * @param value
	 * @throws IOException
	 */
	protected static void writeclf(PrintStream ps, String value) throws IOException {
		writecl(ps, value);
		ps.flush();
	}

	/**
	 * keyValueの値を比較します.
	 *
	 * @param keyValues
	 * @param keyValueMap
	 * @return
	 */
	protected static boolean equalsKeyValues(String[] keyValues, Map<String, String> keyValueMap) {
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
	protected static Socket createSocket(String host, int port, boolean ssl) throws Exception {
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

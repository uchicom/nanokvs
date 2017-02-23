// (c) 2016 uchicom
package com.uchicom.nanokvs;

import java.io.IOException;
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
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.logging.Logger;

/**
 * コネクション.
 *
 * @author uchicom: Shigeki Uchiyama
 *
 */
public class MailConnection implements Connection {

	/** ロガー */
	private static final Logger logger = Logger.getLogger(MailConnection.class.getCanonicalName());

	protected Properties info;

	private Pop3 pop3;
	private Smtp smtp;

	/**
	 * コンストラクタ.
	 */
	public MailConnection(String url, Properties info) {
		if (Boolean.valueOf(info.getProperty("pop3.pipelining"))) {
			pop3 = new PipeliningPop3(url, info);
		} else {
			pop3 = new Pop3(url, info);
		}
		if (Boolean.valueOf(info.getProperty("smtp.pipelining"))) {
			smtp = new PipeliningSmtp(url, info);
		} else {
			smtp = new Smtp(url, info);
		}


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
			try {
				pop3.close();
			} catch (IOException e) {
				throw new SQLException(e);
			} finally {
				try {
					smtp.close();
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
		return pop3.isClosed() && smtp.isClosed();
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
	protected int insert(String box, String json) throws SQLException {
		return smtp.insert(box, json);
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
		return pop3.delete(box, json);
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
		return pop3.select(box, json);
	}

}

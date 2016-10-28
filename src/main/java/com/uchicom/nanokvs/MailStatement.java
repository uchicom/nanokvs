// (c) 2016 uchicom
package com.uchicom.nanokvs;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLSyntaxErrorException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

/**
 * ステートメント.
 *
 * @author uchicom: Shigeki Uchiyama
 *
 */
public class MailStatement implements Statement {

	/** コネクション */
	MailConnection connection;

	/**
	 * 引数ありのコンストラクタ.
	 * @param connection
	 */
	public MailStatement(MailConnection connection) {
		this.connection = connection;
	}

	/* (非 Javadoc)
	 * @see java.sql.Wrapper#unwrap(java.lang.Class)
	 */
	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		return null;
	}

	/* (非 Javadoc)
	 * @see java.sql.Wrapper#isWrapperFor(java.lang.Class)
	 */
	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return false;
	}

	/**
	 *
	 * box名{"name":"John Smith","age":33} //json部分は条件
	 *
	 * @param sql
	 * @return
	 * @throws SQLException
	 */
	/* (非 Javadoc)
	 * @see java.sql.Statement#executeQuery(java.lang.String)
	 */
	@Override
	public ResultSet executeQuery(String sql) throws SQLException {
		//box名(key=a,key=b)
		//pop3接続してデータ取得TOPで全データ取得その後指定のboxを取得する
		String boxobject = sql.trim().toLowerCase();
		String[] splits = boxobject.split("\\{", 2);
		if (splits.length > 1 && !splits[1].endsWith("}")) {
			throw new SQLSyntaxErrorException("}");
		}
		if (splits.length > 1) {
			List<Map<String, String>> resultList = connection.select(splits[0],
					splits[1].substring(0, splits[1].length() - 1));
			return new MailResultSet(resultList);
		} else {
			List<Map<String, String>> resultList = connection.select(splits[0], null);
			return new MailResultSet(resultList);
		}
	}

	/**
	 * insertとdeleteのみ<br/>
	 * insert<br/>
	 * i box名 {"name": "John Smith", "age": 33} //json部分は格納オブジェクト<br/>
	 * delete<br/>
	 * d box名 {"name": "John Smith", "age": 33} //json部分は条件<br/>
	 *
	 * @param sql
	 * @return
	 * @throws SQLException
	 */
	/* (非 Javadoc)
	 * @see java.sql.Statement#executeUpdate(java.lang.String)
	 */
	@Override
	public int executeUpdate(String sql) throws SQLException {
		String lowerSql = sql.trim().toLowerCase();
		String boxobject = lowerSql.substring(1).trim();
		String[] splits = boxobject.split("\\{", 2);
		if (splits.length > 1 && !splits[1].endsWith("}")) {
			throw new SQLSyntaxErrorException("}");
		}
		int count = 0;
		switch (lowerSql.charAt(0)) {
		case 'i':
			//登録処理（メール送信処理）
			count = connection.insert(splits[0], splits[1].substring(0, splits[1].length() - 1));
			break;
		case 'd':
			//削除処理（メール削除処理）
			count = connection.delete(splits[0], splits[1].substring(0, splits[1].length() - 1));
			break;
		default:
			throw new SQLSyntaxErrorException("先頭にiまたはdを指定してください");
		}
		return count;
	}

	/* (非 Javadoc)
	 * @see java.sql.Statement#close()
	 */
	@Override
	public void close() throws SQLException {
	}

	/* (非 Javadoc)
	 * @see java.sql.Statement#getMaxFieldSize()
	 */
	@Override
	public int getMaxFieldSize() throws SQLException {
		return 0;
	}

	/* (非 Javadoc)
	 * @see java.sql.Statement#setMaxFieldSize(int)
	 */
	@Override
	public void setMaxFieldSize(int max) throws SQLException {
	}

	/* (非 Javadoc)
	 * @see java.sql.Statement#getMaxRows()
	 */
	@Override
	public int getMaxRows() throws SQLException {
		return 0;
	}

	/* (非 Javadoc)
	 * @see java.sql.Statement#setMaxRows(int)
	 */
	@Override
	public void setMaxRows(int max) throws SQLException {
	}

	/* (非 Javadoc)
	 * @see java.sql.Statement#setEscapeProcessing(boolean)
	 */
	@Override
	public void setEscapeProcessing(boolean enable) throws SQLException {
	}

	/* (非 Javadoc)
	 * @see java.sql.Statement#getQueryTimeout()
	 */
	@Override
	public int getQueryTimeout() throws SQLException {
		return 0;
	}

	/* (非 Javadoc)
	 * @see java.sql.Statement#setQueryTimeout(int)
	 */
	@Override
	public void setQueryTimeout(int seconds) throws SQLException {
	}

	/* (非 Javadoc)
	 * @see java.sql.Statement#cancel()
	 */
	@Override
	public void cancel() throws SQLException {
	}

	/* (非 Javadoc)
	 * @see java.sql.Statement#getWarnings()
	 */
	@Override
	public SQLWarning getWarnings() throws SQLException {
		return null;
	}

	/* (非 Javadoc)
	 * @see java.sql.Statement#clearWarnings()
	 */
	@Override
	public void clearWarnings() throws SQLException {
	}

	/* (非 Javadoc)
	 * @see java.sql.Statement#setCursorName(java.lang.String)
	 */
	@Override
	public void setCursorName(String name) throws SQLException {
	}

	/* (非 Javadoc)
	 * @see java.sql.Statement#execute(java.lang.String)
	 */
	@Override
	public boolean execute(String sql) throws SQLException {
		return false;
	}

	/* (非 Javadoc)
	 * @see java.sql.Statement#getResultSet()
	 */
	@Override
	public ResultSet getResultSet() throws SQLException {
		return null;
	}

	/* (非 Javadoc)
	 * @see java.sql.Statement#getUpdateCount()
	 */
	@Override
	public int getUpdateCount() throws SQLException {
		return 0;
	}

	/* (非 Javadoc)
	 * @see java.sql.Statement#getMoreResults()
	 */
	@Override
	public boolean getMoreResults() throws SQLException {
		return false;
	}

	/* (非 Javadoc)
	 * @see java.sql.Statement#setFetchDirection(int)
	 */
	@Override
	public void setFetchDirection(int direction) throws SQLException {
	}

	/* (非 Javadoc)
	 * @see java.sql.Statement#getFetchDirection()
	 */
	@Override
	public int getFetchDirection() throws SQLException {
		return 0;
	}

	/* (非 Javadoc)
	 * @see java.sql.Statement#setFetchSize(int)
	 */
	@Override
	public void setFetchSize(int rows) throws SQLException {
	}

	/* (非 Javadoc)
	 * @see java.sql.Statement#getFetchSize()
	 */
	@Override
	public int getFetchSize() throws SQLException {
		return 0;
	}

	/* (非 Javadoc)
	 * @see java.sql.Statement#getResultSetConcurrency()
	 */
	@Override
	public int getResultSetConcurrency() throws SQLException {
		return 0;
	}

	/* (非 Javadoc)
	 * @see java.sql.Statement#getResultSetType()
	 */
	@Override
	public int getResultSetType() throws SQLException {
		return 0;
	}

	/* (非 Javadoc)
	 * @see java.sql.Statement#addBatch(java.lang.String)
	 */
	@Override
	public void addBatch(String sql) throws SQLException {
	}

	/* (非 Javadoc)
	 * @see java.sql.Statement#clearBatch()
	 */
	@Override
	public void clearBatch() throws SQLException {
	}

	/* (非 Javadoc)
	 * @see java.sql.Statement#executeBatch()
	 */
	@Override
	public int[] executeBatch() throws SQLException {
		return null;
	}

	/* (非 Javadoc)
	 * @see java.sql.Statement#getConnection()
	 */
	@Override
	public Connection getConnection() throws SQLException {
		return connection;
	}

	/* (非 Javadoc)
	 * @see java.sql.Statement#getMoreResults(int)
	 */
	@Override
	public boolean getMoreResults(int current) throws SQLException {
		return false;
	}

	/* (非 Javadoc)
	 * @see java.sql.Statement#getGeneratedKeys()
	 */
	@Override
	public ResultSet getGeneratedKeys() throws SQLException {
		return null;
	}

	/* (非 Javadoc)
	 * @see java.sql.Statement#executeUpdate(java.lang.String, int)
	 */
	@Override
	public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
		return 0;
	}

	/* (非 Javadoc)
	 * @see java.sql.Statement#executeUpdate(java.lang.String, int[])
	 */
	@Override
	public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
		return 0;
	}

	/* (非 Javadoc)
	 * @see java.sql.Statement#executeUpdate(java.lang.String, java.lang.String[])
	 */
	@Override
	public int executeUpdate(String sql, String[] columnNames) throws SQLException {
		return 0;
	}

	/* (非 Javadoc)
	 * @see java.sql.Statement#execute(java.lang.String, int)
	 */
	@Override
	public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
		return false;
	}

	/* (非 Javadoc)
	 * @see java.sql.Statement#execute(java.lang.String, int[])
	 */
	@Override
	public boolean execute(String sql, int[] columnIndexes) throws SQLException {
		return false;
	}

	/* (非 Javadoc)
	 * @see java.sql.Statement#execute(java.lang.String, java.lang.String[])
	 */
	@Override
	public boolean execute(String sql, String[] columnNames) throws SQLException {
		return false;
	}

	/* (非 Javadoc)
	 * @see java.sql.Statement#getResultSetHoldability()
	 */
	@Override
	public int getResultSetHoldability() throws SQLException {
		return 0;
	}

	/* (非 Javadoc)
	 * @see java.sql.Statement#isClosed()
	 */
	@Override
	public boolean isClosed() throws SQLException {
		return false;
	}

	/* (非 Javadoc)
	 * @see java.sql.Statement#setPoolable(boolean)
	 */
	@Override
	public void setPoolable(boolean poolable) throws SQLException {
	}

	/* (非 Javadoc)
	 * @see java.sql.Statement#isPoolable()
	 */
	@Override
	public boolean isPoolable() throws SQLException {
		return false;
	}

	/* (非 Javadoc)
	 * @see java.sql.Statement#closeOnCompletion()
	 */
	@Override
	public void closeOnCompletion() throws SQLException {
	}

	/* (非 Javadoc)
	 * @see java.sql.Statement#isCloseOnCompletion()
	 */
	@Override
	public boolean isCloseOnCompletion() throws SQLException {
		return false;
	}

}

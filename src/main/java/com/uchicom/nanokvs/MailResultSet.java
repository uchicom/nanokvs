// (c) 2016 uchicom
package com.uchicom.nanokvs;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 結果セット.
 *
 * @author uchicom: Shigeki Uchiyama
 *
 */
public class MailResultSet implements ResultSet {

	/** 結果マップリスト */
	private List<Map<String, String>> mapList;

	/** カレントマップ */
	private Map<String, String> currentMap;

	/** カレントエントリセット */
	private Entry<String, String>[] currentEntrySetArray;

	/**
	 * 引数ありのコンストラクタ.
	 *
	 * @param mapList 結果マップ（順序あり）リスト（メール取得順）
	 */
	public MailResultSet(List<Map<String, String>> mapList) {
		this.mapList = mapList;
	}

	/* (非 Javadoc)
	 * @see java.sql.Wrapper#isWrapperFor(java.lang.Class)
	 */
	@Override
	public boolean isWrapperFor(Class<?> paramClass) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.Wrapper#unwrap(java.lang.Class)
	 */
	@Override
	public <T> T unwrap(Class<T> paramClass) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#next()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean next() throws SQLException {
		if (mapList.size() > 0) {
			currentMap = mapList.remove(0);
			currentEntrySetArray = currentMap.entrySet().toArray(new Entry[0]);
			return true;
		}
		return false;
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#close()
	 */
	@Override
	public void close() throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#wasNull()
	 */
	@Override
	public boolean wasNull() throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getString(int)
	 */
	@Override
	public String getString(int paramInt) throws SQLException {
		if (paramInt <= currentEntrySetArray.length) {
			return currentEntrySetArray[paramInt - 1].getValue();
		}
		return null;
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getBoolean(int)
	 */
	@Override
	public boolean getBoolean(int paramInt) throws SQLException {
		return Boolean.parseBoolean(currentEntrySetArray[paramInt - 1].getValue());
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getByte(int)
	 */
	@Override
	public byte getByte(int paramInt) throws SQLException {
		return Byte.parseByte(currentEntrySetArray[paramInt - 1].getValue());
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getShort(int)
	 */
	@Override
	public short getShort(int paramInt) throws SQLException {
		return Short.parseShort(currentEntrySetArray[paramInt - 1].getValue());
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getInt(int)
	 */
	@Override
	public int getInt(int paramInt) throws SQLException {
		return Integer.parseInt(currentEntrySetArray[paramInt - 1].getValue());
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getLong(int)
	 */
	@Override
	public long getLong(int paramInt) throws SQLException {
		return Long.parseLong(currentEntrySetArray[paramInt - 1].getValue());
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getFloat(int)
	 */
	@Override
	public float getFloat(int paramInt) throws SQLException {
		return Float.parseFloat(currentEntrySetArray[paramInt - 1].getValue());
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getDouble(int)
	 */
	@Override
	public double getDouble(int paramInt) throws SQLException {
		return Double.parseDouble(currentEntrySetArray[paramInt - 1].getValue());
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getBigDecimal(int, int)
	 */
	@Deprecated
	@Override
	public BigDecimal getBigDecimal(int paramInt1, int paramInt2) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getBytes(int)
	 */
	@Override
	public byte[] getBytes(int paramInt) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getDate(int)
	 */
	@Override
	public Date getDate(int paramInt) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getTime(int)
	 */
	@Override
	public Time getTime(int paramInt) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getTimestamp(int)
	 */
	@Override
	public Timestamp getTimestamp(int paramInt) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getAsciiStream(int)
	 */
	@Override
	public InputStream getAsciiStream(int paramInt) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getUnicodeStream(int)
	 */
	@Override
	public InputStream getUnicodeStream(int paramInt) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getBinaryStream(int)
	 */
	@Override
	public InputStream getBinaryStream(int paramInt) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getString(java.lang.String)
	 */
	@Override
	public String getString(String paramString) throws SQLException {
		return currentMap.get(paramString);
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getBoolean(java.lang.String)
	 */
	@Override
	public boolean getBoolean(String paramString) throws SQLException {
		return Boolean.parseBoolean(currentMap.get(paramString));
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getByte(java.lang.String)
	 */
	@Override
	public byte getByte(String paramString) throws SQLException {
		return Byte.parseByte(currentMap.get(paramString));
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getShort(java.lang.String)
	 */
	@Override
	public short getShort(String paramString) throws SQLException {
		return Short.parseShort(currentMap.get(paramString));
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getInt(java.lang.String)
	 */
	@Override
	public int getInt(String paramString) throws SQLException {
		return Integer.parseInt(currentMap.get(paramString));
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getLong(java.lang.String)
	 */
	@Override
	public long getLong(String paramString) throws SQLException {
		return Long.parseLong(currentMap.get(paramString));
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getFloat(java.lang.String)
	 */
	@Override
	public float getFloat(String paramString) throws SQLException {
		return Float.parseFloat(currentMap.get(paramString));
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getDouble(java.lang.String)
	 */
	@Override
	public double getDouble(String paramString) throws SQLException {
		return Double.parseDouble(currentMap.get(paramString));
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getBigDecimal(java.lang.String, int)
	 */
	@Deprecated
	@Override
	public BigDecimal getBigDecimal(String paramString, int paramInt) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getBytes(java.lang.String)
	 */
	@Override
	public byte[] getBytes(String paramString) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getDate(java.lang.String)
	 */
	@Override
	public Date getDate(String paramString) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getTime(java.lang.String)
	 */
	@Override
	public Time getTime(String paramString) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getTimestamp(java.lang.String)
	 */
	@Override
	public Timestamp getTimestamp(String paramString) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getAsciiStream(java.lang.String)
	 */
	@Override
	public InputStream getAsciiStream(String paramString) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getUnicodeStream(java.lang.String)
	 */
	@Override
	public InputStream getUnicodeStream(String paramString) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getBinaryStream(java.lang.String)
	 */
	@Override
	public InputStream getBinaryStream(String paramString) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getWarnings()
	 */
	@Override
	public SQLWarning getWarnings() throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#clearWarnings()
	 */
	@Override
	public void clearWarnings() throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getCursorName()
	 */
	@Override
	public String getCursorName() throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getMetaData()
	 */
	@Override
	public ResultSetMetaData getMetaData() throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getObject(int)
	 */
	@Override
	public Object getObject(int paramInt) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getObject(java.lang.String)
	 */
	@Override
	public Object getObject(String paramString) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#findColumn(java.lang.String)
	 */
	@Override
	public int findColumn(String paramString) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getCharacterStream(int)
	 */
	@Override
	public Reader getCharacterStream(int paramInt) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getCharacterStream(java.lang.String)
	 */
	@Override
	public Reader getCharacterStream(String paramString) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getBigDecimal(int)
	 */
	@Override
	public BigDecimal getBigDecimal(int paramInt) throws SQLException {
		return new BigDecimal(currentEntrySetArray[paramInt - 1].getValue());
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getBigDecimal(java.lang.String)
	 */
	@Override
	public BigDecimal getBigDecimal(String paramString) throws SQLException {
		return new BigDecimal(currentMap.get(paramString));
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#isBeforeFirst()
	 */
	@Override
	public boolean isBeforeFirst() throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#isAfterLast()
	 */
	@Override
	public boolean isAfterLast() throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#isFirst()
	 */
	@Override
	public boolean isFirst() throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#isLast()
	 */
	@Override
	public boolean isLast() throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#beforeFirst()
	 */
	@Override
	public void beforeFirst() throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#afterLast()
	 */
	@Override
	public void afterLast() throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#first()
	 */
	@Override
	public boolean first() throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#last()
	 */
	@Override
	public boolean last() throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getRow()
	 */
	@Override
	public int getRow() throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#absolute(int)
	 */
	@Override
	public boolean absolute(int paramInt) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#relative(int)
	 */
	@Override
	public boolean relative(int paramInt) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#previous()
	 */
	@Override
	public boolean previous() throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#setFetchDirection(int)
	 */
	@Override
	public void setFetchDirection(int paramInt) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getFetchDirection()
	 */
	@Override
	public int getFetchDirection() throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#setFetchSize(int)
	 */
	@Override
	public void setFetchSize(int paramInt) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getFetchSize()
	 */
	@Override
	public int getFetchSize() throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getType()
	 */
	@Override
	public int getType() throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getConcurrency()
	 */
	@Override
	public int getConcurrency() throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#rowUpdated()
	 */
	@Override
	public boolean rowUpdated() throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#rowInserted()
	 */
	@Override
	public boolean rowInserted() throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#rowDeleted()
	 */
	@Override
	public boolean rowDeleted() throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateNull(int)
	 */
	@Override
	public void updateNull(int paramInt) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateBoolean(int, boolean)
	 */
	@Override
	public void updateBoolean(int paramInt, boolean paramBoolean) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateByte(int, byte)
	 */
	@Override
	public void updateByte(int paramInt, byte paramByte) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateShort(int, short)
	 */
	@Override
	public void updateShort(int paramInt, short paramShort) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateInt(int, int)
	 */
	@Override
	public void updateInt(int paramInt1, int paramInt2) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateLong(int, long)
	 */
	@Override
	public void updateLong(int paramInt, long paramLong) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateFloat(int, float)
	 */
	@Override
	public void updateFloat(int paramInt, float paramFloat) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateDouble(int, double)
	 */
	@Override
	public void updateDouble(int paramInt, double paramDouble) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateBigDecimal(int, java.math.BigDecimal)
	 */
	@Override
	public void updateBigDecimal(int paramInt, BigDecimal paramBigDecimal) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateString(int, java.lang.String)
	 */
	@Override
	public void updateString(int paramInt, String paramString) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateBytes(int, byte[])
	 */
	@Override
	public void updateBytes(int paramInt, byte[] paramArrayOfByte) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateDate(int, java.sql.Date)
	 */
	@Override
	public void updateDate(int paramInt, Date paramDate) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateTime(int, java.sql.Time)
	 */
	@Override
	public void updateTime(int paramInt, Time paramTime) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateTimestamp(int, java.sql.Timestamp)
	 */
	@Override
	public void updateTimestamp(int paramInt, Timestamp paramTimestamp) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateAsciiStream(int, java.io.InputStream, int)
	 */
	@Override
	public void updateAsciiStream(int paramInt1, InputStream paramInputStream, int paramInt2) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateBinaryStream(int, java.io.InputStream, int)
	 */
	@Override
	public void updateBinaryStream(int paramInt1, InputStream paramInputStream, int paramInt2) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateCharacterStream(int, java.io.Reader, int)
	 */
	@Override
	public void updateCharacterStream(int paramInt1, Reader paramReader, int paramInt2) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateObject(int, java.lang.Object, int)
	 */
	@Override
	public void updateObject(int paramInt1, Object paramObject, int paramInt2) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateObject(int, java.lang.Object)
	 */
	@Override
	public void updateObject(int paramInt, Object paramObject) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateNull(java.lang.String)
	 */
	@Override
	public void updateNull(String paramString) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateBoolean(java.lang.String, boolean)
	 */
	@Override
	public void updateBoolean(String paramString, boolean paramBoolean) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateByte(java.lang.String, byte)
	 */
	@Override
	public void updateByte(String paramString, byte paramByte) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateShort(java.lang.String, short)
	 */
	@Override
	public void updateShort(String paramString, short paramShort) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateInt(java.lang.String, int)
	 */
	@Override
	public void updateInt(String paramString, int paramInt) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateLong(java.lang.String, long)
	 */
	@Override
	public void updateLong(String paramString, long paramLong) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateFloat(java.lang.String, float)
	 */
	@Override
	public void updateFloat(String paramString, float paramFloat) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateDouble(java.lang.String, double)
	 */
	@Override
	public void updateDouble(String paramString, double paramDouble) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateBigDecimal(java.lang.String, java.math.BigDecimal)
	 */
	@Override
	public void updateBigDecimal(String paramString, BigDecimal paramBigDecimal) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateString(java.lang.String, java.lang.String)
	 */
	@Override
	public void updateString(String paramString1, String paramString2) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateBytes(java.lang.String, byte[])
	 */
	@Override
	public void updateBytes(String paramString, byte[] paramArrayOfByte) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateDate(java.lang.String, java.sql.Date)
	 */
	@Override
	public void updateDate(String paramString, Date paramDate) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateTime(java.lang.String, java.sql.Time)
	 */
	@Override
	public void updateTime(String paramString, Time paramTime) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateTimestamp(java.lang.String, java.sql.Timestamp)
	 */
	@Override
	public void updateTimestamp(String paramString, Timestamp paramTimestamp) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateAsciiStream(java.lang.String, java.io.InputStream, int)
	 */
	@Override
	public void updateAsciiStream(String paramString, InputStream paramInputStream, int paramInt) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateBinaryStream(java.lang.String, java.io.InputStream, int)
	 */
	@Override
	public void updateBinaryStream(String paramString, InputStream paramInputStream, int paramInt) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateCharacterStream(java.lang.String, java.io.Reader, int)
	 */
	@Override
	public void updateCharacterStream(String paramString, Reader paramReader, int paramInt) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateObject(java.lang.String, java.lang.Object, int)
	 */
	@Override
	public void updateObject(String paramString, Object paramObject, int paramInt) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateObject(java.lang.String, java.lang.Object)
	 */
	@Override
	public void updateObject(String paramString, Object paramObject) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#insertRow()
	 */
	@Override
	public void insertRow() throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateRow()
	 */
	@Override
	public void updateRow() throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#deleteRow()
	 */
	@Override
	public void deleteRow() throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#refreshRow()
	 */
	@Override
	public void refreshRow() throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#cancelRowUpdates()
	 */
	@Override
	public void cancelRowUpdates() throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#moveToInsertRow()
	 */
	@Override
	public void moveToInsertRow() throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#moveToCurrentRow()
	 */
	@Override
	public void moveToCurrentRow() throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getStatement()
	 */
	@Override
	public Statement getStatement() throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getObject(int, java.util.Map)
	 */
	@Override
	public Object getObject(int paramInt, Map<String, Class<?>> paramMap) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getRef(int)
	 */
	@Override
	public Ref getRef(int paramInt) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getBlob(int)
	 */
	@Override
	public Blob getBlob(int paramInt) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getClob(int)
	 */
	@Override
	public Clob getClob(int paramInt) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getArray(int)
	 */
	@Override
	public Array getArray(int paramInt) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getObject(java.lang.String, java.util.Map)
	 */
	@Override
	public Object getObject(String paramString, Map<String, Class<?>> paramMap) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getRef(java.lang.String)
	 */
	@Override
	public Ref getRef(String paramString) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getBlob(java.lang.String)
	 */
	@Override
	public Blob getBlob(String paramString) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getClob(java.lang.String)
	 */
	@Override
	public Clob getClob(String paramString) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getArray(java.lang.String)
	 */
	@Override
	public Array getArray(String paramString) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getDate(int, java.util.Calendar)
	 */
	@Override
	public Date getDate(int paramInt, Calendar paramCalendar) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getDate(java.lang.String, java.util.Calendar)
	 */
	@Override
	public Date getDate(String paramString, Calendar paramCalendar) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getTime(int, java.util.Calendar)
	 */
	@Override
	public Time getTime(int paramInt, Calendar paramCalendar) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getTime(java.lang.String, java.util.Calendar)
	 */
	@Override
	public Time getTime(String paramString, Calendar paramCalendar) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getTimestamp(int, java.util.Calendar)
	 */
	@Override
	public Timestamp getTimestamp(int paramInt, Calendar paramCalendar) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getTimestamp(java.lang.String, java.util.Calendar)
	 */
	@Override
	public Timestamp getTimestamp(String paramString, Calendar paramCalendar) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getURL(int)
	 */
	@Override
	public URL getURL(int paramInt) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getURL(java.lang.String)
	 */
	@Override
	public URL getURL(String paramString) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateRef(int, java.sql.Ref)
	 */
	@Override
	public void updateRef(int paramInt, Ref paramRef) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateRef(java.lang.String, java.sql.Ref)
	 */
	@Override
	public void updateRef(String paramString, Ref paramRef) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateBlob(int, java.sql.Blob)
	 */
	@Override
	public void updateBlob(int paramInt, Blob paramBlob) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateBlob(java.lang.String, java.sql.Blob)
	 */
	@Override
	public void updateBlob(String paramString, Blob paramBlob) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateClob(int, java.sql.Clob)
	 */
	@Override
	public void updateClob(int paramInt, Clob paramClob) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateClob(java.lang.String, java.sql.Clob)
	 */
	@Override
	public void updateClob(String paramString, Clob paramClob) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateArray(int, java.sql.Array)
	 */
	@Override
	public void updateArray(int paramInt, Array paramArray) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateArray(java.lang.String, java.sql.Array)
	 */
	@Override
	public void updateArray(String paramString, Array paramArray) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getRowId(int)
	 */
	@Override
	public RowId getRowId(int paramInt) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getRowId(java.lang.String)
	 */
	@Override
	public RowId getRowId(String paramString) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateRowId(int, java.sql.RowId)
	 */
	@Override
	public void updateRowId(int paramInt, RowId paramRowId) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateRowId(java.lang.String, java.sql.RowId)
	 */
	@Override
	public void updateRowId(String paramString, RowId paramRowId) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getHoldability()
	 */
	@Override
	public int getHoldability() throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#isClosed()
	 */
	@Override
	public boolean isClosed() throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateNString(int, java.lang.String)
	 */
	@Override
	public void updateNString(int paramInt, String paramString) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateNString(java.lang.String, java.lang.String)
	 */
	@Override
	public void updateNString(String paramString1, String paramString2) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateNClob(int, java.sql.NClob)
	 */
	@Override
	public void updateNClob(int paramInt, NClob paramNClob) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateNClob(java.lang.String, java.sql.NClob)
	 */
	@Override
	public void updateNClob(String paramString, NClob paramNClob) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getNClob(int)
	 */
	@Override
	public NClob getNClob(int paramInt) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getNClob(java.lang.String)
	 */
	@Override
	public NClob getNClob(String paramString) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getSQLXML(int)
	 */
	@Override
	public SQLXML getSQLXML(int paramInt) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getSQLXML(java.lang.String)
	 */
	@Override
	public SQLXML getSQLXML(String paramString) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateSQLXML(int, java.sql.SQLXML)
	 */
	@Override
	public void updateSQLXML(int paramInt, SQLXML paramSQLXML) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateSQLXML(java.lang.String, java.sql.SQLXML)
	 */
	@Override
	public void updateSQLXML(String paramString, SQLXML paramSQLXML) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getNString(int)
	 */
	@Override
	public String getNString(int paramInt) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getNString(java.lang.String)
	 */
	@Override
	public String getNString(String paramString) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getNCharacterStream(int)
	 */
	@Override
	public Reader getNCharacterStream(int paramInt) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getNCharacterStream(java.lang.String)
	 */
	@Override
	public Reader getNCharacterStream(String paramString) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateNCharacterStream(int, java.io.Reader, long)
	 */
	@Override
	public void updateNCharacterStream(int paramInt, Reader paramReader, long paramLong) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateNCharacterStream(java.lang.String, java.io.Reader, long)
	 */
	@Override
	public void updateNCharacterStream(String paramString, Reader paramReader, long paramLong) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateAsciiStream(int, java.io.InputStream, long)
	 */
	@Override
	public void updateAsciiStream(int paramInt, InputStream paramInputStream, long paramLong) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateBinaryStream(int, java.io.InputStream, long)
	 */
	@Override
	public void updateBinaryStream(int paramInt, InputStream paramInputStream, long paramLong) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateCharacterStream(int, java.io.Reader, long)
	 */
	@Override
	public void updateCharacterStream(int paramInt, Reader paramReader, long paramLong) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateAsciiStream(java.lang.String, java.io.InputStream, long)
	 */
	@Override
	public void updateAsciiStream(String paramString, InputStream paramInputStream, long paramLong)
			throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateBinaryStream(java.lang.String, java.io.InputStream, long)
	 */
	@Override
	public void updateBinaryStream(String paramString, InputStream paramInputStream, long paramLong)
			throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateCharacterStream(java.lang.String, java.io.Reader, long)
	 */
	@Override
	public void updateCharacterStream(String paramString, Reader paramReader, long paramLong) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateBlob(int, java.io.InputStream, long)
	 */
	@Override
	public void updateBlob(int paramInt, InputStream paramInputStream, long paramLong) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateBlob(java.lang.String, java.io.InputStream, long)
	 */
	@Override
	public void updateBlob(String paramString, InputStream paramInputStream, long paramLong) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateClob(int, java.io.Reader, long)
	 */
	@Override
	public void updateClob(int paramInt, Reader paramReader, long paramLong) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateClob(java.lang.String, java.io.Reader, long)
	 */
	@Override
	public void updateClob(String paramString, Reader paramReader, long paramLong) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateNClob(int, java.io.Reader, long)
	 */
	@Override
	public void updateNClob(int paramInt, Reader paramReader, long paramLong) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateNClob(java.lang.String, java.io.Reader, long)
	 */
	@Override
	public void updateNClob(String paramString, Reader paramReader, long paramLong) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateNCharacterStream(int, java.io.Reader)
	 */
	@Override
	public void updateNCharacterStream(int paramInt, Reader paramReader) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateNCharacterStream(java.lang.String, java.io.Reader)
	 */
	@Override
	public void updateNCharacterStream(String paramString, Reader paramReader) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateAsciiStream(int, java.io.InputStream)
	 */
	@Override
	public void updateAsciiStream(int paramInt, InputStream paramInputStream) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateBinaryStream(int, java.io.InputStream)
	 */
	@Override
	public void updateBinaryStream(int paramInt, InputStream paramInputStream) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateCharacterStream(int, java.io.Reader)
	 */
	@Override
	public void updateCharacterStream(int paramInt, Reader paramReader) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateAsciiStream(java.lang.String, java.io.InputStream)
	 */
	@Override
	public void updateAsciiStream(String paramString, InputStream paramInputStream) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateBinaryStream(java.lang.String, java.io.InputStream)
	 */
	@Override
	public void updateBinaryStream(String paramString, InputStream paramInputStream) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateCharacterStream(java.lang.String, java.io.Reader)
	 */
	@Override
	public void updateCharacterStream(String paramString, Reader paramReader) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateBlob(int, java.io.InputStream)
	 */
	@Override
	public void updateBlob(int paramInt, InputStream paramInputStream) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateBlob(java.lang.String, java.io.InputStream)
	 */
	@Override
	public void updateBlob(String paramString, InputStream paramInputStream) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateClob(int, java.io.Reader)
	 */
	@Override
	public void updateClob(int paramInt, Reader paramReader) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateClob(java.lang.String, java.io.Reader)
	 */
	@Override
	public void updateClob(String paramString, Reader paramReader) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateNClob(int, java.io.Reader)
	 */
	@Override
	public void updateNClob(int paramInt, Reader paramReader) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateNClob(java.lang.String, java.io.Reader)
	 */
	@Override
	public void updateNClob(String paramString, Reader paramReader) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getObject(int, java.lang.Class)
	 */
	@Override
	public <T> T getObject(int paramInt, Class<T> paramClass) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getObject(java.lang.String, java.lang.Class)
	 */
	@Override
	public <T> T getObject(String paramString, Class<T> paramClass) throws SQLException {
		throw new UnsupportedOperationException();
	}
}

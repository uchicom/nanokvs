// (c) 2016 uchicom
package com.uchicom.nanokvs;

/**
 * メールを紐づけるための情報
 * @author uchicom: Shigeki Uchiyama
 *
 */
public class Msg {

	/** メッセージ番号 */
	private Integer num;
	/** ユニークID */
	private String uidl;

	public Msg(Integer num) {
		this.num = num;
	}
	public Msg(Integer num, String uidl) {
		this.num = num;
		this.uidl = uidl;
	}
	public Integer getNum() {
		return num;
	}
	public void setNum(Integer num) {
		this.num = num;
	}
	public String getUidl() {
		return uidl;
	}
	public void setUidl(String uidl) {
		this.uidl = uidl;
	}
}

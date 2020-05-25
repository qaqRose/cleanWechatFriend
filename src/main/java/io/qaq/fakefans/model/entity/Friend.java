package io.qaq.fakefans.model.entity;

import lombok.Data;

/**
 * @author: qiu
 * 微信好友
 */
@Data
public class Friend {


	/**
	 * 昵称
	 */
	private String NickName;
	/**
	 * 用户id
	 * 每次都会不一样
	 */
	private String UserName;
	/**
	 * 显示名称
	 */
	private String DisplayName;
	/**
	 * 性别  1 男
	 */
	private Integer Sex;
	private Integer ContactFlag;
	private Integer VerifyFlag;
	private String Alias;
	/**
	 * 省份
	 */
	private String Province;
	/**
	 * 城市
	 */
	private String City;
	/**
	 * 备注名称
	 */
	private String RemarkName;
	/**
	 * 共同好友
	 */
	private Integer UniFriend;
	/**
	 * 个性签名
	 */
	private String Signature;
}

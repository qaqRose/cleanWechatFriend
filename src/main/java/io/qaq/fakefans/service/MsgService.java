package io.qaq.fakefans.service;

/**
 * @author: qiu
 */
public interface MsgService {

	/**
	 * 通过用户id发送消息
	 * @param userId
	 * @param message
	 */
	void sendById(String userId, String message);

	/**
	 * 根据用户名发送消息
	 * @param userName
	 * @param message
	 */
	void sendByName(String userName, String message);

	/**
	 * 发送消息给文件助手
	 * @param message
	 */
	void sendToFileHelper(String message);
}

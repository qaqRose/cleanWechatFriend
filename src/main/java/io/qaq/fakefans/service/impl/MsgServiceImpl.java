package io.qaq.fakefans.service.impl;

import cn.zhouyafeng.itchat4j.api.MessageTools;
import io.qaq.fakefans.service.MsgService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author: qiu
 */
@Slf4j
@Service
public class MsgServiceImpl implements MsgService {
	@Override
	public void sendById(String userId, String message) {
		log.debug("[id]发送 {} 给 {}", message, userId);
		MessageTools.sendMsgById(message, userId);
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void sendByName(String userName, String message) {
		log.debug("[昵称]发送 {} 给 {}", message, userName);
		MessageTools.sendMsgByNickName(message, userName);
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void sendToFileHelper(String message) {
		final String name = "filehelper";
		sendById(name, message);
	}
}

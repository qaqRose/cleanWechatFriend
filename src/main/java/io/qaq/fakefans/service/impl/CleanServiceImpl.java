package io.qaq.fakefans.service.impl;

import cn.hutool.core.util.StrUtil;
import io.qaq.fakefans.model.entity.Friend;
import io.qaq.fakefans.service.CleanService;
import io.qaq.fakefans.service.FriendService;
import io.qaq.fakefans.service.MsgService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * @author: qiu
 */
@Slf4j
@Service
public class CleanServiceImpl implements CleanService {

	private final FriendService friendService;
	private final MsgService msgService;

	public CleanServiceImpl(FriendService friendService,
	                        MsgService msgService) {
		this.friendService = friendService;
		this.msgService = msgService;
	}

	@Override
	public void clean() {
		List<String> friendIds = friendService.findMyFriendId();
		msgService.sendToFileHelper("程序开始启动");
		List<Friend> myFriend = friendService.findMyFriend();
		Integer man = 0;
		Integer woman = 0;
		for (Friend friend : myFriend) {
			if(friend.getSex() != null && friend.getSex() == 1) {
				man += 1;
			} else {
				woman += 1;
			}
		}
		msgService.sendToFileHelper(StrUtil.format("您一共有好友: {} 人, 其中男性好友 {} 人, 女性好友 {} 人", friendIds.size(),man, woman));
		String msg = "这是一条机器人消息, 请勿回复, 如果打扰到您, 真的非常抱歉.";
		msgService.sendToFileHelper(StrUtil.format("正在发送测试消息 : {} ", msg));
		for (String friendId : friendIds) {
			msgService.sendById(friendId, msg);
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		try {
			// 等待消息处理
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		msgService.sendToFileHelper("消息发送完毕");
		Set<String> delFriendSet = friendService.getDelFriendSet();
		if(delFriendSet.size() == 0) {
			msgService.sendToFileHelper("恭喜您, 并未有好友将您删除");
		} else {
			msgService.sendToFileHelper(StrUtil.format("有 {} 个好友将您删除,名单如下:", delFriendSet.size() ));
		}
		log.info("删除好友集合");
		StringBuilder sb = new StringBuilder();
		int count = 1;
		for (String userId : delFriendSet) {
			try {
				Friend friend = friendService.getFriendById(userId);
				String remarkName = "";
				if(StrUtil.isNotBlank(friend.getRemarkName())) {
					remarkName = "(备注名:"+ friend.getRemarkName() + ")";
				}
				log.info("好友 {} {} 已经将您删除",friend.getNickName(), remarkName);
				sb.append(StrUtil.format("好友{}:{}{}",count++,  friend.getNickName(), remarkName)).append("\r\n");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if(delFriendSet.size() != 0) {
			msgService.sendToFileHelper(sb.toString());
		}
		msgService.sendToFileHelper("已退出登录");
	}


}

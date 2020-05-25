package io.qaq.fakefans.handler;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.zhouyafeng.itchat4j.beans.BaseMsg;
import cn.zhouyafeng.itchat4j.face.IMsgHandlerFace;
import io.qaq.fakefans.model.entity.Friend;
import io.qaq.fakefans.service.CleanService;
import io.qaq.fakefans.service.FriendService;
import io.qaq.fakefans.service.MsgService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

/**
 * @author: qiu
 * 自定义微信消息处理器
 * 暂不做处理
 */
@Slf4j
@Component
public class CustomHandler implements IMsgHandlerFace {

	private final FriendService friendService;

	public CustomHandler(FriendService friendService) {
		this.friendService = friendService;
	}

	/**
	 * 文本消息处理
	 * @param baseMsg
	 * @return
	 */
	@Override
	public String textMsgHandle(BaseMsg baseMsg) {
		log.info("收到消息: " + baseMsg.getContent());
		return null;
	}

	/**
	 * 图片消息处理
	 * @param baseMsg
	 * @return
	 */
	@Override
	public String picMsgHandle(BaseMsg baseMsg) {
		return null;
	}

	/**
	 * 语音消息处理
	 * @param baseMsg
	 * @return
	 */
	@Override
	public String voiceMsgHandle(BaseMsg baseMsg) {
		return null;
	}

	/**
	 * 视频消息处理
	 * @param baseMsg
	 * @return
	 */
	@Override
	public String viedoMsgHandle(BaseMsg baseMsg) {
		return null;
	}

	/**
	 * 名片消息
	 * @param baseMsg
	 * @return
	 */
	@Override
	public String nameCardMsgHandle(BaseMsg baseMsg) {
		return null;
	}

	/**
	 * 系统消息
	 * @param baseMsg
	 */
	@Override
	public void sysMsgHandle(BaseMsg baseMsg) {
		log.info("收到系统消息: " + baseMsg.getContent());
		String content = baseMsg.getContent();
		final String pattern = ".*?开启了朋友验证.*?发送朋友验证.*?";
		boolean isMatch = Pattern.matches(pattern, content);
		if(isMatch) {
			log.info("匹配成功");
			String userId = baseMsg.getFromUserName();
			friendService.addDelFriend(userId);
			// 更改备注名称
		}
	}

	/**
	 * 好友添加消息
	 * @param baseMsg
	 * @return
	 */
	@Override
	public String verifyAddFriendMsgHandle(BaseMsg baseMsg) {
		return null;
	}

	/**
	 * 文件消息
	 * @param baseMsg
	 * @return
	 */
	@Override
	public String mediaMsgHandle(BaseMsg baseMsg) {
		return null;
	}

	public static void main(String[] args) {
		String content = "\"开启了朋友验证，你还不是他（她）朋友。请先发送朋友验证请求，对方验证通过后，才能聊天。<a href=\"weixin://findfriend/verifycontact\">发送朋友验证</a>\"";
		final String pattern = ".*?发送朋友验证.*?";
		boolean isMatch = Pattern.matches(pattern, content);
		System.out.println("isMatch:" +isMatch);
	}
}

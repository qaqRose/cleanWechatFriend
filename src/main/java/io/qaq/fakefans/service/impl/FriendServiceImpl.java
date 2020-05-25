package io.qaq.fakefans.service.impl;

import cn.zhouyafeng.itchat4j.api.WechatTools;
import com.alibaba.fastjson.JSONObject;
import io.qaq.fakefans.model.entity.Friend;
import io.qaq.fakefans.service.FriendService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: qiu
 */
@Slf4j
@Service
public class FriendServiceImpl implements FriendService {

	private boolean isImport = false;
	/**
	 * 好友map
	 * key : id
	 * value : Friend
	 */
	private ConcurrentHashMap<String, Friend> friendMap = new ConcurrentHashMap<>(500);
	/**
	 * 好友名称map
	 * key: 昵称
	 * value: id列表 (可能存在同名好友)
	 */
	private ConcurrentHashMap<String, List<String>> nameMap = new ConcurrentHashMap<>(500);
	/**
	 * 删除好友id集合
	 */
	private Set<String> delFriendIdSet = new HashSet<>(100);

	@Override
	public List<String> findMyFriendId() {
		checkAndImportFriend();
		List<String> idList = new ArrayList<>();
		for (Map.Entry<String, Friend> entry : friendMap.entrySet()) {
			idList.add(entry.getKey());
		}
		return idList;
	}

	/**
	 * 获取所有好友信息
	 * @return
	 */
	@Override
	public List<Friend> findMyFriend() {
		checkAndImportFriend();
		List<Friend> friends = new ArrayList<>();
		for (Map.Entry<String, Friend> entry : friendMap.entrySet()) {
			friends.add(entry.getValue());
		}
		return friends;
	}

	/**
	 * 获取好友昵称
	 * @return
	 */
	@Override
	public List<String> findMyFriendName() {
		checkAndImportFriend();
		List<String> nameList = new ArrayList<>();
		for (Map.Entry<String, List<String>> entry : nameMap.entrySet()) {
			nameList.add(entry.getKey());
		}
		return nameList;
	}

	/**
	 * 修改备注名称
	 * @param nickName
	 * @param remName
	 */
	@Override
	public void remarkFriendsName(String nickName, String remName) {
//		MessageTools(nickName, remName);
	}

	/**
	 * 通过id 获取好友信息
	 * @param id
	 * @return
	 */
	@Override
	public Friend getFriendById(String id) {
		checkAndImportFriend();
		return friendMap.get(id);
	}

	/**
	 * 同名好友可能有多个
	 * @param nickName
	 * @return
	 */
	@Override
	public List<Friend> findFriendsByNickName(String nickName) {
		checkAndImportFriend();
		List<Friend> friends = new ArrayList<>();
		List<String> ids = nameMap.get(nickName);
		for (String id : ids) {
			Friend friend = getFriendById(id);
			friends.add(friend);
		}
		return friends;
	}

	/**
	 * 获取此昵称的第一个好友 (可能有多个)
	 * @param nickName
	 * @return
	 */
	@Override
	public Friend getFriendByNickName(String nickName) {
		List<Friend> friendsList = findFriendsByNickName(nickName);
		if(friendsList.isEmpty()) {
			return null;
		} else {
			return friendsList.get(0);
		}
	}

	@Override
	public void addDelFriend(String userId) {
		delFriendIdSet.add(userId);
	}

	@Override
	public Set<String> getDelFriendSet() {
		return delFriendIdSet;
	}

	/**
	 * 导入好友到map
	 * @return
	 */
	private Map<String, Friend> importFriends() {
		List<JSONObject> contactList = WechatTools.getContactList();
		for (JSONObject jsonObject : contactList) {
			log.info("friend json: {}", jsonObject.toJSONString());
			Friend friend = jsonObject.toJavaObject(Friend.class);
			String userId = friend.getUserName();
			friendMap.put(userId, friend);
			String nickName = friend.getNickName();
			if(nameMap.contains(nickName)) {
				nameMap.get(nickName).add(userId);
			} else {
				List<String> list = new ArrayList<>();
				list.add(userId);
				nameMap.put(nickName, list);
			}
		}
		log.info("好友数量: {}", friendMap.size());
		return friendMap;
	}

	private void checkAndImportFriend() {
		if(!isImport) {
			importFriends();
			isImport = true;
		}
	}
}

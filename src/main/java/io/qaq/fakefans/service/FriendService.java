package io.qaq.fakefans.service;

import io.qaq.fakefans.model.entity.Friend;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author: qiu
 */
public interface FriendService {

	List<String> findMyFriendId();

	List<Friend> findMyFriend();

	List<String> findMyFriendName();

	void remarkFriendsName(String nickName, String remName);

	Friend getFriendById(String id);

	List<Friend> findFriendsByNickName(String nickName);

	Friend getFriendByNickName(String nickName);

	void addDelFriend(String userId);

	Set<String> getDelFriendSet();
}

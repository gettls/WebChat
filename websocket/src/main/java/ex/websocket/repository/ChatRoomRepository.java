package ex.websocket.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Repository;

import ex.websocket.dto.ChatRoom;
import ex.websocket.service.RedisSubscriber;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Repository
@Slf4j
public class ChatRoomRepository {
	
	/*
	 *  REDIS
	 */
	// 채팅방 (topic) 에 발행되는 메시지를 처리할 Listner
	private final RedisMessageListenerContainer redisMessageListener;
	// 구독 처리 서비스
	private final RedisSubscriber redisSubscriber;
	// Redis
	private static final String CHAT_ROOMS = "CHAT_ROOM";
	private final RedisTemplate<String, Object> redisTemplate;
	private HashOperations<String, String, ChatRoom> opsHashChatRoom;
	// 채팅방의 대화 메시지를 발행하기 위한 redis topic
	private Map<String, ChannelTopic> topics;
	
	@PostConstruct
	public void init() {
		opsHashChatRoom = redisTemplate.opsForHash();
		topics = new HashMap<>();
	}
	
	public List<ChatRoom> findAllRoom(){
		return opsHashChatRoom.values(CHAT_ROOMS);
	}
	
	public ChatRoom findRoomById(String id) {
		return opsHashChatRoom.get(CHAT_ROOMS, id);
	}
	
	/*
	 * 채팅방 생성 : 서버간 채팅방 공유를 위해 redis hash 에 저장
	 */
	public ChatRoom createChatRoom(String name) {
		ChatRoom chatRoom = ChatRoom.create(name);
		opsHashChatRoom.put(CHAT_ROOMS, chatRoom.getRoomId(), chatRoom);
		log.info("REPO name : {}", name);
		return chatRoom;
	}

	/*
	 * 채팅방 입장 : redis 에 topic 만들고 pub/sub 통신을 위한 리스너 설정 
	 */
	public void enterChatRoom(String roomId) {
		ChannelTopic topic = topics.get(roomId);
		if(topic == null) {
			topic = new ChannelTopic(roomId);
			redisMessageListener.addMessageListener(redisSubscriber, topic);
			topics.put(roomId, topic);
		}
	}
	
	public ChannelTopic getTopic(String roomId) {
		return topics.get(roomId);
	}
	
	
	/* STOMP 
	private Map<String, ChatRoom> chatRoomMap;
	
	@PostConstruct
	private void init() {
		chatRoomMap = new LinkedHashMap<>();
	}
	
	public ChatRoom findRoomById(String roomId) {
		return chatRoomMap.get(roomId);
	}
	
	public ChatRoom createRoom(String name) {
		log.info("REPOSITORY name : {}", name);
		ChatRoom chatRoom = ChatRoom.create(name);
		chatRoomMap.put(chatRoom.getRoomId(), chatRoom);
		return chatRoom;
	}
	
	public List<ChatRoom> findAllRoom() {
		return new ArrayList<ChatRoom>(chatRoomMap.values());
	}
	*/
}

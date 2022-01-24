package ex.websocket.repository;

import java.util.List;
import java.util.Optional;

import javax.annotation.Resource;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import ex.websocket.dto.ChatRoom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Repository
@Slf4j
public class ChatRoomRepository {
	
	
	/*
	 * integrated User Count
	 */
	 // Redis CacheKeys
    private static final String CHAT_ROOMS = "CHAT_ROOM"; // 채팅룸 저장
    public static final String USER_COUNT = "USER_COUNT"; // 채팅룸에 입장한 클라이언트수 저장
    public static final String ENTER_INFO = "ENTER_INFO"; // 채팅룸에 입장한 클라이언트의 sessionId와 채팅룸 id를 맵핑한 정보 저장

    @Resource(name = "redisTemplate")
    private HashOperations<String, String, ChatRoom> hashOpsChatRoom;
    @Resource(name = "redisTemplate")
    private HashOperations<String, String, String> hashOpsEnterInfo;
    @Resource(name = "redisTemplate")
    private ValueOperations<String, String> valueOps;

    // 모든 채팅방 조회
    public List<ChatRoom> findAllRoom() {
        return hashOpsChatRoom.values(CHAT_ROOMS);
    }

    // 특정 채팅방 조회
    public ChatRoom findRoomById(String id) {
        return hashOpsChatRoom.get(CHAT_ROOMS, id);
    }

    // 채팅방 생성 : 서버간 채팅방 공유를 위해 redis hash에 저장한다.
    public ChatRoom createChatRoom(String name) {
        ChatRoom chatRoom = ChatRoom.create(name);
        hashOpsChatRoom.put(CHAT_ROOMS, chatRoom.getRoomId(), chatRoom);
        return chatRoom;
    }

    // 유저가 입장한 채팅방ID와 유저 세션ID 맵핑 정보 저장
    public void setUserEnterInfo(String sessionId, String roomId) {
        hashOpsEnterInfo.put(ENTER_INFO, sessionId, roomId);
    }

    // 유저 세션으로 입장해 있는 채팅방 ID 조회
    public String getUserEnterRoomId(String sessionId) {
        return hashOpsEnterInfo.get(ENTER_INFO, sessionId);
    }

    // 유저 세션정보와 맵핑된 채팅방ID 삭제
    public void removeUserEnterInfo(String sessionId) {
        hashOpsEnterInfo.delete(ENTER_INFO, sessionId);
    }

    // 채팅방 유저수 조회
    public long getUserCount(String roomId) {
        return Long.valueOf(Optional.ofNullable(valueOps.get(USER_COUNT + "_" + roomId)).orElse("0"));
    }

    // 채팅방에 입장한 유저수 +1
    public long plusUserCount(String roomId) {
        return Optional.ofNullable(valueOps.increment(USER_COUNT + "_" + roomId)).orElse(0L);
    }

    // 채팅방에 입장한 유저수 -1
    public long minusUserCount(String roomId) {
        return Optional.ofNullable(valueOps.decrement(USER_COUNT + "_" + roomId)).filter(count -> count > 0).orElse(0L);
    }
	
	/*
	 *  REDIS
	 */
	// 채팅방 (topic) 에 발행되는 메시지를 처리할 Listner
//	private final RedisMessageListenerContainer redisMessageListener;
	// 구독 처리 서비스
//	private final RedisSubscriber redisSubscriber;
	// Redis
//	private static final String CHAT_ROOMS = "CHAT_ROOM";
//	private final RedisTemplate<String, Object> redisTemplate;
//	private HashOperations<String, String, ChatRoom> opsHashChatRoom;
//	// 채팅방의 대화 메시지를 발행하기 위한 redis topic
////	private Map<String, ChannelTopic> topics;
//	
//	@PostConstruct
//	public void init() {
//		opsHashChatRoom = redisTemplate.opsForHash();
//	}
//	
//	public List<ChatRoom> findAllRoom(){
//		return opsHashChatRoom.values(CHAT_ROOMS);
//	}
//	
//	public ChatRoom findRoomById(String id) {
//		return opsHashChatRoom.get(CHAT_ROOMS, id);
//	}
//	
//	/*
//	 * 채팅방 생성 : 서버간 채팅방 공유를 위해 redis hash 에 저장
//	 */
//	public ChatRoom createChatRoom(String name) {
//		ChatRoom chatRoom = ChatRoom.create(name);
//		opsHashChatRoom.put(CHAT_ROOMS, chatRoom.getRoomId(), chatRoom);
//		log.info("REPO name : {}", name);
//		return chatRoom;
//	}

	/*
	 * 채팅방 입장 : redis 에 topic 만들고 pub/sub 통신을 위한 리스너 설정 
	 */
//	public void enterChatRoom(String roomId) {
//		ChannelTopic topic = topics.get(roomId);
//		if(topic == null) {
//			topic = new ChannelTopic(roomId);
//			redisMessageListener.addMessageListener(redisSubscriber, topic);
//			topics.put(roomId, topic);
//		}
//	}
	
//	public ChannelTopic getTopic(String roomId) {
//		return topics.get(roomId);
//	}
	
	
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

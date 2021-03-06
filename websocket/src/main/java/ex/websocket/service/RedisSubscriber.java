package ex.websocket.service;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import ex.websocket.dto.ChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisSubscriber {

	private final ObjectMapper objectMapper;
//	private final RedisTemplate<String, Object> redisTemplate;
	private final SimpMessageSendingOperations messagingTemplate;
	
	/*
	 *  redis 에서 메세지가 publish 되면 대기하고 있던 onMessage 가 받아서 처리
	 */
	
	public void onMessage(String publishMessage) {
		log.info("message : {}", publishMessage);
		try {
//			// redis 에서 발행된 데이터를 받아 deserialize
//			String publishMessage = (String) redisTemplate.getStringSerializer()
//														  .deserialize(message.getBody());
//			// ChatMessage 로 매핑
//			ChatMessage roomMessage = objectMapper.readValue(publishMessage, ChatMessage.class);
//			// WebSocket subscriber 에게 채팅 메시지 send 
//			log.info("sub... roomMessage : {}",roomMessage);
//			messagingTemplate.convertAndSend("/sub/chat/room/"+roomMessage.getRoomId(),roomMessage);
			
			
			// ChatMessage 객체로 매핑
			ChatMessage chatMessage = objectMapper.readValue(publishMessage, ChatMessage.class);
			
			// 채팅방을 구독하고 있는 클라이언트에게 메시지 전달
			messagingTemplate.convertAndSend("/sub/chat/room/" + chatMessage.getRoomId(), chatMessage);
			log.info("send Complete");
			log.info("chatMessage : {}", chatMessage.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		
	}
}

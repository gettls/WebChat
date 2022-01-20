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
public class RedisSubscriber implements MessageListener{

	private final ObjectMapper objectMapper;
	private final RedisTemplate<String, Object> redisTemplate;
	private final SimpMessageSendingOperations messagingTemplate;
	
	/*
	 *  redis �� �����ϸ� ����ϰ� �ִ� onMessage �� �޾Ƽ� ó��
	 */
	@Override
	public void onMessage(Message message, byte[] pattern) {
		log.info("message : {}", message);
		try {
			// redis ���� ����� �����͸� �޾� deserialize
			String publishMessage = (String) redisTemplate.getStringSerializer()
														  .deserialize(message.getBody());
			// ChatMessage �� ����
			ChatMessage roomMessage = objectMapper.readValue(publishMessage, ChatMessage.class);
			// subscriber ���� ä�� �޽��� send 
			log.info("sub... roomMessage : {}",roomMessage);
			messagingTemplate.convertAndSend("/sub/chat/room/"+roomMessage.getRoomId(),roomMessage);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		
	}
}
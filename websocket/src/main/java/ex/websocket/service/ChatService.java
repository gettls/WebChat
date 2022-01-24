package ex.websocket.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

import ex.websocket.dto.ChatMessage;
import ex.websocket.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class ChatService { 

	private final ChannelTopic channelTopic;
	private final RedisTemplate<String, Object> redisTemplate;
	private final ChatRoomRepository chatRoomRepository;
	
	/*
	 * destination ���� roomId ����
	 */
	public String getRoomId(String destination) {
		int lastIndex = destination.lastIndexOf('/');
		if(lastIndex!=-1) {
			return destination.substring(lastIndex + 1);
		}
		else return "";
	}
	
	/*
	 * ä�ù濡 �޽��� ����
	 */
	public void sendChatMessage(ChatMessage chatMessage) {
		chatMessage.setUserCount(chatRoomRepository.getUserCount(chatMessage.getRoomId()));
		if(ChatMessage.MessageType.ENTER.equals(chatMessage.getType())) {
			log.info("���� �޼���");
			chatMessage.setMessage(chatMessage.getSender() + "���� �濡 �����߽��ϴ�.");
			chatMessage.setSender("[�˸�]");
		}else if(ChatMessage.MessageType.QUIT.equals(chatMessage.getType())){
			log.info("���� �޼���");
			chatMessage.setMessage(chatMessage.getSender() + "���� �濡�� �������ϴ�.");
			chatMessage.setSender("[�˸�]");
		}
		log.info("channelTopic : {}", channelTopic.getTopic());
		redisTemplate.convertAndSend(channelTopic.getTopic(), chatMessage);
	}
}
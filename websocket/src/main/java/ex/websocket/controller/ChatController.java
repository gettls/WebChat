package ex.websocket.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.RestController;

import ex.websocket.dto.ChatMessage;
import ex.websocket.repository.ChatRoomRepository;
import ex.websocket.service.RedisPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ChatController {

	
	private final RedisPublisher redisPublisher;
	private final ChatRoomRepository chatRoomRepository;
	
	/*
	 * "/pub/chat/message" �� ������ �޽����� ����
	 */
	@MessageMapping("/chat/message")
	public void message(ChatMessage chatMessage) {
		if(ChatMessage.MessageType.ENTER.equals(chatMessage.getType())) {
			chatRoomRepository.enterChatRoom(chatMessage.getRoomId());
			chatMessage.setMessage(chatMessage.getSender() + " ���� �����߽��ϴ�.");
		}
		redisPublisher.publish(chatRoomRepository.getTopic(chatMessage.getRoomId()), chatMessage);
	}
	
	
	/*
	stomp ���
	
	private final SimpMessageSendingOperations sendingOperations;
	
	@MessageMapping("chat/message")
	public void createRoom(ChatMessage chatMessage) {
		if(ChatMessage.MessageType.ENTER.equals(chatMessage.getType())) {
			chatMessage.setMessage(chatMessage.getSender() + " ���� �����߽��ϴ�" );
		}
		sendingOperations.convertAndSend("/sub/chat/room/" + chatMessage.getRoomId(), chatMessage);
	}
	*/
}
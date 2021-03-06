package ex.websocket.controller;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.RestController;

import ex.websocket.dto.ChatMessage;
import ex.websocket.repository.ChatRoomRepository;
import ex.websocket.service.ChatService;
import ex.websocket.service.JwtProvider;
import ex.websocket.service.RedisPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ChatController {

	
//	private final RedisPublisher redisPublisher;
	private final ChatRoomRepository chatRoomRepository;
	private final JwtProvider jwtProvider;
//	private final RedisTemplate<String, Object> redisTemplate;
//	private final ChannelTopic channelTopic;
	private final ChatService chatService;
	
	/*
	 * "/pub/chat/message" 로 들어오는 메시지들 매핑
	 */
	// 입장 메시지 삭제
	// 채팅방 인원수 세팅 추가
    @MessageMapping("/chat/message")
    public void message(ChatMessage message, @Header("token") String token) {
        String nickname = jwtProvider.getUserNameFromJwt(token);
        // 로그인 회원 정보로 대화명 설정
        message.setSender(nickname);
        // 채팅방 인원수 세팅
        message.setUserCount(chatRoomRepository.getUserCount(message.getRoomId()));
        // Websocket에 발행된 메시지를 redis로 발행(publish)
        chatService.sendChatMessage(message);
    }
	
	/*
	 * 입장 수 처리 전 (입장 시 메시지 ChatService에서 처리)
	 * 
	@MessageMapping("/chat/message")
	public void message(ChatMessage chatMessage, @Header("token") String token) {
		String nickname = jwtProvider.getUserNameFromJwt(token);
		chatMessage.setSender(nickname);
		if(ChatMessage.MessageType.ENTER.equals(chatMessage.getType())) {
			chatMessage.setSender("[알림]");
			chatMessage.setMessage(nickname +" 님이 입장했습니다.");
		}
		redisTemplate.convertAndSend(channelTopic.getTopic() ,chatMessage);
	}
	*/
	
	
	/*
	stomp 사용
	
	private final SimpMessageSendingOperations sendingOperations;
	
	@MessageMapping("chat/message")
	public void createRoom(ChatMessage chatMessage) {
		if(ChatMessage.MessageType.ENTER.equals(chatMessage.getType())) {
			chatMessage.setMessage(chatMessage.getSender() + " 님이 입장했습니다" );
		}
		sendingOperations.convertAndSend("/sub/chat/room/" + chatMessage.getRoomId(), chatMessage);
	}
	*/
}

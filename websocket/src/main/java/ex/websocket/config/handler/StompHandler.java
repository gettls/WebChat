package ex.websocket.config.handler;

import java.security.Principal;
import java.util.Optional;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import ex.websocket.dto.ChatMessage;
import ex.websocket.dto.ChatRoom;
import ex.websocket.repository.ChatRoomRepository;
import ex.websocket.service.ChatService;
import ex.websocket.service.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class StompHandler implements ChannelInterceptor{
	
	private final JwtProvider jwtProvider;
	private final ChatService chatService;
	private final ChatRoomRepository chatRoomRepository;
	
	
	// websocket �� ���� ���� ��û�� ó���Ǳ��� ����
	@Override
	public Message<?> preSend(Message<?> message, MessageChannel channel) {
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        if (StompCommand.CONNECT == accessor.getCommand()) { // websocket �����û
            String jwtToken = accessor.getFirstNativeHeader("token");
            log.info("CONNECT {}", jwtToken);
            // Header�� jwt token ����
            jwtProvider.validateToken(jwtToken);
        } else if (StompCommand.SUBSCRIBE == accessor.getCommand()) { // ä�÷� ������û
            // header�������� ���� destination������ ���, roomId�� �����Ѵ�.
            String roomId = chatService.getRoomId(Optional.ofNullable((String) message.getHeaders().get("simpDestination")).orElse("InvalidRoomId"));
            // ä�ù濡 ���� Ŭ���̾�Ʈ sessionId�� roomId�� ������ ���´�.(���߿� Ư�� ������ � ä�ù濡 �� �ִ��� �˱� ����)
            String sessionId = (String) message.getHeaders().get("simpSessionId");
            chatRoomRepository.setUserEnterInfo(sessionId, roomId);
            // ä�ù��� �ο����� +1�Ѵ�.
            chatRoomRepository.plusUserCount(roomId);
            // Ŭ���̾�Ʈ ���� �޽����� ä�ù濡 �߼��Ѵ�.(redis publish)
            String name = Optional.ofNullable((Principal) message.getHeaders().get("simpUser")).map(Principal::getName).orElse("UnknownUser");
            chatService.sendChatMessage(ChatMessage.builder().type(ChatMessage.MessageType.ENTER).roomId(roomId).sender(name).build());
            log.info("SUBSCRIBED {}, {}", name, roomId);
        } else if (StompCommand.DISCONNECT == accessor.getCommand()) { // Websocket ���� ����
            // ������ ����� Ŭ���̾�Ʈ sesssionId�� ä�ù� id�� ��´�.
            String sessionId = (String) message.getHeaders().get("simpSessionId");
            String roomId = chatRoomRepository.getUserEnterRoomId(sessionId);
            // ä�ù��� �ο����� -1�Ѵ�.
            chatRoomRepository.minusUserCount(roomId);
            // Ŭ���̾�Ʈ ���� �޽����� ä�ù濡 �߼��Ѵ�.(redis publish)
            String name = Optional.ofNullable((Principal) message.getHeaders().get("simpUser")).map(Principal::getName).orElse("UnknownUser");
            chatService.sendChatMessage(ChatMessage.builder().type(ChatMessage.MessageType.QUIT).roomId(roomId).sender(name).build());
            // ������ Ŭ���̾�Ʈ�� roomId ���� ������ �����Ѵ�.
            chatRoomRepository.removeUserEnterInfo(sessionId);
            log.info("DISCONNECTED {}, {}", sessionId, roomId);
        }
        return message;
	}
	
}
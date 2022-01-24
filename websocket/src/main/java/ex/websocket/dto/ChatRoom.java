package ex.websocket.dto;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.springframework.web.socket.WebSocketSession;

import ex.websocket.dto.ChatMessage.MessageType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatRoom implements Serializable{
	
	private static final long serialVersionUID = 6494678977089006639L;
	
	private String roomId;
	private String name;
	private long userCount;
	
	public static ChatRoom create(String name) {
		ChatRoom chatRoom = new ChatRoom();
		chatRoom.roomId = UUID.randomUUID().toString();
		chatRoom.name = name;
		return chatRoom;
	}
	
	/*
	 * pub/sub 방식에서는 구독자 관리가 알아서 된다 broker에서 하는듯
	 
	private Set<WebSocketSession> sessions = new HashSet<>();
	@Builder
	public ChatRoom(String roomId, String name) {
		this.roomId = roomId;
		this.name = name;
	}
	
	public void handleActions(WebSocketSession session, ChatMessage message, ChatService chatService) {
		if(message.getType().equals(MessageType.ENTER)) {
			sessions.add(session);
			message.setMessage(message.getSender() + " 님이 입장하셨습니다.");
		}
		sendMessage(message, chatService);
	}
	
	public <T> void sendMessage(T message, ChatService chatService) {
		sessions.parallelStream()
				.forEach(session -> chatService.sendMessage(message, session));
	}
	*/
}

package ex.websocket.dto;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.springframework.web.socket.WebSocketSession;

import ex.websocket.dto.ChatMessage.MessageType;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ChatRoom implements Serializable{
	
	private static final long serialVersionUID = 6494678977089006639L;
	
	private String roomId;
	private String name;
	
	public static ChatRoom create(String name) {
		ChatRoom chatRoom = new ChatRoom();
		chatRoom.roomId = UUID.randomUUID().toString();
		chatRoom.name = name;
		return chatRoom;
	}
	
	/*
	 * pub/sub ��Ŀ����� ������ ������ �˾Ƽ� �ȴ� broker���� �ϴµ�/
	 
	private Set<WebSocketSession> sessions = new HashSet<>();
	@Builder
	public ChatRoom(String roomId, String name) {
		this.roomId = roomId;
		this.name = name;
	}
	
	public void handleActions(WebSocketSession session, ChatMessage message, ChatService chatService) {
		if(message.getType().equals(MessageType.ENTER)) {
			sessions.add(session);
			message.setMessage(message.getSender() + " ���� �����ϼ̽��ϴ�.");
		}
		sendMessage(message, chatService);
	}
	
	public <T> void sendMessage(T message, ChatService chatService) {
		sessions.parallelStream()
				.forEach(session -> chatService.sendMessage(message, session));
	}
	*/
}
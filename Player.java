package Ahorcado.Juegos.en.red;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

public class Player {
	private final int id;
	private final WebSocketSession session;
	
	private String name;
	private int roomPlaying;
	
	public Player(int id, WebSocketSession session){
		this.id = id;
		this.session = session;
		this.name = "WebSessionSocket0123456789abc";
		this.roomPlaying = 99999999;
	}
	
	protected void sendMessage(String msg) throws Exception {
		this.session.sendMessage(new TextMessage(msg));
	}
	
	public int getId(){return id;}
	
	public synchronized void setName(String name){this.name = name;}	
	public synchronized String getName(){return this.name;}
	
	public synchronized void setRoomName(int room){this.roomPlaying = room;}	
	public synchronized int getRoomName(){return this.roomPlaying;}
}

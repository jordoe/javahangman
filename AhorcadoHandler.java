package Ahorcado.Juegos.en.red;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import Ahorcado.Juegos.en.red.Player;
import Ahorcado.Juegos.en.red.AhorcadoGame;

public class AhorcadoHandler extends TextWebSocketHandler {
private static final String PLAYER_ATT = "player";
	
	private ObjectMapper mapper = new ObjectMapper();
	private AtomicInteger hangIds = new AtomicInteger(0);
	private AhorcadoGame hangGame = new AhorcadoGame();

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {

		int id = hangIds.getAndIncrement();

		Player p = new Player(id, session);

		session.getAttributes().put(PLAYER_ATT, p);

		hangGame.addPlayer(p);
		System.out.println("New Connection");
		p.sendMessage("connected");

		
	}

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		
		try {
			
			System.out.println("Message received: " + message.getPayload());
			JsonNode node = mapper.readTree(message.getPayload());
			Player p = (Player) session.getAttributes().get(PLAYER_ATT);
			/*
			switch(node.get("type").asText()){
			case "create":
				break;
			case "join":
				break;
			case "leave":
				break;
			}*/

		} catch (Exception e) {
			System.err.println("Exception processing message " + message.getPayload());
			e.printStackTrace(System.err);
		}
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		//CONFIGURAR RESPUESTA DEL SERVIDOR CUANDO UN JUGADOR ABANDONA
		
		Player s = (Player) session.getAttributes().get(PLAYER_ATT);
		
		
		hangGame.removeSnakeAfterClose(s);
		
		System.out.println("Connection closed. Session " + session.getId());		

		
	}
	
	private void requestFailed(String problem, Player snake){
		/*
		String msg = String.format("{\"type\": \"requestFailed\",\"cause\": \"%s\"}", problem);
		
		try {
			snake.sendMessage(msg);
		} catch (Exception e) {}	*/
	}
}

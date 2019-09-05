package Ahorcado.Juegos.en.red;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Random;



public class HangRoom {
	
	private final int MAX_PLAYERS = 2;
	private final int id;
	private ConcurrentHashMap<Integer, Player> playersInRoom = new ConcurrentHashMap<>();
	
	private boolean playing;
	private int turno;
	
	private Random rndm;
	
	private AtomicInteger numPlayers = new AtomicInteger();
	
	public HangRoom(int id){
		this.id = id;
		playing = false;
		turno = -1;
		rndm = new Random();
	}
	
	public void addPlayer(Player p) throws Exception{
		playersInRoom.put(p.getId(), p);
		int count = numPlayers.getAndIncrement();
		
		try {
			joined();
		} catch (Exception e1) {}
		
		if(count == 1){
			playing = true;
			gameStarted();
		}
		
		if(count == 3){
			try {
				
			} catch (Exception e) {}
		}
	}

	public void gameStarted() throws Exception{
		
		String msg = "{\"type\": \"start\"}";
		System.out.println("Sending message: "+msg);
		broadcast(msg);
		
		turno = rndm.nextInt(2);
		
	}

	private int getRoomId() {
		return this.id;
	}

	private void joined() throws Exception {
		StringBuilder sb = new StringBuilder();
		for (Player pl : getPlayers()) {			
			sb.append(String.format("{\"name\": \"%s\",  \"room\": \"%s\"}", pl.getName(), pl.getRoomName()));
			sb.append(',');
		}
		sb.deleteCharAt(sb.length()-1);
		String msg = String.format("{\"type\": \"join\",\"data\":[%s]}", sb.toString());
		
		broadcast(msg);
		
	}
	

	public void broadcast(String message) throws Exception {

		for (Player pl : playersInRoom.values()) {
			try {

				//System.out.println("Sending message " + message + " to " + snake.getName());
				pl.sendMessage(message);

			} catch (Throwable ex) {
				System.err.println("Execption sending message to snake " + pl.getName());
				ex.printStackTrace(System.err);
				//removeSnake(snake);
			}
		}
	}

	public Collection<Player> getPlayers() {
		return playersInRoom.values();
	}
	
	public int getNumPlayers(){return numPlayers.get();}

	public void removePlayer(Player p) {
		
	}

}

package Ahorcado.Juegos.en.red;

import java.util.concurrent.ConcurrentHashMap;


public class AhorcadoGame {

	private static ConcurrentHashMap<Integer, Player> players = new ConcurrentHashMap<>();
	private static ConcurrentHashMap<Integer, HangRoom> rooms = new ConcurrentHashMap<>();
	
	public void addPlayer(Player p){
		players.put(p.getId(), p);
	}

	public void removeSnakeAfterClose(Player p) {
		int room = p.getRoomName();
		
		HangRoom r = rooms.get(room);	
		if(r != null){			
			try {
				r.removePlayer(p);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}				
			
			if(r.getNumPlayers() == 0){
				removeRoom(r);
			}		
		}
		
		removePlayer(p);
		
	}

	private void removePlayer(Player p) {
		// TODO Auto-generated method stub
		
	}

	private void removeRoom(HangRoom r) {
		// TODO Auto-generated method stub
		
	}
}

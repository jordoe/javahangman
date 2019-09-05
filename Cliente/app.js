$(document).ready(function(){

	var Console = {};
	
	Console.log = (function(message) {
		var console = document.getElementById('console');
		var p = document.createElement('p');
		p.style.wordWrap = 'break-word';
		p.innerHTML = message;
		console.appendChild(p);
		while (console.childNodes.length > 25) {
			console.removeChild(console.firstChild);
		}
		console.scrollTop = console.scrollHeight;
	});
	
	let game;
	
	class Game {
	
		constructor(){
			
			this.p1word = null;
			this.p2word = null;
			this.lifes = 5;
			this.lastletter = null;
			this.socket = null;
			this.turn = false;			
			
		}
	
		initialize() {	
			this.resetGame();				
			this.connect();
		}
		
		resetGame(){
			this.playing = false;
			this.waiting = false;
			this.name = null;
			this.roomOwner = "";
		}
	
		setDirection(direc) {
			this.direction = direc;
			var dir = {
				type: "direction",
				direction: direc
				}
			this.socket.send(JSON.stringify(dir));
		}
	
		startGameLoop() {
		
			//gestor de turnos		
		}
	
		stopGameLoop() {
			//terminar los turnos. Este metodo no sería necesario, no?
		}
	
		connect() {
			
			this.socket = new WebSocket("ws://127.0.0.1:8080/ahorcado");
	
			this.socket.onopen = () => {
				
				// Socket open.. start the game loop.
				Console.log('Info: WebSocket connection opened.');
				Console.log('Info: Join a game or create a new one.');				
				
				this.startGameLoop();
			}
	
			this.socket.onclose = () => {
				Console.log('Info: WebSocket closed.');
				this.stopGameLoop();
			}
	
			this.socket.onmessage = (message) => {
	
				var packet = JSON.parse(message.data);
				
				switch (packet.type) {
				case 'joined':
					if(packet.data.length > 1 && packet.data[packet.data.length -1].name == this.name){     //si tu nombre es el nombre del creador de la sala
						for (var j = 0; j < packet.data.length; j++) {
							
							var game = document.getElementById('gameplay');
			
							var state = "Player: "+this.name+". Palabra contrincante: "+this.p1word+"<br>Palabra por resolver: "+this.p2word+"<br>Última letra: "+this.lastletter+". Vidas restantes: "+this.lifes;
							game.innerHTML = state;
						}
						
					}else{
						Console.log(packet.data[packet.data.length -1].name+ " has joined the game");
					}
					
					this.playing=true;
					
					break;
					
					//De estos mensajes de aqui en adelante, leave y turn son los que necesitarian modificarse (tienen solo lo de la anterior practica).
					//El resto ya los he hecho para esta practica. El de wait lo he dejado sin modificar porque no se si sera necesario, por si acaso.
				case 'leave':
					Console.log(packet.name+" has left the game.");
					this.removeSnake(packet.name);
					if(this.snakes.length !=0){
						if(this.snakes[0].name == this.name){
							Console.log("You are the leader. Press 'S' to start the game.");
						}else{
							Console.log("The leader of the room is "+this.snakes[0].name);
						}
					}
					this.updatePlayersText();
					this.updatePlayersScores();
					break;
				case 'turn':
					for (var i = 0; i < packet.data.length; i++) {
						this.updateSnake(packet.data[i].name, packet.data[i].body, packet.data[i].score);	
					}
					this.foodLeft = packet.foodLeft;
					this.updateFood(packet.food);
					this.updatePlayersScores();
					break;
				
				case 'gameover':
					Console.log("You left the game");
					this.resetGame();
					break;
				
				case 'vidas':
					Console.log('Info: You have no lifes left, you´ve lost!!!!');
					this.direction = 'none';
					break;
				case 'palabra':
					Console.log('Info: You won!!!!');
					break;
				case 'start':
					Console.log('Info: THE GAME STARTS NOW!');
					break;
				case 'end':
					Console.log('**********************************');
					Console.log('THE GAME HAS FINISHED!');
					Console.log('The winner is '+packet.winner+');
					Console.log('**********************************');
					break;
				case 'wait':
					//waiting true
					Console.log("Waiting...");
					break;
				case 'requestFailed':
					switch(packet.cause){
					case 'name':
						Console.log('That username already exists');
						this.name =null;
						this.roomOwner = null;
						break;
					case 'full':
						Console.log("That game is full. Create or choose another one. ");
						break;
					}
				}
			}
		}
		
		//De aqui hasta abajo esta sin modificar. Solo he puesto los mensajes que me dijiste que tenia que poder mandar el cliente
		
		sendCreate(msg) {	
		
			this.socket.send(JSON.stringify(msg));
		}
		
		sendJoin(msg) {	
			this.socket.send(JSON.stringify(msg));
		}
		
		sendLeave() {	
			
			var msg = {
				type: "leave",
				name: this.name,
				room: this.roomOwner
			}
			this.socket.send(JSON.stringify(msg));
		}
	}
	
	
	
	game = new Game();
	
	game.initialize();
	
	
	$("#createGame").click(()=> {
	 	if(!game.waiting || !game.playing){
		 	game.name = $("#ninput").val();
		 	game.roomOwner = $("#ninput").val();
			game.p1word = $("#winput").val();
			game.
		 	
		 	var msg = {
		 		type: "create",
		 		name: $("#ninput").val(),
		 		room: $("#rinput").val()
		 	}
		 	
		 	game.sendCreate(msg);
		 	//Console.log("Login Succesful");
	 	}
	});
	
	$("#joinGame").click(()=> { 
		if(!game.waiting || !game.playing){	
		 	game.name = $("#ninput").val();
		 	game.roomOwner = $("#rinput").val();
		 	var msg = {
		 		type: "join",
		 		name: $("#ninput").val(),
		 		room: $("#rinput").val()
		 	}
		 	
		 	game.sendJoin(msg);
		 	//Console.log("Login Succesful");
	 	}
	});
	
	$("#leaveGame").click(()=> {
		if(!game.waiting){
			game.sendLeave();
		}
	});
	
	$("#cancelWait").click(()=> {
		if(!game.playing){
			game.cancelWait();
		}
	});

});

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.LinkedList;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.border.EmptyBorder;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import javax.swing.JOptionPane;
import java.awt.Canvas;
import java.net.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class GameServer extends JPanel implements Runnable, Constants, ActionListener{
	private String playerData;
    private DatagramSocket serverSocket = null;
	private int numPlayers;					//maximum players
	private int connectedPlayers = 0;		//total players connected
	private Thread thread;
	private Handler handler;
	private boolean running = false;
	int viewRange = 6;
	private boolean server_status = true;	//starts as online server
	
	JButton server_switch;					//server switch
	JLabel title;							//server
	JPanel[] playerPane;	//pane for each player
	JLabel[] name;			//names
	JLabel[] ip;			//ip
	JButton[] disconnect;	//button for disconnect
	int index = 0;
	
	public GameServer(){
		try {
            serverSocket = new DatagramSocket(PORT);
			serverSocket.setSoTimeout(100);
		} catch (IOException e) {
            System.err.println("Could not listen on port: "+PORT);
            System.exit(-1);
		}catch(Exception e){}
		init();
	}
	
	//initializes game server
	public void init(){
	
		boolean allow = false;
		while(!allow){
			allow = true;
			String num = (String)JOptionPane.showInputDialog(new JFrame(),
								"Enter number of players\n",
								"Server Accomodation",
								JOptionPane.QUESTION_MESSAGE,null,null,2);
			if(num == null)
				if(JOptionPane.showConfirmDialog(null,
					"Are you sure? You cannot play via network without this.","Close Server",
					JOptionPane.YES_NO_OPTION,
					JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION){
					
					JOptionPane.showMessageDialog(null, "Exiting...", "Server Closing.",  JOptionPane.INFORMATION_MESSAGE); 
					System.exit(0);	
				}else{
					allow = false;
					continue;
				}
				
			try{  
				if(Integer.parseInt(num) > 6 || Integer.parseInt(num) < 2)
					allow = false;  						
			}catch(NumberFormatException nfe){  
				allow = false; 
			}
			

			if(!allow){
				JOptionPane.showMessageDialog(null, "For maximum performance, 2-6 players are strongly recommended.", "Wrong input!",  JOptionPane.ERROR_MESSAGE); 
			}else{
				JOptionPane.showMessageDialog(null, "Maximum players of the server is "+num, "Input accepted!",  JOptionPane.INFORMATION_MESSAGE); 
				numPlayers = Integer.parseInt(num);
			}
		}
		//System.out.println("Server created for "+numPlayers+" players...");
		
		handler = new Handler();
		this.setLayout(new BorderLayout());
		
		JPanel playerPanel = new JPanel();
		JPanel controlPanel = new JPanel();
		playerPanel.setLayout(new GridLayout(numPlayers,1));
		
		playerPane = new JPanel[numPlayers];
		name = new JLabel[numPlayers];
		ip = new JLabel[numPlayers];
		disconnect = new JButton[numPlayers];
		
		title = new JLabel("Dedicated Game Server");
		title.setHorizontalAlignment(JLabel.CENTER); 
		
		for(int i = 0; i < numPlayers; i++){
			playerPane[i] = new JPanel();
			name[i] = new JLabel("<Empty>");
			ip[i] = new JLabel("x.x.x.x");
			disconnect[i] = new JButton("Disconnect Player"); 
			disconnect[i].addActionListener(this);
			disconnect[i].setEnabled(false);; 
			
			playerPane[i].setLayout(new GridLayout(1,3));
			playerPane[i].add(name[i]);
			playerPane[i].add(ip[i]);
			playerPane[i].add(disconnect[i]);
			playerPane[i].setBorder(new EmptyBorder(10,10,10,10));
		
			playerPanel.add(playerPane[i]);
		}
		
		
		//control panel
		server_switch = new JButton("Go Offline");
		server_switch.addActionListener(this);
		controlPanel.add(server_switch);
		
		
		this.add(title, BorderLayout.NORTH);
		this.add(playerPanel, BorderLayout.CENTER);
		this.add(controlPanel, BorderLayout.SOUTH);
	}
	
	public synchronized void start(){
		if(running)
			return;
			
		running = true;
		thread = new Thread(this);
		thread.start();
	}
	
	public void run(){
		this.requestFocus();
		long lastTime = System.nanoTime();
		double amountOfTicks = 60.0;
		double ns = 1000000000 / amountOfTicks;
		double delta = 0;
		long timer = System.currentTimeMillis();
		int updates = 0;
		int frames = 0;
		while(running){
		
			updateGameServer();
		
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			while(delta >= 1){
				tick();
				updates++;
				delta--;
			}		
			if(System.currentTimeMillis() - timer > 1000){
				timer += 1000;
				frames = 0;
				updates = 0;
			}
		}
	}
	
	private void updateGameServer(){
	
		if(server_status){
			// Get the data from players
			byte[] buf = new byte[256];
			DatagramPacket packet = new DatagramPacket(buf, buf.length);
			try{
				serverSocket.receive(packet);
			}catch(Exception ioe){}
			
			/**
			 * Convert the array of bytes to string
			 */
			playerData = new String(buf);
			playerData = playerData.trim();
			if(!playerData.equals("")){
				//System.out.println(playerData);
			}
			
					
			if(playerData.startsWith("CHECK")){
				if(server_status){
					Player player = new Player(packet.getAddress(),packet.getPort());
					send(player, "EXISTING");
				}
			}
			
			//during waiting players
			if(playerData.startsWith("CONNECT")){
				
				if(connectedPlayers == numPlayers){
					Player player = new Player(packet.getAddress(),packet.getPort());
					send(player,"FULL");
				}
				else{
					String tokens[] = playerData.split("<delimiter>");
					int x, y, orientation;
					//random spawn
					while(true){
							x = (int)(100*Math.random())%36;
							y = (int)(100*Math.random())%30;
							if(handler.getObject(x,y) == 0){
								break;
							}
						}
						
					name[connectedPlayers].setText(tokens[1]);
					ip[connectedPlayers].setText(packet.getAddress().toString());
					
					//BUGGY
					disconnect[connectedPlayers].setEnabled(false);
					
					connectedPlayers++;
					Player player = new Player(connectedPlayers, tokens[1],x,y,resWidth/(viewRange*2+1),resHeight/(viewRange*2+1),resWidth/(viewRange*2+1),resHeight/(viewRange*2+1),packet.getAddress(),packet.getPort());
					handler.addPlayer(player);
					send(player,"CONNECTED<delimiter>"+player.toString());
					
					//broadcast a new player
					broadcast("UPDATE<delimiter>"+handler.getPlayerList().size()+handler.getGameMap());		
				}
			}
			
			if(playerData.startsWith("MOVE")){
				String tokens[] = playerData.split("<delimiter>");
				int id = Integer.parseInt(tokens[1]);
				String name = tokens[2];
				int x = Integer.parseInt(tokens[3]);
				int y = Integer.parseInt(tokens[4]);
				int orientation = Integer.parseInt(tokens[5]);

				LinkedList<Player> playerList = handler.getPlayerList();
				for(Player player:playerList){
					if(player.getId() == id){
						handler.positionPlayer(id,x,y,orientation);
					}
				}
				broadcast("MOVE<delimiter>"+handler.getPlayerList().size()+handler.getGameMap());
			}
			if(playerData.startsWith("HIT")){
				String tokens[] = playerData.split("<delimiter>");
				int id = Integer.parseInt(tokens[1]);
				String name = tokens[2];
				int x = Integer.parseInt(tokens[3]);
				int y = Integer.parseInt(tokens[4]);
				int orientation = Integer.parseInt(tokens[5]);
			
				int range;
					
				//down
				if(orientation == 1){
					if(36-x<3)
						range = 36-x;
					else
						range = 3;
					for(int i = 1; i <= range; i++){
						if(handler.getObject(x+i,y) == 8)
							break;
						
						if(handler.getObject(x+i,y) != 0 && handler.getObject(x+i,y) != 7){
							broadcast("HIT<delimiter>"+id+"<delimiter>"+handler.getObject(x+i,y));
							handler.getPlayer(id).hasKilled();
							break;
						}
							
					}	
				//top
				}else if(orientation == 2){
						if(x-1<3)
						range = x-1;
					else
						range = 3;
					for(int i = 1; i <= range; i++){
						if(handler.getObject(x-i,y) == 8)
							break;
						
						if(handler.getObject(x-i,y) != 0 && handler.getObject(x-i,y) != 7){
							broadcast("HIT<delimiter>"+id+"<delimiter>"+handler.getObject(x-i,y));
							handler.getPlayer(id).hasKilled();
							break;
						}
						
						}
					
				//left
				}else if(orientation == 3){
					if(y-1<3)
						range = y-1;
					else
						range = 3;
					for(int i = 1; i <= range; i++){
						if(handler.getObject(x,y-i) == 8)
							break;
						
						if(handler.getObject(x,y-i) != 0 && handler.getObject(x,y-i) != 7){
							broadcast("HIT<delimiter>"+id+"<delimiter>"+handler.getObject(x,y-i));
							handler.getPlayer(id).hasKilled();
							break;
						}
					
					}
					
					
				//right
				}else if(orientation == 4){
					if(30-y<3)
						range = 30-y;
					else
						range = 3;
					for(int i = 1; i <= range; i++){
						if(handler.getObject(x,y+i) == 8)
							break;
					
						if(handler.getObject(x,y+i) != 0 && handler.getObject(x,y+i) != 7){
							broadcast("HIT<delimiter>"+id+"<delimiter>"+handler.getObject(x,y+i));
							handler.getPlayer(id).hasKilled();
							break;
						}
							
					}
				}
				
			}
		
			if(playerData.startsWith("CHAT")){
				String tokens[] = playerData.split("<delimiter>");
				int id = Integer.parseInt(tokens[1]);
				String message = tokens[2];
			
				broadcast("CHAT<delimiter>"+id+"<delimiter>"+message);
			}
			
			if(playerData.startsWith("DISCONNECT")){
				connectedPlayers--;
				
				String tokens[] = playerData.split("<delimiter>");
				int id = Integer.parseInt(tokens[1]);
				//re-arranging the server
				for(int x = id-1; x < connectedPlayers; x++){
					name[x].setText(name[x+1].getText());
					ip[x].setText(ip[x+1].getText());
					disconnect[x].setEnabled(true);
				}
					
				name[connectedPlayers].setText("<Empty>");
				ip[connectedPlayers].setText("x.x.x.x");
				disconnect[connectedPlayers].setEnabled(false);
					
				handler.removePlayer(handler.getPlayer(id));
			}
		}
	}

	private void tick(){
		
			
	}

	public void broadcast(String msg){
		LinkedList<Player> playerList = handler.getPlayerList();
		for(Player player: playerList){			
			send(player,msg);	
		}
	}
	
	public void send(Player player, String msg){
		DatagramPacket packet;	
		byte buf[] = msg.getBytes();		
		packet = new DatagramPacket(buf, buf.length, player.getAddress(),player.getPort());
		try{
			serverSocket.send(packet);
		}catch(IOException ioe){
			ioe.printStackTrace();
		}
	}
	
	public void actionPerformed(ActionEvent e){
		for(int i = 0; i < connectedPlayers; i++){
			if(e.getSource() == disconnect[i]){
							
				broadcast("DISCONNECT<delimiter>"+handler.getPlayer(i+1).toString());
			}
		}
		
		if(e.getSource() == server_switch){
			//when switch is pressed
	
			//when online, switch is pressed
			if(server_status){
				if(JOptionPane.showConfirmDialog(null,
					"Switching server off will disconnect all players!","Switching Server Status",
					JOptionPane.YES_NO_OPTION,
					JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION){

					connectedPlayers = 0;
					
					//clearing players
					for(int i = 0; i < numPlayers; i++){
						name[i].setText("<Empty>");
						ip[i].setText("x.x.x.x");
						disconnect[i].setEnabled(false);
					}
					
					server_switch.setText("Go Online");
					server_status = false;
					broadcast("DISCONNECT<delimiter>SERVER");
				}
			}
			//when offline, switch is pressed
			else{

				JOptionPane.showMessageDialog(null, "Server is now online.", "Online",  JOptionPane.INFORMATION_MESSAGE);
				init();
				server_switch.setText("Go Offline");
				server_status = true;
			}
			//System.out.println(name[0].getText());
		}
	}
	
	public static void main(String args[]){
	
		new TargetServerFrame(400, 200, new GameServer());
	}
}


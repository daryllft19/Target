import java.awt.Canvas;
import java.awt.image.BufferStrategy;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.util.Random;
import java.awt.Toolkit;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import javax.swing.JOptionPane;
import javax.swing.JFrame;
import java.net.*;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

public class Game extends Canvas implements KeyListener, Runnable, Constants{
	
	private static final long serialVersionUID = -6261436164361361187L;
	private boolean connected = false;
	private boolean ipFound = false;
	private boolean running = false;	
	private Thread thread;				//main thread
	private int GAME_STATE = GAME_START;//starts at the menu
	private boolean menuChosen = false;	//true if something is chosen at the menu 
	private int arrowPlacement = 0;		//arrow at the menu
	private String ipAddress = null;	//ip address of the player
	Random rand = new Random();			//random instantiation
	DatagramSocket socket = new DatagramSocket();
	private String username;			//player name
	private int viewRange = 6;			//vision range of player
	private String serverData;			//string data from server
	Handler handler;					//handler of the game variables
	Player player;						//main player of the client
	private JFrame frame;				//used to change frame title
	private boolean fog = false;		//use for handicapping view if in fog
	private int[] smoke = new int[2];
	private String message;				//input chat of player
	private LinkedList<String> conversation = new LinkedList<String>();
	private LinkedList<String> notification = new LinkedList<String>();
	private int chatIndex = 570;
	private int notifIndex = 570;
	Timer timer = new Timer();			//timer for clearing chat
	private boolean typing = false;		//to check whether typing
	private boolean showScores = false;	//for printing stats
	private boolean sentRequest = false;//check if sent request
	int[][] map={
			{8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,7,7,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,8,8,7,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,8,8,7,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,0,0,8,8,8,0,0,0,0,0,0,0,0,0,0,0,8,8,8,7,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,0,0,8,8,8,0,0,0,0,0,0,0,0,0,0,0,8,8,8,7,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,0,0,8,8,8,0,0,8,8,8,0,8,8,8,0,8,8,8,8,7,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,0,0,7,7,7,0,0,7,7,7,0,7,7,7,0,7,7,7,0,0,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,0,0,8,8,8,0,0,8,8,8,0,8,8,8,0,8,8,8,0,0,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,0,0,8,8,8,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,0,0,8,8,8,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,0,0,8,8,8,0,0,8,8,8,0,8,8,8,0,8,8,8,0,0,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,7,7,7,0,0,7,7,7,0,0,7,7,7,0,7,7,7,0,7,7,7,0,0,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,7,8,8,0,0,8,8,8,0,0,8,8,8,0,8,8,8,0,8,8,8,0,0,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,7,8,8,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,7,8,8,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,7,8,8,0,0,8,8,8,0,0,8,8,8,0,8,0,8,0,8,8,8,0,0,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,7,7,7,0,0,7,7,7,0,0,7,7,7,0,7,0,7,0,7,7,7,0,0,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,0,0,8,8,8,0,0,8,8,8,0,8,0,8,0,8,8,8,0,0,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,0,0,0,0,0,0,0,8,8,8,0,0,0,0,0,0,0,0,0,0,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,0,0,0,0,0,0,0,8,8,8,0,0,0,0,0,0,0,0,0,0,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,0,0,8,8,8,0,0,8,8,8,0,8,8,8,0,8,8,8,0,0,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,0,0,7,7,7,0,0,7,7,7,0,7,7,7,0,7,7,7,0,0,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,0,0,8,8,8,0,0,8,8,8,0,8,8,8,0,8,8,8,0,0,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,0,0,8,7,8,0,0,8,7,8,7,8,7,8,7,8,7,8,0,0,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,0,0,8,7,8,0,0,8,7,8,7,8,7,8,7,8,7,8,0,0,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,0,0,8,7,8,0,0,8,7,8,7,8,7,8,7,8,7,8,0,0,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8}
		};//game board
				
	public Game() throws Exception{
		socket.setSoTimeout(100);
		addKeyListener(this);
		init();
	}
	
	public void init(){
		timer.schedule(new clearChat(), 0, 4000);
		timer.schedule(new clearNotif(), 0, 5000);
		handler = new Handler();
		connected = false;
		running = false;
		GAME_STATE = GAME_START;
		menuChosen = false;
		arrowPlacement = 0;
		fog = false;		//use for handicapping view if in fog
		chatIndex = 570;
		typing = false;		//to check whether typing
		showScores = false;	//for printing stats
		ipAddress = null;
		ipFound = false;
	}
	
	public synchronized void start(JFrame frame){
		if(running)
			return;
		
		this.frame = frame;
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
		
			updateGame();
			
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			while(delta >= 1){
				tick();
				updates++;
				delta--;
			}
			render();
			frames++;
					
			if(System.currentTimeMillis() - timer > 1000){
				timer += 1000;
				//System.out.println("FPS: " + frames + " TICKS: " + updates);
				frames = 0;
				updates = 0;
			}
		}
	}
	
	private void updateGame(){
	
		//Get the data from players
		byte[] buf = new byte[256];
		DatagramPacket packet = new DatagramPacket(buf, buf.length);
		try{
			socket.receive(packet);
		}catch(Exception ioe){}
		
		serverData=new String(buf);
		serverData=serverData.trim();
	
		if(ipAddress != null && !ipFound){
			if(sentRequest){
				if(serverData.equals("EXISTING")){
					ipFound = true;
				}
				
			}
			
			send("CHECK");
			sentRequest = true;
		}
		
		if(ipAddress != null && ipFound){

				if(!serverData.equals("")){
					//System.out.println(serverData);
				}
				
				if(serverData.startsWith("FULL")){
					GAME_STATE = GAME_START;
					JOptionPane.showMessageDialog(null, "Server has reached its maximum potential. Try again next time!", "Server full!",  JOptionPane.ERROR_MESSAGE); 
				}
				//CONNECTION ACKNOWLEDGED
				if(!connected && serverData.startsWith("CONNECTED")){					
					connected = true;
					String tokens[] = serverData.split("<delimiter>");
					
					int id = Integer.parseInt(tokens[1]);
					String name = tokens[2];
					int x = Integer.parseInt(tokens[3]);
					int y = Integer.parseInt(tokens[4]);
					int orientation = Integer.parseInt(tokens[5]);
					player= new Player(id,name, x, y, resWidth/(viewRange*2+1),resHeight/(viewRange*2+1),resWidth/(viewRange*2+1),resHeight/(viewRange*2+1),orientation);
					handler.addPlayer(player);
					map[x][y] = id;
					
					handler.positionPlayer( id, x, y, orientation);
					JOptionPane.showMessageDialog(null, "Playing as "+username+" on server "+ipAddress+".", "Joining game...",  JOptionPane.INFORMATION_MESSAGE); 
					send("MOVE<delimiter>"+player.toString());
				}
				
				//MORE CONNECTIONS FROM OTHER CLIENTS
				else if(connected && serverData.startsWith("UPDATE")){
					String data[] = serverData.split(":");
					String first[] = data[0].split("<delimiter>");
					int max = Integer.parseInt(first[1]);
					
					for(int i = 1; i <= max; i++){
						String tokens[] = data[i].split("<delimiter>");
						
						int id = Integer.parseInt(tokens[0]);
						String name = tokens[1];
						int x = Integer.parseInt(tokens[2]);
						int y = Integer.parseInt(tokens[3]);
						int orientation = Integer.parseInt(tokens[4]);
						Player p = new Player(id,name, x, y, resWidth/(viewRange*2+1),resHeight/(viewRange*2+1),resWidth/(viewRange*2+1),resHeight/(viewRange*2+1),orientation);
						
						if(!handler.isOnList(p.getId())){
							handler.addPlayer(p);

							//limit of notification is 5
							if(notification.size() > 5){
								notification.removeFirst();
								notifIndex += 40;
							}
							
							//add message to linked list
							notification.addLast(p.getId()+"<delimiter>is now connected!");
							//printing goes up after every new message
							notifIndex -= 40;
						
						}
						handler.positionPlayer( id, x, y, orientation);
						send("MOVE<delimiter>"+player.toString());
						map[x][y] = id;
					}
				}
				
				//UPDATING PLAYER LOCATION
				if(serverData.startsWith("MOVE")){
					resetMap();
					String data[] = serverData.split(":");
					String first[] = data[0].split("<delimiter>");
					int max = Integer.parseInt(first[1]);
					
					for(int i = 1; i <= max; i++){
						String tokens[] = data[i].split("<delimiter>");
						
						int id = Integer.parseInt(tokens[0]);
						String name = tokens[1];
						int x = Integer.parseInt(tokens[2]);
						int y = Integer.parseInt(tokens[3]);
						int orientation = Integer.parseInt(tokens[4]);
						handler.positionPlayer( id, x, y, orientation);
						
						if(player.getId() == id)
						if(map[player.getX()][player.getY()] == 7)
							viewRange = 2;
						else
							viewRange = 6;
							
						if(map[x][y] == 0 || map[x][y] == 7 && id == player.getId())
							map[x][y] = id;
						
							
					}
					
					
				}
				
				//SOMEONE IS HIT
				if(serverData.startsWith("HIT")){
					String tokens[] = serverData.split("<delimiter>");
					int killerid = Integer.parseInt(tokens[1]);
					int deadid = Integer.parseInt(tokens[2]);
					
					Player killer = handler.getPlayer(killerid);
					Player dead = handler.getPlayer(deadid);
					handler.getPlayer(killerid).hasKilled();
					handler.getPlayer(deadid).hasDied();
					
					if(dead != null && killer != null){
						deadid = dead.getId();
						String killername = killer.getName();
						String name = dead.getName();
						int x = dead.getX();
						int y = dead.getY();
						int orientation = dead.getView();
						int tempx, tempy;
						
						//random spawn
						while(true){
								tempx = (int)(100*Math.random())%36;
								tempy = (int)(100*Math.random())%30;
								orientation = (int)(100*Math.random())%4+1;
								if(handler.getObject(tempx,tempy) == 0){
									break;
								}
						}
						
						smoke[0] = dead.getX();
						smoke[1] = dead.getY();
						//who died
						if(player.getId() == deadid){
							//move the dead player to a new place
							handler.positionPlayer(deadid,tempx,tempy,orientation);
							removeKeyListener(this);
							
							//send to server the new position of the dead player
							send("MOVE<delimiter>"+dead.toString());
							JOptionPane.showMessageDialog(null, "You were killed by "+killername+"!\nReviving..", "Dead",  JOptionPane.INFORMATION_MESSAGE); 
							addKeyListener(this);
						}
						
						
					}	
				}
				
				
				if(serverData.startsWith("CHAT")){
					String tokens[] = serverData.split("<delimiter>");
						int id = Integer.parseInt(tokens[1]);
						String message = tokens[2];
						
						Player p = handler.getPlayer(id);
						//limit of chat is 8 conversation
						if(conversation.size() > 8){
							conversation.removeFirst();
							chatIndex += 21;
						}
						
						//add message to linked list
						conversation.addLast(p.getId()+"<delimiter>"+message);
						//printing goes up after every new message
						chatIndex -= 21;
						
				}
				
				if(serverData.startsWith("DISCONNECT")){
					String tokens[] = serverData.split("<delimiter>");
					if(tokens[1].equals("SERVER")){
						init();
						start(new JFrame());
					}else{
						int id = Integer.parseInt(tokens[1]);
						
						if(id == player.getId()){
							ipAddress = null;
							send("DISCONNECT<delimiter>"+id);
							init();
							start(new JFrame());
						}else{
							
							Player p = handler.getPlayer(id);
							if(map[p.getX()][p.getY()] == id)
								map[p.getX()][p.getY()] = 0;
							handler.removePlayer(p);
							
							//your id is greater than the dead id, you change id
							if(player.getId() > id){
								player.setId(player.getId()-1);
								handler.removePlayer(player);
								handler.addPlayer(player);
							}
						}
					}
				}
		}

	}
	
	private void tick(){
		

	}
	
	//for printing
	private void render(){
		BufferStrategy bs = this.getBufferStrategy();
		if(bs == null){
			this.createBufferStrategy(3);
			return;
		}
		
		Graphics g = bs.getDrawGraphics();
		//////////////////////
		g.fillRect(0,0,getWidth(), getHeight());
		switch(GAME_STATE){
			case GAME_START: 
				g.drawImage(Toolkit.getDefaultToolkit().getImage("images/cover.jpg"), 0,0,getWidth(),getHeight(),this);
				g.drawImage(Toolkit.getDefaultToolkit().getImage("images/arrow.png"), (getWidth()/2)-281/2 -100,245+arrowPlacement*80,82,82,this);
				
				for(int i = 0; i < 4; i++){
					if(menuChosen && i==arrowPlacement){
						g.drawImage(Toolkit.getDefaultToolkit().getImage("images/button/hover/"+(i+1)+".png"), (getWidth()/2)-281/2,250+(i*80),281,70,this);
						continue;
					}
					g.drawImage(Toolkit.getDefaultToolkit().getImage("images/button/normal/"+(i+1)+".png"), (getWidth()/2)-281/2,250+(i*80),281,70,this);
				}
				break;
			case PLAY:
				g.setColor(new Color(0,0,0));
				g.fillRect(0,0,getWidth(),getHeight());
				int tempX=0 , tempY=0;
					//world
					for(int x=player.getX()-viewRange;x<=player.getX()+viewRange;x++){
						tempY=0;
						for(int y=player.getY()-viewRange;y<=player.getY()+viewRange;y++){
							try{
							//if(map[x][y]==0)
								g.drawImage(Toolkit.getDefaultToolkit().getImage("images/ice.png"), tempY*resWidth/(viewRange*2+1),tempX*resHeight/(viewRange*2+1),resWidth/(viewRange*2+1)+1,resHeight/(viewRange*2+1)+1,this);
							
							//printing players
							LinkedList<Player> playerList = handler.getPlayerList();
							for(Player p: playerList){
								if(map[x][y] == p.getId())
									g.drawImage(Toolkit.getDefaultToolkit().getImage("images/"+p.getId()+"/"+p.getView()+".png"), tempY*resWidth/(viewRange*2+1),tempX*resHeight/(viewRange*2+1),resWidth/(viewRange*2+1)+1,resHeight/(viewRange*2+1)+1,this);	
							}
							
							if(map[x][y]==7){
								g.drawImage(Toolkit.getDefaultToolkit().getImage("images/fog.png"), tempY*resWidth/(viewRange*2+1),tempX*resHeight/(viewRange*2+1),resWidth/(viewRange*2+1)+1,resHeight/(viewRange*2+1)+1,this);
							}
							if(map[x][y]==8)
								g.drawImage(Toolkit.getDefaultToolkit().getImage("images/banga.png"), tempY*resWidth/(viewRange*2+1),tempX*resHeight/(viewRange*2+1),resWidth/(viewRange*2+1)+1,resHeight/(viewRange*2+1)+1,this);
							}
							catch(Exception e){
								g.fillRect(tempY*resWidth/(viewRange*2+1),tempX*resHeight/(viewRange*2+1),resWidth/(viewRange*2+1),resHeight/(viewRange*2+1));
							}
							tempY++;
						}
						tempX++;
					}
					
					g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
					if(typing){
						g.setColor(new Color(5, 5, 5,230));
						//g.drawRect (10, 10, 200, 200);
						g.fillRect(10,560,getWidth()-25,35);
						g.setColor(new Color(255,255,255));
						g.drawString(message,20,580);
					}
					
					//calling function for printing conversation
					drawChat(g,conversation,getWidth()/2-80,chatIndex);
					
					//calling function for printing notification
					drawNotif(g,notification,640,notifIndex);
					
					if(showScores){
						showTabulatedScore(g);
					}
				break;
				case INSTRUCTIONS:
					g.drawImage(Toolkit.getDefaultToolkit().getImage("images/instructions.jpg"), 0,0,getWidth(),getHeight(),this);
				break;
				case ABOUT:
					g.drawImage(Toolkit.getDefaultToolkit().getImage("images/about.jpg"), 0,0,getWidth(),getHeight(),this);
				break;
		}
		//////////////////////
		g.dispose();
		bs.show();
	}
	
	public void send(String msg){
		try{
		byte[] buf = msg.getBytes();
        	InetAddress address = InetAddress.getByName(ipAddress);
        	DatagramPacket packet = new DatagramPacket(buf, buf.length, address, PORT);
        	socket.send(packet);
        }catch(Exception e){}
		
	}
	
	public static void main(String[] args)  throws Exception{
		new TargetFrame(800, 600, new Game());
	}
	
	
	public void keyReleased(KeyEvent e){
		if(e.getKeyCode() == 192){
			if(showScores == true)
				showScores = false;
		}
	}
	public void keyPressed(KeyEvent e){
	
		//KEYS for menu
		if(GAME_STATE == GAME_START){
			if(e.getKeyCode() == KeyEvent.VK_UP){
				if(arrowPlacement > 0)
					arrowPlacement--;
			
			}else if(e.getKeyCode() == KeyEvent.VK_DOWN){
				if(arrowPlacement < 3)
					arrowPlacement++;
			
			}else if(e.getKeyCode()  == KeyEvent.VK_ENTER){
				menuChosen = true;
				try {
					Thread.sleep(500);                 //1000 milliseconds is one second.
					
					//Chosen menu is play
					if(arrowPlacement+1 == PLAY){
						boolean allow = true;
						String display = "Format is x.x.x.x; 0<=x<=255.";
							
						String ip = (String)JOptionPane.showInputDialog(new JFrame(),
							"Enter the server's IP address.\n",
							"Server IP Address",
							JOptionPane.QUESTION_MESSAGE,null,null,"127.0.0.1");
						
						if(ip == null){
							allow = false;
						
						//asking for ip address
						}else{
							String[] token = ip.split("\\.");
							if(token.length != 4){
								allow = false;
							}
								
							for(int i = 0; i< token.length; i++){
								try{  
									if(Integer.parseInt(token[i]) > 255 || Integer.parseInt(token[i]) < 0)
										allow = false;  
										
								}catch(NumberFormatException nfe){  
									allow = false; 
								}
								
							}
							
							if(ip.length() != 0)
									display += "\nYour input IP is "+ip;
							
							
							if(!allow){
								JOptionPane.showMessageDialog(null, display, "Wrong IP Address input!",  JOptionPane.ERROR_MESSAGE); 
								menuChosen = false;
								return;
							//validated ip address
							}else{
								ipAddress = ip;
							}
							
							//to allow client to search for server
							Thread.sleep(100);  
							JOptionPane.showMessageDialog(null, "Searching for a game server...", ip,  JOptionPane.PLAIN_MESSAGE); 
							if(!ipFound){
								JOptionPane.showMessageDialog(null, "Server is either offline or not existing.", "No game server found on "+ip+"!",  JOptionPane.ERROR_MESSAGE); 
								menuChosen = false;
								return;
							}
								String temp_username = null;
								while(temp_username == null){
									temp_username = (String)JOptionPane.showInputDialog(new JFrame(),
															"Enter your desired username.\nMin: 1 characters.\nMax: 8 characters:\n",
															"Username",
															JOptionPane.QUESTION_MESSAGE,null,null,"daryll");
								}
									temp_username = temp_username.trim();
									
								if(temp_username.length() > 8){
									JOptionPane.showMessageDialog(null, "Username should not exceed 8 charaters", "Wrong username format!",  JOptionPane.ERROR_MESSAGE); 
									menuChosen = false;
									return;
								}else if(temp_username.length() < 1){
									JOptionPane.showMessageDialog(null, "Username should be more than 1 charaters", "Wrong username format!",  JOptionPane.ERROR_MESSAGE); 
									menuChosen = false;
									return;
								
								//validated
								}else{
									username = temp_username;
									send("CONNECT<delimiter>"+username);
									frame.setTitle(APP_NAME+": "+username);
									GAME_STATE = PLAY;
								}
								
						}
					}else if(arrowPlacement+1 == INSTRUCTIONS){
						GAME_STATE = INSTRUCTIONS;
					}else if(arrowPlacement+1 == ABOUT){
						GAME_STATE = ABOUT;
					}else if(arrowPlacement+1 == END){
						if(JOptionPane.showConfirmDialog(null,
							"Are you sure?","Close Client",
							JOptionPane.YES_NO_OPTION,
							JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION){
								GAME_STATE = END;
								System.exit(0);	
							}	
					}
					
				menuChosen = false;
				} catch(InterruptedException ex) {
					Thread.currentThread().interrupt();
				}
				
			}
			
		}
		
		//KEYS for playing
		else if(GAME_STATE == PLAY){
		
			//can type anytime
			if(e.getKeyCode() == KeyEvent.VK_ENTER){
				
				if(typing == true){
					message = message.trim();
					
					if(!message.equals(""))
						send("CHAT<delimiter>"+player.getId()+"<delimiter>"+message);
					
					typing = false;
				}
				else if(typing == false){
					message = "";
					typing = true;
				}
			}
			
			
			//typing
			if(typing == true){
				//getting printable characters
				if(e.getKeyCode() > 31 && e.getKeyCode() < 37 || e.getKeyCode() > 40 && e.getKeyCode() != 127  ){
				message += e.getKeyChar();
				}
				
				//backspace
				if(e.getKeyCode() == KeyEvent.VK_BACK_SPACE){
					if(!message.equals(""))
						message = message.substring(0,message.length()-1);
				}
			}
			
			else if(typing == false)
				// the character ---> (`)
				if(e.getKeyCode() == 192){
					if(showScores == false)
						showScores = true;
				}else if(e.getKeyCode() == KeyEvent.VK_SPACE){
					send("HIT<delimiter>"+player.toString());	
				}else{
					int tempX = player.getX(), tempY = player.getY();
					if(e.getKeyCode() == KeyEvent.VK_UP){
						player.moveUp();
					}else if(e.getKeyCode() == KeyEvent.VK_DOWN){
						player.moveDown();
					}else if(e.getKeyCode() == KeyEvent.VK_LEFT){
						player.moveLeft();
					}else if(e.getKeyCode() == KeyEvent.VK_RIGHT){
						player.moveRight();
					}
					
					
					//is on a fog
					if(map[player.getX()][player.getY()] == 7)
						fog = true;
					//walked out of fog
					else if(map[player.getX()][player.getY()] == 0)
						fog= false;
					
					//lessen visibility when in fog
					if(fog)
						viewRange = 2;
					//lessen visibility when in fog
					else 
						viewRange = 6;
					if((tempX == player.getX() && tempY == player.getY()) || map[player.getX()][player.getY()] != 0 && map[player.getX()][player.getY()] != 7){
						player.setX(tempX);
						player.setY(tempY);
					}
					send("MOVE<delimiter>"+player.toString());
				}
		}
		
		//INSTRUCTIONS or ABOUT button clicked
		else if(GAME_STATE == INSTRUCTIONS || GAME_STATE == ABOUT){
			//click any button to continue
			GAME_STATE = GAME_START;	
		}
	}
	public void keyTyped(KeyEvent e){}
	
	//customized drawstring function to print chat
	private void drawChat(Graphics g, LinkedList<String> text, int x, int y) {
	
		Point p = new Point(getWidth()/2-80,y);
		if(text != null)
        for (String line : text){
			String tokens[] = line.split("<delimiter>");
			int id = Integer.parseInt(tokens[0]);
			switch(id){
				case 1: g.setColor(new Color(0,0,255));break;
				case 2: g.setColor(new Color(0,255,0));break;
				case 3: g.setColor(new Color(255,0,0));break;
				case 4: g.setColor(new Color(150,150,150));break;
				case 5: g.setColor(new Color(0,0,0));break;
				case 6: g.setColor(new Color(255,150,100));break;
			}
			String name = handler.getPlayer(id).getName()+": ";
			String message = tokens[1];
			
			//gets width of the player name
			int width = g.getFontMetrics().stringWidth( name  ); 
			//prints player name with color
			g.drawString(name, x, y);
			
			//prints message
			g.setColor(new Color(255,255,255));
			g.drawString(message, p.x+width, y);
			g.drawString("", x, y += g.getFontMetrics().getHeight());
		
		
		}
	}
	
	//customized drawstring function to print notif
	private void drawNotif(Graphics g, LinkedList<String> text, int x, int y) {
	
		if(text != null)
        for (String line : text){
			String tokens[] = line.split("<delimiter>");
			int id = Integer.parseInt(tokens[0]);
			
			//printing background
			g.setColor(new Color(0,0,0,100));
			g.fillRect(630,y-20, 160,40);
			
			//printing user color
			switch(id){
				case 1: g.setColor(new Color(0,0,255));break;
				case 2: g.setColor(new Color(0,255,0));break;
				case 3: g.setColor(new Color(255,0,0));break;
				case 4: g.setColor(new Color(150,150,150));break;
				case 5: g.setColor(new Color(0,0,0));break;
				case 6: g.setColor(new Color(255,150,100));break;
			}
			
			String name = handler.getPlayer(id).getName()+" ";
			//System.out.println(line);
			String message = tokens[1];
			
			g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
			//gets width of the player name
			int width = g.getFontMetrics().stringWidth( name  ); 
			//prints player name with color
			g.drawString(name, x, y);
			
			//prints message
			g.setColor(new Color(255,255,255));
			g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
			g.drawString(message, x, y+= g.getFontMetrics().getHeight());
			g.drawString("", x, y += 24);
		
		
		}
	}
	
	//function for printing tabulated scores]
	public void showTabulatedScore(Graphics g){
		g.setColor(new Color(0,0,0,230));
		int index = 0;
		//printing players
		LinkedList<Player> playerList = handler.getPlayerList();
		g.fillRect(10,10, 300,90+(50*(playerList.size()-1)));
		g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
		g.setColor(new Color(255,255,255));
		g.drawString("Kills", 160, 50);
		g.drawString("Deaths", 220, 50);
		
		String scores = handler.getScores();
		
		String playerScore[] = scores.split(":");
		
		//loop for every player
		for(String player:playerScore){
			//split data in a player
			String data[] = player.split("<delimiter>");
			//printing the picture facing down
			g.drawImage(Toolkit.getDefaultToolkit().getImage("images/icon/"+data[0]+".png"), 30,60+index,30,30,this);	
			//for name
			g.drawString(handler.getPlayer(Integer.parseInt(data[0])).getName(), 70, 85+index);
			//for kills
			g.drawString(data[1], 170, 85+index);
			//for deaths
			g.drawString(data[2], 240, 85+index);
			index+=50;
		}
	}
	
	//clearing conversation
	class clearChat extends TimerTask{
		public void run() {
			try{
				conversation.removeFirst();
				chatIndex += 21;
			}catch(Exception e){}
		}
	}
	
	//clearing notification
	class clearNotif extends TimerTask{
		public void run() {
			try{
				notification.removeFirst();
				notifIndex += 21;
			}catch(Exception e){}
		}
	}
	
	public void resetMap(){
	
		int[][] newMap = {
			{8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,7,7,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,8,8,7,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,8,8,7,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,0,0,8,8,8,0,0,0,0,0,0,0,0,0,0,0,8,8,8,7,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,0,0,8,8,8,0,0,0,0,0,0,0,0,0,0,0,8,8,8,7,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,0,0,8,8,8,0,0,8,8,8,0,8,8,8,0,8,8,8,8,7,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,0,0,7,7,7,0,0,7,7,7,0,7,7,7,0,7,7,7,0,0,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,0,0,8,8,8,0,0,8,8,8,0,8,8,8,0,8,8,8,0,0,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,0,0,8,8,8,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,0,0,8,8,8,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,0,0,8,8,8,0,0,8,8,8,0,8,8,8,0,8,8,8,0,0,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,7,7,7,0,0,7,7,7,0,0,7,7,7,0,7,7,7,0,7,7,7,0,0,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,7,8,8,0,0,8,8,8,0,0,8,8,8,0,8,8,8,0,8,8,8,0,0,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,7,8,8,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,7,8,8,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,7,8,8,0,0,8,8,8,0,0,8,8,8,0,8,0,8,0,8,8,8,0,0,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,7,7,7,0,0,7,7,7,0,0,7,7,7,0,7,0,7,0,7,7,7,0,0,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,0,0,8,8,8,0,0,8,8,8,0,8,0,8,0,8,8,8,0,0,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,0,0,0,0,0,0,0,8,8,8,0,0,0,0,0,0,0,0,0,0,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,0,0,0,0,0,0,0,8,8,8,0,0,0,0,0,0,0,0,0,0,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,0,0,8,8,8,0,0,8,8,8,0,8,8,8,0,8,8,8,0,0,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,0,0,7,7,7,0,0,7,7,7,0,7,7,7,0,7,7,7,0,0,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,0,0,8,8,8,0,0,8,8,8,0,8,8,8,0,8,8,8,0,0,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,0,0,8,7,8,0,0,8,7,8,7,8,7,8,7,8,7,8,0,0,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,0,0,8,7,8,0,0,8,7,8,7,8,7,8,7,8,7,8,0,0,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,0,0,8,7,8,0,0,8,7,8,7,8,7,8,7,8,7,8,0,0,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8}
				};
		
		map = newMap;
	}

}
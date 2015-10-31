import java.net.InetAddress;
import java.awt.Graphics;
import java.util.LinkedList;
import java.awt.Color;

public class Player implements Comparable<Player>{

	private InetAddress address;
	private int port;
	private String name;
	private int id;
	private int x;
	private int y;
	private int hp;
	private int width;
	private int height;
	private int mapWidth;
	private int mapHeight;
	private int kills = 0;
	private int deaths = 0;
	
	/**
	 * The view of the player
	 * 1 - down
	 * 2 - top
	 * 3 - left
	 * 4 - right
	 */
	private int view;
	private int isConnected;
	private int inGame;
	
	//for full
	public Player(InetAddress address, int port){
		this.address = address;
		this.port = port;
	}
	
	//for server constructor
	public Player(int id, String name, int x, int y, int width, int height, int mapWidth, int mapHeight,InetAddress address, int port){
		this.id = id;
		this.name = name;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.mapWidth = mapWidth;
		this.mapHeight = mapHeight;
		this.address = address;
		this.port = port;
		setView((int)((100*Math.random())%4)+1);
		isConnected = 1;
		inGame = 1;
	}
	
	//for client constructor
	public Player(int id, String name, int x, int y, int width, int height, int mapWidth, int mapHeight, int orientation){
		this.id = id;
		this.name = name;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.mapWidth = mapWidth;
		this.mapHeight = mapHeight;
		setView(orientation);
		isConnected = 1;
		inGame = 1;
	}

	public InetAddress getAddress(){
		return address;
	}

	public int getPort(){
		return port;
	}

	public int getId(){
		return id;
	}

	public String getName(){
		return name;
	}
	
	public int getX(){
		return x;
	}

	public int getY(){
		return y;
	}

	public void setId(int id){
		this.id = id;
	}
	
	public void setX(int x){
		this.x = x;
	}

	public void setY(int y){
		this.y = y;
	}

	public int getHp(){
		return hp;
	}

	public int isConnected(){
		return isConnected;
	}
	
	public int inGame(){
		return inGame;
	}

	public void connect(){
		this.isConnected = 1;
	}

	public void disconnect(){
		this.isConnected = 2;
	}
	
	public void connectIg(){
		this.inGame = 1;
	}
	
	public void disconnectIg(){
		this.inGame = 2;
	}
	
	public void setHp(int hp){
		this.hp = hp;
	}

	public void moveUp(){
		
		if(getView() != 2)
			setView(2);
		else
			this.x--;
	}

	public void moveDown(){
	
		if(getView() != 1)
			setView(1);
		else
			this.x++;
	}

	public void moveLeft(){
		
		if(getView() != 3)
			setView(3);
		else
			this.y--;
	}
	
	public void moveRight(){
		
		if(getView() != 4)
			setView(4);
		else
			this.y++;
	}

	public void setView(int view) {
		this.view = view;
	}

	public int getView() {
		return this.view;
	}

	public int getWidth() {
		return this.width;
	}
	
	public int getHeight() {
		return this.height;
	}
	
	public void getShot(int damage){
		this.hp -= damage;
	}

	public String toString(){
		String retval="";
		retval+=id+"<delimiter>";
		retval+=name+"<delimiter>";
		retval+=x+ "<delimiter>";
		retval+=y+"<delimiter>";
		retval+=view;
		
		return retval;
	}	
	
	public int getKills(){
		return kills;
	}
	
	public void hasKilled(){
		kills++;
	}
	
	public int getDeaths(){
		return deaths;
	}
	
	public void hasDied(){
		deaths++;
	}
	
	public void tick(LinkedList<Player> player){
	
	}
	
	public void render(Graphics g){
	}
	
	
	//used for sorting in handler class
	public int compareTo(Player p) {
		int comparedSize = p.id;
		if (this.id > comparedSize) {
			return 1;
		} else if (this.id == comparedSize) {
			return 0;
		} else {
			return -1;
		}
	}

}

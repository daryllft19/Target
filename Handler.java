import java.util.LinkedList;
import java.awt.Graphics;
import java.util.Collections;

public class Handler{
	
	private LinkedList<Player> playerList = new LinkedList<Player>();
	private Player player;
	int[][] map={	//30x36 map
			{8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,8,8,0,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,8,8,0,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,0,0,8,8,8,0,0,0,0,0,0,0,0,0,0,0,8,8,8,0,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,0,0,8,8,8,0,0,0,0,0,0,0,0,0,0,0,8,8,8,0,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,0,0,8,8,8,0,0,8,8,8,0,8,8,8,0,8,8,8,8,0,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,0,0,8,8,8,0,0,8,8,8,0,8,8,8,0,8,8,8,0,0,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,0,0,8,8,8,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,0,0,8,8,8,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,0,0,8,8,8,0,0,8,8,8,0,8,8,8,0,8,8,8,0,0,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,0,8,8,0,0,8,8,8,0,0,8,8,8,0,8,8,8,0,8,8,8,0,0,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,0,8,8,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,0,8,8,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,0,8,8,0,0,8,8,8,0,0,8,8,8,0,8,0,8,0,8,8,8,0,0,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,0,0,8,8,8,0,0,8,8,8,0,8,0,8,0,8,8,8,0,0,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,0,0,0,0,0,0,0,8,8,8,0,0,0,0,0,0,0,0,0,0,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,0,0,0,0,0,0,0,8,8,8,0,0,0,0,0,0,0,0,0,0,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,0,0,8,8,8,0,0,8,8,8,0,8,8,8,0,8,8,8,0,0,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,0,0,8,8,8,0,0,8,8,8,0,8,8,8,0,8,8,8,0,0,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,0,0,8,0,8,0,0,8,0,8,0,8,0,8,0,8,0,8,0,0,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,0,0,8,0,8,0,0,8,0,8,0,8,0,8,0,8,0,8,0,0,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,0,0,8,0,8,0,0,8,0,8,0,8,0,8,0,8,0,8,0,0,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8}
		};//game board
				
	public void tick(){
	
		for(int i = 0; i < playerList.size(); i++){
			player = playerList.get(i);
			player.tick(playerList);
		}
			
	}
	
	public void render(Graphics g){
	
		for(int i = 0; i < playerList.size(); i++){
			player = playerList.get(i);
			player.render(g);
		}
		
	}
	
	public int getObject(int x, int y){
		return map[x][y];
	}
	
	public void addPlayer(Player player){
		playerList.add(player);
		Collections.sort(playerList);
	}
	
	public void removePlayer(Player player){
		int startIndex = player.getId();
		map[player.getX()][player.getY()] = 0;
		playerList.remove(player);
		
		Collections.sort(playerList);
		for(int x = startIndex; x < playerList.size()-1; x++){
			getPlayer(x).setId(getPlayer(x+1).getId());
		}
	}
	
	public String getScores(){
		String scores="";
		
			player = playerList.get(0);
			scores += player.getId()+"<delimiter>";
			scores += +player.getKills()+"<delimiter>";
			scores += player.getDeaths();
		
		for(int i = 1; i < playerList.size(); i++){
			player = playerList.get(i);
			scores += ":"+player.getId()+"<delimiter>";
			scores += +player.getKills()+"<delimiter>";
			scores += player.getDeaths();
			
		}
		return scores;
	}
	
	public boolean isOnList(int id){
		for(int i = 0; i < playerList.size(); i++){
			player = playerList.get(i);
			if(player.getId() == id){
				return true;
			}
		}
	
		return false;
	}
	
	public Player getPlayer(int id){
		for(int i = 0; i < playerList.size(); i++){
			player = playerList.get(i);
			if(player.getId() == id){
				return player;
			}
		}
	
		return null;
	}
	
	public void positionPlayer(int id, int x, int y, int orientation){
		for(int i = 0; i < playerList.size(); i++){
			player = playerList.get(i);
			if(player.getId() == id){
				map[player.getX()][player.getY()] = resetMap()[player.getX()][player.getY()];
				player.setX(x);
				player.setY(y);
				player.setView(orientation);
				map[x][y] = id;
				break;
			}
		}
	}
	
	public LinkedList<Player> getPlayerList(){
		return playerList;
	}
	
	public String getGameMap(){
		String retval = "";
		
		for(int i = 0; i < playerList.size(); i++){
			player = playerList.get(i);
			retval+= ":"+player.toString();
		}
		return retval;
	}
	
	public int[][] resetMap(){
	
		int[][] baseMap = {
			{8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,8,8,0,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,8,8,0,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,0,0,8,8,8,0,0,0,0,0,0,0,0,0,0,0,8,8,8,0,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,0,0,8,8,8,0,0,0,0,0,0,0,0,0,0,0,8,8,8,0,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,0,0,8,8,8,0,0,8,8,8,0,8,8,8,0,8,8,8,8,0,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,0,0,8,8,8,0,0,8,8,8,0,8,8,8,0,8,8,8,0,0,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,0,0,8,8,8,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,0,0,8,8,8,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,0,0,8,8,8,0,0,8,8,8,0,8,8,8,0,8,8,8,0,0,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,0,8,8,0,0,8,8,8,0,0,8,8,8,0,8,8,8,0,8,8,8,0,0,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,0,8,8,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,0,8,8,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,0,8,8,0,0,8,8,8,0,0,8,8,8,0,8,0,8,0,8,8,8,0,0,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,0,0,8,8,8,0,0,8,8,8,0,8,0,8,0,8,8,8,0,0,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,0,0,0,0,0,0,0,8,8,8,0,0,0,0,0,0,0,0,0,0,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,0,0,0,0,0,0,0,8,8,8,0,0,0,0,0,0,0,0,0,0,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,0,0,8,8,8,0,0,8,8,8,0,8,8,8,0,8,8,8,0,0,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,0,0,8,8,8,0,0,8,8,8,0,8,8,8,0,8,8,8,0,0,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,0,0,8,0,8,0,0,8,0,8,0,8,0,8,0,8,0,8,0,0,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,0,0,8,0,8,0,0,8,0,8,0,8,0,8,0,8,0,8,0,0,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,0,0,8,0,8,0,0,8,0,8,0,8,0,8,0,8,0,8,0,0,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8},
			{8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8}
				};
		
		return baseMap;
	}
	
}
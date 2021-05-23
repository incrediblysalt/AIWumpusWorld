import java.util.HashMap;

public class BlindNode {
	
	private static int numNodes = 0;
	
	private GameTile[][] maze;
	private int posX;
	private int posY;
	
	private BlindNode next;
	private int numActions = 0;
	private String action = "";
	
	private String mazeStr = "7";
	private static HashMap<String, Boolean> explored = new HashMap<String, Boolean>();
	private static boolean victory = false;
	private static boolean gold = true;
	//private static String[][] baseMaze;
	//private String baseStr = "7";
	
	// Getter's
	public int getNumNodes() {return numNodes;}
	
	public GameTile[][] getMaze() {return maze;}
	
	public int getX() {return posX;}
	
	public int getY() {return posY;}
	
	public BlindNode getNext() {return next;}
	
	public int getNumActions() {return numActions;}
	
	public String getAction() {return action;}
	
	public static boolean getVict() {return victory;}
	
	public static boolean getGold() {return gold;}
	
	//public static String[][] getBMaze() {return baseMaze;}
	
	// Setter's
	public void setX(int x) {posX = x;}
	
	public void setY(int y) {posY = y;}
	
	public void setNext(BlindNode next) {this.next = next;}
	
	public void setAction(String action) {this.action += action;}
	
	public void setMazeString(String mazeStr) {this.mazeStr = mazeStr;}
	
	public static void setVict(boolean bool) {victory = bool;}
	
	public static void setGold(boolean bool) {gold = bool;}
	
	static public void addToHash(String hash) {explored.put(hash, true);}
	
	//static public void changeBMaze(int x, int y, String str) {baseMaze[x][y] = str;}
	
	static public boolean inHash(String hash) {return explored.containsKey(hash);}
	
	public BlindNode(GameTile[][] tileMaze, int x, int y, int actions, String action) {
		
		numNodes++;
		maze = new GameTile[tileMaze.length][tileMaze[0].length];
		
		for(int i=0; i<tileMaze.length; i++) {
			for(int j=0; j<tileMaze[0].length; j++) {
				if(tileMaze[i][j] == null) {
					maze[i][j] = null;
				}
				else {
					maze[i][j] = new GameTile(tileMaze[i][j]);
				}
			}
		}
		
		/*
		baseMaze = new String[tileMaze.length][tileMaze[0].length];
		
		for(int i=0; i<tileMaze.length; i++) {
			for(int j=0; j<tileMaze[0].length; j++) {
				if(tileMaze[i][j] == null) {
					baseMaze[i][j] = "*";
				} 
				else if(tileMaze[i][j].isWall()) {
					baseMaze[i][j] = "w";
				}
				else {
					baseMaze[i][j] = "_";
				}
			}
		}
		*/
		posX = x;
		posY = y;
		
		//baseMaze[x][y] = "P";
		
		numActions = actions+1;
		this.action = action;
	}
	
	@Override
	public String toString() {
		if(mazeStr.equals("7")) {
			mazeStr = posX + " " + posY + " ";
			for(int i=0; i<maze.length; i++) {
				for(int j=0; j<maze[0].length; j++) {
					mazeStr = mazeStr + maze[i][j];
				}
			}
			
			return mazeStr;
		}
		else {
			return mazeStr;
		}
	}
	
	/*
	public String stringMap() {
		if(baseStr.equals("7")) {
			baseStr = posX + " " + posY + " ";
			for(int i=0; i<baseMaze.length; i++) {
				for(int j=0; j<baseMaze[0].length; j++) {
					baseStr = baseStr + baseMaze[i][j];
				}
			}
			
			return baseStr;
		}
		else {
			return baseStr;
		}
	}
	*/
	
	public BlindNode[] genChildren() {
		BlindNode[] w = new BlindNode[] {null, null, null, null, null};
		// NODES: pickup, down, left, up, right
		// DO NOT generate if tile has wumpus or pit
		
		// Things are a little... different now.
		// When a square has not been discovered, it is NULL
		// Therefore, it's hard to checkNode on a node which is null
		// So, here's the plan:
		// 1. Check if we're on gold. If on gold, pick up and we're good.
		// 2. Check if we're on a Breeze or a Stink
		//	- So, you're on a bad square. Check the squares around you (up down left right)
		//	- If they're all nulls/walls, don't generate any children
		//	- If there's a not null/wall square, check for wumpus/pit
		//	- No wumpus or pit? Go there!
		// 3. If we're not on a stink/breeze, generate all children
		
		if(maze[posX][posY] == null) {
			return w;
		}
		
		if(maze[posX][posY].hasGlitter() && checkNode(maze[posX][posY])) {
			w[0] = new BlindNode(maze, posX, posY, numActions, action);
			w[0].setAction("pickup ");
			w[0].maze[posX][posY].setGlitter(false);
			
			return w;
		}
		
		if(maze[posX][posY].hasBreeze() || maze[posX][posY].hasStench()) {
			// Alright, we have a breeze/stench.
			// Check down left right up
			// Is null? Dangerous, don't go there.
			// Discovered? Does it have the wumpus/pit? No? you can go there
			
			// Check down: x + 1
			if(posX + 1 <= maze.length) {
				if(maze[posX+1][posY] != null) {
					if(checkNode(maze[posX+1][posY])) {
						w[1] = new BlindNode(maze, posX+1, posY, numActions, action);
						w[1].setAction("movedown ");
					}
				}
			}
			
			// Check left: y - 1
			if(posY - 1 >= 0) {
				if(maze[posX][posY-1] != null) {
					if(checkNode(maze[posX][posY-1])) {
						w[2] = new BlindNode(maze, posX, posY-1, numActions, action);
						w[2].setAction("moveleft ");
					}
				}
			}
			
			// Check right: y + 1
			if(posY + 1 <= maze.length) {
				if(maze[posX][posY+1] != null) {
					if(checkNode(maze[posX][posY+1])) {
						w[3] = new BlindNode(maze, posX, posY+1, numActions, action);
						w[3].setAction("moveright ");
					}
				}
			}
			
			// Check up: x - 1
			if(posX - 1 <= maze.length) {
				if(maze[posX-1][posY] != null) {
					if(checkNode(maze[posX-1][posY])) {
						w[4] = new BlindNode(maze, posX-1, posY, numActions, action);
						w[4].setAction("moveup ");
					}
				}
			}
		}
		else {
			// No stink or breeze; generate all nodes (if not a wall)
			// Move Down node: x + 1
			if(posX + 1 <= maze.length) {
				if(maze[posX+1][posY] == null) {
					// Null -> Undiscovered. Generate node
					w[1] = new BlindNode(maze, posX+1, posY, numActions, action);
					w[1].setAction("movedown ");
				}
				else if(checkNode(maze[posX+1][posY])) {
					// Node is not null, so check if it's a wall, has the wumpus, or a pit
					// None of those? Then make the node!
					w[1] = new BlindNode(maze, posX+1, posY, numActions, action);
					w[1].setAction("movedown ");
				}
			}
			
			// Move Left node: y - 1
			if(posY - 1 >= 0) {
				if(maze[posX][posY-1] == null) {
					w[2] = new BlindNode(maze, posX, posY-1, numActions, action);
					w[2].setAction("moveleft ");
				}
				else if(checkNode(maze[posX][posY-1])) {
					//System.out.println("MOVING LEFT TO: " + (posX-1) + " " + posY);
					w[2] = new BlindNode(maze, posX, posY-1, numActions, action);
					w[2].setAction("moveleft ");
				}
			}
			
			// Move Right node: y + 1
			if(posY + 1 <= maze[0].length) {
				if(maze[posX][posY+1] == null) {
					w[3] = new BlindNode(maze, posX, posY+1, numActions, action);
					w[3].setAction("moveright ");
				}
				else if(checkNode(maze[posX][posY+1])) {
					//System.out.println("MOVING RIGHT TO: " + (posX+1) + " " + posY);
					w[3] = new BlindNode(maze, posX, posY+1, numActions, action);
					w[3].setAction("moveright ");
				}
			}
			
			// Move Up node: x - 1
			if(posX - 1 >= 0) {
				if(maze[posX-1][posY] == null) {
					w[4] = new BlindNode(maze, posX-1, posY, numActions, action);
					w[4].setAction("moveup ");
				}
				else if(checkNode(maze[posX-1][posY])) {
					//System.out.println("MOVING UP TO: " + posX + " " + (posY-1));
					w[4] = new BlindNode(maze, posX-1, posY, numActions, action);
					w[4].setAction("moveup ");
				}
			}
		}
		
		return w;
	}
	
	boolean checkNode(GameTile tile) {
		if(!tile.isWall() && !tile.hasPit() && !tile.hasWumpus()) {
			return true;
		}
		else {
			return false;
		}
	}
	
	/*
	public WumpusNode[] genShots() {
		WumpusNode[] w = new WumpusNode[] {null, null, null, null, null};
		// NODES: pickup, down, left, up, right
		// DO NOT generate if tile has wumpus or pit
		
		if(maze[posX][posY].hasGlitter() && checkNode(maze[posX][posY])) {
			w[0] = new WumpusNode(maze, posX, posY, numActions, action);
			w[0].setAction("pickup ");
			w[0].setMazeString(w[0].goldString());
			w[0].maze[posX][posY].setGlitter(false);
			
			return w;
		}
		
		findWumpus();
		
		// Shoot Down node:
		if(shootDown(maze, posX, posY)) {
			w[1] = new WumpusNode(maze, posX, posY, numActions, action);
			w[1].setAction("shootdown ");
			w[1].maze[wumpusX][wumpusY].setWumpus(false);
		}
		
		// Shoot Left node:
		if(shootLeft(maze, posX, posY)) {
			w[2] = new WumpusNode(maze, posX, posY, numActions, action);
			w[2].setAction("shootleft ");
			w[2].maze[wumpusX][wumpusY].setWumpus(false);
		}
		
		// Shoot Up node:
		if(shootUp(maze, posX, posY)) {
			w[3] = new WumpusNode(maze, posX, posY, numActions, action);
			w[3].setAction("shootup ");
			w[3].maze[wumpusX][wumpusY].setWumpus(false);
		}
		
		// Shoot Right node:
		if(shootRight(maze, posX, posY)) {
			w[4] = new WumpusNode(maze, posX, posY, numActions, action);
			w[4].setAction("shootright ");
			w[4].maze[wumpusX][wumpusY].setWumpus(false);
		}
		
		return w;
	}
	
	boolean shootDown(GameTile[][] maze, int x, int y) {
		boolean wumpus = false;
		
		if(maze[x][y].hasPit() || maze[x][y].hasWumpus()) {
			return false;
		}
		
		for(int i=x; i<maze.length-1; i++) {
			if(maze[i][y].isWall()) {
				return false;
			}
			else if(maze[i][y].hasWumpus()) {
				wumpus = true;
			}
		}
		
		return wumpus;
	}
	
	boolean shootLeft(GameTile[][] maze, int x, int y) {
		boolean wumpus = false;
		
		if(maze[x][y].hasPit() || maze[x][y].hasWumpus()) {
			return false;
		}
		
		for(int i=y; i>0; i--) {
			if(maze[x][i].isWall()) {
				return false;
			}
			else if(maze[x][i].hasWumpus()) {
				wumpus = true;
			}
		}
		
		return wumpus;
	}

	boolean shootUp(GameTile[][] maze, int x, int y) {
		boolean wumpus = false;
		
		if(maze[x][y].hasPit() || maze[x][y].hasWumpus()) {
			return false;
		}
		
		for(int i=x; i>0; i--) {
			if(maze[i][y].isWall()) {
				return false;
			}
			else if(maze[i][y].hasWumpus()) {
				wumpus = true;
			}
		}
		
		return wumpus;
	}

	boolean shootRight(GameTile[][] maze, int x, int y) {
		boolean wumpus = false;
		
		if(maze[x][y].hasPit() || maze[x][y].hasWumpus()) {
			return false;
		}
		
		for(int i=y; i<maze[0].length-1; i++) {
			if(maze[x][i].isWall()) {
				return false;
			}
			else if(maze[x][i].hasWumpus()) {
				wumpus = true;
			}
		}
		
		return wumpus;
	}
	*/
	
	boolean victoryCheck() {
		if(posX == 4 && posY == 1) {
			victory = true;
			return true;
		}
		else {return false;}
	}
	
	public static int[] findPlayer(GameTile[][] maze) {
		int [] pos = new int [2];
		
		for(int i=0; i<maze.length; i++) {
			for(int j=0; j<maze[0].length; j++) {
				if(maze[i][j].hasPlayer()) {
					pos[0] = i;
					pos[1] = j;
					return pos;
				}
			}
		}
		
		System.out.println("Player Location Not Found: Default to 0,0");
		return new int[] {0,0};
	}
	
	public static void reset() {
		victory = false;
		gold = true;
		explored.clear();
	}
}
package compiler.lexer;

public class Position {
	private int x,y;
	
	public Position(int x,int y) {
		this.x = x;
		this.y = y;
	}
	
	public int getPosX() {
		return x;
	}
	
	public int getPosY() {
		return y;
	}
	
	public void print() {
		System.out.println("row:"+x+" col:"+y);
	}
}

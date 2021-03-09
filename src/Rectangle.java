import java.awt.Color;
import java.awt.Graphics;

/**
 * A rectangle-shaped Shape
 * Defined by an upper-left corner (x1,y1) and a lower-right corner (x2,y2)
 * with x1<=x2 and y1<=y2
 * 
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Fall 2012
 * @author CBK, updated Fall 2016
 */
public class Rectangle implements Shape {
	// TODO: YOUR CODE HERE
	private int x,y, height, width;
	private Color color;

	public Rectangle(int x1,int y1, Color color){
		x = x1;
		y = y1;
		this.color =color;
	}

	public Rectangle(int x1, int y1, int x2, int y2, Color color) {
		x = Math.min(x1, x2); y = Math.min(y1, y2);
		width = Math.abs(x1-x2);
		height = Math.abs(y1-y2);
	}

	public void setCorner(int x1, int y1, int x2, int y2){
		x = Math.min(x1, x2); y = Math.min(y1, y2);
		width = Math.abs(x1-x2);
		height = Math.abs(y1-y2);
	}

	@Override
	public void moveBy(int dx, int dy) {
		x+=dx;
		y+=dy;
	}

	@Override
	public Color getColor() {
		return color;
	}

	@Override
	public void setColor(Color color) {
		this.color = color;
	}
		
	@Override
	public boolean contains(int x, int y) {
		if(  (x > this.x && x < this.x + width) && (y > this.y && y < this.y +height) ) return true;
		else return false;
	}

	@Override
	public void draw(Graphics g) {
		g.setColor(color);
		g.fillRect(x,y,width, height);
	}

	public String toString() {
		return "rectangle " + x + " " + y + " " + width + " " + height + " " + color.getRGB();
 	}
}

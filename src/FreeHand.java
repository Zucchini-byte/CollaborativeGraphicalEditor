import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class FreeHand implements Shape{
    //list of segments; hold the tiny segments we will use for freeHand
    private List<Segment> segments;
    private Color color;

    //constructor with no params
    public FreeHand(){
        segments = new ArrayList<>();
    }

    //constructor but takes parameters of the x, y and color of the first segment
    public FreeHand(int x, int y, Color color){
        segments = new ArrayList<>();
        this.color = color;
        segments.add(new Segment(x,y, this.color));
    }

    //constructor that takes in a list of segments and the color
    public FreeHand(List<Segment> segments, Color color){
        this.segments = segments;
        this.color = color;

    }

    //takes in x,y and color and adds a new segment to the segment list
    public void freedraw(int x, int y, Color color){
        segments.add(new Segment(x, y, color));
    }

    //moves each segment by dx and dy
    @Override
    public void moveBy(int dx, int dy) {
        for(Segment s: segments){
            s.moveBy(dx,dy);
        }
    }


    //tells us if any of the segments in the list contain point x,y
    @Override
    public boolean contains(int x, int y) {
        for(Segment s: segments){
            if(s.contains(x,y)) return true;
        }

        return false;
    }

    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public void setColor(Color color) {
        for(Segment s: segments){
            s.setColor(color);
        }
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(color);
        ((Graphics2D)g).setStroke(new BasicStroke(5));
        for(Segment s: segments){
            s.draw(g);
        }
    }

    //returns a string to inform us of the current state of the segments in the segment list.
    public String toString(){
        String msg = "free-hand ";
        for(Segment s: segments){
            msg += s.toString() + " ";
        }

        return msg;
    }
}

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class FreeHand implements Shape{
    private List<Segment> segments;
    private Color color;

    public FreeHand(){
        segments = new ArrayList<>();
    }

    public FreeHand(int x, int y, Color color){
        segments = new ArrayList<>();
        this.color = color;
        segments.add(new Segment(x,y, this.color));
    }

    public FreeHand(List<Segment> segments, Color color){
        this.segments = segments;
        this.color = color;

    }

    public void freedraw(int x, int y, Color color){
        segments.add(new Segment(x, y, color));
    }

    @Override
    public void moveBy(int dx, int dy) {
        for(Segment s: segments){
            s.moveBy(dx,dy);
        }
    }

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

    public String toString(){
        String msg = "free-hand ";
        for(Segment s: segments){
            msg += s.toString() + " ";
        }

        return msg;
    }
}

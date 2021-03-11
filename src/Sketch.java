import java.awt.*;
import java.util.TreeMap;

public class Sketch {
    private TreeMap<Integer, Shape> sketchMap;
    private int maxId;

    public Sketch(){
        sketchMap = new TreeMap<>();
        maxId =0;
    }

    public synchronized void setMaxID(int maxId){
        this.maxId = maxId;
    }

    public synchronized TreeMap<Integer,Shape> getSketchMap(){
        return sketchMap;
    }

    // get the ID of a shape
    public synchronized Integer getID(Shape shape){
        for(Integer key: sketchMap.descendingKeySet()){
            if(sketchMap.get(key) == shape){
                return key;
            }
        }
        return -1;
    }


    // using point to get shape at
    public synchronized Shape getShapeAt(Point p){
        for(Integer key: sketchMap.descendingKeySet()){
            if(sketchMap.get(key).contains(p.x, p.y)){
                return sketchMap.get(key);
            }
        }
        return null;
    }

    public synchronized void addShape(Shape newShape){ ;
        sketchMap.put(maxId,newShape);
        maxId++;
    }

    public synchronized void deleteShape(Integer id){
        sketchMap.remove(id);
    }

    public synchronized void changeColor(Integer id, Color color){
        sketchMap.get(id).setColor(color);
    }

    public synchronized Shape get(int key){
        return sketchMap.get(key);
    }

    public synchronized void moveShape(int key, int dx, int dy){
        sketchMap.get(key).moveBy(dx, dy);
    }

    public synchronized void recolorShape(int key, Color color){
        sketchMap.get(key).setColor(color);
    }


    public synchronized String toString(){
       String sketchString = "";
       for(int key: sketchMap.keySet()){
           sketchString += "add " +sketchMap.get(key).toString() + " " + key + "\n";
       }


       return sketchString;
    }




}

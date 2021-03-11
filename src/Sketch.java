import java.awt.*;
import java.util.TreeMap;

public class Sketch {
    private TreeMap<Integer, Shape> sketchMap;
    private int maxId;

    public Sketch(){
        sketchMap = new TreeMap<>();
        maxId = 0;
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

    public synchronized void updateShape(int key, Shape shape){
        sketchMap.put(key, shape);
    }

    public synchronized String toString(){
       String sketchString = "";
       for(int key: sketchMap.keySet()){
           sketchString += "add " + sketchMap.get(key).toString() + "\n";
       }


       return sketchString;
    }




}

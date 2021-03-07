import java.util.TreeMap;

public class Sketch {
    private TreeMap<Integer, Shape> sketchMap;

    public Sketch(){
        sketchMap = new TreeMap<>();
    }


    public void addShape(Integer uniqueId,Shape newShape){
        sketchMap.put(uniqueId,newShape);
    }

    public void moveShape(Integer id, int dx, int dy){
        sketchMap.get(id).moveBy(dx,dy);
    }

    public void deleteShape(Integer id){
        sketchMap.remove(id);
    }




}

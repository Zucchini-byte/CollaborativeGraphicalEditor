import java.awt.*;

public class Message {
    private String msg;
    private Sketch sketch;

    public Message(String msg, Sketch sketch) {
        this.msg = msg;
        this.sketch = sketch;
    }

    public synchronized void handleMsg() {
        String[] command = msg.split(" ");
        if (command[0].equals("delete")) {
            int key = Integer.parseInt(command[1]);
            sketch.deleteShape(key);
        }
        else if(msg.equals("")){

        }
        else {
            String type = command[1];
            int x1 = Integer.parseInt(command[2]);
            int y1 = Integer.parseInt(command[3]);
            int x2 = Integer.parseInt(command[4]);
            int y2 = Integer.parseInt(command[5]);
            Color color = new Color(Integer.parseInt(command[6]));

            Shape shape = null;

            if (type.equals("ellipse")) {
                shape = new Ellipse(x1, y1, x2, y2, color);
            } else if (type.equals("rectangle")) {
                shape = new Rectangle(x1, y1, x2, y2, color);
            } else if (type.equals("segment")) {
                shape = new Segment(x1, y1, x2, y2, color);
            }

            if (command[0].equals("add")) {
                sketch.addShape(shape);
            } else if (command[0].equals("update")) {
                int key = Integer.parseInt(command[7]);
                sketch.updateShape(key, shape);
            }
        }
    }
}

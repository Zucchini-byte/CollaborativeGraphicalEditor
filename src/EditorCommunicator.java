import java.awt.*;
import java.io.*;
import java.net.Socket;

/**
 * Handles communication to/from the server for the editor
 * 
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Fall 2012
 * @author Chris Bailey-Kellogg; overall structure substantially revised Winter 2014
 * @author Travis Peters, Dartmouth CS 10, Winter 2015; remove EditorCommunicatorStandalone (use echo server for testing)
 */
public class EditorCommunicator extends Thread {
	private PrintWriter out;		// to server
	private BufferedReader in;		// from server
	protected Editor editor;		// handling communication for

	/**
	 * Establishes connection and in/out pair
	 */
	public EditorCommunicator(String serverIP, Editor editor) {
		this.editor = editor;
		System.out.println("connecting to " + serverIP + "...");
		try {
			Socket sock = new Socket(serverIP, 4242);
			out = new PrintWriter(sock.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			System.out.println("...connected");
		}
		catch (IOException e) {
			System.err.println("couldn't connect");
			System.exit(-1);
		}
	}

	/**
	 * Sends message to the server
	 */
	public void send(String msg) {
		out.println(msg);
	}

	/**
	 * Keeps listening for and handling (your code) messages from the server
	 */
	public void run() {
		try {
			// Handle messages
			// TODO: YOUR CODE HERE
			String msg;
			while((msg=in.readLine()) != null){
				System.out.println(msg);
				handleMsg(msg);
				editor.repaint();
			}

		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			System.out.println("server hung up");
		}
	}

	public void handleMsg(String msg){
		System.out.println(msg);
		String[] command = msg.split(" ");
		if(command[0].equals("delete")){
			int key = Integer.parseInt(command[1]);
			editor.getSketch().deleteShape(key);
		}
		else if(msg.equals("")){

		}
		else{
			String type = command[1];
			int x1 = Integer.parseInt(command[2]);
			int y1 = Integer.parseInt(command[3]);
			int x2 = Integer.parseInt(command[4]);
			int y2 = Integer.parseInt(command[5]);
			Color color = new Color(Integer.parseInt(command[6]));
			Shape shape = null;

			if(type.equals("ellipse")){
				shape = new Ellipse(x1, y1, x2, y2, color);
			}
			else if(type.equals("rectangle")){
				shape = new Rectangle(x1, y1, x2, y2, color);
			}
			else if(type.equals("segment")){
				shape = new Segment(x1, y1, x2, y2, color);
			}

			if(command[0].equals("add")){
				editor.getSketch().addShape(shape);
			}
			else if(command[0].equals("update")){
				int key = Integer.parseInt(command[7]);
				editor.getSketch().updateShape(key, shape);
			}
		}




	}

	// Send editor requests to the server
	// TODO: YOUR CODE HERE

	public void addToSketch(Shape shape){
		send("add " + shape.toString());
	}

	public void updateSketch(int key, Shape shape){
		send("update " + shape.toString() + " " +key);
	}

	public void deleteShape(int key){
		send("delete " + key);
	}
	
}

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
				System.out.println("got " + msg);
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

		Message editorMsg = new Message(msg, editor.getSketch());
		editorMsg.handleMsg();


	}

	// Send editor requests to the server
	// TODO: YOUR CODE HERE


	public void addToSketch(Shape shape){
		send("add " + shape.toString() );
	}

	public void moveShape(int key, int dx, int dy){
		System.out.println(key);
		send("move " + key + " " +dx + " " + dy );
	}

	public void recolorShape(int key, Color color){
		send("recolor " + key + " " + color.getRGB());
	}

	public void deleteShape(int key){
		send("delete " + key);
	}
	
}

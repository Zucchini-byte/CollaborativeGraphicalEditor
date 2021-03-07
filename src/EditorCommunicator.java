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
				System.out.println("got msg " + msg);
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
		if(msg.equals("none")){

		}
		else{
			String[] msgA = msg.split(" ");
			String type =msgA[0];
			int x1 = Integer.parseInt(msgA[1]);
			int y1 = Integer.parseInt(msgA[1]);
			int x2 = Integer.parseInt(msgA[1]);
			int y2 = Integer.parseInt(msgA[1]);
			Color color = new Color(Integer.parseInt(msgA[5]));

			if(type.equals("ellipse")){
				
			}

		}


	}

	// Send editor requests to the server
	// TODO: YOUR CODE HERE

	public void updateShape(Shape shape){
		send(shape.toString());
	}

	public void deleteShape(){

	}
	
}

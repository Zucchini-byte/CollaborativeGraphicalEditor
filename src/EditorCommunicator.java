import org.opencv.core.Rect;

import java.awt.*;
import java.io.*;
import java.net.Socket;

/**
 * Handles communication to/from the server for the editor
 * 
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Fall 2012
 * @author Chris Bailey-Kellogg; overall structure substantially revised Winter 2014
 * @author Travis Peters, Dartmouth CS 10, Winter 2015; remove EditorCommunicatorStandalone (use echo server for testing)
 * Modified by Kashan Mahmood for PSET 6 - March 7,2021
 */
public class EditorCommunicator extends Thread {
	private PrintWriter out;        // to server
	private BufferedReader in;        // from server
	protected Editor editor;        // handling communication for

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
		} catch (IOException e) {
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

			String line;
			//while the message received isn't null
			while ((line = in.readLine()) != null) {

				//creates a new message object with the received message (line in this case) and the editor
				Message msg = new Message(line, editor);

				//the message is processed here. Whether that is adding, removing, moving, or recoloring something
				msg.process();

				//repaints the editor since changes were made here
				editor.repaint();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			System.out.println("server hung up");
		}
	}

}

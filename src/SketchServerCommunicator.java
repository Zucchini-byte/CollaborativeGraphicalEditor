import java.io.*;
import java.net.Socket;

/**
 * Handles communication between the server and one client, for SketchServer
 *
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Fall 2012; revised Winter 2014 to separate SketchServerCommunicator
 * Modified by Kashan Mahmood for PSET 6 - March 7,2021
 */
public class SketchServerCommunicator extends Thread {
	private Socket sock;					// to talk with client
	private BufferedReader in;				// from client
	private PrintWriter out;				// to client
	private SketchServer server;			// handling communication for

	public SketchServerCommunicator(Socket sock, SketchServer server) {
		this.sock = sock;
		this.server = server;
	}

	/**
	 * Sends a message to the client
	 * @param msg
	 */
	public void send(String msg) {
		out.println(msg);
	}
	
	/**
	 * Keeps listening for and handling (your code) messages from the client
	 */
	public void run() {
		try {
			System.out.println("someone connected");
			
			// Communication channel
			in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			out = new PrintWriter(sock.getOutputStream(), true);

			// Tell the client the current state of the world

			for(int id: server.getSketch().getShapeSketch().navigableKeySet()){
				//sends out a message to draw each of the shapes to the editor communicator.
				//needed especially if a new editor joins that wasn't there from the start.
				send("draw "+ server.getSketch().getShapeSketch().get(id));
				send("setID " + id);
			}

			// Keep getting and handling messages from the client

			String line;
			while((line=in.readLine())!= null){
				//for each request received, make it into a new message with the line and server as the parameters
				Message msg= new Message(line, server);
				System.out.println(line);
				//process this message and do what is asked
				msg.process();

				//broadcast the request to all editor communicator so they can update
				//their editor's local sketch
				server.broadcast(line);
			}


			// Clean up -- note that also remove self from server's list so it doesn't broadcast here
			server.removeCommunicator(this);
			out.close();
			in.close();
			sock.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}

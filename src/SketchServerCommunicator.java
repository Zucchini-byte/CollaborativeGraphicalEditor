import java.awt.*;
import java.io.*;
import java.net.Socket;

/**
 * Handles communication between the server and one client, for SketchServer
 *
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Fall 2012; revised Winter 2014 to separate SketchServerCommunicator
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
			// TODO: YOUR CODE HERE
			System.out.println(server.getSketch().toString());
			if(!server.getSketch().equals("")){
				server.broadcast(server.getSketch().toString());
			}

			// Keep getting and handling messages from the client
			// TODO: YOUR CODE HERE
			String msg;

			while( (msg = in.readLine()) != null){
				System.out.println("Got msg: " + msg);

				String[] command = msg.split(" ");
				if(command[0].equals("delete")){
					int key = Integer.parseInt(command[1]);
					server.getSketch().deleteShape(key);
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
						server.getSketch().addShape(shape);
					}
					else if(command[0].equals("update")){
						int key = Integer.parseInt(command[7]);
						server.getSketch().updateShape(key, shape);
					}
				}
				server.broadcast(msg);


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

import java.awt.*;
import java.awt.event.*;
import java.sql.SQLOutput;

import javax.swing.*;

/**
 * Client-server graphical editor
 * 
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Fall 2012; loosely based on CS 5 code by Tom Cormen
 * @author CBK, winter 2014, overall structure substantially revised
 * @author Travis Peters, Dartmouth CS 10, Winter 2015; remove EditorCommunicatorStandalone (use echo server for testing)
 * @author CBK, spring 2016 and Fall 2016, restructured Shape and some of the GUI
 * Modified by Kashan Mahmood for PSET 6 - March 7,2021
 */

public class Editor extends JFrame {	
	private static String serverIP = "localhost";			// IP address of sketch server
	// "localhost" for your own machine;
	// or ask a friend for their IP address

	private static final int width = 800, height = 800;		// canvas size

	// Current settings on GUI
	public enum Mode {
		DRAW, MOVE, RECOLOR, DELETE
	}
	private Mode mode = Mode.DRAW;				// drawing/moving/recoloring/deleting objects
	private String shapeType = "ellipse";		// type of object to add
	private Color color = Color.black;			// current drawing color

	// Drawing state
	// these are remnants of my implementation; take them as possible suggestions or ignore them
	private Shape curr = null;					// current shape (if any) being drawn
	private Sketch sketch;						// holds and handles all the completed objects
	private int movingId = -1;					// current shape id (if any; else -1) being moved
	private Point drawFrom = null;				// where the drawing started
	private Point moveFrom = null;				// where object is as it's being dragged


	// Communication
	private EditorCommunicator comm;			// communication with the sketch server

	public Editor() {
		super("Graphical Editor");

		sketch = new Sketch();

		// Connect to server
		comm = new EditorCommunicator(serverIP, this);
		comm.start();

		// Helpers to create the canvas and GUI (buttons, etc.)
		JComponent canvas = setupCanvas();
		JComponent gui = setupGUI();

		// Put the buttons and canvas together into the window
		Container cp = getContentPane();
		cp.setLayout(new BorderLayout());
		cp.add(canvas, BorderLayout.CENTER);
		cp.add(gui, BorderLayout.NORTH);

		// Usual initialization
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setVisible(true);
	}

	/**
	 * Creates a component to draw into
	 */
	private JComponent setupCanvas() {
		JComponent canvas = new JComponent() {
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				drawSketch(g);
			}
		};
		
		canvas.setPreferredSize(new Dimension(width, height));

		canvas.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent event) {
				handlePress(event.getPoint());
			}

			public void mouseReleased(MouseEvent event) {
				handleRelease();
			}
		});		

		canvas.addMouseMotionListener(new MouseAdapter() {
			public void mouseDragged(MouseEvent event) {
				handleDrag(event.getPoint());
			}
		});
		
		return canvas;
	}

	/**
	 * Creates a panel with all the buttons
	 */
	private JComponent setupGUI() {
		// Select type of shape
		String[] shapes = {"ellipse", "rectangle", "segment"};
		JComboBox<String> shapeB = new JComboBox<String>(shapes);
		shapeB.addActionListener(e -> shapeType = (String)((JComboBox<String>)e.getSource()).getSelectedItem());

		// Select drawing/recoloring color
		// Following Oracle example
		JButton chooseColorB = new JButton("choose color");
		JColorChooser colorChooser = new JColorChooser();
		JLabel colorL = new JLabel();
		colorL.setBackground(Color.black);
		colorL.setOpaque(true);
		colorL.setBorder(BorderFactory.createLineBorder(Color.black));
		colorL.setPreferredSize(new Dimension(25, 25));
		JDialog colorDialog = JColorChooser.createDialog(chooseColorB,
				"Pick a Color",
				true,  //modal
				colorChooser,
				e -> { color = colorChooser.getColor(); colorL.setBackground(color); },  // OK button
				null); // no CANCEL button handler
		chooseColorB.addActionListener(e -> colorDialog.setVisible(true));

		// Mode: draw, move, recolor, or delete
		JRadioButton drawB = new JRadioButton("draw");
		drawB.addActionListener(e -> mode = Mode.DRAW);
		drawB.setSelected(true);
		JRadioButton moveB = new JRadioButton("move");
		moveB.addActionListener(e -> mode = Mode.MOVE);
		JRadioButton recolorB = new JRadioButton("recolor");
		recolorB.addActionListener(e -> mode = Mode.RECOLOR);
		JRadioButton deleteB = new JRadioButton("delete");
		deleteB.addActionListener(e -> mode = Mode.DELETE);
		ButtonGroup modes = new ButtonGroup(); // make them act as radios -- only one selected
		modes.add(drawB);
		modes.add(moveB);
		modes.add(recolorB);
		modes.add(deleteB);
		JPanel modesP = new JPanel(new GridLayout(1, 0)); // group them on the GUI
		modesP.add(drawB);
		modesP.add(moveB);
		modesP.add(recolorB);
		modesP.add(deleteB);

		// Put all the stuff into a panel
		JComponent gui = new JPanel();
		gui.setLayout(new FlowLayout());
		gui.add(shapeB);
		gui.add(chooseColorB);
		gui.add(colorL);
		gui.add(modesP);
		return gui;
	}

	/**
	 * Getter for the sketch instance variable
	 */
	public Sketch getSketch() {
		return sketch;
	}

	/**
	 * Draws all the shapes in the sketch,
	 * along with the object currently being drawn in this editor (not yet part of the sketch)
	 */
	public void drawSketch(Graphics g) {

		// if the curr shape isn't null, draw it for this editor
		if(curr!= null) curr.draw(g);

		//calls the draw method in sketch to draw the shapes in this local sketch
		sketch.draw(g);
	}

	// Helpers for event handlers
	
	/**
	 * Helper method for press at point
	 * In drawing mode, start a new object;
	 * in moving mode, (request to) start dragging if clicked in a shape;
	 * in recoloring mode, (request to) change clicked shape's color
	 * in deleting mode, (request to) delete clicked shape
	 */
	private void handlePress(Point p) {

		//if the mode is draw
		if (mode== mode.DRAW){
			//sets the drawFrom to current point if its null so it can be used in handleDrag
			if(drawFrom==null){
				drawFrom= p;
			}
		}

		// In moving mode
		else if(mode== mode.MOVE){
			//checks the sketch and sees if there is a shape at point p, else it returns a -1
			if (sketch.topShapeIdAt(p)!= -1){

				//sets the moving id to the shape that's the newest at point p in the local sketch
				movingId= sketch.topShapeIdAt(p);
				//sets the moveFrom point to point p
				moveFrom=p;
			}
		}

		// In recoloring mode
		else if(mode==mode.RECOLOR){
			//checks the sketch and sees if there is a shape at point p, else it returns a -1
			if (sketch.topShapeIdAt(p)!= -1){

				//have the comm send a message to the server to recolor the newest shape at point p
				comm.send("recolor " + sketch.topShapeIdAt(p) + " "+ color.getRGB());
			}
		}
		// In delete mode
		else if(mode== mode.DELETE){
			//checks the sketch and sees if there is a shape at point p, else it returns a -1
			if (sketch.topShapeIdAt(p)!= -1){

				//have the comm send a message to the server to delete the newest shape at point p
				comm.send("delete " + sketch.topShapeIdAt(p));
			}
		}


	}

	/**
	 * Helper method for drag to new point
	 * In drawing mode, update the other corner of the object;
	 * in moving mode, (request to) drag the object
	 */
	private void handleDrag(Point p) {
		// if mode is draw
		if(mode==mode.DRAW){

			//depending on the shapeType, sets curr shape to a new shape with the respective parameters
			if (shapeType.equals("ellipse")) {
				curr = new Ellipse(drawFrom.x, drawFrom.y, p.x, p.y, color);
			}else if(shapeType.equals("rectangle")){
				curr = new Rectangle(drawFrom.x, drawFrom.y, p.x, p.y, color);
			}else if(shapeType.equals("segment")) {
				curr = new Segment(drawFrom.x, drawFrom.y, p.x, p.y, color);
			}
		}

		//if mode is move
		else if (mode== mode.MOVE){

			//if moveFrom isn't null
			if(moveFrom!= null){

				//have the comm send a message to the server to move the shape at movingId in the server
				comm.send("move "+ movingId + " "+ (p.x - moveFrom.x)+ " "+(p.y - moveFrom.y));
				moveFrom = p;
			}

		}

		//repaint
		repaint();
	}

	/**
	 * Helper method for release
	 * In drawing mode, pass the add new object request on to the server;
	 * in moving mode, release it		
	 */
	private void handleRelease() {

		//if the mode is draw
		if(mode==mode.DRAW) {

			//if the curr shape isn't null
			if (curr!= null) {

				//have the comm send a message to the server to draw the current shape for everyone
				comm.send("draw " + curr.toString());

				//since we are done with curr shape, we set it to null to be used again
				curr = null;
			}

			//drawFrom set to null since we are done using it
			drawFrom = null;
		}

		//if mode is move
		else if(mode==mode.MOVE){

			//when the mouse releases, moveFrom and movingId are reset
			moveFrom=null;
			movingId=-1;
		}

	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new Editor();
			}
		});	
	}
}

package image.blender.Main;

import image.blender.Manager.Input;
import image.blender.Manager.StateManager;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import javax.swing.*;

/**
 * Sets up the application. Mostly does scary swing stuff.
 */
public class Panel extends JPanel implements Runnable
{
	private static final long serialVersionUID = 1L;

	public static final int RESOLUTION_WIDTH = 1920; // Change this
	public static final int RESOLUTION_HEIGHT = 1080; // Change this

	public static final int WIDTH = 1920;
	public static final int HEIGHT = 1080;

	public static final int FPS = 30;

	private final int TARGET_TIME = 1000 / FPS;
	private Thread thread;
	private boolean running = false;

	private BufferedImage image;
	private Graphics2D g;
	private AffineTransform defaultForm;

	private StateManager sm;
	private Input input;

	/**
	 * Constructs panel.
	 */
	public Panel()
	{
		setPreferredSize(new Dimension(RESOLUTION_WIDTH, RESOLUTION_HEIGHT));
		setFocusable(true);
		requestFocus();

		running = true;
		image = new BufferedImage(RESOLUTION_WIDTH, RESOLUTION_HEIGHT, 1);
		g = (Graphics2D)image.getGraphics();
		g.scale((double)RESOLUTION_WIDTH / WIDTH, (double)RESOLUTION_HEIGHT / HEIGHT);
		g.clipRect(0, 0, Panel.WIDTH, Panel.HEIGHT);
		defaultForm = g.getTransform();
		sm = new StateManager();
		input = new Input();
	}

	/**
	 * Called when object is added to a container.
	 */
	@Override
	public void addNotify()
	{
		super.addNotify();
		if(thread == null)
		{
			addKeyListener(input);
			addMouseListener(input);
			addMouseMotionListener(input);
			thread = new Thread(this);
			thread.start();
		}
	}

	/**
	 * Main loop that aims for 30 FPS.
	 */
	@Override
	public void run()
	{
		long ticks = 0; // For counter
		long skips = 0; // For counter

		long start;
		long elapsed;
		long wait;

		while(running)
		{
			start = System.currentTimeMillis();

			update();
			render();
			draw();

			elapsed = System.currentTimeMillis() - start;

			wait = TARGET_TIME - elapsed;
			while(wait < 0)
			{
				wait += TARGET_TIME;
				skips++;
				if((ticks + skips) == FPS)
				{
					System.out.println("FPS: " + ticks);
					ticks = skips = 0;
				}
			}

			try
			{
				Thread.sleep(wait);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}

			ticks++;
			if((ticks + skips) == FPS)
			{
				System.out.println("FPS: " + ticks);
				ticks = skips = 0;
			}
		}
	}

	/**
	 * Updates based on current state and checks for inputs.
	 */
	private void update()
	{
		sm.update();
		input.update();
	}

	/**
	 * Draws based on current game state.
	 */
	private void render()
	{
		sm.render(g);
		g.setTransform(defaultForm);
	}

	/**
	 * Buffer strategy.
	 */
	private void draw()
	{
		Graphics g2 = getGraphics();
		g2.drawImage(image, 0, 0, RESOLUTION_WIDTH, RESOLUTION_HEIGHT, null);
		g2.dispose();
	}

	/**
	 * Creates the custom cursor to be used.
	 * 
	 * @param image The image for the cursor.
	 */
	public void setCursor(BufferedImage image)
	{
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Cursor c = toolkit.createCustomCursor(image, new Point(0, 0), "Cursor");
		setCursor(c);
	}
}
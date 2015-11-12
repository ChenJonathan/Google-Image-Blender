package image.blender.Manager;

import image.blender.State.BlendState;
import image.blender.State.MenuState;
import image.blender.State.State;
import image.blender.Manager.Data;

import java.awt.Graphics2D;

/**
 * Manages the state of the game. Game state determines what is currently being updated and rendered.
 */
public class StateManager
{
	private Data data;
	
	private State currentState;

	public static final int MENU = 0;
	public static final int BLEND = 1;

	/**
	 * Sets up the GameManager with the current game instance.
	 * 
	 * @param game The current game session.
	 */
	public StateManager()
	{
		data = new Data();

		setState(MENU);
	}

	/**
	 * Changes the game state and removes the previous state.
	 * 
	 * @param state The new game state.
	 */
	public void setState(int state)
	{
		currentState = null;
		Content.dispose();
		if(state == MENU)
		{
			currentState = new MenuState(this);
		}
		else if(state == BLEND)
		{
			currentState = new BlendState(this);
		}
	}

	/**
	 * Delegates game updates to current game state. Priority is given to "temporary" game states.
	 */
	public void update()
	{
		if(currentState != null)
		{
			currentState.update();
		}
	}

	/**
	 * Renders the current game state. Priority is given to "temporary" game states.
	 * 
	 * @param g The graphics to be rendered.
	 */
	public void render(Graphics2D g)
	{
		if(currentState != null)
		{
			currentState.render(g);
		}
	}

	/**
	 * Relays Data object to State objects.
	 * 
	 * @return The data to be relayed.
	 */
	public Data getData()
	{
		return data;
	}
}
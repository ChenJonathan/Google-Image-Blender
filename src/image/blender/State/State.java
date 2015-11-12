package image.blender.State;

import image.blender.Manager.StateManager;
import image.blender.Manager.Data;

import java.awt.Graphics2D;

/**
 * Template for game states.
 */
public abstract class State
{
	protected Data data;
	protected StateManager sm;

	/**
	 * Sets up the game state and passes in the game state manager, allowing for control of game data, input, and sound.
	 * 
	 * @param sm The game state manager.
	 */
	public State(StateManager sm)
	{
		this.sm = sm;
		data = sm.getData();
	}

	/**
	 * The running loop, called continuously to manages changes during the state.
	 */
	public abstract void update();

	/**
	 * Renders the graphics with each update to the state.
	 * 
	 * @param g The graphics to be rendered.
	 */
	public abstract void render(Graphics2D g);

	/**
	 * Manages the input with the help of the input manager.
	 */
	public abstract void handleInput();
}
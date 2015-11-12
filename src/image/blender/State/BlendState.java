package image.blender.State;

import image.blender.Manager.StateManager;

import java.awt.Graphics2D;

public class BlendState extends State
{
	public BlendState(StateManager sm)
	{
		super(sm);
	}

	@Override
	public void update()
	{
		handleInput();
	}

	@Override
	public void render(Graphics2D g)
	{
		
	}

	@Override
	public void handleInput()
	{
		
	}
}
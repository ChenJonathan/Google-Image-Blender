package image.blender.State;

import image.blender.Manager.Input;
import image.blender.Manager.StateManager;

import java.awt.Graphics2D;

import javax.swing.JOptionPane;

public class MenuState extends State
{
	public MenuState(StateManager sm)
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
		if(Input.mouseLeftRelease())
		{
			data.setSearch(JOptionPane.showInputDialog("Search:"));
			sm.setState(StateManager.BLEND);
		}
	}
}
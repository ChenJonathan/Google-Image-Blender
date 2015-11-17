package image.blender.State;

import image.blender.Main.Panel;
import image.blender.Manager.Content;
import image.blender.Manager.Input;
import image.blender.Manager.StateManager;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.util.ArrayList;

import javax.swing.JOptionPane;

public class MenuState extends State
{
	private int lineIndex;
	private int timer;

	private ArrayList<String> text = new ArrayList<String>();

	public static final int FADE_DURATION = 50;

	public MenuState(StateManager sm)
	{
		super(sm);

		text.add("The Google Image Blender is an electronic artifact that takes in a Google image search,");
		text.add("compiles the resulting image search results,");
		text.add("and blends them into a single image that represents the average result for the search.");
		text.add("");
		text.add("These images have a tendency to vary greatly in colors and textures.");
		text.add("");
		text.add("Try it for yourself!");
	}

	@Override
	public void update()
	{
		handleInput();

		if(lineIndex < text.size())
		{
			if(timer == FADE_DURATION)
			{
				if(lineIndex < text.size() - 1)
				{
					lineIndex++;
					timer = 0;
				}
			}
			else
			{
				timer++;
			}
		}
	}

	@Override
	public void render(Graphics2D g)
	{
		g.drawImage(Content.getImage(Content.MENU_BACKGROUND), 0, 0, null);
		g.setColor(Color.BLACK);
		g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 72));
		FontMetrics fontMetrics = g.getFontMetrics();
		String title = "Google Image Blender";
		g.drawString(title, Panel.WIDTH / 2 - fontMetrics.stringWidth(title) / 2, 200);
		g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 40));
		fontMetrics = g.getFontMetrics();
		for(int count = 0; count <= lineIndex; count++)
		{
			String line = text.get(count);
			if(count == lineIndex)
			{
				g.setColor(new Color(0, 0, 0, (float)timer / FADE_DURATION));
			}
			g.drawString(line, Panel.WIDTH / 2 - fontMetrics.stringWidth(line) / 2, 350 + count * 80);
		}
		if(lineIndex == text.size() - 1 && timer == FADE_DURATION)
		{
			if(Input.mouseInRect(760, 900, 400, 106))
			{
				g.drawImage(Content.getImage(Input.mouseLeftDown()? Content.GLOW_RECTANGLE_CLICK
						: Content.GLOW_RECTANGLE_HOVER), 730, 870, null);
			}
			g.drawImage(Content.getImage(Content.BUTTON_SEARCH), 760, 900, null);
		}
	}

	@Override
	public void handleInput()
	{
		if(Input.mouseLeftRelease())
		{
			if(lineIndex == text.size() - 1 && timer == FADE_DURATION && Input.mouseInRect(760, 900, 400, 106))
			{
				String query = JOptionPane.showInputDialog("Search:");
				if(query != null && !query.equals(""))
				{
					data.setSearch(query);
					sm.setState(StateManager.BLEND);
				}
			}
			else if(lineIndex < text.size() - 1)
			{
				lineIndex++;
				while(text.get(lineIndex).equals(""))
				{
					lineIndex++;
				}
				timer = 0;
			}
			else
			{
				timer = FADE_DURATION;
			}
		}
	}
}
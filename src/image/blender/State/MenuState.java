package image.blender.State;

import image.blender.Main.Panel;
import image.blender.Manager.Content;
import image.blender.Manager.Input;
import image.blender.Manager.StateManager;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.util.ArrayList;

import javax.swing.JOptionPane;

public class MenuState extends State
{
	private int lineIndex = -1;
	private int timer;

	private ArrayList<String> text = new ArrayList<String>();

	public static final int FADE_DURATION = 30;

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
		if(lineIndex <= text.size())
		{
			if(timer == FADE_DURATION)
			{
				if(lineIndex < text.size())
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

		handleInput();
	}

	@Override
	public void render(Graphics2D g)
	{
		// Drawing the background
		g.drawImage(Content.getImage(Content.MENU_BACKGROUND), 0, 0, null);

		// Drawing the title
		g.setColor(Color.BLACK);
		g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 72));
		FontMetrics fontMetrics = g.getFontMetrics();
		String title = "Google Image Blender";
		if(lineIndex == -1)
		{
			AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float)timer / FADE_DURATION);
			Graphics2D temp = (Graphics2D)g.create();
			temp.setComposite(ac);
			temp.drawString(title, Panel.WIDTH / 2 - fontMetrics.stringWidth(title) / 2, 200);
		}
		else
		{
			g.drawString(title, Panel.WIDTH / 2 - fontMetrics.stringWidth(title) / 2, 200);
		}

		// Drawing description text
		g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 40));
		fontMetrics = g.getFontMetrics();
		for(int count = 0; count <= Math.min(lineIndex, text.size() - 1); count++)
		{
			String line = text.get(count);
			if(count == lineIndex)
			{
				g.setColor(new Color(0, 0, 0, (float)timer / FADE_DURATION));
			}
			g.drawString(line, Panel.WIDTH / 2 - fontMetrics.stringWidth(line) / 2, 350 + count * 80);
		}

		// Drawing buttons
		if(lineIndex == text.size())
		{
			AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float)timer / FADE_DURATION);
			Graphics2D temp = (Graphics2D)g.create();
			temp.setComposite(ac);
			if(Input.mouseInRect(552, 900, 358, 106))
			{
				temp.drawImage(Content.getImage(Input.mouseLeftDown()? Content.GLOW_RECTANGLE_CLICK
						: Content.GLOW_RECTANGLE_HOVER), 522, 870, null);
			}
			if(Input.mouseInRect(1010, 900, 358, 106))
			{
				temp.drawImage(Content.getImage(Input.mouseLeftDown()? Content.GLOW_RECTANGLE_CLICK
						: Content.GLOW_RECTANGLE_HOVER), 980, 870, null);
			}
			temp.drawImage(Content.getImage(Content.BUTTON_SEARCH), 552, 900, null);
			temp.drawImage(Content.getImage(Content.BUTTON_QUIT), 1010, 900, null);
		}
	}

	@Override
	public void handleInput()
	{
		if(Input.mouseLeftRelease())
		{
			if(lineIndex == text.size())
			{
				if(Input.mouseInRect(552, 900, 358, 106))
				{
					String query = JOptionPane.showInputDialog("Search:");
					if(query != null && !query.equals(""))
					{
						data.setSearch(query);
						sm.setState(StateManager.BLEND);
					}
				}
				else if(Input.mouseInRect(1010, 900, 358, 106))
				{
					System.exit(0);
				}
			}
			else
			{
				lineIndex = text.size();
				timer = FADE_DURATION;
			}
		}
	}
}
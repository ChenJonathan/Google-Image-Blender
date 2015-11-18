package image.blender.State;

import image.blender.Main.Panel;
import image.blender.Manager.Content;
import image.blender.Manager.Input;
import image.blender.Manager.StateManager;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;

public class BlendState extends State
{
	private int timer = 0;
	private int waitTime = WAIT_TIME * NUM_SPEEDS;
	private boolean paused = false;

	private int index = 0;
	private BufferedImage composite;
	private ArrayList<BufferedImage> images = new ArrayList<BufferedImage>();
	private ArrayList<String> links = new ArrayList<String>();

	private boolean loading = true;
	private ArrayList<String> loadStatus = new ArrayList<String>();
	private RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(LOAD_TIME)
			.setConnectTimeout(LOAD_TIME).setSocketTimeout(LOAD_TIME).build();

	// TODO Increase margins
	public static final int IMAGE_WIDTH = 1000;
	public static final int IMAGE_HEIGHT = 1000;
	public static final int LOAD_TIME = 250; // In milliseconds
	public static final int WAIT_TIME = 10; // In frames - At max speed setting
	public static final int NUM_SPEEDS = 3;
	public static final int NUM_PAGES = 2;

	public BlendState(StateManager sm)
	{
		super(sm);

		try
		{
			for(int page = 0; page < NUM_PAGES; page++)
			{
				// String key = "AIzaSyDTW8BEDD4JRbTtyWqzQkIg5Wnd8pUUMP8";
				// String id = "002593508493133637657:0vhkg_zxi2w";
				String key = "AIzaSyAAAyt4P5JXEknZ2zqFHANY0PWiH2rxzP0";
				String id = "000143486869577366964:oymz9n45neu";
				String query = data.getSearch().replace(" ", "%20");
				URL url = new URL("https://www.googleapis.com/customsearch/v1?key=" + key + "&cx=" + id + "&q=" + query
						+ "&searchType=image&fileType=jpg&alt=json&safe=medium&start=" + (page * 10 + 1));
				HttpURLConnection conn = (HttpURLConnection)url.openConnection();
				conn.setRequestMethod("GET");
				conn.setRequestProperty("Accept", "application/json");
				BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

				String output;
				while((output = br.readLine()) != null)
				{
					if(output.contains("\"link\": \""))
					{
						String link = output.substring(output.indexOf("\"link\": \"") + ("\"link\": \"").length(),
								output.indexOf("\","));
						System.out.println(link); // Will print the google search links
						links.add(link);
					}
				}
				conn.disconnect();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void update()
	{
		if(loading)
		{
			// Loading the image
			BufferedImage image = null;
			try
			{
				HttpGet get = new HttpGet(links.get(index));
				get.setConfig(requestConfig);
				HttpResponse response = HttpClients.createDefault().execute(get);
				image = ImageIO.read(response.getEntity().getContent());

				// Scaling and cropping the image
				int xOffset = (image.getWidth() > image.getHeight())? (image.getWidth() - image.getHeight()) / 2 : 0;
				int yOffset = (image.getWidth() < image.getHeight())? (image.getHeight() - image.getWidth()) / 2 : 0;
				BufferedImage resizedImage = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);
				Graphics2D g = resizedImage.createGraphics();
				g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
				g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g.drawImage(image, 0, 0, IMAGE_WIDTH, IMAGE_HEIGHT, xOffset, yOffset, image.getWidth() - xOffset,
						image.getHeight() - yOffset, null);
				g.dispose();
				image = resizedImage;
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}

			if(image != null)
			{
				images.add(image);
				loadStatus.add("Successfully loaded image #" + (index + 1));
			}
			else
			{
				loadStatus.add("Failed to load image #" + (index + 1));
			}
			if(loadStatus.size() == 11)
			{
				loadStatus.remove(0);
			}

			index++;
			if(index == links.size())
			{
				index = 0;
				loading = false;
				composite = images.get(0);
			}
		}
		else if(!paused)
		{
			timer++;
			if(timer % waitTime == 0)
			{
				if(index < images.size() - 1)
				{
					index++;

					// Blending the image
					for(int y = 0; y < IMAGE_HEIGHT; y++)
					{
						for(int x = 0; x < IMAGE_WIDTH; x++)
						{
							int oldPixel = composite.getRGB(x, y);
							int oldRed = (oldPixel >> 16) & 0xFF;
							int oldGreen = (oldPixel >> 8) & 0xFF;
							int oldBlue = oldPixel & 0xFF;

							int newPixel = images.get(index).getRGB(x, y);
							int newRed = (newPixel >> 16) & 0xFF;
							int newGreen = (newPixel >> 8) & 0xFF;
							int newBlue = newPixel & 0xFF;

							int blendedPixel = (index * oldRed + newRed) / (index + 1);
							blendedPixel = (blendedPixel << 8) + (index * oldGreen + newGreen) / (index + 1);
							blendedPixel = (blendedPixel << 8) + (index * oldBlue + newBlue) / (index + 1);

							composite.setRGB(x, y, blendedPixel);
						}
					}
				}
			}
		}

		handleInput();

	}

	@Override
	public void render(Graphics2D g)
	{
		g.drawImage(Content.getImage(Content.MENU_BACKGROUND), 0, 0, null);
		if(loading)
		{
			g.setColor(Color.WHITE);
			g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 36));
			FontMetrics fontMetrics = g.getFontMetrics();
			String text = "Attempting to load " + links.size() + " images. Please wait!";
			g.drawString(text, Panel.WIDTH / 2 - fontMetrics.stringWidth(text) / 2, 200);

			g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
			fontMetrics = g.getFontMetrics();
			for(int count = 0; count < loadStatus.size(); count++)
			{
				String line = loadStatus.get(count);
				g.drawString(line, Panel.WIDTH / 2 - fontMetrics.stringWidth(line) / 2, 300 + count * 50);
			}
		}
		else
		{
			// Draw images
			g.drawImage(images.get(index), 40, 40, 800, 800, null);
			g.drawImage(composite, 880, 40, IMAGE_WIDTH, IMAGE_HEIGHT, null);

			// Draw buttons and glow effects
			if(Input.mouseInRect(40, 880, 170, 160))
			{
				g.drawImage(
						Content.getImage(Input.mouseLeftDown()? Content.GLOW_SQUARE_CLICK : Content.GLOW_SQUARE_HOVER),
						10, 850, null);
			}
			else if(Input.mouseInRect(250, 880, 170, 160))
			{
				g.drawImage(
						Content.getImage(Input.mouseLeftDown()? Content.GLOW_SQUARE_CLICK : Content.GLOW_SQUARE_HOVER),
						220, 850, null);
			}
			else if(Input.mouseInRect(460, 880, 170, 160))
			{
				g.drawImage(
						Content.getImage(Input.mouseLeftDown()? Content.GLOW_SQUARE_CLICK : Content.GLOW_SQUARE_HOVER),
						430, 850, null);
			}
			else if(Input.mouseInRect(670, 880, 170, 160))
			{
				g.drawImage(Content.getImage(Content.GLOW_SQUARE_HOVER), 640, 850, null);
			}
			g.setColor(Color.LIGHT_GRAY);
			g.fillRect(40, 880, 170, 160);
			g.fillRect(250, 880, 170, 160);
			g.fillRect(460, 880, 170, 160);
			g.fillRect(670, 880, 170, 160);

			// Draw image counter
			g.setColor(Color.WHITE);
			g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 48));
			FontMetrics fontMetrics = g.getFontMetrics();
			String text = index + 1 + " / " + images.size();
			g.drawString(text, 755 - fontMetrics.stringWidth(text) / 2, 960 + fontMetrics.getHeight() / 4);

			// Draw hover tooltip
			g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
			fontMetrics = g.getFontMetrics();
			String[] tooltipText = new String[0];
			int tooltipX = Input.mouseX() + 20;
			int tooltipY = Input.mouseY();
			int tooltipWidth = 20;
			int tooltipHeight = 10;
			if(Input.mouseInRect(880, 40, IMAGE_WIDTH, IMAGE_HEIGHT))
			{
				tooltipText = new String[2];
				tooltipText[0] = "Blended Image:";
				tooltipText[1] = "The blended result of your Google image search.";
			}
			else if(Input.mouseInRect(40, 40, 800, 800))
			{
				tooltipText = new String[2];
				tooltipText[0] = "Last Image:";
				tooltipText[1] = "The last image added";
			}
			else if(Input.mouseInRect(40, 880, 170, 160))
			{
				tooltipText = new String[2];
				tooltipText[0] = "Pause Button:";
				tooltipText[1] = "Pauses the image blending process.";
			}
			else if(Input.mouseInRect(250, 880, 170, 160))
			{
				tooltipText = new String[3];
				tooltipText[0] = "Speed Button:";
				tooltipText[1] = "Changes the image blending speed.";
				tooltipText[2] = "Switches between 1x, 2x, and 3x.";
			}
			else if(Input.mouseInRect(460, 880, 170, 160))
			{
				tooltipText = new String[2];
				tooltipText[0] = "Exit:";
				tooltipText[1] = "Return to the main menu.";
			}
			else if(Input.mouseInRect(670, 880, 170, 160))
			{
				tooltipText = new String[3];
				tooltipText[0] = "Image Counter:";
				tooltipText[1] = "Displays the number of images added and";
				tooltipText[2] = "the total number of images to be added.";
			}
			for(String line : tooltipText)
			{
				tooltipWidth = Math.max(tooltipWidth, fontMetrics.stringWidth(line) + 20);
				tooltipHeight += fontMetrics.getHeight() / 2 + 10;
			}
			tooltipX = (tooltipX < 0)? 0 : tooltipX;
			tooltipX = (tooltipX + tooltipWidth + 20 > Panel.WIDTH)? Panel.WIDTH - tooltipWidth : tooltipX;
			tooltipY = (tooltipY < 0)? 0 : tooltipY;
			tooltipY = (tooltipY + tooltipHeight + 30 > Panel.HEIGHT)? Panel.HEIGHT - tooltipHeight : tooltipY;
			if(tooltipText.length > 0)
			{
				g.setColor(Color.LIGHT_GRAY);
				g.fillRect(tooltipX, tooltipY, tooltipWidth, tooltipHeight);
				g.setStroke(new BasicStroke(3));
				g.setColor(Color.GRAY);
				g.drawRect(tooltipX, tooltipY, tooltipWidth, tooltipHeight);
				g.setColor(Color.BLACK);
				for(int count = 0; count < tooltipText.length; count++)
				{
					String line = tooltipText[count];
					g.drawString(line, tooltipX + 10, tooltipY + (fontMetrics.getHeight() / 2 + 10) * (count + 1));
				}
			}
		}
	}

	@Override
	public void handleInput()
	{
		if(loading)
		{
			if(Input.keyRelease(Input.ESCAPE))
			{
				sm.setState(StateManager.MENU);
			}
		}
		else
		{
			if(Input.mouseLeftRelease())
			{
				if(Input.mouseInRect(40, 880, 170, 160))
				{
					// Pausing the process
					paused = !paused;
				}
				else if(Input.mouseInRect(250, 880, 170, 160))
				{
					// Changing process speed
					waitTime -= WAIT_TIME;
					if(waitTime == 0)
					{
						waitTime = WAIT_TIME * NUM_SPEEDS;
					}
				}
				else if(Input.mouseInRect(460, 880, 170, 160))
				{
					// Quitting to main menu
					sm.setState(StateManager.MENU);
				}
			}
		}
	}
}
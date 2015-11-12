package image.blender.State;

import image.blender.Manager.StateManager;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class BlendState extends State
{
	private int timer = -1;
	private int index = -1;
	
	private BufferedImage composite;
	private ArrayList<BufferedImage> images = new ArrayList<BufferedImage>();
	
	public static final int IMAGE_WIDTH = 900;
	public static final int IMAGE_HEIGHT = 900;
	public static final int WAIT_TIME = 30;
	
	public BlendState(StateManager sm)
	{
		super(sm);

		try
		{
			String key = "AIzaSyDTW8BEDD4JRbTtyWqzQkIg5Wnd8pUUMP8";
			String query = data.getSearch();
			String id = "002593508493133637657:0vhkg_zxi2w";
			URL url = new URL("https://www.googleapis.com/customsearch/v1?key=" + key + "&cx=" + id + "&q=" + query
					+ "&searchType=image&fileType=jpg&alt=json");
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
					images.add(ImageIO.read(new URL(link)));
				}
			}
			conn.disconnect();
			composite = images.get(0);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void update()
	{
		timer++;
		if(timer % WAIT_TIME == 0)
		{
			if(index < images.size())
			{
				index++;
				// Code here
			}
		}
		
		handleInput();
	}

	@Override
	public void render(Graphics2D g)
	{
		g.drawImage(images.get(index), 40, 90, IMAGE_WIDTH, IMAGE_HEIGHT, null);
		g.drawImage(composite, IMAGE_WIDTH + 80, 90, IMAGE_WIDTH, IMAGE_HEIGHT, null);
	}

	@Override
	public void handleInput()
	{

	}
}
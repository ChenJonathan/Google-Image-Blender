package image.blender.Manager;

import java.awt.image.BufferedImage;
import java.util.HashMap;

import javax.imageio.ImageIO;

/**
 * This class is the content manager for Indigo. Call {@link ContentManager#getImage(ImageData)},
 * {@link ContentManager#getAnimation(AnimationData)}, or {@link ContentManager#getSound(SoundData)} with the respective
 * static image or animation data to get and cache the content, if it hasn't already. <br>
 * <br>
 * Call {@link #dispose()} whenever the stage is changed. To prevent RAM hogging.<br>
 * <br>
 * Conventions should dictate that {@link #dispose()} can be called at any given time, so images and animations should
 * be reattained from the cache every frame.
 */
public class Content
{
	public static ImageData MENU_BACKGROUND = new ImageData("/menu_background.png", 1920, 1080);
	public static ImageData BUTTON_SEARCH = new ImageData("/button_search.png", 358, 106);
	public static ImageData BUTTON_QUIT = new ImageData("/button_quit.png", 358, 106);
	public static ImageData GLOW_SQUARE_CLICK = new ImageData("/glow_square_click.png", 230, 220);
	public static ImageData GLOW_SQUARE_HOVER = new ImageData("/glow_square_hover.png", 230, 220);
	public static ImageData GLOW_RECTANGLE_CLICK = new ImageData("/glow_rectangle_click.png", 418, 166);
	public static ImageData GLOW_RECTANGLE_HOVER = new ImageData("/glow_rectangle_hover.png", 418, 166);

	// Storage
	private static HashMap<ImageData, BufferedImage> imageMap;

	static
	{
		imageMap = new HashMap<>();
	}

	/**
	 * @param ad The ImageData to retrieve the image from. Should be attained from {@link ContentManager}.
	 * @return A BufferedImage composing the requested image.
	 */
	public static BufferedImage getImage(ImageData id)
	{
		BufferedImage img = imageMap.get(id);
		if(img != null)
		{
			return img;
		}
		img = load(id);
		imageMap.put(id, img);
		return img;
	}

	private static class ImageData
	{
		private String path;
		private int width, height;

		private ImageData(String path, int width, int height)
		{
			this.path = path;
			this.width = width;
			this.height = height;
		}
	}

	private static BufferedImage load(ImageData id)
	{
		return load(id.path, id.width, id.height);
	}

	private static BufferedImage load(String path, int width, int height)
	{
		BufferedImage img;
		try
		{
			img = ImageIO.read(Content.class.getResourceAsStream(path));
			img = img.getSubimage(0, 0, width, height);
			return img;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("Error loading graphics.");
			System.exit(0);
		}
		return null;
	}

	/**
	 * Clears the image cache. To be called between states, but should conventionally be able to
	 * be called at any time.
	 */
	public static void dispose()
	{
		imageMap.clear();
	}
}
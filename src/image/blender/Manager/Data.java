package image.blender.Manager;

/**
 * Stores general persisting information.
 */
public class Data
{
	private String search;

	/**
	 * Initializes data.
	 */
	public Data()
	{
		search = "";
	}
	
	public String getSearch()
	{
		return search;
	}
	
	public void setSearch(String search)
	{
		this.search = search;
	}
}

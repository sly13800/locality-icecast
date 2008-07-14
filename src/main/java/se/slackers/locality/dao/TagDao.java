package se.slackers.locality.dao;

import java.util.List;

import se.slackers.locality.model.Tag;

public interface TagDao {
	/**
	 * Removes the tag from the database and deletes all references to it.
	 * @param tag
	 */
	public void delete(Tag tag);
	
	/**
	 * Find a tag by the tag id. If no tag was found a DataRetrievalFailureException is thrown.
	 * 
	 * @param id
	 * @return
	 */
	public Tag get(Long id);

	/**
	 * Find a tag by the name of the tag. If no tag was found a DataRetrievalFailureException is thrown.
	 * 
	 * @param name
	 * @return
	 */
	public Tag get(String name);

	/**
	 * Searches for tags that have similar names to the given string. Before the search the name is converted to
	 * lower case.
	 * 
	 * @param name
	 * @return
	 */
	public List<Tag> getLike(String name);

	/**
	 * Saves or updates the tag.
	 * 
	 * @param tag
	 */
	public void save(Tag tag);
}

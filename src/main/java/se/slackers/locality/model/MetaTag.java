package se.slackers.locality.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

/**
 * 
 * @author eb
 * 
 */
@Entity
public class MetaTag {
	private Long id;
	private String name;
	private List<Tag> tags;
	
	/**
	 * 
	 */
	public MetaTag() {
		id = new Long(0);
		name = "";
		tags = new ArrayList<Tag>();
	}	

	/**
	 * Adds a metatag to the specified tag. This method fixes the the bidirectional dependency.
	 * @see Tag#addMetaTag(MetaTag)
	 * @param tag
	 */
	public void addTag(Tag tag) {
		assert tag != null : "The given tag is null";
		
		getTags().add(tag);
		tag.getMetaTags().add(this);
	}

	/**
	 * 
	 * @return
	 */
	@Id
	@Column(name = "id", unique = true)
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	/**
	 * 
	 * @return
	 */
	@Basic
	@Column(name = "name", unique = true)
	public String getName() {
		return name;
	}
	
	/**
	 * 
	 * @return
	 */
	@ManyToMany(targetEntity = Tag.class, cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH })
	@JoinTable(name = "MetaTag_Tag", joinColumns = @JoinColumn(name = "MetaTag_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "Tag_id", referencedColumnName = "id"))
	public List<Tag> getTags() {
		return tags;
	}
	
	/**
	 * 
	 * @param tag
	 */
	public void removeTag(Tag tag) {
		assert tag != null : "The given tag is null";
		
		getTags().remove(tag);
		tag.getMetaTags().remove(this);		
	}

	/**
	 * 
	 * @param id
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 
	 * @param tags
	 */
	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String toString() {
		int num = tags == null ? 0 : tags.size();
		return "MetaTag [id:"+id+", name:"+name+", tags: "+num+"]";
	}	
}

package se.slackers.locality.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

/**
 * 
 * @author eb
 * 
 */
@Entity
public class Tag implements Serializable {
	private static final long serialVersionUID = 825416798925249763L;

	private Long id;
	private String name;
	private List<MetaTag> metaTags;

	/**
	 * 
	 */
	public Tag() {
		id = new Long(0);
		name = "";
		metaTags = new ArrayList<MetaTag>();
	}

	/**
	 * Adds a metatag to the tag. This method also fixes the the bidirectional dependency.
	 * @see MetaTag#addTag(Tag)
	 * @param metatag
	 */
	public void addMetaTag(MetaTag metatag) {
		assert metatag != null : "The given meta tag is null";

		getMetaTags().add(metatag);
		metatag.getTags().add(this);
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
	@ManyToMany(targetEntity = MetaTag.class, cascade = { CascadeType.PERSIST, CascadeType.MERGE }, mappedBy = "tags")
	public List<MetaTag> getMetaTags() {
		return metaTags;
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
	 * @param metatag
	 */
	public void removeMetaTag(MetaTag metatag) {
		assert metatag != null : "The given meta tag is null";

		getMetaTags().remove(metatag);
		metatag.getTags().remove(this);
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
	 * @param metaTags
	 */
	public void setMetaTags(List<MetaTag> metaTags) {
		this.metaTags = metaTags;
	}

	/**
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * {@inheritDoc}
	 */
	public String toString() {
		int num = metaTags == null ? 0 : metaTags.size();
		return "Tag [id:" + id + ", name:" + name + ", metatags: " + num + "]";
	}
}

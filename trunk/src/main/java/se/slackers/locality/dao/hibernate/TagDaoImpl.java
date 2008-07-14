package se.slackers.locality.dao.hibernate;

import java.util.List;

import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import se.slackers.locality.dao.TagDao;
import se.slackers.locality.model.Tag;

public class TagDaoImpl extends HibernateDaoSupport implements TagDao {
	
	/**
	 * {@inheritDoc}
	 */	
	public void delete(Tag tag) {
		getHibernateTemplate().delete(tag);		
	}
	
	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public Tag get(Long id) {
		List<Tag> result = (List<Tag>)getHibernateTemplate().find("from Tag tag fetch all properties where tag.id=?", id);
		
		if (result.isEmpty())
			throw new DataRetrievalFailureException("No tag with id "+id+" could be found");
		
		assert result.size() == 1 : "More than one tag found with id "+id;
		
		return result.get(0);
	}
	
	/**
	 * {@inheritDoc}
	 */	
	@SuppressWarnings("unchecked")
	public Tag get(String name) {
		List<Tag> result = (List<Tag>)getHibernateTemplate().find("from Tag tag fetch all properties where tag.name=?", name);
		
		if (result.isEmpty())
			throw new DataRetrievalFailureException("No tag with name "+name+" could be found");
		
		assert result.size() == 1 : "More than one tag found with name "+name;
		
		return result.get(0);		
	}
	
	/**
	 * {@inheritDoc}
	 */	
	@SuppressWarnings("unchecked")
	public List<Tag> getLike(String name) {
		return (List<Tag>)getHibernateTemplate().find("from Tag tag fetch all properties where lower(tag.name) like ? order by tag.name", name.toLowerCase());
	}
	
	/**
	 * {@inheritDoc}
	 */	
	public void save(Tag tag) {
		getHibernateTemplate().saveOrUpdate(tag);
	}
}

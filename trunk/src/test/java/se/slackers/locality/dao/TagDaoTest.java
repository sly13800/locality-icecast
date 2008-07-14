package se.slackers.locality.dao;

import org.hibernate.SessionFactory;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.test.AbstractTransactionalDataSourceSpringContextTests;

import se.slackers.locality.model.Tag;

public class TagDaoTest extends AbstractTransactionalDataSourceSpringContextTests {

	protected TagDao tagDao;
	protected SessionFactory sessionFactory = null;

	public void testSave() {
		Tag tag = new Tag();
		tag.setName("testTag");		
		tagDao.save(tag);		
		
		Tag fetchedTag = tagDao.get("testTag");		
		assertEquals(tag.getId(), fetchedTag.getId());
	}
	
	public void testDelete() {
		Tag tag = new Tag();
		tag.setName("testMetaTag");
		tagDao.save(tag);

		tagDao.delete(tag);
		try {
			tagDao.get("testMetaTag");
			fail();
		} catch (DataRetrievalFailureException e) {
		}
	}	

	protected String[] getConfigLocations() {
		return new String[] { "application-context/main.xml", "application-context/data.xml" };
	}

	public void setTagDao(TagDao tagDao) {
		this.tagDao = tagDao;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
}

package se.slackers.locality.dao;

import org.hibernate.SessionFactory;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.test.AbstractTransactionalDataSourceSpringContextTests;

import se.slackers.locality.model.MetaTag;

public class MetaTagDaoTest extends AbstractTransactionalDataSourceSpringContextTests {

	protected MetaTagDao tagDao;
	protected SessionFactory sessionFactory = null;

	public void testSave() {
		MetaTag tag = new MetaTag();
		tag.setName("testMetaTag");
		tagDao.save(tag);

		MetaTag fetchedMetaTag = tagDao.get("testMetaTag");
		assertEquals(tag.getId(), fetchedMetaTag.getId());
	}

	public void testDelete() {
		MetaTag tag = new MetaTag();
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

	public void setMetaTagDao(MetaTagDao tagDao) {
		this.tagDao = tagDao;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
}
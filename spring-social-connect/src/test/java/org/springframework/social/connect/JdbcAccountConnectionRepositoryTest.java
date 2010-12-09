package org.springframework.social.connect;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.security.encrypt.SearchableStringEncryptor;
import org.springframework.security.encrypt.StringEncryptor;
import org.springframework.social.test.utils.SpringSocialTestDatabaseBuilder;
import org.springframework.social.twitter.TwitterOperations;

// TODO This is testing more than just the jdbc account connection repository - factor out and focus on the data access logic
public class JdbcAccountConnectionRepositoryTest {

	private EmbeddedDatabase db;

	private JdbcTemplate jdbcTemplate;

	private ServiceProvider<TwitterOperations> serviceProvider;

	private JdbcServiceProviderFactory providerFactory;

	@Before
	public void setup() {
		db = new SpringSocialTestDatabaseBuilder().connectedAccount().testData(getClass()).getDatabase();
		jdbcTemplate = new JdbcTemplate(db);
		StringEncryptor encryptor = new SearchableStringEncryptor("secret", "5b8bd7612cdab5ed");
		providerFactory = new JdbcServiceProviderFactory(jdbcTemplate, encryptor);
		serviceProvider = providerFactory.getServiceProvider("twitter", TwitterOperations.class);
	}

	@After
	public void destroy() {
		if (db != null) {
			db.shutdown();
		}
	}

	@Test
	public void addConnection() {
		assertFalse(serviceProvider.isConnected(2L));
		serviceProvider.addConnection(2L, "accessToken", "kdonald");
		assertTrue(serviceProvider.isConnected(2L));
		TwitterOperations api = serviceProvider.getServiceOperations(2L);
		assertNotNull(api);
	}

	@Test
	public void connected() {
		assertTrue(serviceProvider.isConnected(1L));
	}

	@Test
	public void notConnected() {
		assertFalse(serviceProvider.isConnected(2L));
	}

	@Test
	public void getApi() {
		TwitterOperations api = serviceProvider.getServiceOperations(1L);
		assertNotNull(api);
	}

	@Test
	public void getApiNotConnected() {
		TwitterOperations api = serviceProvider.getServiceOperations(2L);
		assertNotNull(api);
	}

	@Test
	public void getConnectedAccountId() {
		assertEquals("habuma", serviceProvider.getProviderAccountId(1L));
	}

	@Test
	public void getConnectedAccountIdNotConnected() {
		assertNull(serviceProvider.getProviderAccountId(2L));
	}

	@Test
	public void disconnect() {
		assertTrue(serviceProvider.isConnected(1L));
		serviceProvider.disconnect(1L);
		assertFalse(serviceProvider.isConnected(1L));
	}

}
package com.tradeshift.codechallenge.saleh.test.database.repository;

import com.tradeshift.codechallenge.saleh.database.repository.NodeEntityRepository;
import com.tradeshift.codechallenge.saleh.database.to.NodeEntity;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.PostgreSQLContainer;

import javax.persistence.EntityManager;
import javax.sql.DataSource;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@RunWith(SpringRunner.class)
@DataJpaTest
@TestPropertySource(locations = "/application.yml")
@ContextConfiguration(initializers = {NodeRepositoryTest.Initializer.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class NodeRepositoryTest {

	@ClassRule
	public static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:11.1")
			.withDatabaseName("integration-tests-db")
			.withUsername("sa")
			.withPassword("sa");

	@Autowired
	private DataSource dataSource;
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private EntityManager entityManager;
	@Autowired
	private NodeEntityRepository nodeEntityRepository;

	@Test
	public void injectedComponentsAreNotNull() {
		assertThat(dataSource).isNotNull();
		assertThat(jdbcTemplate).isNotNull();
		assertThat(entityManager).isNotNull();
		assertThat(nodeEntityRepository).isNotNull();
	}


	@Test
	public void testSave_givenNodeEntity_PersistSuccess() {
		// save root
		NodeEntity nodeEntity = nodeEntityRepository.save(new NodeEntity(null, "root", null));
		Assert.assertEquals(new Integer(1), nodeEntity.getId());

		// update Path
		nodeEntity.setPath(String.valueOf(nodeEntity.getId()));
		nodeEntity = nodeEntityRepository.save(nodeEntity);
		Assert.assertEquals("1", nodeEntity.getPath());

		NodeEntity child = nodeEntityRepository.save(new NodeEntity(null, "child1", "1,2"));
		Assert.assertEquals(new Integer(2), child.getId());

		child = nodeEntityRepository.save(new NodeEntity(null, "child2", "1,3"));
		Assert.assertEquals(new Integer(3), child.getId());

		child = nodeEntityRepository.save(new NodeEntity(null, "child1.1", "1,2,4"));
		Assert.assertEquals(new Integer(4), child.getId());

		child = nodeEntityRepository.save(new NodeEntity(null, "child1.2", "1,2,5"));
		Assert.assertEquals(new Integer(5), child.getId());

		child = nodeEntityRepository.save(new NodeEntity(null, "child1.1.1", "1,2,4,6"));
		Assert.assertEquals(new Integer(6), child.getId());

		NodeEntity retrievedRoot = nodeEntityRepository.findRoot();
		Assert.assertEquals(new Integer(1), retrievedRoot.getId());

		List<NodeEntity> child1Descendants = nodeEntityRepository.findAllByPathLike("1,2,%");
		Assert.assertEquals(3, child1Descendants.size());

		List<NodeEntity> child2Descendants = nodeEntityRepository.findAllByPathLike("1,3,%");
		Assert.assertEquals(0, child2Descendants.size());
	}

	static class Initializer
			implements ApplicationContextInitializer<ConfigurableApplicationContext> {
		public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
			TestPropertyValues.of(
					"spring.datasource.url=" + postgreSQLContainer.getJdbcUrl(),
					"spring.datasource.username=" + postgreSQLContainer.getUsername(),
					"spring.datasource.password=" + postgreSQLContainer.getPassword()
			).applyTo(configurableApplicationContext.getEnvironment());
		}
	}
}

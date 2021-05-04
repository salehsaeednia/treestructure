package com.tradeshift.codechallenge.saleh.test.database.service.impl;

import com.tradeshift.codechallenge.saleh.database.repository.NodeEntityRepository;
import com.tradeshift.codechallenge.saleh.database.service.impl.NodeEntityServiceImpl;
import com.tradeshift.codechallenge.saleh.database.to.NodeEntity;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class NodeEntityServiceTest {

	private NodeEntity root;
	private NodeEntity ch1;
	private NodeEntity ch2;
	private NodeEntity ch1_1;
	private NodeEntity ch1_2;
	private NodeEntity ch1_1_1;
	private NodeEntity ch1_1_2;
	private NodeEntity ch1_1_1_1;
	private NodeEntity ch1_1_1_2;

	@Before
	public void initTest() {
		root = new NodeEntity(1, "root", "1");
		ch1 = new NodeEntity(2, "ch1", "1,2");
		ch2 = new NodeEntity(3, "ch2", "1,3");
		ch1_1 = new NodeEntity(4, "ch1.2", "1,2,4");
		ch1_2 = new NodeEntity(5, "ch1.2", "1,2,5");
		ch1_1_1 = new NodeEntity(6, "ch1.2", "1,2,4,6");
		ch1_1_2 = new NodeEntity(7, "ch1.2", "1,2,4,7");
		ch1_1_1_1 = new NodeEntity(8, "ch1.2", "1,2,4,6,8");
		ch1_1_1_2 = new NodeEntity(9, "ch1.2", "1,2,4,6,9");


	}
	@Mock
	NodeEntityRepository repository;

	@InjectMocks
	NodeEntityServiceImpl nodeEntityService;

	@Test
	public void testSave_givenEntity_Success() {
		when(repository.save(any(NodeEntity.class))).then(returnsFirstArg());
		NodeEntity node = nodeEntityService.save(root);
		Assert.assertEquals(node.getId(), root.getId());
	}
}

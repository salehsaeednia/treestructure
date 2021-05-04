package com.tradeshift.codechallenge.saleh.test.business.service;


import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

import com.tradeshift.codechallenge.saleh.business.service.NodeService;
import com.tradeshift.codechallenge.saleh.database.service.NodeEntityService;
import com.tradeshift.codechallenge.saleh.database.to.NodeEntity;
import com.tradeshift.codechallenge.saleh.dto.NodeDto;
import com.tradeshift.codechallenge.saleh.exception.BaseException;
import com.tradeshift.codechallenge.saleh.exception.ResultError;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;



@RunWith(MockitoJUnitRunner.class)
public class NodeServiceTest {

	private NodeEntity root = new NodeEntity(1, "root", "1");
	private NodeEntity ch1 = new NodeEntity(2, "ch1", "1,2");
	private NodeEntity ch2 = new NodeEntity(3, "ch2", "1,3");
	private NodeEntity ch1_1 = new NodeEntity(4, "ch1.1", "1,2,4");
	private NodeEntity ch1_2 = new NodeEntity(5, "ch1.2", "1,2,5");
	private NodeEntity ch1_1_1 = new NodeEntity(6, "ch1.1.1", "1,2,4,6");
	private NodeEntity ch1_1_2 = new NodeEntity(7, "ch1.1.2", "1,2,4,7");
	private NodeEntity ch1_1_1_1 = new NodeEntity(8, "ch1.1.1.1", "1,2,4,6,8");
	private NodeEntity ch1_1_1_2 = new NodeEntity(9, "ch1.1.1.2", "1,2,4,6,9");
	List<NodeEntity> all = new ArrayList<>();
	List<NodeEntity> ch1_1Descendant = new ArrayList<>();

	@Before
	public void initTest() {
		all.add(root);
		all.add(ch1);
		all.add(ch2);
		all.add(ch1_1);
		all.add(ch1_2);
		all.add(ch1_1_1);
		all.add(ch1_1_2);
		all.add(ch1_1_1_1);
		all.add(ch1_1_1_2);

		ch1_1Descendant.add(ch1_1_1);
		ch1_1Descendant.add(ch1_1_2);
		ch1_1Descendant.add(ch1_1_1_1);
		ch1_1Descendant.add(ch1_1_1_2);
	}

	@Mock
	NodeEntityService nodeEntityService;

	@InjectMocks
	NodeService nodeService;

	@Test
	public void testSave_RootEntity_SuccessRootDto() {
		when(nodeEntityService.save(any(NodeEntity.class))).thenAnswer(saveEntity);
		when(nodeEntityService.findRoot()).thenReturn(null);
		NodeDto node = nodeService.save(new NodeDto(null, "Root", null, 0));
		Assert.assertNotNull(node);
		Assert.assertNull(node.getParent());
		Assert.assertEquals(0, (int) node.getHeight());
	}

	@Test
	public void testSave_DuplicateRootEntity_ThrowRootAlreadyExist() {
		when(nodeEntityService.findRoot()).thenReturn(root);
		BaseException exception = Assert.assertThrows(BaseException.class, () -> {
			nodeService.save(new NodeDto(null, "Root", null, 0));
		});
		Assert.assertEquals(ResultError.RootAlreadyExist, exception.getError());

	}

	@Test
	public void testSave_NewChildEntity_SuccessChildDto() {
		when(nodeEntityService.save(any(NodeEntity.class))).thenAnswer(saveEntity);
		when(nodeEntityService.findById(1)).thenReturn(root);
		NodeDto node = nodeService.save(new NodeDto(null, "Child1", new NodeDto(1, null, null, 0), 0));
		Assert.assertNotNull(node);
		Assert.assertNotNull(node.getParent());
		Assert.assertEquals(1, (int) node.getParent().getId());
		Assert.assertEquals(1, (int) node.getHeight());

	}

	@Test
	public void testSave_NewChildInvalidParentEntity_ThrowInvalidParent() {
		when(nodeEntityService.findById(1)).thenReturn(null);
		BaseException exception = Assert.assertThrows(BaseException.class, () -> {
			NodeDto node = nodeService.save(new NodeDto(null, "Child1", new NodeDto(1, null, null, 0), 0));
		});
		Assert.assertEquals(ResultError.InvalidParent, exception.getError());
	}

	@Test
	public void testSave_ExistingEntityWithDifferentName_SuccessChangeName() {
		when(nodeEntityService.save(any(NodeEntity.class))).thenAnswer(saveEntity);
		when(nodeEntityService.findById(1)).thenReturn(root);
		when(nodeEntityService.findById(2)).thenReturn(ch1);
		NodeDto node = nodeService.save(new NodeDto(2, "Child1", new NodeDto(1, null, null, 0), 0));
		Assert.assertNotNull(node);
		Assert.assertEquals(2, (int) node.getId());
		Assert.assertNotNull(node.getParent());
		Assert.assertEquals(1, (int) node.getParent().getId());
		Assert.assertEquals(1, (int) node.getHeight());
		Assert.assertEquals("Child1", node.getName());
	}

	@Test
	public void testSave_ExistingEntityWithDifferentNameInvalidParent_ThrowInvalidParent() {
		when(nodeEntityService.findById(1)).thenReturn(null);
		BaseException exception = Assert.assertThrows(BaseException.class, () -> {
			NodeDto node = nodeService.save(new NodeDto(2, "Child1", new NodeDto(1, null, null, 0), 0));
		});
		Assert.assertEquals(ResultError.InvalidParent, exception.getError());
	}

	@Test
	public void testSave_ExistingEntityWithDifferentNameAndParent_SuccessChangeNameAndParent() {
		when(nodeEntityService.save(any(NodeEntity.class))).thenAnswer(saveEntity);
		when(nodeEntityService.findById(3)).thenReturn(ch2);
		when(nodeEntityService.findById(2)).thenReturn(ch1);
		when(nodeEntityService.changeDescendantParents(ch1, ch2)).thenAnswer(changeEntityPath);
		NodeDto node = nodeService.save(new NodeDto(2, "Child1", new NodeDto(3, null, null, 0), 0));
		Assert.assertNotNull(node);
		Assert.assertEquals(2, (int) node.getId());
		Assert.assertNotNull(node.getParent());
		Assert.assertEquals(3, (int) node.getParent().getId());
		Assert.assertEquals(2, (int) node.getHeight());
		Assert.assertEquals("Child1", node.getName());
	}

	@Test
	public void testSave_ExistingEntityWithDifferentParentAndLoopCondition1_ThrowInvalidParent() {
		when(nodeEntityService.findById(2)).thenReturn(ch1);
		BaseException exception = Assert.assertThrows(BaseException.class, () -> {
			NodeDto node = nodeService.save(new NodeDto(2, "Child1", new NodeDto(2, null, null, 0), 0));
		});
		Assert.assertEquals(ResultError.InvalidParent, exception.getError());
	}

	@Test
	public void testSave_ChangeNotExistingEntity_ThrowInvalidNode() {
		when(nodeEntityService.findById(2)).thenReturn(ch1);
		when(nodeEntityService.findById(3)).thenReturn(null);
		BaseException exception = Assert.assertThrows(BaseException.class, () -> {
			NodeDto node = nodeService.save(new NodeDto(3, "Child1.1", new NodeDto(2, null, null, 0), 0));
		});
		Assert.assertEquals(ResultError.InvalidNode, exception.getError());
	}

	@Test
	public void testSave_ExistingEntityWithDifferentParentAndLoopCondition2_ThrowInvalidParent() {
		when(nodeEntityService.findById(2)).thenReturn(ch1);
		when(nodeEntityService.findById(8)).thenReturn(ch1_1_1_1);
		BaseException exception = Assert.assertThrows(BaseException.class, () -> {
			NodeDto node = nodeService.save(new NodeDto(2, "Child1", new NodeDto(8, null, null, 0), 0));
		});
		Assert.assertEquals(ResultError.InvalidParent, exception.getError());
	}

	@Test
	public void testGetRoot_RootExist_SuccessRootDto() {
		when(nodeEntityService.findRoot()).thenReturn(root);
		NodeDto node = nodeService.getRoot();
		Assert.assertNotNull(node);
		Assert.assertNull(node.getParent());
		Assert.assertEquals(0, (int) node.getHeight());
	}

	@Test
	public void testGetAll_TreeExist_SuccessListOfNodes() {
		when(nodeEntityService.getAll()).thenReturn(all);
		List<NodeDto> nodes = nodeService.getAll();
		Assert.assertNotNull(nodes);
		Assert.assertEquals(all.size(), nodes.size());
	}

	@Test
	public void testGetDescendant_NodeExist_SuccessListOfNodes() {
		when(nodeEntityService.findById((int) ch1_1.getId())).thenReturn(ch1_1);
		when(nodeEntityService.getDescendant(ch1_1)).thenReturn(ch1_1Descendant);
		List<NodeDto> nodes = nodeService.getDescendant(ch1_1.getId());
		Assert.assertNotNull(nodes);
		Assert.assertEquals(ch1_1Descendant.size() + 1, nodes.size());
	}

	@Test
	public void testGetDescendant_NodeNotExist_ThrowInvalidNode() {
		when(nodeEntityService.findById((int) ch1_1.getId())).thenReturn(null);
		BaseException exception = Assert.assertThrows(BaseException.class, () -> {
			List<NodeDto> nodes = nodeService.getDescendant(ch1_1.getId());
		});
		Assert.assertEquals(ResultError.InvalidNode, exception.getError());
	}

	Answer<NodeEntity> saveEntity = invocation -> {
		Object[] args = invocation.getArguments();
		NodeEntity input = (NodeEntity) args[0];
		if (input.getId() == null) {
			input.setId(Math.abs(new Random().nextInt()));
		}
		return input;
	};

	Answer<NodeEntity> changeEntityPath = invocation -> {
		Object[] args = invocation.getArguments();
		NodeEntity entity = (NodeEntity) args[0];
		NodeEntity newParent = (NodeEntity) args[1];
		entity.setPath(newParent.getPath() + "," + entity.getId());
		return entity;
	};




}

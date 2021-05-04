package com.tradeshift.codechallenge.saleh.test.business.converter;

import com.tradeshift.codechallenge.saleh.business.mapper.NodeMapper;
import com.tradeshift.codechallenge.saleh.database.to.NodeEntity;
import com.tradeshift.codechallenge.saleh.dto.NodeDto;
import org.junit.Assert;
import org.junit.Test;

public class NodeMapperTest {

	@Test
	public void testConvert_nodeEntityNameAndId_SuccessDto() {
		NodeDto dto = new NodeDto(1, "Root", null, 0);
		NodeEntity entity = NodeMapper.convert(dto);
		Assert.assertEquals(dto.getName(), entity.getName());
		Assert.assertEquals(dto.getId(), entity.getId());
	}

	@Test
	public void testConvert_rootEntity_SuccessRootDto() {
		NodeEntity nodeEntity = new NodeEntity(1, "Root", "1");
		NodeDto dto = NodeMapper.convert(nodeEntity);
		Assert.assertEquals(nodeEntity.getName(), dto.getName());
		Assert.assertEquals(nodeEntity.getId(), dto.getId());
		Assert.assertNull(dto.getParent());
		Assert.assertEquals(new Integer(0), dto.getHeight());
	}

	@Test
	public void testConvert_ChildEntityHeightAndParent_SuccessChildDto() {
		NodeEntity nodeEntity1 = new NodeEntity(2, "Child1", "1,2");
		NodeDto dto1 = NodeMapper.convert(nodeEntity1);
		Assert.assertEquals(nodeEntity1.getName(), dto1.getName());
		Assert.assertEquals(nodeEntity1.getId(), dto1.getId());
		Assert.assertEquals(new Integer(1), dto1.getParent().getId());
		Assert.assertEquals(new Integer(1), dto1.getHeight());

		NodeEntity nodeEntity2 = new NodeEntity(3, "Child1.1", "1,2,3");
		NodeDto dto2 = NodeMapper.convert(nodeEntity2);
		Assert.assertEquals(nodeEntity2.getName(), dto2.getName());
		Assert.assertEquals(nodeEntity2.getId(), dto2.getId());
		Assert.assertEquals(new Integer(2), dto2.getParent().getId());
		Assert.assertEquals(new Integer(2), dto2.getHeight());

		NodeEntity nodeEntity3 = new NodeEntity(4, "Child1.1.1.1.1.1", "1,2,3,4,5,6,7");
		NodeDto dto3 = NodeMapper.convert(nodeEntity3);
		Assert.assertEquals(nodeEntity3.getName(), dto3.getName());
		Assert.assertEquals(nodeEntity3.getId(), dto3.getId());
		Assert.assertEquals(new Integer(6), dto3.getParent().getId());
		Assert.assertEquals(new Integer(6), dto3.getHeight());
	}

}

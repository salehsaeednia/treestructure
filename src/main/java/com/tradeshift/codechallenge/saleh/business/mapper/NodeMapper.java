package com.tradeshift.codechallenge.saleh.business.mapper;

import com.tradeshift.codechallenge.saleh.database.to.NodeEntity;
import com.tradeshift.codechallenge.saleh.dto.NodeDto;

public class NodeMapper {

	public static NodeDto convert(NodeEntity entity) {
		if (entity == null) {
			return null;
		}
		NodeDto dto = new NodeDto();
		dto.setId(entity.getId());
		dto.setName(entity.getName());
		String[] path = entity.getPath().split(",");
		dto.setHeight(entity.getPath().split(",").length - 1);
		dto.setParent(getParent(entity));
		return  dto;
	}

	public static NodeDto getParent(NodeEntity entity) {
		if (entity == null) {
			return null;
		}
		NodeDto parent = null;
		String[] path = entity.getPath().split(",");
		if (path.length > 1) {
			parent = new NodeDto();
			parent.setId(Integer.valueOf(path[path.length - 2]));
		}
		return  parent;
	}

	public static NodeEntity convert(NodeDto dto) {
		if (dto == null) {
			return null;
		}
		NodeEntity entity = new NodeEntity();
		entity.setId(dto.getId());
		entity.setName(dto.getName());
		return  entity;
	}
}

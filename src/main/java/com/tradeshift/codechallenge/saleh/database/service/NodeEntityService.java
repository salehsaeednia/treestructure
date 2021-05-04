package com.tradeshift.codechallenge.saleh.database.service;

import com.tradeshift.codechallenge.saleh.database.to.NodeEntity;

import java.util.List;

public interface NodeEntityService {
	Iterable<NodeEntity> getAll();
	NodeEntity findById(Integer id);
	List<NodeEntity> getDescendant(NodeEntity parent);
	NodeEntity save(NodeEntity entity);
	NodeEntity findRoot();
	NodeEntity changeDescendantParents(NodeEntity emp, NodeEntity newParent);
}

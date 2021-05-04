package com.tradeshift.codechallenge.saleh.database.service.impl;

import com.tradeshift.codechallenge.saleh.database.repository.NodeEntityRepository;
import com.tradeshift.codechallenge.saleh.database.service.NodeEntityService;
import com.tradeshift.codechallenge.saleh.database.to.NodeEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NodeEntityServiceImpl implements NodeEntityService {

	private final NodeEntityRepository repository;

	public NodeEntityServiceImpl(NodeEntityRepository repository) {
		this.repository = repository;
	}

	@Override
	public Iterable<NodeEntity> getAll() {
		return repository.findAll();
	}

	@Override
	public NodeEntity findById(Integer id) {
		return repository.findById(id).orElse(null);
	}

	@Override
	public NodeEntity findRoot() {
		return repository.findRoot();
	}

	@Override
	public List<NodeEntity> getDescendant(NodeEntity parent) {
		return repository.findAllByPathLike(parent.getPath()  + ",%");
	}

	@Override
	public NodeEntity changeDescendantParents(NodeEntity emp, NodeEntity newParent) {
		String oldPathLike = emp.getPath() + ",%";
		String newPath = newParent.getPath() + "," + emp.getId();
		// first update all descendants
		repository.updateParent(oldPathLike.length() - 1, newPath, oldPathLike);
		// update current node
		emp.setPath(newParent.getPath() + "," + emp.getId());
		return repository.save(emp);
	}

	@Override
	public NodeEntity save(NodeEntity entity) {
		return repository.save(entity);
	}
}

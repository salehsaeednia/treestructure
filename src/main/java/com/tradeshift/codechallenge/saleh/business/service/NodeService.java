package com.tradeshift.codechallenge.saleh.business.service;

import com.tradeshift.codechallenge.saleh.business.mapper.NodeMapper;
import com.tradeshift.codechallenge.saleh.database.service.NodeEntityService;
import com.tradeshift.codechallenge.saleh.database.to.NodeEntity;
import com.tradeshift.codechallenge.saleh.dto.NodeDto;
import com.tradeshift.codechallenge.saleh.exception.InvalidNodeException;
import com.tradeshift.codechallenge.saleh.exception.InvalidParentException;
import com.tradeshift.codechallenge.saleh.exception.RootAlreadyExistException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class NodeService {


	private final NodeEntityService nodeEntityService;

	public NodeService(NodeEntityService nodeEntityService) {
		this.nodeEntityService = nodeEntityService;
	}

	public NodeDto getRoot() {
		return NodeMapper.convert(nodeEntityService.findRoot());
	}

	public NodeDto save(NodeDto nodeDto) {
		NodeEntity emp = null;
		// check if new node is root
		if (nodeDto.getParent() == null) {
			// check if we already have root
			emp = nodeEntityService.findRoot();
			if (emp != null) {
				throw new RootAlreadyExistException();
				// todo change root
			}
			emp = NodeMapper.convert(nodeDto);
			emp = nodeEntityService.save(emp);
			emp.setPath(String.valueOf(emp.getId()));
			emp = nodeEntityService.save(emp);
		} else {
			NodeEntity parent = nodeEntityService.findById(nodeDto.getParent().getId());
			if (parent == null) {
				throw new InvalidParentException();
			}
			if (nodeDto.getId() != null) {
				emp = nodeEntityService.findById(nodeDto.getId());
				if (emp == null) {
					throw new InvalidNodeException();
				}
				NodeDto p = NodeMapper.getParent(emp);
				if (!p.getId().equals(nodeDto.getParent().getId())) {
					emp = changeParent(emp, parent);
				}
				emp.setName(nodeDto.getName());
			} else {
				emp = NodeMapper.convert(nodeDto);
				emp.setId(null);
				emp = nodeEntityService.save(emp);
				emp.setPath(parent.getPath() + "," + emp.getId());
			}
			emp = nodeEntityService.save(emp);
		}
		return NodeMapper.convert(emp);
	}

	public List<NodeDto> getDescendant(Integer id) {
		NodeEntity parent = nodeEntityService.findById(id);
		if (parent == null) {
			throw new InvalidNodeException();
		}
		Map<Integer, NodeDto> descendantsMap = new HashMap<>();
		List<NodeEntity> descendants = nodeEntityService.getDescendant(parent);
		Collections.sort(descendants);
		List<NodeDto> result = new ArrayList<>();
		NodeDto parentDto = NodeMapper.convert(parent);
		result.add(parentDto);
		descendantsMap.put(parentDto.getId(), parentDto);
		for (NodeEntity entity : descendants) {
			NodeDto nodeDto = NodeMapper.convert(entity);
			result.add(nodeDto);
			descendantsMap.put(nodeDto.getId(), nodeDto);
			if (nodeDto.getParent() != null && nodeDto.getParent().getId() != null && descendantsMap.containsKey(nodeDto.getParent().getId())) {
				nodeDto.setParent(descendantsMap.get(nodeDto.getParent().getId()));
			}
		}
		return result;
	}

	public NodeDto changeParent(NodeDto nodeDto) {
		NodeEntity emp = nodeEntityService.findById(nodeDto.getId());
		if (emp == null) {
			throw new InvalidNodeException();
		}
		NodeDto e = NodeMapper.convert(emp);
		e.setParent(nodeDto.getParent());
		return save(e);
	}

	private NodeEntity changeParent(NodeEntity emp, NodeEntity newParent) {
		// check for loop
		String[] ancestors = newParent.getPath().split(",");
		boolean isAllowed = true;
		for (String ancestor : ancestors) {
			if (Integer.valueOf(ancestor).equals(emp.getId())) {
				isAllowed = false;
				break;
			}
		}
		if (!isAllowed) {
			throw new InvalidParentException();
		}

		return nodeEntityService.changeDescendantParents(emp, newParent);
	}

	public List<NodeDto> getAll() {
		Iterable<NodeEntity> all = nodeEntityService.getAll();
		List<NodeDto> result = new ArrayList<>();
		Map<Integer, NodeDto> descendantsMap = new HashMap<>();
		for (NodeEntity entity : all) {
			NodeDto node = NodeMapper.convert(entity);
			result.add(node);
			descendantsMap.put(node.getId(), node);
		}
		setNodeParents(result, descendantsMap);
		Collections.sort(result);
		return result;
	}

	private void setNodeParents(List<NodeDto> nodes, Map<Integer, NodeDto> nodeDtoMap){
		for (NodeDto n : nodes){
			if (n.getParent() != null && n.getParent().getId() != null && nodeDtoMap.containsKey(n.getParent().getId())) {
				n.setParent(nodeDtoMap.get(n.getParent().getId()));
			}
		}
	}
}

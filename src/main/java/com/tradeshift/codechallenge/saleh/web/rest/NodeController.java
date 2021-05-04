package com.tradeshift.codechallenge.saleh.web.rest;

import com.tradeshift.codechallenge.saleh.business.service.NodeService;
import com.tradeshift.codechallenge.saleh.dto.NodeDto;
import com.tradeshift.codechallenge.saleh.exception.ResultError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("rest/v1/node")
public class NodeController {


	private final NodeService nodeService;

	public NodeController(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	@PostMapping("save")
	public RestResult<NodeDto> save(@Valid @RequestBody NodeDto nodeDto) {
		return new RestResult<>(ResultError.Success, nodeService.save(nodeDto));
	}

	@PostMapping("changeParent")
	public RestResult<NodeDto> changeParent(@Valid @RequestBody NodeDto nodeDto) {
		return new RestResult<>(ResultError.Success, nodeService.changeParent(nodeDto));
	}

	@GetMapping("descendant/{id}")
	public RestResult<List<NodeDto>> descendant(@PathVariable("id") Integer id) {
		return new RestResult<>(ResultError.Success, nodeService.getDescendant(id));
	}

	@GetMapping("all")
	public RestResult<List<NodeDto>> getAll() {
		return new RestResult<>(ResultError.Success, nodeService.getAll());
	}

	@GetMapping("root")
	public RestResult<NodeDto> getRoot() {
		return new RestResult<>(ResultError.Success, nodeService.getRoot());
	}

}

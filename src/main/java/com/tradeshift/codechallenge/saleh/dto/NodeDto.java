package com.tradeshift.codechallenge.saleh.dto;

import javax.validation.constraints.NotBlank;
import java.util.Objects;

public class NodeDto implements Comparable {

	private Integer id;

	@NotBlank
	private String name;
	private NodeDto parent;
	private Integer height;

	public NodeDto() {
	}

	public NodeDto(Integer id, @NotBlank String name, NodeDto parent, Integer height) {
		this.id = id;
		this.name = name;
		this.parent = parent;
		this.height = height;
	}

	// region <Getters - Setters>
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public NodeDto getParent() {
		return parent;
	}

	public void setParent(NodeDto parent) {
		this.parent = parent;
	}

	public Integer getHeight() {
		return height;
	}

	public void setHeight(Integer height) {
		this.height = height;
	}

	// endregion


	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		NodeDto nodeDto = (NodeDto) o;
		return Objects.equals(id, nodeDto.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public int compareTo(Object o) {
		if (this == o) return -1;
		if (o == null || getClass() != o.getClass()) return -1;
		NodeDto nodeDto = (NodeDto) o;
		if (nodeDto.getHeight().equals(getHeight())) {
			return getId().compareTo(nodeDto.getId());
		}
		return getHeight().compareTo(nodeDto.getHeight());
	}
}

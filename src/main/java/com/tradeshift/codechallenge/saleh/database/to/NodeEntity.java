package com.tradeshift.codechallenge.saleh.database.to;

import com.tradeshift.codechallenge.saleh.dto.NodeDto;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "NODE")
@SequenceGenerator(name = "NODE_SEQ_GEN", allocationSize = 1, sequenceName = "SEQ_NODE")
public class NodeEntity implements Comparable {


	@Id
	@GeneratedValue(generator = "NODE_SEQ_GEN", strategy = GenerationType.SEQUENCE)
	private Integer id;

	private String name;

	private String path;

	public NodeEntity() {
	}

	public NodeEntity(Integer id, String name, String path) {
		this.id = id;
		this.name = name;
		this.path = path;
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

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	// endregion


	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		NodeEntity that = (NodeEntity) o;
		return Objects.equals(id, that.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public int compareTo(Object o) {

		if (this == o) return -1;
		if (o == null || getClass() != o.getClass()) return -1;
		NodeEntity other = (NodeEntity) o;
		int otherHeight = other.getPath().split(",").length;
		int thisHeight = getPath().split(",").length;
		if (thisHeight == otherHeight) {
			if (getId() != null && other.getId() != null) {
				return getId().compareTo(other.getId());
			}
		}
		return thisHeight - otherHeight;
	}
}

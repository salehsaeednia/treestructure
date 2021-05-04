package com.tradeshift.codechallenge.saleh.database.repository;

import com.tradeshift.codechallenge.saleh.database.to.NodeEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface NodeEntityRepository extends CrudRepository<NodeEntity, Integer> {
	NodeEntity findByPath(String path);
	List<NodeEntity> findAllByPathLike(String path);

	@Query(value = "SELECT e From NodeEntity e where  e.path = cast(e.id as string)")
	NodeEntity findRoot();

	@Transactional
	@Modifying
	@Query(value = "update NodeEntity e set e.path = concat(:newPath,substring( e.path,:oldPathLength)) " +
			"where  e.path like :oldPathLike")
	void updateParent(Integer oldPathLength, String newPath, String oldPathLike);
}

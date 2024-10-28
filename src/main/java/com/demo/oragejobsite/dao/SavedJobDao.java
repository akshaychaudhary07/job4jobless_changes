package com.demo.oragejobsite.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.demo.oragejobsite.entity.SavedJob;

@Repository
public interface SavedJobDao extends MongoRepository<SavedJob, String>{

	List<SavedJob> findByUid(String uid);

	SavedJob findByJobidAndUid(String jobid, String uid);
	Page<SavedJob> findByJobidAndUid(String jobid, String uid,Pageable pageable);
	
	List<SavedJob> findByUidAndSaveStatusTrue(String uid);
}

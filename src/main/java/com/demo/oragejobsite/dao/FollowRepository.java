package com.demo.oragejobsite.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.demo.oragejobsite.entity.Follow;
import java.util.List;

public interface FollowRepository extends MongoRepository<Follow, String> {
    List<Follow> findByUid(String uid);
	Page<Follow> findByUid(String uid, Pageable pageable);
    List<Follow> findByEmpid(String empid); // Updated to use empid
    List<Follow> findByUidAndEmpid(String uid, String empid); // Updated to use empid
    Page<Follow> findByEmpid(String empid, Pageable pageable);
	long countByUid(String uid);
	long countByEmpid(String empid);
}


package com.demo.oragejobsite.dao;


import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.demo.oragejobsite.entity.Blogs;
@Repository
public interface BlogDao extends MongoRepository<Blogs, String>{

}

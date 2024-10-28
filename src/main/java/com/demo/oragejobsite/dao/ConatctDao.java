package com.demo.oragejobsite.dao;


import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.demo.oragejobsite.entity.Contact;

@Repository
public interface ConatctDao extends MongoRepository<Contact, String>{

}

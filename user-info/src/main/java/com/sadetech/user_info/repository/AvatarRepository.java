package com.sadetech.user_info.repository;

import com.sadetech.user_info.model.Avatar;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AvatarRepository extends MongoRepository<Avatar,String> {
}

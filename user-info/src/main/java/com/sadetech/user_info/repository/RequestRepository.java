package com.sadetech.user_info.repository;

import com.sadetech.user_info.model.MoneyRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestRepository extends MongoRepository<MoneyRequest,String> {
    List<MoneyRequest> findBySenderPlayerIdAndRequestSummaryStatus(String senderPlayerId, String requestSummaryStatus);
}

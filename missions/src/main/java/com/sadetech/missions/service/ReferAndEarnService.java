package com.sadetech.missions.service;

import com.sadetech.missions.exception.ResourceNotFoundException;
import com.sadetech.missions.model.ReferAndEarnAmount;
import com.sadetech.missions.repository.ReferAndEarnRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReferAndEarnService {

    @Autowired
    private ReferAndEarnRepository referAndEarnRepository;

    public String postReferAndEarnAmount(ReferAndEarnAmount referAndEarnAmount){
        ReferAndEarnAmount referAndEarnAmount1 = referAndEarnRepository.save(referAndEarnAmount);
        return "Refer and Earn reward amount posted successfully";
    }

    public ReferAndEarnAmount getAmount(int rank){

        if(rank < 0 || rank > 15){
            throw new IllegalArgumentException("Rank should not be below 1 and above 15");
        }

        return referAndEarnRepository.findByRank(rank)
                .orElseThrow(() -> new ResourceNotFoundException("No rank found"));
    }

    public List<ReferAndEarnAmount> getAllRank(){
        List<ReferAndEarnAmount> rank = referAndEarnRepository.findAll();
        if(rank.isEmpty()){
            throw new ResourceNotFoundException("No details found");
        }
        return rank;
    }
}

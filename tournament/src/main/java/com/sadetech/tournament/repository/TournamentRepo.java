package com.sadetech.tournament.repository;

import com.sadetech.tournament.model.Tournament;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TournamentRepo extends MongoRepository<Tournament,String> {
    List<Tournament> findByTournamentStatusAndMatchStartingAtBefore(String tournamentStatus, LocalDateTime now);

    Tournament findByTournamentStatus(String tournamentStatus);

    List<Tournament> findByCreatedAtAfterOrderByCreatedAtDesc(LocalDateTime sevenDaysAgo);
}

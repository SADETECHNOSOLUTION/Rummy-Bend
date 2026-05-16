package com.sadetech.tournament.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TournamentScheduler {

    @Autowired
    private TournamentService tournamentService;

    @Scheduled(fixedRate = 60000) // Runs every 60 seconds
    public void checkAndStartTournaments() {
        tournamentService.updateTournamentStatusToStarted();
    }
}

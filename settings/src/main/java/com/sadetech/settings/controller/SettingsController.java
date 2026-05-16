package com.sadetech.settings.controller;

import com.sadetech.settings.exception.PlayerNotFoundException;
import com.sadetech.settings.model.Settings;
import com.sadetech.settings.service.SettingsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/settings")
public class SettingsController {

    private final SettingsService settingsService;

    public SettingsController(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    @PostMapping("/post")
    public ResponseEntity<Settings> addSettings(@RequestBody Settings settings) {
        return ResponseEntity.ok(settingsService.addSettingsToPlayer(settings));
    }

    @GetMapping("/{playerId}")
    public ResponseEntity<Settings> getSettings(@PathVariable String playerId) {
        return ResponseEntity.ok(settingsService.getSettingsOfPlayer(playerId));
    }

    @PatchMapping("/{playerId}/sound")
    public ResponseEntity<Settings> updateSound(@PathVariable String playerId, @RequestParam boolean sound) {
        return ResponseEntity.ok(settingsService.updateSound(playerId, sound));
    }

    @PatchMapping("/{playerId}/vibration")
    public ResponseEntity<Settings> updateVibration(@PathVariable String playerId, @RequestParam boolean vibration) {
        return ResponseEntity.ok(settingsService.updateVibration(playerId, vibration));
    }

    @PatchMapping("/{playerId}/notification")
    public ResponseEntity<Settings> updateNotification(@PathVariable String playerId, @RequestParam boolean notification) {
        return ResponseEntity.ok(settingsService.updateNotification(playerId, notification));
    }

    @PatchMapping("/{playerId}/autoShuffle")
    public ResponseEntity<Settings> updateAutoShuffle(@PathVariable String playerId, @RequestParam boolean autoShuffleCards) {
        return ResponseEntity.ok(settingsService.updateAutoShuffleCardSettings(playerId, autoShuffleCards));
    }

    @PatchMapping("/{playerId}/location")
    public ResponseEntity<Settings> updateLocationStatus(@PathVariable String playerId, @RequestParam boolean location) {
        return ResponseEntity.ok(settingsService.updateLocationStatus(playerId, location));
    }

    @PatchMapping("/{playerId}/calendar")
    public ResponseEntity<Settings> updateCalendar(@PathVariable String playerId, @RequestParam boolean calendar) {
        return ResponseEntity.ok(settingsService.updateCalendar(playerId, calendar));
    }

}
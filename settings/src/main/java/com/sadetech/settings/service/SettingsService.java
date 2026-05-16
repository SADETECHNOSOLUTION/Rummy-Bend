package com.sadetech.settings.service;

import com.sadetech.settings.exception.PlayerNotFoundException;
import com.sadetech.settings.exception.ResourceNotFoundException;
import com.sadetech.settings.feign.UserFeignClient;
import com.sadetech.settings.model.Settings;
import com.sadetech.settings.repository.SettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SettingsService {

    @Autowired
    private SettingsRepository settingsRepository;

    @Autowired
    private UserFeignClient userFeignClient;

    public Settings addSettingsToPlayer(Settings settings){
        return settingsRepository.save(settings);
    }

    public Settings getSettingsOfPlayer(String playerId){

        if(playerId == null || playerId.isBlank()){
            throw new IllegalArgumentException("Player id should not be empty.");
        }

        playerId = playerId.trim().replaceAll("\\s+", "");

        if ( !playerId.matches("^[a-fA-F0-9]{24}$")) {
            throw new IllegalArgumentException("Player ID must be a valid 24-character hexadecimal string.");
        }

      boolean user = userFeignClient.findUserExistOrNot(playerId);
      if(!user){
          throw new PlayerNotFoundException("Player not found for the player id : " + playerId);
      }

      return settingsRepository.findByPlayerId(playerId).orElseThrow(() -> new ResourceNotFoundException("No settings found for the player Id"));
    }

    public Settings updateSound(String playerId, boolean sound){

        if(playerId == null || playerId.isBlank()){
            throw new IllegalArgumentException("Player id should not be empty.");
        }

        playerId = playerId.trim().replaceAll("\\s+", "");

        if ( !playerId.matches("^[a-fA-F0-9]{24}$")) {
            throw new IllegalArgumentException("Player ID must be a valid 24-character hexadecimal string.");
        }

        boolean user = userFeignClient.findUserExistOrNot(playerId);
        if(!user){
            throw new PlayerNotFoundException("Player not found for the player id : " + playerId);
        }
        Settings settings = settingsRepository.findByPlayerId(playerId).orElseThrow(() -> new ResourceNotFoundException("No settings found for the player Id"));
        settings.setSound(sound);
        return settingsRepository.save(settings);
    }

    public Settings updateVibration(String playerId, boolean vibration){

        if(playerId == null || playerId.isBlank()){
            throw new IllegalArgumentException("Player id should not be empty.");
        }

        playerId = playerId.trim().replaceAll("\\s+", "");

        if ( !playerId.matches("^[a-fA-F0-9]{24}$")) {
            throw new IllegalArgumentException("Player ID must be a valid 24-character hexadecimal string.");
        }

        boolean user = userFeignClient.findUserExistOrNot(playerId);
        if(!user){
            throw new PlayerNotFoundException("Player not found for the player id : " + playerId);
        }
        Settings settings = settingsRepository.findByPlayerId(playerId).orElseThrow(() -> new ResourceNotFoundException("No settings found for the player Id"));
        settings.setVibration(vibration);
        return settingsRepository.save(settings);
    }

    public Settings updateNotification(String playerId, boolean notification){

        if(playerId == null || playerId.isBlank()){
            throw new IllegalArgumentException("Player id should not be empty.");
        }

        playerId = playerId.trim().replaceAll("\\s+", "");

        if ( !playerId.matches("^[a-fA-F0-9]{24}$")) {
            throw new IllegalArgumentException("Player ID must be a valid 24-character hexadecimal string.");
        }

        boolean user = userFeignClient.findUserExistOrNot(playerId);
        if(!user){
            throw new PlayerNotFoundException("Player not found for the player id : " + playerId);
        }
        Settings settings = settingsRepository.findByPlayerId(playerId).orElseThrow(() -> new ResourceNotFoundException("No settings found for the player Id"));
        settings.setNotification(notification);
        return settingsRepository.save(settings);
    }

    public Settings updateAutoShuffleCardSettings(String playerId, boolean autoShuffleCards){

        if(playerId == null || playerId.isBlank()){
            throw new IllegalArgumentException("Player id should not be empty.");
        }

        playerId = playerId.trim().replaceAll("\\s+", "");

        if ( !playerId.matches("^[a-fA-F0-9]{24}$")) {
            throw new IllegalArgumentException("Player ID must be a valid 24-character hexadecimal string.");
        }

        boolean user = userFeignClient.findUserExistOrNot(playerId);
        if(!user){
            throw new PlayerNotFoundException("Player not found for the player id : " + playerId);
        }
        Settings settings = settingsRepository.findByPlayerId(playerId).orElseThrow(() -> new ResourceNotFoundException("No settings found for the player Id"));
        settings.setAutoShuffleCards(autoShuffleCards);
        return settingsRepository.save(settings);
    }

    public Settings updateLocationStatus(String playerId, boolean location){

        if(playerId == null || playerId.isBlank()){
            throw new IllegalArgumentException("Player id should not be empty.");
        }

        playerId = playerId.trim().replaceAll("\\s+", "");

        if ( !playerId.matches("^[a-fA-F0-9]{24}$")) {
            throw new IllegalArgumentException("Player ID must be a valid 24-character hexadecimal string.");
        }

        boolean user = userFeignClient.findUserExistOrNot(playerId);
        if(!user){
            throw new PlayerNotFoundException("Player not found for the player id : " + playerId);
        }
        Settings settings = settingsRepository.findByPlayerId(playerId).orElseThrow(() -> new ResourceNotFoundException("No settings found for the player Id"));
        settings.setLocation(location);
        return settingsRepository.save(settings);
    }

    public Settings updateCalendar(String playerId, boolean calendar){

        if(playerId == null || playerId.isBlank()){
            throw new IllegalArgumentException("Player id should not be empty.");
        }

        playerId = playerId.trim().replaceAll("\\s+", "");

        if ( !playerId.matches("^[a-fA-F0-9]{24}$")) {
            throw new IllegalArgumentException("Player ID must be a valid 24-character hexadecimal string.");
        }

        boolean user = userFeignClient.findUserExistOrNot(playerId);
        if(!user){
            throw new PlayerNotFoundException("Player not found for the player id : " + playerId);
        }
        Settings settings = settingsRepository.findByPlayerId(playerId).orElseThrow(() -> new ResourceNotFoundException("No settings found for the player Id"));
        settings.setCalendar(calendar);
        return settingsRepository.save(settings);
    }
}

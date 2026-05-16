package com.sadetech.user_info.service;

import com.sadetech.user_info.exception.AvatarNotFoundException;
import com.sadetech.user_info.model.Avatar;
import com.sadetech.user_info.repository.AvatarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class AvatarService {

    @Autowired
    private AvatarRepository avatarRepository;

    @Autowired
    private FileUploadService fileUploadService;

    public Avatar addAvatarToDB(String name, MultipartFile imagePath) throws IOException {
        try {
            // Upload the file and get the file path
            String avatarImagePath = fileUploadService.uploadFile(imagePath);

            // Create and populate the Avatar object
            Avatar avatar = new Avatar();
            avatar.setName(name);
            avatar.setImagePath(avatarImagePath);

            // Save the Avatar object to the database
            return avatarRepository.save(avatar);
        } catch (IOException e) {
            throw new IOException("Error uploading avatar image: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("An unexpected error occurred: " + e.getMessage(), e);
        }
    }

    public List<Avatar> getAllAvatar(){
        return avatarRepository.findAll();
    }

    public Avatar getAvatar(String id){
        return avatarRepository.findById(id)
                .orElseThrow(() -> new AvatarNotFoundException("No avatar found"));

    }
}

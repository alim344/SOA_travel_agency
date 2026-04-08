package com.example.stakeholders_service.service;

import com.example.stakeholders_service.dto.ProfileDTO;
import com.example.stakeholders_service.model.Profile;
import com.example.stakeholders_service.repository.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProfileService {
    @Autowired
    private ProfileRepository profileRepository;

    public void save(Profile profile) {
        profileRepository.save(profile);
    }

    public Profile getProfileById(Long id) {
        return profileRepository.findByUserId(id);
    }

    public Profile updateProfile(Long userId, ProfileDTO dto) {
        Profile profile = profileRepository.findByUserId(userId);

        profile.setFirstName(dto.getFirstName());
        profile.setLastName(dto.getLastName());
        profile.setProfilePhoto(dto.getProfilePhoto());
        profile.setBiography(dto.getBiography());
        profile.setMotto(dto.getMotto());

        return profileRepository.save(profile);
    }
}

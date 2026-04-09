package com.example.stakeholders_service.controller;

import com.example.stakeholders_service.dto.ProfileDTO;
import com.example.stakeholders_service.model.Profile;
import com.example.stakeholders_service.model.User;
import com.example.stakeholders_service.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/profiles")
public class ProfileController {
    @Autowired
    private ProfileService profileService;

    @GetMapping("/me")
    public ResponseEntity<ProfileDTO> getProfile(Authentication authentication) {
        User user = (User) authentication.getPrincipal();

        Profile profile = profileService.getProfileById(user.getId());

        return ResponseEntity.ok(profileToDTO(profile));
    }

    @PutMapping("/update")
    public ResponseEntity<ProfileDTO> updateProfile(@RequestBody ProfileDTO profileDTO, Authentication authentication){
        User user = (User) authentication.getPrincipal();

        Profile profile = profileService.updateProfile(user.getId(), profileDTO);

        return ResponseEntity.ok(profileToDTO(profile));
    }

    public ProfileDTO profileToDTO(Profile p) {
        ProfileDTO profileDTO = new ProfileDTO(p.getUserId(), p.getFirstName(), p.getLastName(),
                p.getProfilePhoto(), p.getBiography(), p.getMotto());

        return profileDTO;
    }
}

package com.example.stakeholders_service.dto;

import com.example.stakeholders_service.model.Profile;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String profilePhoto;
    private String biography;
    private String motto;

    ProfileDTO() {}

    public ProfileDTO(Long id, String firstName, String lastName, String profilePhoto, String biography, String motto) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.profilePhoto = profilePhoto;
        this.biography = biography;
        this.motto = motto;
    }
}

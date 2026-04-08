package com.example.stakeholders_service.controller;

import com.example.stakeholders_service.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class ProfileController {
    @Autowired
    private ProfileService profileService;
}

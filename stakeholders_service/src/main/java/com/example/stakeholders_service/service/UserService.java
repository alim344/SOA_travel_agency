package com.example.stakeholders_service.service;

import com.example.stakeholders_service.dto.RegistrationDTO;
import com.example.stakeholders_service.model.Profile;
import com.example.stakeholders_service.model.User;
import com.example.stakeholders_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleService roleService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ProfileService profileService;

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }


    public User registerFromDTO( RegistrationDTO dto){

        User user = new User();

        user.setEnabled(true);
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setUsername(dto.getUsername());

        user.setLastPasswordResetDate(new Timestamp(new Date().getTime()));

        if(dto.getRole().equalsIgnoreCase("ROLE_GUIDE")){
            user.setRole(roleService.findByRoleName("ROLE_GUIDE"));
        } else if(dto.getRole().equalsIgnoreCase("ROLE_TOURIST")){
            user.setRole(roleService.findByRoleName("ROLE_TOURIST"));
        }

        Profile profile = new Profile();
        profile.setFirstName(dto.getFirstName());
        profile.setLastName(dto.getLastName());
        profile.setMotto(dto.getMotto());


        profile.setUser(user);
        user.setProfile(profile);
        return userRepository.save(user);

    }



}

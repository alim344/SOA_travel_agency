package com.example.stakeholders_service.service;

import com.example.stakeholders_service.dto.AccountDTO;
import com.example.stakeholders_service.model.Profile;
import com.example.stakeholders_service.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AdminService {

    @Autowired
    private UserService userService;

    public List<AccountDTO> getAllUsers(){

        List<AccountDTO> dtos = new ArrayList<>();

        List<User> users = userService.findAll();

        for(User user : users){

            AccountDTO dto = new AccountDTO();
            dto.setId(user.getId());
            dto.setEmail(user.getEmail());
            dto.setUsername(user.getUsername());
            dto.setRole(user.getRole().getName());
            Profile profile = user.getProfile();
            dto.setFirstName(profile.getFirstName());
            dto.setLastName(profile.getLastName());
            dto.setMotto(profile.getMotto());
            dto.setActive(user.isEnabled());
            dtos.add(dto);

        }

        return dtos;
    }

    public boolean blockUser(long user_id){
        User user = userService.findById(user_id);
        if(user == null){
            return false;
        }

        user.setEnabled(false);
        userService.save(user);
        return true;
    }

    public boolean unblockUser(long user_id){
        User user = userService.findById(user_id);
        if(user == null){
            return false;
        }
        user.setEnabled(true);
        userService.save(user);
        return true;
    }


}

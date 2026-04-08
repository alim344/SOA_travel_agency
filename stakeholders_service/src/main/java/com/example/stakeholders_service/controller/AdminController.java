package com.example.stakeholders_service.controller;

import com.example.stakeholders_service.dto.AccountDTO;
import com.example.stakeholders_service.service.AdminService;
import com.example.stakeholders_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/getAccounts")
    public ResponseEntity<List<AccountDTO>> getAllAccounts() {

        List<AccountDTO> users = adminService.getAllUsers();
        if(users.isEmpty()){
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(users);

    }

    @PatchMapping("/blockUser/{id}")
    public ResponseEntity<String> blockUser(@PathVariable Long id){
        if(adminService.blockUser(id)){
            return ResponseEntity.ok("User blocked");
        }
        return new ResponseEntity<>("User doesnt exist",HttpStatus.CONFLICT);
    }

    @PatchMapping("/unblockUser/{id}")
    public ResponseEntity<String> unblockUser(@PathVariable Long id){
        if(adminService.unblockUser(id)) {
            return ResponseEntity.ok("User unblocked");
        }
        return new ResponseEntity<>("User doesnt exist", HttpStatus.CONFLICT);
    }


}

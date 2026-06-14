package com.example.stakeholders_service.controller;

import com.example.stakeholders_service.dto.AccountDTO;
import com.example.stakeholders_service.saga.BlockUserSagaOrchestrator;
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

    @Autowired
    private BlockUserSagaOrchestrator sagaOrchestrator;

    @GetMapping("/getAccounts")
    public ResponseEntity<List<AccountDTO>> getAllAccounts() {

        List<AccountDTO> users = adminService.getAllUsers();
        if(users.isEmpty()){
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(users);

    }

    @PatchMapping("/blockUser/{id}")
    public ResponseEntity<String> blockUser(@PathVariable Long id) {
        try {
            boolean result = sagaOrchestrator.blockUserSaga(id, false);
            if (!result) return new ResponseEntity<>("User doesn't exist", HttpStatus.CONFLICT);
            return ResponseEntity.ok("User blocked and tours archived");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PatchMapping("/unblockUser/{id}")
    public ResponseEntity<String> unblockUser(@PathVariable Long id){
        if(adminService.unblockUser(id)) {
            return ResponseEntity.ok("User unblocked");
        }
        return new ResponseEntity<>("User doesnt exist", HttpStatus.CONFLICT);
    }

    @PatchMapping("/blockUser-test-fail/{id}")
    public ResponseEntity<String> blockUserTestFail(@PathVariable Long id) {
        try {
            sagaOrchestrator.blockUserSaga(id, true);
            return ResponseEntity.ok("ok");
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

}

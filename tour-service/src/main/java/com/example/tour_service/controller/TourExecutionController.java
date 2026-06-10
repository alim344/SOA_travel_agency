package com.example.tour_service.controller;

import com.example.tour_service.DTO.LocationDTO;
import com.example.tour_service.DTO.TourExecutionDTO;
import com.example.tour_service.model.TourExecution;
import com.example.tour_service.service.TourExecutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/session")
public class TourExecutionController {

    @Autowired
    private TourExecutionService tourExecutionService;

    @PostMapping("/start")
    public ResponseEntity<TourExecutionDTO> startSession(@RequestParam Long touristId, @RequestParam Long tourId){


        TourExecutionDTO execution = tourExecutionService.startSession(touristId, tourId);
        if(execution == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(execution);

    }

    @PostMapping("/abandon/{executionId}")
    public ResponseEntity<TourExecutionDTO> abandonSession(@PathVariable Long executionId){

        TourExecutionDTO execution = tourExecutionService.abandonTour(executionId);
        if(execution == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(execution);

    }

    @PostMapping("/check-position/{executionId}")
    public ResponseEntity<TourExecutionDTO> checkPosition(@PathVariable Long executionId, @RequestBody LocationDTO location){

        TourExecutionDTO execution = tourExecutionService.checkPosition(executionId, location);
        if(execution == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(execution);
    }

}

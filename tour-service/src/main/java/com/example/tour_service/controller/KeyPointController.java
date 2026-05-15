package com.example.tour_service.controller;

import com.example.tour_service.DTO.PointDTO;
import com.example.tour_service.model.KeyPoint;
import com.example.tour_service.service.KeyPointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/keypoint")
public class KeyPointController {

    @Autowired
    private KeyPointService keyPointService;

    @GetMapping("/getDtosByTour/{tourId}")
    public ResponseEntity<List<PointDTO>> getByTourId(@PathVariable Long tourId) {

        List<PointDTO> dtos = keyPointService.GetByTourId(tourId);
        return ResponseEntity.ok(dtos);

    }

    @PostMapping("/add/{tourId}")
    public ResponseEntity<String> addKeyPointToTour(@PathVariable Long tourId, @RequestBody PointDTO pointDTO) {

        boolean i = keyPointService.addKeyPointToTour(pointDTO, tourId);
        if (i){
            return ResponseEntity.ok("success");
        }
        return ResponseEntity.ok("tour doesnt exist");

    }

    @DeleteMapping("/delete/{Id}")
    public ResponseEntity<String> deleteKeyPointFromTour(@PathVariable Long Id) {
        boolean i = keyPointService.deletePoint(Id);
        if (i){
            return ResponseEntity.ok("success");
        }
        return ResponseEntity.ok("key point doesnt exist");
    }

    @PutMapping("/update")
    public ResponseEntity<PointDTO> updatePointDTO(@RequestBody PointDTO pointDTO) {
        PointDTO dto = keyPointService.updateKeyPoint(pointDTO);
        return ResponseEntity.ok(dto);
    }

}

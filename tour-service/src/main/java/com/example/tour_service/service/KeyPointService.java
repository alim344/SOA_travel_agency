package com.example.tour_service.service;

import com.example.tour_service.DTO.PointDTO;
import com.example.tour_service.model.KeyPoint;
import com.example.tour_service.model.Tour;
import com.example.tour_service.repo.KeyPointRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;

import java.awt.*;

@Service
public class KeyPointService {

    @Autowired
    private KeyPointRepository keyPointRepository;

    @Autowired
    private TourService tourService;



    public  List<PointDTO> GetByTourId(Long id){
        List<PointDTO> dtos = new ArrayList<>();

        List<KeyPoint> keyPoints = keyPointRepository.findKeyPointByTourId(id);
        for (KeyPoint keyPoint : keyPoints) {

            PointDTO dto = getPointDTO(keyPoint);
            dtos.add(dto);

        }

        return dtos;

    }

    private static PointDTO getPointDTO(KeyPoint keyPoint) {
        PointDTO dto = new PointDTO();
        dto.setId(keyPoint.getId());
        dto.setLatitude(keyPoint.getLatitude());
        dto.setLongitude(keyPoint.getLongitude());
        dto.setDescription(keyPoint.getDescription());
        dto.setName(keyPoint.getName());
        dto.setImagePath(keyPoint.getImagePath());
        return dto;
    }


    public boolean addKeyPointToTour(PointDTO dto, Long tourId){

        Tour tour = tourService.getById(tourId);
        if(tour == null){
            return false;
        }

        KeyPoint keyPoint = new KeyPoint();
        keyPoint.setDescription(dto.getDescription());
        keyPoint.setLatitude(dto.getLatitude());
        keyPoint.setLongitude(dto.getLongitude());
        keyPoint.setName(dto.getName());
        keyPoint.setImagePath(dto.getImagePath());
        keyPoint.setTour(tour);
        keyPointRepository.save(keyPoint);
        return true;
    }

    public boolean exists(Long id){
        return keyPointRepository.existsById(id);
    }

    public boolean deletePoint(Long id){

        if(!exists(id)){
            return false;
        }

        keyPointRepository.deleteById(id);
        return true;
    }




    public PointDTO updateKeyPoint( PointDTO dto) {

        KeyPoint point = keyPointRepository.findById(dto.getId()).get();
        point.setDescription(dto.getDescription());
        point.setLatitude(dto.getLatitude());
        point.setLongitude(dto.getLongitude());
        point.setName(dto.getName());
        point.setImagePath(dto.getImagePath());

        KeyPoint updatePoint = keyPointRepository.save(point);
        return getPointDTO(updatePoint);

    }

}

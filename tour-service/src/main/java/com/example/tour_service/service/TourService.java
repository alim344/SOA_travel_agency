package com.example.tour_service.service;

import com.example.tour_service.DTO.TourPointDTO;
import com.example.tour_service.model.Tour;
import com.example.tour_service.repo.TourRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TourService {

    @Autowired
    private TourRepository tourRepository;



    public List<TourPointDTO> getTourPointDTOS(){

        List<Tour> tours = tourRepository.findAll();
        List<TourPointDTO> tourPointDTOS = new ArrayList<>();
        for(Tour tour : tours){

            TourPointDTO dto = new TourPointDTO();
            dto.setId(tour.getId());
            dto.setName(tour.getName());
            dto.setPrice(tour.getPrice());
            dto.setAuthorId(tour.getAuthorId());
            dto.setDifficulty(tour.getDifficulty());
            dto.setDescription(tour.getDescription());
            dto.setStatus(tour.getStatus().toString());
            dto.setTags(tour.getTags());
            tourPointDTOS.add(dto);
        }

        return tourPointDTOS;

    }

    public Tour getById(Long id){
        return tourRepository.getById(id);
    }



}

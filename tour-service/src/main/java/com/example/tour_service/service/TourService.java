package com.example.tour_service.service;


import com.example.tour_service.DTO.TourDTO;
import com.example.tour_service.model.Tour;
import com.example.tour_service.model.TourStatus;

import com.example.tour_service.DTO.TourPointDTO;
import com.example.tour_service.repo.TourRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import java.util.ArrayList;


@Service
public class TourService {

    @Autowired
    private TourRepository tourRepository;


    public Tour createTour(TourDTO dto, Long authorId) {
        Tour tour = new Tour();
        tour.setName(dto.getName());
        tour.setDescription(dto.getDescription());
        tour.setDifficulty(dto.getDifficulty());

        String tagsString = String.join(",", dto.getTags());
        tour.setTags(tagsString);

        tour.setAuthorId(authorId);
        tour.setStatus(TourStatus.DRAFT);
        tour.setPrice(0.0);

        return tourRepository.save(tour);
    }

    public List<TourDTO> getToursByAuthor(Long authorId) {
        List<Tour> tours = tourRepository.findByAuthorId(authorId);
        return tours.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    private TourDTO mapToResponseDTO(Tour tour) {
        TourDTO dto = new TourDTO();
        dto.setId(tour.getId());
        dto.setName(tour.getName());
        dto.setDescription(tour.getDescription());
        dto.setDifficulty(tour.getDifficulty());
        dto.setStatus(tour.getStatus());
        dto.setPrice(tour.getPrice());
        dto.setAuthorId(tour.getAuthorId());

        if (tour.getTags() != null && !tour.getTags().isEmpty()) {
            dto.setTags(Arrays.asList(tour.getTags().split(",")));
        } else {
            dto.setTags(Collections.emptyList());
        }

        return dto;
    }



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

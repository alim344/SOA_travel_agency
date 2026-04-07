package com.example.stakeholders_service.controller;

import com.example.stakeholders_service.service.GuideService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/guide")
public class GuideController {

    @Autowired
    private GuideService guideService;
}

package com.lumenlabs.energymanagement.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lumenlabs.energymanagement.dto.dashboard.DashboardResponse;
import com.lumenlabs.energymanagement.service.DashboardService;
import com.lumenlabs.energymanagement.util.SecurityUtils;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;
    
    @Autowired
    private SecurityUtils securityUtils;

    @GetMapping
    public ResponseEntity<DashboardResponse> getDashboard(
            @RequestParam(required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        DashboardResponse response = dashboardService.getDashboardData(securityUtils.getLoggedUserCompany().getId(), startDate, endDate);
        
        return ResponseEntity.ok(response);
    }
}
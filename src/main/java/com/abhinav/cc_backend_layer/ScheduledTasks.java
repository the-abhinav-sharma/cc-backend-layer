package com.abhinav.cc_backend_layer;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.abhinav.cc_backend_layer.service.CCMasterService;

@Component
public class ScheduledTasks {

	@Autowired
	CCMasterService ccMasterService;

    @Scheduled(cron = "0 30 23 * * *", zone = "Asia/Kolkata")
    public void execute() {
    	ccMasterService.sendNotifications();
    }
}
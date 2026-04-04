package com.fitness.activityservice.service;

import com.fitness.activityservice.dto.ActivityRequest;
import com.fitness.activityservice.dto.ActivityResponse;
import com.fitness.activityservice.model.Activity;
import com.fitness.activityservice.repository.ActivityRepository;
import org.springframework.stereotype.Service;

@Service
public class ActivityService {
    private  final ActivityRepository activityRepository;
    public ActivityService(ActivityRepository activityRepository){
        this.activityRepository = activityRepository;
    }
    public ActivityResponse trackActivity(ActivityRequest request) {
        Activity activity = Activity.builder()
                .userId(request.getUserId())
                .type(request.getType())
                .duration(request.getDuration())
                .startTime(request.getStartTime())
                .additionalMetrics(request.getAdditionalMetrics())
                .caloriesBurned(request.getCaloriesBurned())
                .build();
        Activity savedActivity = activityRepository.save(activity);

        return mapToResponse(savedActivity);


    }

    private ActivityResponse mapToResponse(Activity savedActivity) {
        ActivityResponse activityRes = new ActivityResponse();

        activityRes.setId(savedActivity.getId());
        activityRes.setUserId(savedActivity.getUserId());
        activityRes.setType(savedActivity.getType());
        activityRes.setAdditionalMetrics(savedActivity.getAdditionalMetrics());
        activityRes.setDuration(savedActivity.getDuration());
        activityRes.setStartTime(savedActivity.getStartTime());
        activityRes.setCaloriesBurned(savedActivity.getCaloriesBurned());
        activityRes.setCreatedAt(savedActivity.getCreatedAt());
        activityRes.setUpdatedAt(savedActivity.getUpdatedAt());

        return activityRes;
    }
}

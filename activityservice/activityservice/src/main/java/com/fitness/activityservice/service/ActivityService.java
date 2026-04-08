package com.fitness.activityservice.service;

import com.fitness.activityservice.dto.ActivityRequest;
import com.fitness.activityservice.dto.ActivityResponse;
import com.fitness.activityservice.model.Activity;
import com.fitness.activityservice.repository.ActivityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor

@Slf4j
public class ActivityService {

    private  final ActivityRepository activityRepository;

    private final UserValidationService userValidationService;
    private final KafkaTemplate<String ,Activity> kafkaTemplate;

    @Value("${kafka.topic.name}")
    private String topicName;

    public ActivityResponse trackActivity(ActivityRequest request) {

         boolean isValidUser = userValidationService.validateUser(request.getUserId());

         if(!isValidUser){
             throw new RuntimeException( "Invalid User: " + request.getUserId());
         }

        Activity activity = Activity.builder()
                .userId(request.getUserId())
                .type(request.getType())
                .duration(request.getDuration())
                .startTime(request.getStartTime())
                .additionalMetrics(request.getAdditionalMetrics())
                .caloriesBurned(request.getCaloriesBurned())
                .build();
        Activity savedActivity = activityRepository.save(activity);

        //publishing activity to the kafka
        try{
            kafkaTemplate.send(topicName, savedActivity.getUserId(),savedActivity);
        } catch (Exception e) {
            throw new RuntimeException("Failed to publish activity to Kafka", e);
        }

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

package com.uoi.spmsearch.controller;

import com.uoi.spmsearch.dto.*;
import com.uoi.spmsearch.errorhandling.ResourceNotFoundException;
import com.uoi.spmsearch.service.PointOfInterestService;
import com.uoi.spmsearch.service.SearchService;
import com.uoi.spmsearch.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/user")
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Component
public class UserController {

    private final PointOfInterestService pointOfInterestService;
    private final UserService userService;
    private final SearchService searchService;

    @PostMapping("/create/{uid}")
    public void createUserProfile(@PathVariable("uid") String uid, @RequestBody User user) {
        userService.addUserToFirestore(user, uid);
    }

    @GetMapping("/{userUID}")
    public User getUserProfile(@PathVariable("userUID") String uid)
            throws ResourceNotFoundException, ExecutionException, InterruptedException {
        return userService.getUserFromFirestore(uid);
    }

    @GetMapping("/{userUID}/poi/list")
    public HashMap<String, PointOfInterest> getUserPointsOfInterest(@PathVariable("userUID") String userUID)
            throws ExecutionException, InterruptedException, ResourceNotFoundException {
        return userService.getUserPointsOfInterest(userUID);
    }

    @PostMapping("/{userUID}/poi/createandadd")
    public Map<String, PointOfInterest> createAndAddPointOfInterest(@PathVariable("userUID") String userUID, @RequestBody LocationRequest locationRequest)
            throws InterruptedException, ExecutionException, IOException, ResourceNotFoundException {
        PointOfInterest pointOfInterest = pointOfInterestService.createPoIForFirestore(userUID, locationRequest);
        return pointOfInterestService.addPoIToFirestore(userUID, pointOfInterest);
    }

    @PostMapping("/{userUID}/poi/add")
    public Map<String, PointOfInterest> addPointOfInterest(@PathVariable("userUID") String userUID, @RequestBody PointOfInterest pointOfInterest)
            throws InterruptedException, ExecutionException, ResourceNotFoundException {
        return pointOfInterestService.addPoIToFirestore(userUID, pointOfInterest);
    }

    @GetMapping("/{userUID}/poi/{poiUID}/delete")
    public void deletePointOfInterest(@PathVariable("userUID") String userUID, @PathVariable("poiUID") String poiUID)
            throws ExecutionException, InterruptedException, ResourceNotFoundException {
        pointOfInterestService.deletePoIFromFirestore(userUID, poiUID);
    }

    @PostMapping("/{userUID}/poi/batch/delete")
    public void deletePointOfInterestBatch(@PathVariable("userUID") String userUID, @RequestBody List<String> poiUIDs)
            throws ExecutionException, InterruptedException, ResourceNotFoundException {
        pointOfInterestService.deletePoiBatchFromFirestore(userUID, poiUIDs);
    }

    @PostMapping("/{userUID}/poi/{poiUID}/edit")
    public void editPointOfInterest(@PathVariable("userUID") String userUID, @PathVariable("poiUID") String poiUID, @RequestBody PointOfInterest newPoI)
            throws ExecutionException, InterruptedException, ResourceNotFoundException {
        pointOfInterestService.editPoIFromFirestore(userUID, poiUID, newPoI);
    }

    @PostMapping("/{userUID}/warehouseify")
    public List<Listing> searchWarehouseify(@PathVariable("userUID") String userUID, @RequestBody UserQuery userQuery)
            throws ExecutionException, InterruptedException, IOException, ResourceNotFoundException {
        return searchService.searchWarehousify(userUID, userQuery);
    }
}

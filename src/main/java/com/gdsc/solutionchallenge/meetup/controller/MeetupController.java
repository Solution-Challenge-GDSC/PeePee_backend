package com.gdsc.solutionchallenge.meetup.controller;

import com.gdsc.solutionchallenge.global.exception.ApiException;
import com.gdsc.solutionchallenge.global.exception.ApiResponse;
import com.gdsc.solutionchallenge.meetup.GeometryUtils;
import com.gdsc.solutionchallenge.meetup.dto.MeetupReq;
import com.gdsc.solutionchallenge.meetup.dto.MeetupRes;
import com.gdsc.solutionchallenge.meetup.entity.Meetup;
import com.gdsc.solutionchallenge.meetup.service.MeetupService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/meetup")
public class MeetupController {
    @Autowired
    private MeetupService meetupService;

    @PostMapping("/checkDistance/{meetupId}")
    public String checkDistance(@PathVariable Long meetupId, HttpServletRequest request) throws ParseException {
        Meetup meetup = meetupService.findById(meetupId);
        if (meetup == null) {
            return "Meetup not found";
        }

        // 사용자의 현재 위치를 가져오기 위해 HTTP 헤더나 쿼리 파라미터 등을 사용
        Double latitude = Double.parseDouble(request.getHeader("latitude"));
        Double longitude = Double.parseDouble(request.getHeader("longitude"));

        // 사용자의 현재 위치를 Point 객체로 변환
        Point userPoint = (Point) GeometryUtils.createPoint(latitude, longitude);

        // Meetup 엔티티의 위치 정보를 가져와서 Point 객체로 변환
        Point meetupPoint = (Point) GeometryUtils.createPoint(meetup.getLatitude(), meetup.getLongitude());

        // 두 지점 간의 거리를 계산
        double distance = userPoint.distance(meetupPoint);

        return "Distance between user and meetup: " + distance + " meters";
    }

    @PostMapping
    public ApiResponse<String> createMeetup(Principal principal, @RequestPart(value = "image", required = false) List<MultipartFile> multipartFiles,
                                            @Validated @RequestPart(value = "postMeetupReq") MeetupReq.PostMeetupReq postMeetupReq) {
        try {
            return new ApiResponse<>(meetupService.createMeetup(principal.getName(), postMeetupReq, multipartFiles));
        }
        catch (ApiException exception) {
            throw new ApiException(exception.getStatus());
        }
    }

    @GetMapping("my")
    public ApiResponse<List<MeetupRes.GetMeetupRes>> getMeetupById(Principal principal) {
        try{
            return new ApiResponse<>(meetupService.getMeetupById(principal.getName()));
        } catch (ApiException exception){
            throw new ApiException(exception.getStatus());
        }
    }

    /** 게시글 수정하기 **/
    @PatchMapping
    public ApiResponse<String> modifyMeetup(Principal principal, @RequestPart(value = "image", required = false) List<MultipartFile> multipartFiles,
                                            @Validated @RequestPart(value = "patchMeetupReq") MeetupReq.PatchMeetupReq patchMeetupReq) {
        try {
            return new ApiResponse<>(meetupService.modifyMeetup(principal.getName(), patchMeetupReq, multipartFiles));
        }
        catch (ApiException exception){
            throw new ApiException(exception.getStatus());
        }
    }


    /** 게시글을 Id로 삭제하기 **/
    @DeleteMapping("/{board_id}")
    public ApiResponse<String> deleteBoard(Principal principal, @PathVariable(name = "meetup_id") Long meetupId){
        try{
            return new ApiResponse<>(meetupService.deleteMeetup(principal.getName(), meetupId));
        } catch (ApiException exception){
            throw new ApiException(exception.getStatus());
        }
    }
}
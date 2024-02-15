package com.gdsc.solutionchallenge.meetup.controller;

import com.gdsc.solutionchallenge.global.exception.ApiException;
import com.gdsc.solutionchallenge.global.exception.ApiResponse;
import com.gdsc.solutionchallenge.meetup.dto.MeetupReq;
import com.gdsc.solutionchallenge.meetup.dto.MeetupRes;
import com.gdsc.solutionchallenge.meetup.dto.LocationDto;
import com.gdsc.solutionchallenge.meetup.service.MeetupService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/meetup")
public class MeetupController {

    private final MeetupService meetupService;

    @GetMapping("/meetups")
    public ApiResponse<List<MeetupRes.GetMeetupRes>> getMeetupsWithinDistance(
            @RequestParam("latitude") double latitude,
            @RequestParam("longitude") double longitude
    ) {
        try {
            // 올바른 순서로 위도와 경도 값을 LocationDto.Post 객체에 전달
            LocationDto.Post userLocation = LocationDto.Post.of(latitude, longitude);

            return new ApiResponse<>(meetupService.findMeetupsWithinDistance(userLocation));
        }
        catch (ApiException exception) {
            throw new ApiException(exception.getStatus());
        }
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
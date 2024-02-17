package com.gdsc.solutionchallenge.meetup.controller;

import com.gdsc.solutionchallenge.global.exception.ApiException;
import com.gdsc.solutionchallenge.global.exception.ApiResponse;
import com.gdsc.solutionchallenge.meetup.dto.MeetupReq;
import com.gdsc.solutionchallenge.meetup.dto.MeetupRes;
import com.gdsc.solutionchallenge.meetup.dto.LocationDto;
import com.gdsc.solutionchallenge.meetup.service.MeetupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/meetup")
@Tag(name="Meetup❤", description = "Meetup 관련 Api")
public class MeetupController {

    private final MeetupService meetupService;

    /** 반경 내 MEETUP 조회 **/
    @GetMapping("/meetups")
    @Operation(summary = "반경 내 meetup 조회", description = "반경 내 meetup 조회")
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

    /** MEETUP 생성하기 **/
    @PostMapping
    @Operation(summary = "meetup 생성", description = "meetup 생성")
    public ApiResponse<String> createMeetup(Principal principal, @RequestPart(value = "image", required = false) List<MultipartFile> multipartFiles,
                                            @Validated @RequestPart(value = "postMeetupReq") MeetupReq.PostMeetupReq postMeetupReq) {
        try {
            return new ApiResponse<>(meetupService.createMeetup(principal.getName(), postMeetupReq, multipartFiles));
        }
        catch (ApiException exception) {
            throw new ApiException(exception.getStatus());
        }
    }

    /** MEETUP을 meetupId로 조회하기 **/
    @GetMapping("/one/{meetupId}")
    @Operation(summary = "meetup, meetupId로 조회", description = "meetup, meetupId로 조회")
    public ApiResponse<MeetupRes.GetMeetupDetailRes> getMeetupByMeetupId(@PathVariable Long meetupId) {
        try{
            return new ApiResponse<>(meetupService.getMeetupByMeetupId(meetupId));
        } catch (ApiException exception){
            throw new ApiException(exception.getStatus());
        }
    }

    /** 내 MEETUP 조회하기 **/
    @GetMapping("my")
    @Operation(summary = "내가 쓴 meetup 조회", description = "내가 쓴 meetup 조회")
    public ApiResponse<List<MeetupRes.GetMeetupRes>> getMeetupByUserId(Principal principal) {
        try{
            return new ApiResponse<>(meetupService.getMeetupByUserId(principal.getName()));
        } catch (ApiException exception){
            throw new ApiException(exception.getStatus());
        }
    }

    /** MEETUP 수정하기 **/
    @PatchMapping
    @Operation(summary = "meetup 수정", description = "meetup 수정")
    public ApiResponse<String> modifyMeetup(Principal principal, @RequestPart(value = "image", required = false) List<MultipartFile> multipartFiles,
                                            @Validated @RequestPart(value = "patchMeetupReq") MeetupReq.PatchMeetupReq patchMeetupReq) {
        try {
            return new ApiResponse<>(meetupService.modifyMeetup(principal.getName(), patchMeetupReq, multipartFiles));
        }
        catch (ApiException exception){
            throw new ApiException(exception.getStatus());
        }
    }


    /** MEETUP을 Id로 삭제하기 **/
    @DeleteMapping("/{meetup_id}")
    @Operation(summary = "meetup 삭제", description = "meetup 삭제")
    public ApiResponse<String> deleteMeetup(Principal principal, @PathVariable(name = "meetup_id") Long meetupId){
        try{
            return new ApiResponse<>(meetupService.deleteMeetup(principal.getName(), meetupId));
        } catch (ApiException exception){
            throw new ApiException(exception.getStatus());
        }
    }
}
package com.gdsc.solutionchallenge.diary.controller;


import com.gdsc.solutionchallenge.diary.dto.DiaryReq;
import com.gdsc.solutionchallenge.diary.dto.DiaryRes;
import com.gdsc.solutionchallenge.diary.service.DiaryService;
import com.gdsc.solutionchallenge.global.exception.ApiException;
import com.gdsc.solutionchallenge.global.exception.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/diary")
@CrossOrigin(origins = "*")
@Tag(name="Diary❤", description = "Diary 관련 Api")
public class DiaryController {
    private final DiaryService diaryService;
    private final Logger logger = LoggerFactory.getLogger(DiaryController.class);


    /** 다이어리 작성하기 **/
    @PostMapping(consumes = {"multipart/form-data"})
    @Operation(summary = "다이어리 생성", description = "다이어리 생성")
    public ApiResponse<String> createDiary(Principal principal, @RequestPart(value = "image", required = false) List<MultipartFile> multipartFiles,
                                           @RequestPart(value = "postDiaryReq") DiaryReq.postDiaryReq postDiaryReq) {
        try {
            logger.info("Received postDiaryReq: {}", postDiaryReq);
            return new ApiResponse<>(diaryService.createDiary(principal.getName(), postDiaryReq, multipartFiles));
        }
        catch (ApiException exception) {
            return new ApiResponse<>(exception.getStatus());
        }
    }

    /** 날짜별로 다이어리 조회하기 **/
    @GetMapping
    @Operation(summary = "다이어리 조회", description = "다이어리 조회")
    public ApiResponse<DiaryRes.GetDiaryResWrapper> getDiaryByDate(Principal principal, @RequestParam String date) {
        try{
            return new ApiResponse<>(diaryService.getDiaryByDate(principal.getName(), date));
        } catch (ApiException exception){
            return new ApiResponse<>(exception.getStatus());
        }
    }


    /** 다이어리 수정하기 **/
    @PatchMapping
    @Operation(summary = "다이어리 수정", description = "다이어리 수정")
    public ApiResponse<String> modifyDiary(Principal principal, @RequestPart(value = "image", required = false) List<MultipartFile> multipartFiles,
                                           @Validated @RequestPart(value = "patchDiaryReq") DiaryReq.patchDiaryReq patchDiaryReq) {
        try {
            return new ApiResponse<>(diaryService.modifyDiary(principal.getName(), patchDiaryReq, multipartFiles));
        }
        catch (ApiException exception){
            return new ApiResponse<>(exception.getStatus());
        }
    }


    /** 다이어리 삭제하기 **/
    @DeleteMapping("/{diary_id}")
    @Operation(summary = "다이어리 삭제", description = "다이어리 삭제")
    public ApiResponse<String> deleteDiary(Principal principal, @PathVariable(name = "diary_id") Long diaryId){
        try{
            return new ApiResponse<>(diaryService.deleteDiary(principal.getName(), diaryId));
        } catch (ApiException exception){
            return new ApiResponse<>(exception.getStatus());
        }
    }



}

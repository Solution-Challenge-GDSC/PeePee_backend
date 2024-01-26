package com.gdsc.solutionchallenge.board.controller;

import com.gdsc.solutionchallenge.board.dto.BoardReq;
import com.gdsc.solutionchallenge.board.dto.BoardRes;
import com.gdsc.solutionchallenge.board.dto.BoardType;
import com.gdsc.solutionchallenge.board.service.BoardService;
import com.gdsc.solutionchallenge.global.exception.ApiException;
import com.gdsc.solutionchallenge.global.exception.ApiResponse;
import com.gdsc.solutionchallenge.global.jwt.JwtTokenUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/board")
@Tag(name="Board❤", description = "Board 관련 Api")
public class BoardController {
    private final BoardService boardService;

    /** 게시글 생성하기 **/
    @PostMapping
    public ApiResponse<String> createBoard(Principal principal, @RequestPart(value = "image", required = false) List<MultipartFile> multipartFiles,
                                           @Validated @RequestPart(value = "postBoardReq") BoardReq.PostBoardReq postBoardReq) {
        try {
            return new ApiResponse<>(boardService.createBoard(principal.getName(), postBoardReq, multipartFiles));
        }
        catch (ApiException exception) {
            throw new ApiException(exception.getStatus());
        }
    }

    /** 게시글을 category로 조회하기(최신순) **/
    @GetMapping("/{category}")
    public ApiResponse<List<BoardRes.GetBoardRes>> getBoardsByCategory(@PathVariable BoardType category) {
        try{
            return new ApiResponse<>(boardService.getBoardsByCategory(category));
        } catch (ApiException exception){
            throw new ApiException(exception.getStatus());
        }
    }


    /** 게시글을 boardId로 조회하기 **/
    @GetMapping("/one/{boardId}")
    public ApiResponse<BoardRes.GetBoardDetailRes> getBoardByBoardId(@PathVariable Long boardId) {
        try{
            return new ApiResponse<>(boardService.getBoardByBoardId(boardId));
        } catch (ApiException exception){
            throw new ApiException(exception.getStatus());
        }
    }

    /** 게시글을 멤버Id로 조회하기 **/
    @GetMapping("my")
    public ApiResponse<List<BoardRes.GetBoardRes>> getBoardByUserId(Principal principal) {
        try{
            return new ApiResponse<>(boardService.getBoardById(principal.getName()));
        } catch (ApiException exception){
            throw new ApiException(exception.getStatus());
        }
    }

    /** 게시글 수정하기 **/
    @PatchMapping
    public ApiResponse<String> modifyBoard(Principal principal, @RequestPart(value = "image", required = false) List<MultipartFile> multipartFiles,
                                            @Validated @RequestPart(value = "patchBoardReq") BoardReq.PatchBoardReq patchBoardReq) {
        try {
            return new ApiResponse<>(boardService.modifyBoard(principal.getName(), patchBoardReq, multipartFiles));
        }
        catch (ApiException exception){
            throw new ApiException(exception.getStatus());
        }
    }


    /** 게시글을 Id로 삭제하기 **/
    @DeleteMapping("/{board_id}")
    public ApiResponse<String> deleteBoard(Principal principal, @PathVariable(name = "board_id") Long boardId){
        try{
            return new ApiResponse<>(boardService.deleteBoard(principal.getName(), boardId));
        } catch (ApiException exception){
            throw new ApiException(exception.getStatus());
        }
    }

    /** 게시글 좋아요 및 좋아요 취소 **/
    @PostMapping("/{boardId}/like")
    public ApiResponse<String> likeOrUnlikeBoard(Principal principal, @PathVariable Long boardId){
        try{
            return new ApiResponse<>(boardService.likeOrUnlikeBoard(principal.getName(), boardId));
        }
        catch (ApiException exception) {
            throw  new ApiException(exception.getStatus());
        }
    }

    /** 게시글을 내용 or 제목으로 검색하기 **/
  /*  @GetMapping("/search")
    public ApiResponse<List<BoardRes.GetBoardRes>> getBoardsByTitleOrContent(@RequestParam(name="keyword") String keyword) {
        try{
            return new ApiResponse<>(boardService.getBoardsByTitleOrContent(keyword));
        } catch (ApiException exception){
            throw new ApiException(exception.getStatus());
        }
    } */

}

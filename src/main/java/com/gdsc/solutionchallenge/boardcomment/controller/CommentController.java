package com.gdsc.solutionchallenge.boardcomment.controller;


import com.gdsc.solutionchallenge.boardcomment.dto.CommentReq;
import com.gdsc.solutionchallenge.boardcomment.dto.CommentRes;
import com.gdsc.solutionchallenge.boardcomment.repository.CommentRepository;
import com.gdsc.solutionchallenge.boardcomment.service.CommentService;
import com.gdsc.solutionchallenge.global.exception.ApiException;
import com.gdsc.solutionchallenge.global.exception.ApiResponse;
import com.gdsc.solutionchallenge.global.jwt.JwtTokenUtil;
import com.gdsc.solutionchallenge.user.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comment")
@Tag(name="Comment❤", description = "Comment 관련 Api")
public class CommentController {
    private final CommentService commentService;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final JwtTokenUtil jwtTokenUtil;

    /** 댓글 생성 **/
    @PostMapping("/{boardId}")
    @Operation(summary = "댓글 생성", description = "댓글 생성")
    public ApiResponse<String> addComment(Principal principal,
                                                             @RequestBody @Valid CommentReq.PostCommentReq postCommentReq,
                                                             @PathVariable Long boardId) {
        try {
            return new ApiResponse<>(commentService.addComment(principal.getName(),boardId, postCommentReq));
        } catch (ApiException exception) {
            return new ApiResponse<>(exception.getStatus());
        }
    }

    /** 댓글 전체 조회 **/
    @GetMapping("/{boardId}")
    @Operation(summary = "댓글 전체 조회", description = "댓글 전체 조회")
    public ApiResponse<List<CommentRes.GetCommentRes>> getCommentsByBoardId(
            @PathVariable Long boardId) {
        return new ApiResponse<>(commentService.findComments(boardId));
    }

    /** 댓글 수정 **/
    @PatchMapping("/{commentId}")
    @Operation(summary = "댓글 수정", description = "댓글 수정")
    public ApiResponse<String> updateComment(Principal principal,
            @PathVariable Long commentId,
            @RequestBody @Valid CommentReq.PatchCommentReq patchCommentReq){
        try{
            return new ApiResponse<>(commentService
                    .updateComment(principal.getName(),commentId,patchCommentReq));
        }catch (ApiException exception) {
            return new ApiResponse<>(exception.getStatus());
        }
    }

    /** 댓글 삭제 **/
    @DeleteMapping("/{commentId}")
    @Operation(summary = "댓글 삭제", description = "댓글 삭제")
    public ApiResponse<String> deleteComment(Principal principal,
            @PathVariable Long commentId
    ){
        try{
            return new ApiResponse<>(commentService.deleteComment(principal.getName(),commentId));
        } catch (ApiException exception) {
            return new ApiResponse<>(exception.getStatus());
        }
    }


}

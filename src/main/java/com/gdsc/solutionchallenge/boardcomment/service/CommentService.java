package com.gdsc.solutionchallenge.boardcomment.service;

import com.gdsc.solutionchallenge.board.entity.Board;
import com.gdsc.solutionchallenge.board.repository.BoardRepository;
import com.gdsc.solutionchallenge.boardcomment.dto.CommentReq;
import com.gdsc.solutionchallenge.boardcomment.dto.CommentRes;
import com.gdsc.solutionchallenge.boardcomment.entity.Comment;
import com.gdsc.solutionchallenge.boardcomment.repository.CommentRepository;
import com.gdsc.solutionchallenge.global.exception.ApiException;
import com.gdsc.solutionchallenge.global.exception.ApiResponseStatus;
import com.gdsc.solutionchallenge.user.entity.User;
import com.gdsc.solutionchallenge.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import static com.gdsc.solutionchallenge.board.service.BoardService.*;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    /**
     * 댓글 생성
     */
    @Transactional
    public String addComment(String email, Long boardId, CommentReq.PostCommentReq postCommentReq){
        // comment 에대한 black 유저 검증
        // reportService.checkBlackUser("comment", userId);
        try {
            User user = userRepository.findByEmail(email).orElseThrow(() -> {
                throw new ApiException(ApiResponseStatus.NONE_EXIST_USER);
            });
            Board board = boardRepository.findById(boardId).orElseThrow(() -> {
                throw new ApiException(ApiResponseStatus.NOT_EXIST_POST);
            });
            Comment comment = Comment.builder()
                    .board(board)
                    .user(user)
                    .content(postCommentReq.getContent()).build();
            commentRepository.save(comment);
            // board의 댓글 count + 1
            boardRepository.incrementCommentsCountById(boardId);

            return "commentId: " + comment.getCommentId() + "인 댓글을 생성했습니다.";
        } catch (ApiException exception) {
            throw new ApiException(exception.getStatus());
        }
    }

    /**
     * 댓글 전체 조회
     **/
    public List<CommentRes.GetCommentRes> findComments(Long boardId) {
        try {
            List<Comment> comments = commentRepository.findAllByBoardBoardId(boardId);
            List<CommentRes.GetCommentRes> getCommentRes = comments.stream()
                    .map(comment -> new CommentRes.GetCommentRes(comment.getCommentId(), comment.getContent(),
                            comment.getUser().getNickname(), comment.getUser().getProfileImage(), convertLocalDateTimeToLocalDate(comment.getCreatedDate()),
                            convertLocalDateTimeToLocalDate(comment.getUpdatedDate()))).collect(Collectors.toList());

            return getCommentRes;
        } catch (ApiException exception) {
            throw new ApiException(exception.getStatus());
        }
    }

    /**
     * 댓글 수정
     */
    @Transactional
    public String updateComment(String email, Long commentId, CommentReq.PatchCommentReq patchCommentReq){
        try {
            User user = userRepository.findByEmail(email).orElseThrow(() -> {
                throw new ApiException(ApiResponseStatus.NONE_EXIST_USER);
            });
            // 댓글 검증
            Comment comment = commentRepository.findById(commentId).orElseThrow(() -> {
                throw new ApiException(ApiResponseStatus.BAD_REQUEST);
            });
            // 댓글 작성자와 접근자가 같은 사람인지 확인
            if (comment.getUser().getUserId() != user.getUserId()) {
                throw new ApiException(ApiResponseStatus.BAD_REQUEST);
            } else {
                comment.updateComment(patchCommentReq.getContent());
            }
            return "commentId: " + comment.getCommentId() + "인 댓글을 수정했습니다.";
        } catch (ApiException exception) {
            throw new ApiException(exception.getStatus());
        }
    }

    /**
     * 댓글 삭제
     */
    @Transactional
    public String deleteComment(String email, Long commentId){
        try {
            User user = userRepository.findByEmail(email).orElseThrow(() -> {
                throw new ApiException(ApiResponseStatus.NONE_EXIST_USER);
            });

            // 댓글 검증
            Comment comment = commentRepository.findById(commentId).orElseThrow(() -> {
                throw new ApiException(ApiResponseStatus.BAD_REQUEST);
            });
            // 댓글 작성자와 접근자가 같은 사람인지 확인
            if (comment.getUser().getUserId() != user.getUserId()) {
                throw new ApiException(ApiResponseStatus.BAD_REQUEST);
            } else {
                commentRepository.deleteById(commentId);
            }
            // board의 댓글 count - 1
            this.boardRepository.decrementCommentsCountById(comment.getBoard().getBoardId());

            return "commentId: " + comment.getCommentId() + "인 댓글을 삭제했습니다.";
        } catch (ApiException exception) {
            throw new ApiException(exception.getStatus());
        }
    }

    public static String convertLocalDateTimeToLocalDate(LocalDateTime localDateTime) {
        return localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
    public static String convertLocalDateTimeToTime(LocalDateTime localDateTime) {
        LocalDateTime now = LocalDateTime.now();

        long diffTime = localDateTime.until(now, ChronoUnit.SECONDS); // now보다 이후면 +, 전이면 -

        if (diffTime < SEC){
            return diffTime + "초 전";
        }
        diffTime = diffTime / SEC;
        if (diffTime < MIN) {
            return diffTime + "분 전";
        }
        diffTime = diffTime / MIN;
        if (diffTime < HOUR) {
            return diffTime + "시간 전";
        }
        diffTime = diffTime / HOUR;
        if (diffTime < DAY) {
            return diffTime + "일 전";
        }
        diffTime = diffTime / DAY;
        if (diffTime < MONTH) {
            return diffTime + "개월 전";
        }
        diffTime = diffTime / MONTH;
        return diffTime + "년 전";
    }


}

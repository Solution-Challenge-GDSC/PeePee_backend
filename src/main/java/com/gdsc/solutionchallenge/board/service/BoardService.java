package com.gdsc.solutionchallenge.board.service;

import com.gdsc.solutionchallenge.board.dto.BoardReq;
import com.gdsc.solutionchallenge.board.dto.BoardRes;
import com.gdsc.solutionchallenge.board.dto.BoardType;
import com.gdsc.solutionchallenge.board.entity.Board;
import com.gdsc.solutionchallenge.board.entity.LikeBoard;
import com.gdsc.solutionchallenge.board.entity.PostPhoto;
import com.gdsc.solutionchallenge.board.repository.BoardRepository;
import com.gdsc.solutionchallenge.board.repository.LikeBoardRepository;
import com.gdsc.solutionchallenge.board.repository.PostPhotoRepository;
import com.gdsc.solutionchallenge.global.exception.ApiException;
import com.gdsc.solutionchallenge.global.exception.ApiResponseStatus;
import com.gdsc.solutionchallenge.global.image.GetGDSRes;
import com.gdsc.solutionchallenge.global.image.ImageUploadService;
import com.gdsc.solutionchallenge.user.entity.User;
import com.gdsc.solutionchallenge.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.gdsc.solutionchallenge.global.exception.ApiResponseStatus.DATABASE_ERROR;
import static com.gdsc.solutionchallenge.global.exception.ApiResponseStatus.USER_WITHOUT_PERMISSION;


@Service
@RequiredArgsConstructor
public class BoardService {

    public static final int SEC = 60;
    public static final int MIN = 60;
    public static final int HOUR = 24;
    public static final int DAY = 30;
    public static final int MONTH = 12;

    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final PostPhotoRepository postPhotoRepository;
    private final PostPhotoService postPhotoService;
    private final LikeBoardRepository likeBoardRepository;
    private final ImageUploadService imageUploadService;

    @Transactional
    public String createBoard(String email, BoardReq.PostBoardReq postBoardReq, List<MultipartFile> multipartFiles) {
        try {
            // 블랙 유저 검증
           // reportService.checkBlackUser("board",userId);

            User user = userRepository.findByEmail(email).orElseThrow(() -> {
                throw new ApiException(ApiResponseStatus.NONE_EXIST_USER);
            });

            Board board = Board.builder()
                    .title(postBoardReq.getTitle())
                    .content(postBoardReq.getContent())
                    .commentCount(0L)
                    .likeCount(0L)
                    .boardType(postBoardReq.getBoardType())
                    .photoList(new ArrayList<>())
                    .user(user)
                    .build();
            boardRepository.save(board);

            //TODO: 구글 클라우드에 사진 저장
            if (multipartFiles != null) {
                List<GetGDSRes> getS3ResList = imageUploadService.uploadImage(multipartFiles);
                postPhotoService.saveAllPostPhotoByBoard(getS3ResList, board);
            }


            return "boardId: " + board.getBoardId() + "인 게시글을 생성했습니다.";
        } catch (ApiException exception) {
            throw new ApiException(exception.getStatus());
        }
    }

    @Transactional
    public BoardRes.GetBoardDetailRes getBoardByBoardId(Long boardId) {
        Board board = boardRepository.findById(boardId).orElseThrow(() -> {
            throw new ApiException(ApiResponseStatus.NOT_EXIST_POST);
        });
        List<PostPhoto> postPhotos = postPhotoRepository.findAllByBoardId(boardId).orElse(Collections.emptyList());


        List<GetGDSRes> getGDSRes = postPhotos.stream()
                .map(photo -> new GetGDSRes(photo.getImgUrl(), photo.getFileName()))
                .collect(Collectors.toList());
        String profile = null;
        if (board.getUser().getProfileImage() != null) {
            profile = board.getUser().getProfileImage();
        }


        BoardRes.GetBoardDetailRes getBoardDetailRes = new BoardRes.GetBoardDetailRes(board.getBoardId(),
                board.getBoardType(), convertLocalDateTimeToLocalDate(board.getCreatedDate()),
                convertLocalDateTimeToTime(board.getCreatedDate()), board.getUser().getNickname(),
                board.getUser().getProfileImage(), board.getTitle(),  board.getContent(),
                board.getCommentCount(), board.getLikeCount(), getGDSRes);

        return getBoardDetailRes;
    }


    /**
     * 게시글 카테고리별 전체 조회
     **/
    @Transactional
    public List<BoardRes.GetBoardRes> getBoardsByCategory(BoardType category) {
        try {
            List<Board> boards = boardRepository.findAllByBoardTypeOrderByBoardIdDesc(category);
            List<BoardRes.GetBoardRes> getBoardRes = boards.stream()
                    .map(board -> new BoardRes.GetBoardRes(board.getBoardId(), board.getBoardType(),
                            convertLocalDateTimeToLocalDate(board.getCreatedDate()),
                            convertLocalDateTimeToTime(board.getCreatedDate()),
                            board.getUser().getNickname(), board.getTitle(), board.getContent(),
                            board.getCommentCount(), board.getLikeCount()))
                    .collect(Collectors.toList());

            return getBoardRes;
        } catch (Exception exception) {
            throw new ApiException(DATABASE_ERROR);
        }
    }



    @Transactional
    public List<BoardRes.GetBoardRes> getBoardById(String email) {
        try {
            List<Board> boards = boardRepository.findBoardByUser_EmailOrderByBoardIdDesc(email);
            List<BoardRes.GetBoardRes> getBoardRes = boards.stream()
                    .map(board -> new BoardRes.GetBoardRes(board.getBoardId(), board.getBoardType(),
                            convertLocalDateTimeToLocalDate(board.getCreatedDate()),
                            convertLocalDateTimeToTime(board.getCreatedDate()),
                            board.getUser().getNickname(), board.getTitle(), board.getContent(),
                            board.getCommentCount(), board.getLikeCount()))
                    .collect(Collectors.toList());
            return getBoardRes;
        } catch (Exception exception) {
            throw new ApiException(DATABASE_ERROR);
        }
    }

/*
    @Transactional
    public List<BoardRes.GetBoardRes> getBoardsByTitleOrContent(String keyword) {
        try {
            List<Board> boards = boardRepository.findBoardsByTitleOrContentContainingAndHouseId(keyword, houseId);
            List<BoardRes.GetBoardRes> getBoardRes = boards.stream()
                    .map(board -> new BoardRes.GetBoardRes(board.getBoardId(), board.getBoardType(),
                            convertLocalDateTimeToLocalDate(board.getCreatedDate()),
                            convertLocalDateTimeToTime(board.getCreatedDate()),
                            board.getUser().getNickname(), board.getTitle(), board.getContent(),
                            board.getCommentCount(), board.getLikeCount()))
                    .collect(Collectors.toList());

            return getBoardRes;
        } catch (Exception exception) {
            throw new ApiException(DATABASE_ERROR);
        }
    }
*/
    @Transactional
    public String deleteBoard(String email, Long boardId) {

        Board board = boardRepository.findById(boardId).orElseThrow(() -> {
            throw new ApiException(ApiResponseStatus.NOT_EXIST_POST);
        });
        Long writerId = boardRepository.findById(boardId).get().getUser().getUserId();
        Long visitorId = userRepository.findByEmail(email).get().getUserId();

        //TODO: 업로드한 게시글 사진들 삭제
        if (writerId == visitorId) {
            // S3에 업로드된 파일을 삭제하는 명령
            List<PostPhoto> allByBoardId = postPhotoService.findAllByBoardId(boardId);
            if (!allByBoardId.isEmpty()) {
                postPhotoService.deleteAllPostPhotos(allByBoardId);
                postPhotoRepository.deletePostPhotoByBoardId(boardId);
            }
            // 댓글이 있는 경우 댓글 먼저 삭제해야 함.
          /*  List<Comment> comments = commentRepository.findCommentsByBoardId(boardId);
            if (!comments.isEmpty()) {
                commentRepository.deleteCommentsByBoardId(boardId);
            } */
            // 게시글을 삭제하는 명령
            boardRepository.delete(board);
            return "요청하신 게시글에 대한 삭제가 완료되었습니다.";
        } else {
            throw new ApiException(USER_WITHOUT_PERMISSION);
        }
    }

    @Transactional
    public String modifyBoard(String email, BoardReq.PatchBoardReq patchBoardReq,
                              List<MultipartFile> multipartFiles) {
        try {
            Long boardId = patchBoardReq.getBoardId();

            Board board = boardRepository.findById(boardId).orElseThrow(() -> {
                throw new ApiException(ApiResponseStatus.NOT_EXIST_POST);
            });
            User writer = boardRepository.findById(boardId).get().getUser();
            User visitor = userRepository.findByEmail(email).get();
            if (writer.getUserId() == visitor.getUserId()) {
                board.updateBoard(patchBoardReq.getTitle(), patchBoardReq.getContent());
                //TODO: 사진 업데이트, 지우고 다시 저장
                List<PostPhoto> allByBoardId = postPhotoService.findAllByBoardId(boardId);
                postPhotoService.deleteAllPostPhotos(allByBoardId);
                List<Long> ids = postPhotoService.findAllId(board.getBoardId());
                postPhotoService.deleteAllPostPhotoByBoard(ids);

                if (multipartFiles != null) {
                    List<GetGDSRes> getS3ResList = imageUploadService.uploadImage(multipartFiles);
                    postPhotoService.saveAllPostPhotoByBoard(getS3ResList, board);
                }
                return "boardId " + board.getBoardId() + "의 게시글을 수정했습니다.";
            } else {
                throw new ApiException(USER_WITHOUT_PERMISSION);
            }
        } catch (ApiException exception) {
            throw new ApiException(exception.getStatus());
        }
    }

    @Transactional
    public String likeOrUnlikeBoard(String email, Long boardId) {
        try {
            Board board = boardRepository.findById(boardId).orElseThrow(() -> {
            throw new ApiException(ApiResponseStatus.NOT_EXIST_POST);
            });
            User user = userRepository.findByEmail(email).orElseThrow(() -> {
                throw new ApiException(ApiResponseStatus.NONE_EXIST_USER);
            });

            Optional<LikeBoard> likeBoardOptional = likeBoardRepository.findByBoard_BoardIdAndUser_Email(boardId, email);
            if (likeBoardOptional.isPresent()) {
                // 이미 좋아요가 눌러져 있는 상태 -> 좋아요 취소
                LikeBoard likeBoard = likeBoardOptional.get();
                this.likeBoardRepository.deleteById(likeBoard.getId());
                // board의 좋아요 count - 1;
                this.boardRepository.decrementlikesCountById(boardId);
                return "게시글의 좋아요를 취소했습니다.";
            } else {
                // 좋아요가 눌러져 있지 않은 상태 -> 좋아요
                this.likeBoardRepository.save(new LikeBoard(user, board));
                // board의 좋아요 count + 1;
                this.boardRepository.incrementlikesCountById(boardId);
                return "게시글에 좋아요를 눌렀습니다.";
            }
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

package com.gdsc.solutionchallenge.board.repository;

import com.gdsc.solutionchallenge.board.dto.BoardType;
import com.gdsc.solutionchallenge.board.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
    List<Board> findAllByBoardTypeOrderByBoardIdDesc(BoardType boardType);
    List<Board> findBoardByUser_EmailOrderByBoardIdDesc(String email);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Board b SET b.likeCount = b.likeCount + 1 WHERE b.boardId = :boardId")
    void incrementlikesCountById(@Param("boardId") Long boardId);

    @Modifying
    @Query("UPDATE Board b SET b.likeCount = b.likeCount - 1 WHERE b.boardId = :boardId")
    void decrementlikesCountById(@Param("boardId") Long boardId);

    @Modifying
    @Query("UPDATE Board b SET b.commentCount = b.commentCount + 1 WHERE b.boardId = :boardId")
    void incrementCommentsCountById(@Param("boardId") Long boardId);

    @Modifying
    @Query("UPDATE Board b SET b.commentCount = b.commentCount - 1 WHERE b.boardId = :boardId")
    void decrementCommentsCountById(@Param("boardId") Long boardId);
}

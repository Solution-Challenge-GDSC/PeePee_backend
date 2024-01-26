package com.gdsc.solutionchallenge.board.repository;

import com.gdsc.solutionchallenge.board.entity.LikeBoard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeBoardRepository extends JpaRepository<LikeBoard, Long> {
    //Boolean existsByBoard_BoardIdAndUserId(Long boardId, Long userId);
    Optional<LikeBoard> findByBoard_BoardIdAndUser_Email(Long boardId, String email);
   // Long countByBoardBoardId(Long board);
}

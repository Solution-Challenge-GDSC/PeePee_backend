package com.gdsc.solutionchallenge.board.dto;

import lombok.Getter;

@Getter
public enum BoardType {
    FREE_BOARD(2), // 자유 소통 게시판
    INFORMATION_BOARD(3), // 정보 게시판
    WORRY_BOARD(4); // 고민 소통 게시판

    private int value;

    BoardType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}

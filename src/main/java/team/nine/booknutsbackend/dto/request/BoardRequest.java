package team.nine.booknutsbackend.dto.request;

import lombok.Getter;
import team.nine.booknutsbackend.domain.Board;
import team.nine.booknutsbackend.domain.User;

import javax.validation.constraints.NotBlank;

@Getter
public class BoardRequest {

    @NotBlank String title;
    @NotBlank String content;
    @NotBlank String bookTitle;
    @NotBlank String bookAuthor;
    @NotBlank String bookImgUrl;
    @NotBlank String bookGenre;

    public BoardRequest(String title, String content, String bookTitle, String bookAuthor, String bookImgUrl, String bookGenre) {
        this.title = title;
        this.content = content;
        this.bookTitle = bookTitle;
        this.bookAuthor = bookAuthor;
        this.bookImgUrl = bookImgUrl;
        this.bookGenre = bookGenre;
    }

    public static Board boardRequest(BoardRequest boardRequest, User user) {
        Board board = new Board();
        board.setTitle(boardRequest.getTitle());
        board.setContent(boardRequest.getContent());
        board.setBookTitle(boardRequest.getBookTitle());
        board.setBookAuthor(boardRequest.getBookAuthor());
        board.setBookImgUrl(boardRequest.getBookImgUrl());
        board.setBookGenre(boardRequest.getBookGenre());
        board.setUser(user);

        return board;
    }

}

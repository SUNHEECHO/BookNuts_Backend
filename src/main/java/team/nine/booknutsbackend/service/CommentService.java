package team.nine.booknutsbackend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.nine.booknutsbackend.domain.Board;
import team.nine.booknutsbackend.domain.Comment;
import team.nine.booknutsbackend.domain.User;
import team.nine.booknutsbackend.dto.request.CommentRequest;
import team.nine.booknutsbackend.dto.response.CommentResponse;
import team.nine.booknutsbackend.exception.board.BoardNotFoundException;
import team.nine.booknutsbackend.exception.comment.CommentNotFoundException;
import team.nine.booknutsbackend.exception.comment.NotNewCommentCreateException;
import team.nine.booknutsbackend.exception.user.NoAuthException;
import team.nine.booknutsbackend.repository.BoardRepository;
import team.nine.booknutsbackend.repository.CommentRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;

    //댓글 작성
    @Transactional
    public Comment writeComment(Long boardId, CommentRequest commentRequest, User user) {
        if (commentRequest.getContent() == null) throw new NotNewCommentCreateException();

        Board board = boardRepository.findById(boardId).orElseThrow(BoardNotFoundException::new);
        Comment comment = CommentRequest.commentRequest(commentRequest, user, board);
        return commentRepository.save(comment);
    }

    //대댓글 작성
    //Todo: (refactoring) 댓글이 삭제되었을 때 컬럼(isDeleted)으로 비교? or 갱신 테이블 생성?
    @Transactional
    public Comment writeReComment(Long boardId, Long commentId, CommentRequest commentRequest, User user) {
        if (commentRequest.getContent() == null) throw new NotNewCommentCreateException();
        Board board = boardRepository.findById(boardId).orElseThrow(BoardNotFoundException::new);
        Comment parentComment = commentRepository.findById(commentId).orElseThrow(CommentNotFoundException::new);
        if (parentComment.getContent().isEmpty()) throw new CommentNotFoundException();
        Comment reComment = CommentRequest.RecommentRequest(commentRequest, user, board, parentComment);
        return commentRepository.save(reComment);
    }

    //댓글 한 개 조회
    @Transactional(readOnly = true)
    public Comment getComment(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(CommentNotFoundException::new);
    }

    //게시글 별 댓글 리스트 조회
    @Transactional(readOnly = true)
    public List<CommentResponse> getCommentList(Long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(BoardNotFoundException::new);
        List<Comment> comments = commentRepository.findByBoard(board);

        List<Comment> commentList = new ArrayList<>();
        List<CommentResponse> commentResponseList = new ArrayList<>();

        for (Comment comment : comments) {
            if (comment.getParent() == null) {
                commentList.add(comment);
                List<Comment> childComment = commentRepository.findByParentOrderByCreatedDate(comment);
                if (childComment != null) commentList.addAll(childComment);
            }
        }

        for (Comment comment : commentList) {
            commentResponseList.add(CommentResponse.commentResponse(comment));
        }

        return commentResponseList;
    }

    //댓글 수정
    @Transactional
    public Comment updateComment(Long commentId, CommentRequest commentRequest, User user) {
        if (commentRequest.getContent() == null) throw new NotNewCommentCreateException();
        Comment comment = getComment(commentId);
        if (!Objects.equals(comment.getUser().getUserId(), user.getUserId())) throw new NoAuthException();
        comment.setContent(commentRequest.getContent());

        return commentRepository.save(comment);
    }

    //댓글 삭제
    @Transactional
    public void deleteComment(Long commentId, User user) {
        Comment comment = getComment(commentId);
        if (!Objects.equals(comment.getUser().getUserId(), user.getUserId())) throw new NoAuthException();

        //자식 댓글인 경우 & 자식이 없는 부모 댓글인 경우
        if ((comment.getParent() != null) || (comment.getChildren().size() == 0)) {
            commentRepository.delete(comment);
        } else { //자식이 있는 부모 댓글인 경우
            comment.setContent(null);
            commentRepository.save(comment);
        }
    }

}

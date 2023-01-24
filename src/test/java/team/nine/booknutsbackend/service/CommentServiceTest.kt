package team.nine.booknutsbackend.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import team.nine.booknutsbackend.domain.Board
import team.nine.booknutsbackend.domain.Comment
import team.nine.booknutsbackend.domain.User
import team.nine.booknutsbackend.dto.request.CommentRequest
import team.nine.booknutsbackend.exception.comment.CommentNotFoundException
import team.nine.booknutsbackend.exception.comment.NotNewCommentCreateException
import team.nine.booknutsbackend.repository.BoardRepository
import team.nine.booknutsbackend.repository.CommentRepository
import team.nine.booknutsbackend.repository.UserRepository
import java.time.LocalDateTime

@SpringBootTest
class CommentServiceTest @Autowired constructor(
    userRepository: UserRepository,
    private val commentRepository: CommentRepository,
    private val boardRepository: BoardRepository,
    private val commentService: CommentService
) {

    private val user = userRepository.save(User(1L, "loginId", "password", "name", "nickname", "ss2@naver.com", null, null, null, true, null, emptyList(), emptyList(), emptyList(), emptyList(), emptyList()))

    @AfterEach
    fun deleteAll(){
        commentRepository.deleteAll()
        boardRepository.deleteAll()
    }

    @DisplayName("게시글에 댓글 작성이 정상 작동")
    @Test
    fun writeComment() {
        //given
        val commentRequest = CommentRequest("commentContent")
        val board = boardRepository.save(Board(1L, "boardTitle", "boardContent", LocalDateTime.now().toString(), "bookTItle", "bookCotent", "bookAuthor", "bookGenre", user, null, null, null, null))

        //when
        commentService.writeComment(board.boardId, commentRequest, user)

        //then
        val result = commentRepository.findAll()
        assertThat(result).hasSize(1)
        assertThat(result[0].user.userId).isEqualTo(user.userId)
    }

    @DisplayName("부모댓글에 대댓글을 작성이 정상 작동")
    @Test
    fun writeReComment() {
        //given
        val board = boardRepository.save(Board(1L, "boardTitle", "boardContent", LocalDateTime.now().toString(), "bookTItle", "bookCotent", "bookAuthor", "bookGenre", user, null, null, null, null))
        val parentComment = commentRepository.save(Comment(1L, "부모댓글!", LocalDateTime.now().toString(), user, null, null, board))
        val reComment = CommentRequest("commentContent")

        //when
        val result = commentService.writeReComment(board.boardId, parentComment.commentId, reComment, user)

        //then
        assertThat(result.parent.commentId).isEqualTo(parentComment.commentId)
        assertThat(result.content).isEqualTo(reComment.content)
    }

    @DisplayName("삭제된 부모댓글에 대댓글을 작성할 경우 에러 발생")
    @Test
    fun writeReCommentAboutDeletedParentCommentException() {
        //given
        val board = boardRepository.save(Board(1L, "boardTitle", "boardContent", LocalDateTime.now().toString(), "bookTItle", "bookCotent", "bookAuthor", "bookGenre", user, null, null, null, null))
        val reComment = CommentRequest("commentContent")

        //when & then
        val exception = assertThrows<CommentNotFoundException> {
            commentService.writeReComment(board.boardId, 1L, reComment, user)
        }
        assertThat(exception.message).isEqualTo("존재하지 않는 댓글 아이디입니다.")
    }

    @DisplayName("댓글 한개를 조회가 정상 작동")
    @Test
    fun getComment() {
        //given
        val board = boardRepository.save(Board(1L, "boardTitle", "boardContent", LocalDateTime.now().toString(), "bookTItle", "bookCotent", "bookAuthor", "bookGenre", user, null, null, null, null))
        val comment = commentRepository.save(Comment(1L, "댓글!!", LocalDateTime.now().toString(), user, null, null, board))

        //when
        val result = commentService.getComment(comment.commentId)

        //then
        assertThat(result.commentId).isEqualTo(comment.commentId)
        assertThat(result.content).isEqualTo(comment.content)
    }

    @DisplayName("게시글 별 댓글 리스트 조회가 정상 작동")
    @Test
    fun getCommentList() {
        //given
        val board = boardRepository.save(Board(1L, "boardTitle", "boardContent", LocalDateTime.now().toString(), "bookTItle", "bookCotent", "bookAuthor", "bookGenre", user, null, null, null, null))
        val parentComment1 = commentRepository.save(Comment(1L, "부모댓글!", LocalDateTime.now().toString(), user, null, null, board))
        val reComment = commentRepository.save(Comment(2L, "대댓글!", LocalDateTime.now().toString(), user, parentComment1, null, board))
        val parentComment2 = commentRepository.save(Comment(3L, "부모댓글2!", LocalDateTime.now().toString(), user, null, null, board))

        //when
        val results = commentService.getCommentList(board.boardId)

        //then
        assertThat(results).hasSize(3)
        assertThat(results).extracting("commentId").containsExactlyInAnyOrder(parentComment1.commentId, parentComment2.commentId, reComment.commentId)
    }
    
    @DisplayName("댓글 수정이 정상적으로 작동")
    @Test
    fun updateComment() {
        //given
        val board = boardRepository.save(Board(1L, "boardTitle", "boardContent", LocalDateTime.now().toString(), "bookTItle", "bookCotent", "bookAuthor", "bookGenre", user, null, null, null, null))
        val comment = commentRepository.save(Comment(1L, "댓글!", LocalDateTime.now().toString(), user, null, null, board))
        val updateComment = CommentRequest("update Comment")

        //when
        val result = commentService.updateComment(comment.commentId, updateComment, user)

        //then
        assertThat(result.commentId).isEqualTo(comment.commentId)
        assertThat(result.content).isEqualTo(updateComment.content)
    }

    @DisplayName("수정한 댓글 내용이 없는 경우 에러 발생")
    @Test
    fun updateCommentContentIsNull() {
        //given
        val board = boardRepository.save(Board(1L, "boardTitle", "boardContent", LocalDateTime.now().toString(), "bookTItle", "bookCotent", "bookAuthor", "bookGenre", user, null, null, null, null))
        val comment = commentRepository.save(Comment(1L, "댓글!", LocalDateTime.now().toString(), user, null, null, board))
        val updateComment = CommentRequest(null)

        //when & then
        val exception = assertThrows<NotNewCommentCreateException> {
            commentService.updateComment(comment.commentId, updateComment, user)
        }
        assertThat(exception.message).isEqualTo("댓글 내용을 작성하셔야 합니다")
    }

    @DisplayName("대댓글이 없는 댓글을 삭제하는 경우가 정상 작동")
    @Test
    fun deleteSoloComment() {
        //given
        val board = boardRepository.save(Board(1L, "boardTitle", "boardContent", LocalDateTime.now().toString(), "bookTItle", "bookCotent", "bookAuthor", "bookGenre", user, null, null, null, null))
        val comment = commentRepository.save(Comment(1L, "부모댓글!", LocalDateTime.now().toString(), user, null, null, board))

        //when
        commentService.deleteComment(comment.commentId, user)

        //then
        val results = commentRepository.findAll()
        assertThat(results).hasSize(0)
    }

    @DisplayName("대댓글이 있는 댓글을 삭제하는 경우가 정상 작동")
    @Test
    fun deleteParentComment() {
        //given
        val board = boardRepository.save(Board(1L, "boardTitle", "boardContent", LocalDateTime.now().toString(), "bookTItle", "bookCotent", "bookAuthor", "bookGenre", user, null, null, null, null))
        val parentComment = commentRepository.save(Comment(1L, "부모댓글!", LocalDateTime.now().toString(), user, null, null, board))
        val reComment = commentRepository.save(Comment(2L, "대댓글!", LocalDateTime.now().toString(), user, parentComment, null, board))

        //when
        commentService.deleteComment(parentComment.commentId, user)

        //then
        val results = commentRepository.findAll()
        assertThat(results).hasSize(2)
        assertThat(results).extracting("commentId").containsExactlyInAnyOrder(parentComment.commentId, reComment.commentId)
        assertThat(results).extracting("content").containsExactlyInAnyOrder(reComment.content, null)
    }
}
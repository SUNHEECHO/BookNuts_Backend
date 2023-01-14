package team.nine.booknutsbackend.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import team.nine.booknutsbackend.domain.Board
import team.nine.booknutsbackend.domain.Follow
import team.nine.booknutsbackend.domain.User
import team.nine.booknutsbackend.dto.request.BoardRequest
import team.nine.booknutsbackend.repository.BoardRepository
import team.nine.booknutsbackend.repository.FollowRepository
import team.nine.booknutsbackend.repository.UserRepository
import java.time.LocalDateTime

@SpringBootTest
class BoardServiceTest @Autowired constructor(
    private val userRepository: UserRepository,
    private val boardRepository: BoardRepository,
    private val followRepository: FollowRepository,
    private val boardService: BoardService,
){

    @AfterEach
    fun clean() {
        boardRepository.deleteAll()
        followRepository.deleteAll()
        userRepository.deleteAll()
    }

    @Test
    @DisplayName("특정 게시글 조회가 정상 작동")
    fun getPost() {
        //given
        val user = userRepository.save(User(1L, "loginId", "password", "name", "nickname", "ss2@naver.com", null, null, null, true, null, emptyList(), emptyList(), emptyList(), emptyList(), emptyList()))
        val board = boardRepository.save(Board(1L, "boardTitle", "boardContent", LocalDateTime.now().toString(), "bookTitle", "bookContent", "bookAuthor", "bookGenre", user, null, null, null, null))

        //when
        val result = boardService.getPost(board.boardId)

        //then
        assertThat(result.boardId).isEqualTo(board.boardId)
        assertThat(result.bookAuthor).isEqualTo(board.bookAuthor)
    }

    @Test
    @DisplayName("게시글 작성이 정상 작동")
    fun writePost() {
        //given
        val boardRequest = BoardRequest("boardTitle", "boardContent", "bookTitle", "bookAuthor", "bookImgUrl", "bookGenre")
        val user = userRepository.save(User(1L, "loginId", "password", "name", "nickname", "ss2@naver.com", null, null, null, true, null, emptyList(), emptyList(), emptyList(), emptyList(), emptyList()))

        //when
        boardService.writePost(BoardRequest.boardRequest(boardRequest, user))

        //then
        val result = boardRepository.findAll()
        assertThat(result).hasSize(1)
        assertThat(result[0].bookAuthor).isEqualTo(boardRequest.bookAuthor)
    }

    @Test
    @DisplayName("나의 구독 게시글 목록 조회 정상 작동")
    fun getBoardMySubScribe() {
        //given
        val userA = userRepository.save(User(1L, "userA", "password", "userA", "nicknameA", "ss2@naver.com", null, null, null, true, null, emptyList(), emptyList(), emptyList(), emptyList(), emptyList()))
        val userB = userRepository.save(User(2L, "userB", "password", "userB", "nicknameB", "ss@naver.com", null, null, null, true, null, emptyList(), emptyList(), emptyList(), emptyList(), emptyList()))
        followRepository.save(Follow(1L, userA, userB))
        boardRepository.save(Board(1L, "boardTitle1", "boardContent", LocalDateTime.now().toString(), "bookTitle", "bookContent", "bookAuthor", "bookGenre", userA, null, null, null, null))
        boardRepository.save(Board(2L, "boardTitle2", "boardContent", LocalDateTime.now().toString(), "bookTitle", "bookContent", "bookAuthor2", "bookGenre", userA, null, null, null, null))

        //when
        val result = boardService.getBoard(userB, 0)

        //then
        assertThat(result).hasSize(2)
        assertThat(result).extracting("title").containsExactlyInAnyOrder("boardTitle1", "boardTitle2")
    }

    @Test
    @DisplayName("독립 서적 게시글 목록 조회 정상 작동")
    fun getBoardTodayRecommend() {
        //given
        val user = userRepository.save(User(1L, "loginId", "password", "name", "nickname", "ss2@naver.com", null, null, null, true, null, emptyList(), emptyList(), emptyList(), emptyList(), emptyList()))
        boardRepository.save(Board(1L, "boardTitle", "boardContent", LocalDateTime.now().toString(), "bookTitle", "bookContent", "bookAuthor", "bookGenre", user, null, null, null, null))
        val board2 = boardRepository.save(Board(2L, "boardTitle", "boardContent", LocalDateTime.now().toString(), "bookTitle", "bookContent", "bookAuthor2", "독립서적", user, null, null, null, null))

        //when
        val result = boardService.getBoard(user, 2)

        //then
        assertThat(result).hasSize(1)
        assertThat(result[0].bookAuthor).isEqualTo(board2.bookAuthor)
    }

    @Test
    @DisplayName("특정 유저의 게시글 목록 조회 정상 작동")
    fun getBoardList() {
        //given
        val userA = userRepository.save(User(1L, "userA", "password", "userA", "nicknameA", "ss2@naver.com", null, null, null, true, null, emptyList(), emptyList(), emptyList(), emptyList(), emptyList()))
        boardRepository.save(Board(1L, "boardTitle1", "boardContent", LocalDateTime.now().toString(), "bookTitle", "bookContent", "bookAuthor", "bookGenre", userA, null, null, null, null))
        boardRepository.save(Board(2L, "boardTitle2", "boardContent", LocalDateTime.now().toString(), "bookTitle", "bookContent", "bookAuthor2", "bookGenre", userA, null, null, null, null))

        //when
        val result = boardService.getBoardList(userA)

        //then
        assertThat(result).hasSize(2)
        assertThat(result).extracting("title").containsExactlyInAnyOrder("boardTitle1", "boardTitle2")
    }

    @Test
    @DisplayName("게시글 수정이 정상 작동")
    fun updatePost() {
        //given
        val user = userRepository.save(User(1L, "userA", "password", "userA", "nicknameA", "ss2@naver.com", null, null, null, true, null, emptyList(), emptyList(), emptyList(), emptyList(), emptyList()))
        val board = boardRepository.save(Board(1L, "boardTitle1", "boardContent", LocalDateTime.now().toString(), "bookTitle", "bookContent", "bookAuthor", "bookGenre", user, null, null, null, null))
        val boardRequest = BoardRequest("updateTitle", null, "bookTitle", "bookAuthor", "bookImgUrl", "bookGenre")

        //when
        val result = boardService.updatePost(board.boardId, boardRequest, user)

        //then
        assertThat(result.title).isEqualTo(boardRequest.title)
        assertThat(result.content).isEqualTo(board.content)
    }

    @Test
    @DisplayName("게시글 삭제가 정상 작동")
    fun deletePost() {
        //given
        val user = userRepository.save(User(1L, "userA", "password", "userA", "nicknameA", "ss2@naver.com", null, null, null, true, null, emptyList(), emptyList(), emptyList(), emptyList(), emptyList()))
        val board = boardRepository.save(Board(1L, "boardTitle1", "boardContent", LocalDateTime.now().toString(), "bookTitle", "bookContent", "bookAuthor", "bookGenre", user, null, null, null, null))

        //when
        boardService.deletePost(board.boardId, user)

        //then
        val results = boardRepository.findAll()
        assertThat(results).hasSize(0)

    }
}
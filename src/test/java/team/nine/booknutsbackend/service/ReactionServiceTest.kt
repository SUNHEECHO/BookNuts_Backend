package team.nine.booknutsbackend.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import team.nine.booknutsbackend.domain.Board
import team.nine.booknutsbackend.domain.User
import team.nine.booknutsbackend.domain.reaction.Heart
import team.nine.booknutsbackend.domain.reaction.Nuts
import team.nine.booknutsbackend.repository.BoardRepository
import team.nine.booknutsbackend.repository.HeartRepository
import team.nine.booknutsbackend.repository.NutsRepository
import team.nine.booknutsbackend.repository.UserRepository
import java.time.LocalDateTime

@SpringBootTest
class ReactionServiceTest @Autowired constructor(
    private val userRepository: UserRepository,
    private val heartRepository: HeartRepository,
    private val nutsRepository: NutsRepository,
    private val boardRepository: BoardRepository,
    private val reactionService: ReactionService
){

    @AfterEach
    fun deleteAll() {
        nutsRepository.deleteAll()
        heartRepository.deleteAll()
        boardRepository.deleteAll()
        userRepository.deleteAll()
    }

    @DisplayName("넛츠(유익해요) 클릭이 게시글에 정상적으로 반영")
    @Test
    fun clickNuts() {
        //given
        val user = userRepository.save(User(1L, "loginId", "password", "name", "nickname", "ss2@naver.com", null, null, null, true, null, emptyList(), emptyList(), emptyList(), emptyList(), emptyList()))
        val board = boardRepository.save(Board(1L, "boardTitle1", "boardContent", LocalDateTime.now().toString(), "bookTitle", "bookContent", "bookAuthor", "bookGenre", user, null, null, null, null))

        //when
        val result = reactionService.clickNuts(board.boardId, user)

        //then
        assertThat(result).isEqualTo("넛츠 누름")
    }

    @DisplayName("넛츠(유익해요) 클릭이 게시글에 정상적으로 취소")
    @Test
    fun clickNutsToCancel() {
        //given
        val user = userRepository.save(User(1L, "loginId", "password", "name", "nickname", "ss2@naver.com", null, null, null, true, null, emptyList(), emptyList(), emptyList(), emptyList(), emptyList()))
        val board = boardRepository.save(Board(1L, "boardTitle1", "boardContent", LocalDateTime.now().toString(), "bookTitle", "bookContent", "bookAuthor", "bookGenre", user, null, null, null, null))
        val nuts = nutsRepository.save(Nuts(1L, user, board))
        user.nutsList = listOf(nuts)
        userRepository.save(user)

        //when
        val result = reactionService.clickNuts(board.boardId, user)

        //then
        assertThat(result).isEqualTo("넛츠 취소")
    }

    @DisplayName("하트(좋아요) 클릭이 게시글에 정상적으로 취소")
    @Test
    fun clickHeart() {
        //given
        val user = userRepository.save(User(1L, "loginId", "password", "name", "nickname", "ss2@naver.com", null, null, null, true, null, emptyList(), emptyList(), emptyList(), emptyList(), emptyList()))
        val board = boardRepository.save(Board(1L, "boardTitle1", "boardContent", LocalDateTime.now().toString(), "bookTitle", "bookContent", "bookAuthor", "bookGenre", user, null, null, null, null))

        //when
        val result = reactionService.clickHeart(board.boardId, user)

        //then
        assertThat(result).isEqualTo("좋아요 누름")
    }

    @DisplayName("하트(좋아요) 클릭이 게시글에 정상적으로 반영")
    @Test
    fun clickHeartToCancel() {
        //given
        val user = userRepository.save(User(1L, "loginId", "password", "name", "nickname", "ss2@naver.com", null, null, null, true, null, emptyList(), emptyList(), emptyList(), emptyList(), emptyList()))
        val board = boardRepository.save(Board(1L, "boardTitle1", "boardContent", LocalDateTime.now().toString(), "bookTitle", "bookContent", "bookAuthor", "bookGenre", user, null, null, null, null))
        val heart = heartRepository.save(Heart(1L, user, board))
        user.hearts = listOf(heart)
        userRepository.save(user)

        //when
        val result = reactionService.clickHeart(board.boardId, user)

        //then
        assertThat(result).isEqualTo("좋아요 취소")
    }

    @DisplayName("회원 탈퇴 시, 모든 넛츠,/좋아요 삭제가 정상적으로 작동")
    @Test
    fun deleteAllReaction() {
        //given
        val user = userRepository.save(User(1L, "loginId", "password", "name", "nickname", "ss2@naver.com", null, null, null, true, null, emptyList(), emptyList(), emptyList(), emptyList(), emptyList()))
        val board = boardRepository.save(Board(1L, "boardTitle1", "boardContent", LocalDateTime.now().toString(), "bookTitle", "bookContent", "bookAuthor", "bookGenre", user, null, null, null, null))
        val heart = heartRepository.save(Heart(1L, user, board))
        val nuts = nutsRepository.save(Nuts(1L, user, board))
        user.nutsList = listOf(nuts)
        user.hearts = listOf(heart)
        userRepository.save(user)

        //when
        reactionService.deleteAllReaction(user)

        //then
        val nutsResult = nutsRepository.findAll()
        assertThat(nutsResult).hasSize(0)
        val heartResult = heartRepository.findAll()
        assertThat(heartResult).hasSize(0)
    }


}
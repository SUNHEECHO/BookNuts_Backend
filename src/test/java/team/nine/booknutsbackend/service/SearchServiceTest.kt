package team.nine.booknutsbackend.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import team.nine.booknutsbackend.domain.Board
import team.nine.booknutsbackend.domain.User
import team.nine.booknutsbackend.domain.debate.DebateRoom
import team.nine.booknutsbackend.repository.BoardRepository
import team.nine.booknutsbackend.repository.DebateRoomRepository
import team.nine.booknutsbackend.repository.UserRepository
import java.time.LocalDateTime

//Todo: 각 search board, debate, user service로 옮기기?!
@SpringBootTest
class SearchServiceTest @Autowired constructor(
    private val boardRepository: BoardRepository,
    private val debateRoomRepository: DebateRoomRepository,
    private val userRepository: UserRepository,
    private val searchService: SearchService
){

    @AfterEach
    fun deleteAll() {
        debateRoomRepository.deleteAll()
        boardRepository.deleteAll()
        userRepository.deleteAll()
    }

    @Test
    @DisplayName("게시글 관련 키워드로 게시글 검색이 정상 작동")
    fun searchBoard() {
        //given
        val user = userRepository.save(User(1L, "loginId", "password", "name", "nickname", "ss2@naver.com", null, null, null, true, null, emptyList(), emptyList(), emptyList(), emptyList(), emptyList()))
        val board = boardRepository.save(Board(1L, "boardTitle", "boardContent", LocalDateTime.now().toString(), "bookTitle", "bookContent", "bookAuthor", "bookGenre", user, null, null, null, null))

        //when
        val result = searchService.searchBoard("board", user)

        //then
        assertThat(result).hasSize(1)
        assertThat(result[0].boardId).isEqualTo(board.boardId)
    }

    @Test
    @DisplayName("채팅방 관련 키워드로 채팅방 검색이 정상 작동")
    fun searchRoom() {
        //given
        val user = userRepository.save(User(1L, "loginId", "password", "name", "nickname", "ss2@naver.com", null, null, null, true, null, emptyList(), emptyList(), emptyList(), emptyList(), emptyList()))
        val debate = debateRoomRepository.save(DebateRoom(1L, "bookTitle", "bookAuthor", "bookImg", "genre", "topic", "coverImg", 0, 4, 0, 1, 0, user, LocalDateTime.now()))

        //when
        val result = searchService.searchRoom("book")

        //then
        assertThat(result).hasSize(1)
        assertThat(result[0].roomId).isEqualTo(debate.debateRoomId)
    }

    @Test
    @DisplayName("사용자 관련 키워드로 사용자 검색이 정상 작동")
    fun searchUser() {
        //given
        val loginUser = userRepository.save(User(1L, "loginId", "password", "name", "nickname", "ss2@naver.com", null, null, null, true, null, emptyList(), emptyList(), emptyList(), emptyList(), emptyList()))
        val targetUser = userRepository.save(User(2L, "targetId", "password", "targetName", "targetNick", "target@naver.com", null, null, null, true, null, emptyList(), emptyList(), emptyList(), emptyList(), emptyList()))

        //when
        val result = searchService.searchUser("target", loginUser)

        //then
        assertThat(result).hasSize(1)
        assertThat(result[0].userId).isEqualTo(targetUser.userId)
    }
}
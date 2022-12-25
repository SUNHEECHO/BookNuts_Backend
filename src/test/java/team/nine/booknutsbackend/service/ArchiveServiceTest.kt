package team.nine.booknutsbackend.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import team.nine.booknutsbackend.domain.Board
import team.nine.booknutsbackend.domain.User
import team.nine.booknutsbackend.domain.archive.Archive
import team.nine.booknutsbackend.domain.archive.ArchiveBoard
import team.nine.booknutsbackend.dto.request.ArchiveRequest
import team.nine.booknutsbackend.exception.archive.ArchiveDuplicateException
import team.nine.booknutsbackend.repository.ArchiveBoardRepository
import team.nine.booknutsbackend.repository.ArchiveRepository
import team.nine.booknutsbackend.repository.BoardRepository
import team.nine.booknutsbackend.repository.UserRepository
import java.time.LocalDateTime

@SpringBootTest
class ArchiveServiceTest @Autowired constructor(
    private val archiveRepository: ArchiveRepository,
    private val userRepository: UserRepository,
    private val boardRepository: BoardRepository,
    private val archiveBoardRepository: ArchiveBoardRepository,
    private val archiveService: ArchiveService,
) {

//    private val userEntity = User(1L, "loginId", "password", "name", "nickname", "ss2@naver.com", null, null, null, true, null, emptyList(), emptyList(), emptyList(), emptyList(), emptyList())
//    private val archiveEntity = Archive(1L, "title", "content", userEntity, null, LocalDateTime.now().toString(), null)
//    private val boardEntity = Board(1L, "boardTitle", "boardContent", LocalDateTime.now().toString(), "bookTItle", "bookCotent", "bookAuthor", "bookGenre", userEntity, null, null, null, null)

    @AfterEach
    fun clean() {
        boardRepository.deleteAll()
        archiveRepository.deleteAll()
        userRepository.deleteAll()
    }

    @Test
    @DisplayName("아카이브 조회가 정상 작동한다")
    fun getArchiveTest() {
        //given
        val user = userRepository.save(User(1L, "loginId", "password", "name", "nickname", "ss2@naver.com", null, null, null, true, null, emptyList(), emptyList(), emptyList(), emptyList(), emptyList()))
        val archive = archiveRepository.save(Archive(1L, "title", "content", user, null, LocalDateTime.now().toString(), null))

        //when
        val result = archiveService.getArchive(archive.archiveId)

        //then
        assertThat(result.content).isEqualTo("content")
    }

    @Test
    @DisplayName("아카이브 리스트 조회가 정상 작동한다")
    fun getArchiveListTest() {
        //given
        val user = userRepository.save(User(1L, "loginId", "password", "name", "nickname", "ss2@naver.com", null, null, null, true, null, emptyList(), emptyList(), emptyList(), emptyList(), emptyList()))
        archiveRepository.saveAll(listOf(
            Archive(1L, "title", "content", user, null, LocalDateTime.now().toString(), null),
            Archive(2L, "title2", "content2", user, null, LocalDateTime.now().toString(), null)
        ))

        //when
        val result = archiveService.getArchiveList(user)

        //then
        assertThat(result).hasSize(2)
        assertThat(result).extracting("title").containsExactlyInAnyOrder("title", "title2")
    }

    @Test
    @DisplayName("아카이브 생성이 정상 작동한다")
    fun createArchive() {
        //given
        val user = userRepository.save(User(1L, "loginId", "password", "name", "nickname", "ss2@naver.com", null, null, null, true, null, emptyList(), emptyList(), emptyList(), emptyList(), emptyList()))
        val archive = archiveRepository.save(Archive(1L, "title", "content", user, null, LocalDateTime.now().toString(), null))

        //when
        val result = archiveService.createArchive(null, archive)

        //then
        assertThat(result.content).isEqualTo("content")
    }

    @Test
    @DisplayName("특정 아카이브 내의 게시글 조회가 정상 작동한다")
    fun getArchiveBoards() {
        //given
        val user = userRepository.save(User(1L, "loginId", "password", "name", "nickname", "ss2@naver.com", null, null, null, true, null, emptyList(), emptyList(), emptyList(), emptyList(), emptyList()))
        val board = boardRepository.save(Board(1L, "boardTitle", "boardContent", LocalDateTime.now().toString(), "bookTItle", "bookCotent", "bookAuthor", "bookGenre", user, null, null, null, null))
        val archive = archiveRepository.save(Archive(1L, "title", "content", user, null, LocalDateTime.now().toString(), null))
        archiveBoardRepository.save(ArchiveBoard(archive, board, user))

        //when
        val results = archiveService.getArchiveBoards(archive.archiveId, user)

        //then
        assertThat(results).hasSize(1)
        assertThat(results[0].archiveCnt).isEqualTo(1)
        assertThat(results[0].boardId).isEqualTo(board.boardId)
    }

    @Test
    @DisplayName("아카이브에 게시글 추가가 정상 작동")
    fun addPostToArchiveTest() {
        //given
        val user = userRepository.save(User(1L, "loginId", "password", "name", "nickname", "ss2@naver.com", null, null, null, true, null, emptyList(), emptyList(), emptyList(), emptyList(), emptyList()))
        val board = boardRepository.save(Board(1L, "boardTitle", "boardContent", LocalDateTime.now().toString(), "bookTItle", "bookCotent", "bookAuthor", "bookGenre", user, null, null, null, null))
        val archive = archiveRepository.save(Archive(1L, "title", "content", user, null, LocalDateTime.now().toString(), null))

        //when
        archiveService.addPostToArchive(archive.archiveId, board.boardId, user)
        val result = archiveBoardRepository.findAll()

         //then
        assertThat(result[0].archive.archiveId).isEqualTo(archive.archiveId)
        assertThat(result[0].board.boardId).isEqualTo(board.boardId)
        assertThat(result[0].owner.userId).isEqualTo(user.userId)
    }

    @Test
    @DisplayName("아카이브에 게시글 추가가 에러 발생")
    fun addPostToArchiveExceptionTest() {
        //given
        val user = userRepository.save(User(1L, "loginId", "password", "name", "nickname", "ss2@naver.com", null, null, null, true, null, emptyList(), emptyList(), emptyList(), emptyList(), emptyList()))
        val board = boardRepository.save(Board(1L, "boardTitle", "boardContent", LocalDateTime.now().toString(), "bookTItle", "bookCotent", "bookAuthor", "bookGenre", user, null, null, null, null))
        val archive = archiveRepository.save(Archive(1L, "title", "content", user, null, LocalDateTime.now().toString(), null))
        archiveBoardRepository.save(ArchiveBoard(archive, board, user))

        //when & then
        val message = assertThrows<ArchiveDuplicateException> {
            archiveService.addPostToArchive(archive.archiveId, board.boardId, user)
        }.message
        assertThat(message).isEqualTo("이미 아카이브에 존재하는 게시글 아이디입니다.")
    }

    @Test
    @DisplayName("아카이브 삭제가 정상 작동")
    fun deleteArchiveTest() {
        //given
        val user = userRepository.save(User(1L, "loginId", "password", "name", "nickname", "ss2@naver.com", null, null, null, true, null, emptyList(), emptyList(), emptyList(), emptyList(), emptyList()))
        val board = boardRepository.save(Board(1L, "boardTitle", "boardContent", LocalDateTime.now().toString(), "bookTItle", "bookCotent", "bookAuthor", "bookGenre", user, null, null, null, null))
        val archive = archiveRepository.save(Archive(1L, "title", "content", user, null, LocalDateTime.now().toString(), null))
        archiveBoardRepository.save(ArchiveBoard(archive, board, user))

        //when
        archiveService.deleteArchive(archive.archiveId, user)
        val result = archiveBoardRepository.findAll()
        val result2 = archiveRepository.findAll()

        //then
        assertThat(result).hasSize(0)
        assertThat(result2).hasSize(0)
    }

    @Test
    @DisplayName("아카이브 내의 게시글 삭제가 정상 작동")
    fun deleteArchivePostTest() {
        //given
        val user = userRepository.save(User(1L, "loginId", "password", "name", "nickname", "ss2@naver.com", null, null, null, true, null, emptyList(), emptyList(), emptyList(), emptyList(), emptyList()))
        val board = boardRepository.save(Board(1L, "boardTitle", "boardContent", LocalDateTime.now().toString(), "bookTItle", "bookCotent", "bookAuthor", "bookGenre", user, null, null, null, null))
        val archive = archiveRepository.save(Archive(1L, "title", "content", user, null, LocalDateTime.now().toString(), null))
        archiveBoardRepository.save(ArchiveBoard(archive, board, user))

        //when
        archiveService.deleteArchivePost(archive.archiveId, board.boardId, user)
        val result = archiveBoardRepository.findAll()

        //then
        assertThat(result).hasSize(0)
    }

    @Test
    @DisplayName("아카이브 수정이 정상 작동")
    fun updateArchiveTest() {
        //given
        val user = userRepository.save(User(1L, "loginId", "password", "name", "nickname", "ss2@naver.com", null, null, null, true, null, emptyList(), emptyList(), emptyList(), emptyList(), emptyList()))
        val archive = archiveRepository.save(Archive(1L, "title", "content", user, null, LocalDateTime.now().toString(), null))
        val archiveRequest = ArchiveRequest("new Title", "new Content")

        //then
        val result = archiveService.updateArchive(archive.archiveId, archiveRequest, user)

        //when
        assertThat(result.content).isEqualTo("new Content")
    }

    @Test
    @DisplayName("회원 탈퇴 시 회원의 모든 아카이브 삭제 정상 작동")
    fun deleteAllUserArchiveTest() {
        //given
        val user = userRepository.save(User(1L, "loginId", "password", "name", "nickname", "ss2@naver.com", null, null, null, true, null, emptyList(), emptyList(), emptyList(), emptyList(), emptyList()))
        archiveRepository.save(Archive(1L, "title", "content", user, null, LocalDateTime.now().toString(), null))

        //when
        archiveService.deleteAllArchive(user)
        val result = archiveRepository.findAll()

        //then
        assertThat(result).hasSize(0)
    }
}
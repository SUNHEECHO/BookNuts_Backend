package team.nine.booknutsbackend.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.crypto.password.PasswordEncoder
import team.nine.booknutsbackend.dto.request.UserRequest
import team.nine.booknutsbackend.exception.user.PasswordErrorException
import team.nine.booknutsbackend.repository.UserRepository
import team.nine.booknutsbackend.domain.User

@SpringBootTest
class UserServiceTest @Autowired constructor(
    private val userRepository: UserRepository,
    private val userService: UserService,
    private val passwordEncoder: PasswordEncoder,
) {

    private val userRequest = UserRequest("loginId", "password", "name", "nickname", "ss@naver.com", null)
    private val userEntity = User(1L, "loginId", passwordEncoder.encode("password"), "name", "nickname", "ss2@naver.com", null, null, null, true, null, null, null, null, null, null)

    @AfterEach
    fun clean() {
        userRepository.deleteAll()
    }

    @Test
    @DisplayName("사용자 회원가입이 정상 동작한다")
    fun joinTest() {
        //given

        //when
        userService.join(null, UserRequest.userRequest(userRequest))

        //then
        val result = userRepository.findAll()
        assertThat(result).hasSize(1)
        assertThat(result[0].loginId).isEqualTo("loginId")
    }

    @Test
    @DisplayName("사용자 로그인이 정상 동작한다")
    fun loginTest() {
        //given
        userRepository.save(userEntity)

        //when
        val result = userService.login("loginId", "password")

        //then
        assertThat(result.loginId).isEqualTo("loginId")
    }

    @Test
    @DisplayName("사용자 비밀번호 오류로 에러가 발생한다")
    fun loginExceptionTest() {
        //given
        userRepository.save(userEntity)

        //when & then
        try {
            userService.login("loginId", "password2")
        } catch (e: PasswordErrorException) {
            return
        } catch (e: Exception) {
            throw IllegalStateException()
        }
        throw IllegalStateException("기대하는 예외가 발생X")
    }

    @Test
    @DisplayName("사용자 닉네임 존재 여부 확인이 정상 동작한다")
    fun checkNicknameDuplicationTest() {
        //given
        userRepository.save(userEntity)

        //when
        val result = userService.checkNicknameDuplication("nickname")

        //then
        assertThat(result).isTrue
    }

    @Test
    @DisplayName("사용자 아이디 존재 여부 확인이 정상 동작한다")
    fun checkLoginIdDuplicationTest() {
        //given
        userRepository.save(userEntity)

        //when
        val result = userService.checkNicknameDuplication("loginId2")

        //then
        assertThat(result).isFalse
    }

    //Todo: test 불가능? 다른 방법 알아보기
    //BCrypt 라는 해시 함수를 사용한 구현체는 단순히 해시를 하는것 뿐만 아니라 Salt 를 넣는 작업까지 하므로,
    //입력값이 같음에도 불구하고 매번 다른 encoded된 값을 return 해주게 된다.
//
//    @Test
//    fun updatePasswordTest() {
//        //given
//        val user = userRepository.save(userEntity)
//        //when
//        userService.updatePassword("password", "newPassword", user)
//
//        //then
//        val result = userRepository.findAll()[0]
//        assertThat(result.password).isEqualTo(passwordEncoder.encode("newPassword"))
//
//    }

    //Todo: profileImage update 테스트 필요 (s3 연결 후에)
}
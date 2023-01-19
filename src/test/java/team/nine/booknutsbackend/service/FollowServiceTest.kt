package team.nine.booknutsbackend.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import team.nine.booknutsbackend.domain.Follow
import team.nine.booknutsbackend.domain.User
import team.nine.booknutsbackend.exception.follow.AlreadyFollowingException
import team.nine.booknutsbackend.exception.follow.CannotFollowException
import team.nine.booknutsbackend.exception.follow.NotFollowingException
import team.nine.booknutsbackend.repository.FollowRepository
import team.nine.booknutsbackend.repository.UserRepository

@SpringBootTest
class FollowServiceTest @Autowired constructor(
    private val followRepository: FollowRepository,
    private val userRepository: UserRepository,
    private val followService: FollowService
) {

    @AfterEach
    fun deleteAll(){
        followRepository.deleteAll()
        userRepository.deleteAll()
    }

    @DisplayName("팔로우하지 않은 사용자 팔로우가 정상적으로 작동")
    @Test
    fun follow() {
        //given
        val followingUser = userRepository.save(User(1L, "following", "password", "following", "following", "following@naver.com", null, null, null, true, null, emptyList(), emptyList(), emptyList(), emptyList(), emptyList()))
        val followerUser = userRepository.save(User(2L, "follower", "password", "follower", "follower", "follower@naver.com", null, null, null, true, null, emptyList(), emptyList(), emptyList(), emptyList(), emptyList()))

        //when
        followService.follow(followingUser.userId, followerUser.userId)

        //then
        val result = followRepository.findAll()
        assertThat(result).hasSize(1)
        assertThat(result[0].following.userId).isEqualTo(followingUser.userId)
    }

    @DisplayName("이미 팔로우한 사용자를 팔로우 시 에러 발생")
    @Test
    fun alreadyFollowingException() {
        //given
        val followingUser = userRepository.save(User(1L, "following", "password", "following", "following", "following@naver.com", null, null, null, true, null, emptyList(), emptyList(), emptyList(), emptyList(), emptyList()))
        val followerUser = userRepository.save(User(2L, "follower", "password", "follower", "follower", "follower@naver.com", null, null, null, true, null, emptyList(), emptyList(), emptyList(), emptyList(), emptyList()))
        followRepository.save(Follow(1L, followingUser, followerUser))

        //when & then
        val message = assertThrows<AlreadyFollowingException> {
            followService.follow(followingUser.userId, followerUser.userId)
        }.message
        assertThat(message).isEqualTo("이미 팔로잉 중인 계정입니다.")
    }

    @DisplayName("팔로우한 사용자가 본인일 경우 에러 발생")
    @Test
    fun followingUserEqualsFollowerUserException() {
        //given
        val user = userRepository.save(User(1L, "following", "password", "following", "following", "following@naver.com", null, null, null, true, null, emptyList(), emptyList(), emptyList(), emptyList(), emptyList()))

        //when & then
        val message = assertThrows<CannotFollowException> {
            followService.follow(user.userId, user.userId)
        }.message
        assertThat(message).isEqualTo("팔로우할 수 없는 계정입니다.")
    }

    @DisplayName("언팔로우가 정상적으로 작동")
    @Test
    fun unfollow() {
        //given
        val followingUser = userRepository.save(User(1L, "following", "password", "following", "following", "following@naver.com", null, null, null, true, null, emptyList(), emptyList(), emptyList(), emptyList(), emptyList()))
        val followerUser = userRepository.save(User(2L, "follower", "password", "follower", "follower", "follower@naver.com", null, null, null, true, null, emptyList(), emptyList(), emptyList(), emptyList(), emptyList()))
        followRepository.save(Follow(1L, followingUser, followerUser))

        //when
        followService.unfollow(followingUser.userId, followerUser.userId)

        //then
        val result = followRepository.findAll()
        assertThat(result).hasSize(0)
    }

    @DisplayName("팔로우하지 않은 상태에서 언팔로우가 되는 경우 에러 발생")
    @Test
    fun unFollowAboutNotFollowingException() {
        //given
        val userA = userRepository.save(User(1L, "userA", "password", "userA", "userA", "userA@naver.com", null, null, null, true, null, emptyList(), emptyList(), emptyList(), emptyList(), emptyList()))
        val userB = userRepository.save(User(2L, "userB", "password", "userB", "userB", "userB@naver.com", null, null, null, true, null, emptyList(), emptyList(), emptyList(), emptyList(), emptyList()))

        //when & then
        val message = assertThrows<NotFollowingException> {
            followService.unfollow(userA.userId, userB.userId)
        }.message
        assertThat(message).isEqualTo("팔로우하고 있지 않은 계정입니다.")

    }

    @DisplayName("나의 팔로잉 리스트 조회가 정상적으로 작동")
    @Test
    fun getMyFollowingList() {
        //given
        val followingUser = userRepository.save(User(1L, "following", "password", "following", "following", "following@naver.com", null, null, null, true, null, emptyList(), emptyList(), emptyList(), emptyList(), emptyList()))
        val followerUser = userRepository.save(User(2L, "follower", "password", "follower", "follower", "follower@naver.com", null, null, null, true, null, emptyList(), emptyList(), emptyList(), emptyList(), emptyList()))
        followRepository.save(Follow(1L, followingUser, followerUser))

        //when
        val results = followService.getMyFollowingList(followerUser)

        //then
        assertThat(results).hasSize(1)
        assertThat(results[0].userId).isEqualTo(followingUser.userId)
    }

    @DisplayName("나의 팔로워 리스트 조회가 정상적 작동")
    @Test
    fun getMyFollowerList() {
        //given
        val followingUser = userRepository.save(User(1L, "following", "password", "following", "following", "following@naver.com", null, null, null, true, null, emptyList(), emptyList(), emptyList(), emptyList(), emptyList()))
        val followerUser = userRepository.save(User(2L, "follower", "password", "follower", "follower", "follower@naver.com", null, null, null, true, null, emptyList(), emptyList(), emptyList(), emptyList(), emptyList()))
        followRepository.save(Follow(1L, followingUser, followerUser))

        //when
        val results = followService.getMyFollowerList(followingUser)

        //then
        assertThat(results).hasSize(1)
        assertThat(results[0].userId).isEqualTo(followerUser.userId)
    }
}
package team.nine.booknutsbackend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.nine.booknutsbackend.domain.Follow;
import team.nine.booknutsbackend.domain.User;
import team.nine.booknutsbackend.dto.response.FollowResponse;
import team.nine.booknutsbackend.exception.follow.AlreadyFollowingException;
import team.nine.booknutsbackend.exception.follow.CannotFollowException;
import team.nine.booknutsbackend.exception.follow.NotFollowingException;
import team.nine.booknutsbackend.exception.user.UserNotFoundException;
import team.nine.booknutsbackend.repository.FollowRepository;
import team.nine.booknutsbackend.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@RequiredArgsConstructor
@Service
public class FollowService {

    private final FollowRepository followRepository;

    private final UserRepository userRepository;

    //팔로우
    @Transactional
    public void follow(Long followingUserId, Long followerUserId) {
        Follow follow = new Follow();

        if (followRepository.findByFollowingUserIdAndFollowerUserId(followingUserId, followerUserId).isPresent())
            throw new AlreadyFollowingException();
        if (Objects.equals(followingUserId, followerUserId)) throw new CannotFollowException();

        User followingUser = userRepository.findById(followingUserId).orElseThrow(UserNotFoundException::new);
        User followerUser = userRepository.findById(followerUserId).orElseThrow(UserNotFoundException::new);

        follow.setFollowing(followingUser);
        follow.setFollower(followerUser);

        followRepository.save(follow);
    }

    //언팔로우
    @Transactional
    public void unfollow(Long unfollowingUserId, Long followerUserId) {
        Follow follow = followRepository.findByFollowingUserIdAndFollowerUserId(unfollowingUserId, followerUserId)
                .orElseThrow(NotFollowingException::new);
        followRepository.delete(follow);
    }

    //나의 팔로잉 리스트
    @Transactional(readOnly = true)
    public List<FollowResponse> getMyFollowingList(User user) {
        List<Follow> followList = followRepository.findByFollower(user);
        List<FollowResponse> followingList = new ArrayList<>();

        for (Follow follow : followList) {
            followingList.add(FollowResponse.followUserResponse(follow.getFollowing()));
        }

        return followingList;
    }

    //나의 팔로워 리스트
    @Transactional(readOnly = true)
    public List<FollowResponse> getMyFollowerList(User user) {
        List<Follow> followList = followRepository.findByFollowing(user);
        List<FollowResponse> followerList = new ArrayList<>();

        for (Follow follow : followList) {
            followerList.add(FollowResponse.followUserResponse(follow.getFollower()));
        }

        return followerList;

    }

    //회원 탈퇴 시, 모든 팔로우 관계 삭제
    @Transactional
    public void deleteAllFollow(User user) {
        followRepository.deleteAllByFollower(user);
    }

}

package team.nine.booknutsbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.nine.booknutsbackend.domain.Follow;
import team.nine.booknutsbackend.domain.User;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    Optional<Follow> findByFollowingAndFollower(User unfollowing, User follower);
    List<Follow> findByFollower(User curUser); //내 팔로잉 리스트
    List<Follow> findByFollowing(User curUser); //내 팔로워 리스트
    void deleteAllByFollower(User user);
}

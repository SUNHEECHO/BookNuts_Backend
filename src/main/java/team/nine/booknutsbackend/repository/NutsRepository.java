package team.nine.booknutsbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.nine.booknutsbackend.domain.User;
import team.nine.booknutsbackend.domain.reaction.Nuts;

import java.util.Optional;

public interface NutsRepository extends JpaRepository<Nuts, Long> {
    Optional<Nuts> findByBoard_BoardIdAndUser_UserId(Long boardId, Long userId);
    void deleteAllByUser(User user);
}

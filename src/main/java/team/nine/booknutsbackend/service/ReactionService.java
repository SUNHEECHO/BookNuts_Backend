package team.nine.booknutsbackend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.nine.booknutsbackend.domain.Board;
import team.nine.booknutsbackend.domain.User;
import team.nine.booknutsbackend.domain.reaction.Heart;
import team.nine.booknutsbackend.domain.reaction.Nuts;
import team.nine.booknutsbackend.exception.board.BoardNotFoundException;
import team.nine.booknutsbackend.repository.BoardRepository;
import team.nine.booknutsbackend.repository.HeartRepository;
import team.nine.booknutsbackend.repository.NutsRepository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ReactionService {

    private final HeartRepository heartRepository;
    private final NutsRepository nutsRepository;
    private final BoardRepository boardRepository;

    @Transactional
    public String clickNuts(Long boardId, User user) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(BoardNotFoundException::new);

        List<Long> nutsList = user.getNutsList().stream().map(Nuts::getNutsId).toList();
        Optional<Nuts> targetNuts = nutsRepository.findByBoard_BoardIdAndUser_UserId(board.getBoardId(), user.getUserId());

        if (targetNuts.isPresent() && nutsList.contains(targetNuts.get().getNutsId())) {
            nutsRepository.delete(targetNuts.get());
            return "넛츠 취소";
        }

        Nuts nuts = new Nuts();
        nuts.setBoard(board);
        nuts.setUser(user);
        nutsRepository.save(nuts);
        return "넛츠 누름";
    }

    @Transactional
    public String clickHeart(Long boardId, User user) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(BoardNotFoundException::new);

        List<Long> hearts = user.getHearts().stream().map(Heart::getHeartId).toList();
        Optional<Heart> targetHeart = heartRepository.findByBoard_BoardIdAndUser_UserId(board.getBoardId(), user.getUserId());

        if (targetHeart.isPresent() && hearts.contains(targetHeart.get().getHeartId())) {
            heartRepository.delete(targetHeart.get());
            return "좋아요 취소";
        }

        Heart heart = new Heart();
        heart.setBoard(board);
        heart.setUser(user);
        heartRepository.save(heart);
        return "좋아요 누름";
    }

    //회원 탈퇴 시, 모든 넛츠/좋아요 삭제
    @Transactional
    public void deleteAllReaction(User user) {
        nutsRepository.deleteAllByUser(user);
        heartRepository.deleteAllByUser(user);
    }

}

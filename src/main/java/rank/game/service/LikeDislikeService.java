package rank.game.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rank.game.entity.BoardEntity;
import rank.game.entity.LikeDislikeEntity;
import rank.game.entity.MemberEntity;
import rank.game.repository.BoardRepository;
import rank.game.repository.LikeDislikeRepository;
import rank.game.repository.MemberRepository;

import java.util.Optional;

@Service
public class LikeDislikeService {

    @Autowired
    private LikeDislikeRepository likeDislikeRepository;

    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private MemberRepository memberRepository;

    public boolean toggleLikeBoard(Long boardId, Long memberNum) {
        MemberEntity member = memberRepository.findById(memberNum).orElseThrow();
        BoardEntity board = boardRepository.findById(boardId).orElseThrow();

        // 현재 상태 확인
        Optional<LikeDislikeEntity> existingLike = likeDislikeRepository.findByBoardAndMemberAndIsLike(board, member, true);
        Optional<LikeDislikeEntity> existingDislike = likeDislikeRepository.findByBoardAndMemberAndIsLike(board, member, false);

        // 좋아요 상태 토글
        if (existingLike.isPresent()) {
            // 현재 좋아요 상태일 때, 좋아요를 취소하고 좋아요 수 감소
            likeDislikeRepository.delete(existingLike.get());
            board.setLikeCount(board.getLikeCount() - 1);
            boardRepository.save(board);
            return false;
        } else if (existingDislike.isPresent()) {
            // 현재 싫어요 상태일 때, 싫어요를 취소하고 좋아요 추가
            likeDislikeRepository.delete(existingDislike.get());
            board.setDislikeCount(board.getDislikeCount() - 1);
            board.setLikeCount(board.getLikeCount() + 1);
            LikeDislikeEntity like = new LikeDislikeEntity(board, member, true);
            likeDislikeRepository.save(like);
            boardRepository.save(board);
            return true;
        } else {
            // 현재 상태가 없음 (좋아요와 싫어요 모두 없음)
            LikeDislikeEntity like = new LikeDislikeEntity(board, member, true);
            likeDislikeRepository.save(like);
            board.setLikeCount(board.getLikeCount() + 1);
            boardRepository.save(board);
            return true;
        }
    }

    public boolean toggleDislikeBoard(Long boardId, Long memberNum) {
        MemberEntity member = memberRepository.findById(memberNum).orElseThrow();
        BoardEntity board = boardRepository.findById(boardId).orElseThrow();

        // 현재 상태 확인
        Optional<LikeDislikeEntity> existingLike = likeDislikeRepository.findByBoardAndMemberAndIsLike(board, member, true);
        Optional<LikeDislikeEntity> existingDislike = likeDislikeRepository.findByBoardAndMemberAndIsLike(board, member, false);

        // 싫어요 상태 토글
        if (existingDislike.isPresent()) {
            // 현재 싫어요 상태일 때, 싫어요를 취소하고 싫어요 수 감소
            likeDislikeRepository.delete(existingDislike.get());
            board.setDislikeCount(board.getDislikeCount() - 1);
            boardRepository.save(board);
            return false;
        } else if (existingLike.isPresent()) {
            // 현재 좋아요 상태일 때, 좋아요를 취소하고 싫어요 추가
            likeDislikeRepository.delete(existingLike.get());
            board.setLikeCount(board.getLikeCount() - 1);
            board.setDislikeCount(board.getDislikeCount() + 1);
            LikeDislikeEntity dislike = new LikeDislikeEntity(board, member, false);
            likeDislikeRepository.save(dislike);
            boardRepository.save(board);
            return true;
        } else {
            // 현재 상태가 없음 (좋아요와 싫어요 모두 없음)
            LikeDislikeEntity dislike = new LikeDislikeEntity(board, member, false);
            likeDislikeRepository.save(dislike);
            board.setDislikeCount(board.getDislikeCount() + 1);
            boardRepository.save(board);
            return true;
        }
    }
}

package rank.game.repository.mybatis;

import org.apache.ibatis.annotations.Mapper;
import rank.game.entity.CommentEntity;

import java.util.List;

@Mapper
public interface CommentMapper {
    void insertComment(CommentEntity commentEntity);
    List<CommentEntity> selectCommentsByBoardId(Long board);
}
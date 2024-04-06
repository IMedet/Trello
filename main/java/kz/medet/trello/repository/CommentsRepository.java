package kz.medet.trello.repository;

import kz.medet.trello.model.Comments;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentsRepository extends JpaRepository<Comments,Long> {
    List<Comments> findCommentsByTaskId(Long taskId);
    void deleteAllByTaskId(Long taskId);


}

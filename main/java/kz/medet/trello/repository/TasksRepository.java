package kz.medet.trello.repository;

import kz.medet.trello.model.Tasks;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TasksRepository extends JpaRepository<Tasks,Long> {
    List<Tasks> findTasksByFolder_Id(Long folderId);
    void deleteAllByFolder_Id(Long folderId);
}

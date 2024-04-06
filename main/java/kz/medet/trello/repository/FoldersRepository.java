package kz.medet.trello.repository;

import jakarta.transaction.Transactional;
import kz.medet.trello.model.Folders;
import kz.medet.trello.model.TaskCategories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FoldersRepository extends JpaRepository<Folders,Long> {
}

package kz.medet.trello.repository;

import kz.medet.trello.model.TaskCategories;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskCategoriesRepository extends JpaRepository<TaskCategories,Long> {
}

package kz.medet.trello.controller;

import kz.medet.trello.model.Comments;
import kz.medet.trello.model.Folders;
import kz.medet.trello.model.TaskCategories;
import kz.medet.trello.model.Tasks;
import kz.medet.trello.repository.CommentsRepository;
import kz.medet.trello.repository.FoldersRepository;
import kz.medet.trello.repository.TaskCategoriesRepository;
import kz.medet.trello.repository.TasksRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
public class HomeController {
    @Autowired
    private CommentsRepository commentsRepository;

    @Autowired
    private FoldersRepository foldersRepository;

//    public void removeCategoryByIdFromFolder(Long categoryId, Long folderId){
//        foldersRepository.removeCategoryByIdFromFolder(categoryId, folderId);
//    }


    @Autowired
    private TasksRepository tasksRepository;

    @Autowired
    private TaskCategoriesRepository taskCategoriesRepository;

    @GetMapping(value = "/home")
    public String homePage(Model model){

        List<Folders> folders = foldersRepository.findAll();
        model.addAttribute("folders",folders);

        return "home";
    }

    @PostMapping(value = "/addFolder")
    public String getNewFolder(@RequestParam(value = "newFolder") String newFolder){
        Folders folder = new Folders();
        folder.setName(newFolder);
        foldersRepository.save(folder);

        return "redirect:/home";
    }

    @GetMapping(value = "/allTasks/{id}")
    public String allTasksByFolderId(@PathVariable(value = "id") Long folderId,
                                     Model model){
        Folders folder = foldersRepository.findById(folderId).orElseThrow();
        List<Tasks> tasksList = tasksRepository.findTasksByFolder_Id(folderId);
        List<TaskCategories> categoriesList = taskCategoriesRepository.findAll();

        model.addAttribute("categoriesList", categoriesList);
        model.addAttribute("tasksList" , tasksList);
        model.addAttribute("folderId" , folderId);
        model.addAttribute("folder" , folder);

        return "tasksPage";
    }

    @PostMapping(value = "/addTask")
    public String addTask(@RequestParam(value = "taskTitle") String taskTitle,
                          @RequestParam(value = "taskDesc") String taskDesc,
                          @RequestParam(value = "folderId") Long folderId
                          ){
        int initial_status = 0;
        Tasks task = new Tasks();
        task.setDescription(taskDesc);
        task.setTitle(taskTitle);
        task.setStatus(initial_status);
        Folders folder = foldersRepository.findById(folderId).orElseThrow();
        task.setFolder(folder);

        tasksRepository.save(task);

        return "redirect:/allTasks/" + folderId;
    }

    @PostMapping(value = "/addCategory")
    public String addcategory(@RequestParam(value = "folderId") Long folderId,
                              @RequestParam(value = "categoryId") Long categoryId){
        TaskCategories category = taskCategoriesRepository.findById(categoryId).orElseThrow();
        Folders folder = foldersRepository.findById(folderId).orElseThrow();
        List<TaskCategories> categoriesList = folder.getCategories();

        if(categoriesList==null){
            categoriesList=new ArrayList<>();
        }

        taskCategoriesRepository.save(category);

        categoriesList.add(category);
        folder.setCategories(categoriesList);

        foldersRepository.save(folder);

        return "redirect:/allTasks/" + folderId;
    }

    @PostMapping(value = "/removeCategory")
    public String removeCategory(@RequestParam(value = "folderId") Long folderId,
                                 @RequestParam(value = "categoryId") Long categoryId){
        Folders folder = foldersRepository.findById(folderId).orElseThrow();
        List<TaskCategories> taskCategoriesList = folder.getCategories();
        TaskCategories taskCategories = taskCategoriesRepository.findById(categoryId).orElseThrow();


        if (taskCategoriesList != null) {
            taskCategoriesList.remove(taskCategories);
        }

        folder.setCategories(taskCategoriesList);

        foldersRepository.save(folder);

        return "redirect:/allTasks/"+folderId;
    }

    @GetMapping(value = "/taskDetails/{id}")
    public String taskDetails(@PathVariable(value = "id") Long taskId,
                              Model model){
        Tasks task = tasksRepository.findById(taskId).orElseThrow();

        model.addAttribute("task",task);

        List<Comments> comments = commentsRepository.findCommentsByTaskId(taskId);

        model.addAttribute("comments",comments);

        return "taskDetails";
    }

    @PostMapping(value = "/updateTask/{id}")
    public String updateTask(@RequestParam(value = "taskName") String taskName,
                             @PathVariable(value = "id") Long id,
                             @RequestParam(value = "taskStatus") Integer taskStatus,
                             @RequestParam(value = "taskDesc") String taskDesc){
        Tasks task = tasksRepository.findById(id).orElseThrow();
        task.setStatus(taskStatus);
        task.setDescription(taskDesc);
        task.setTitle(taskName);
        tasksRepository.save(task);

        return "redirect:/home";
    }
    @Transactional
    @PostMapping(value = "/deleteTask/{id}")
    public String deleteTask(@PathVariable(value = "id") Long taskId){
        commentsRepository.deleteAllByTaskId(taskId);

        tasksRepository.deleteById(taskId);

        return "redirect:/home";
    }

    @PostMapping(value = "/addComment")
    public String addComment(Model model,
                             @RequestParam(value = "comment") String comment,
                             @RequestParam(value = "taskId") Long taskId){
        Comments comments = new Comments();
        comments.setComment(comment);
        comments.setTask(tasksRepository.findById(taskId).orElseThrow());

        commentsRepository.save(comments);

        return "redirect:/taskDetails/" + taskId;
    }

    @Transactional
    @PostMapping(value = "/deleteFolder/{id}")
    public String deleteFolder(@PathVariable(value = "id") Long id){
        Folders folder = foldersRepository.findById(id).orElseThrow();
        folder.getCategories().clear();

        List<Tasks> tasksList = tasksRepository.findTasksByFolder_Id(id);

        for(Tasks task : tasksList){
            commentsRepository.deleteAllByTaskId(task.getId());
        }

        tasksRepository.deleteAllByFolder_Id(id);

        foldersRepository.delete(folder);

        return "redirect:/home";
    }

    @GetMapping(value = "/getCategoriesPage")
    public String getCategoriesPage(Model model){
        List<TaskCategories> categoriesList = taskCategoriesRepository.findAll();

        model.addAttribute("categoriesList" , categoriesList);

        return "categories";
    }

    @PostMapping(value = "/addTaskCategory")
    public String addCategory(@RequestParam(value = "newCategory") String newCategory){
        TaskCategories taskCategory = new TaskCategories();
        taskCategory.setName(newCategory);

        taskCategoriesRepository.save(taskCategory);

        return "redirect:/getCategoriesPage";
    }

    @GetMapping(value = "/taskCategoryDetails/{id}")
    public String taskCategoryDetails(@PathVariable(value = "id") Long id,
                                      Model model){
        TaskCategories categories = taskCategoriesRepository.findById(id).orElseThrow();
        model.addAttribute("categories" , categories);

        return "taskCategoryDetails";
    }

    @PostMapping(value = "/updateTaskCategory/{id}")
    public String updateTaskCategory(@PathVariable(value = "id") Long taskId,
                                     @RequestParam(value = "taskName") String taskName){
        TaskCategories categories = taskCategoriesRepository.findById(taskId).orElseThrow();
        categories.setName(taskName);

        taskCategoriesRepository.save(categories);

        return "redirect:/getCategoriesPage";
    }

    @PostMapping(value = "/deleteTaskCategory/{id}")
    public String deleteTaskCategory(@PathVariable(value = "id") Long id){
        taskCategoriesRepository.deleteById(id);

        return "redirect:/getCategoriesPage";
    }
}

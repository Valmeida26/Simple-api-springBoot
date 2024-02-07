package com.vinineves.simple.services;

import com.vinineves.simple.models.Task;
import com.vinineves.simple.models.User;
import com.vinineves.simple.repositories.TaskRepository;
import com.vinineves.simple.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private UserService userService;

    public Task findById(Long id){
        Optional<Task> task = this.taskRepository.findById(id);
        return task.orElseThrow(() -> new RuntimeException(
                "tarefa não encontrada! id: " + id + ", Tipo: " + Task.class.getName()
        ));
    }

    @Transactional
    public Task create(Task obj){
        User user = this.userService.findById(obj.getUser().getId());
        obj.setId(null);
        obj.setUser(user);
        obj = this.taskRepository.save(obj);
        return obj;
    }

    @Transactional
    public Task update(Task obj){
        Task newObj = findById(obj.getId());
        newObj.setDescription(obj.getDescription());
        return this.taskRepository.save(newObj);
    }

    public  void delete(Long id){
        findById(id);
        try {
            this.taskRepository.deleteById(id);
        }catch (Exception e){
            throw new RuntimeException("Não é possível excluir pois há entidades relacionadas");
        }
    }
}

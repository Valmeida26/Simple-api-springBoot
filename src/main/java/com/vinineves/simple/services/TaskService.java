package com.vinineves.simple.services;

import com.vinineves.simple.exceptions.AuthorizationException;
import com.vinineves.simple.models.Task;
import com.vinineves.simple.models.User;
import com.vinineves.simple.models.enuns.ProfileEnum;
import com.vinineves.simple.repositories.TaskRepository;
import com.vinineves.simple.security.UserSpringSecurity;
import com.vinineves.simple.services.exceptions.DataBindingViolationException;
import com.vinineves.simple.services.exceptions.ObjectNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private UserService userService;

    public Task findById(Long id){
        Task task = this.taskRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException(
                "tarefa não encontrada! id: " + id + ", Tipo: " + Task.class.getName()
        ));

        UserSpringSecurity userSpringSecurity = UserService.authenticated();
        if (Objects.isNull(userSpringSecurity) || !userSpringSecurity.hasRole(ProfileEnum.ADMIN) && !userHasTask(userSpringSecurity, task)){
            throw new AuthorizationException("Acesso Negado.");
        }
        return task;
    }

    public List<Task> findAllByUser() {
        UserSpringSecurity userSpringSecurity = UserService.authenticated();
        if (Objects.isNull(userSpringSecurity)){
            throw new AuthorizationException("Acesso Negado.");
        }
        List<Task> tasks = this.taskRepository.findByUser_Id(userSpringSecurity.getId());
        return tasks;
    }

    @Transactional
    public Task create(Task obj){
        UserSpringSecurity userSpringSecurity = UserService.authenticated();
        if (Objects.isNull(userSpringSecurity)){
            throw new AuthorizationException("Acesso Negado.");
        }

        User user = this.userService.findById(userSpringSecurity.getId());
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
            throw new DataBindingViolationException("Não é possível excluir pois há entidades relacionadas");
        }
    }

    //Verifica se a task pertence ao usuario logado
    private Boolean userHasTask(UserSpringSecurity userSpringSecurity, Task task){
        return task.getUser().getId().equals(userSpringSecurity.getId());
    }
}

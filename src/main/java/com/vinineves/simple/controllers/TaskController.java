package com.vinineves.simple.controllers;

import com.vinineves.simple.models.Task;
import com.vinineves.simple.services.TaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/task")
public class TaskController {
    @Autowired
    private TaskService taskService;

    //Buscar task
    @GetMapping("/{id}")
    public ResponseEntity<Task> findbyId(@PathVariable Long id){
        Task obj = this.taskService.findById(id);
        return ResponseEntity.ok().body(obj);
    }

    //Busca todas as tasks de usuarios por id
    @GetMapping("/user")
    public ResponseEntity<List<Task>> findAllByUser() {
        List<Task> objs = this.taskService.findAllByUser();
        return ResponseEntity.ok().body(objs);
    }
    //Criar task
    @PostMapping
    @Validated
    public ResponseEntity<Void> create(@Valid @RequestBody Task obj){
        this.taskService.create(obj);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(obj.getId()).toUri();
        return ResponseEntity.created(uri).build();
    }

    //Atualizar task
    @PutMapping("/{id}")
    @Validated
    public ResponseEntity<Void> update(@Valid @RequestBody Task obj, @PathVariable Long id){
        obj.setId(id);
        this.taskService.update(obj);
        return ResponseEntity.noContent().build();
    }

    //Deletar task
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        this.taskService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

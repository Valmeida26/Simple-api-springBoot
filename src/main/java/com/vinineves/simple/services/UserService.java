package com.vinineves.simple.services;

import com.vinineves.simple.models.User;
import com.vinineves.simple.repositories.TaskRepository;
import com.vinineves.simple.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TaskRepository taskRepository;

    //Procurar o usuario pela id
    public User findById(Long id){

        //O Optional quer dizer que posso receber esse objeto User ou posso não receber e ficar vazio, entao pra
        //não dar erro na aplicação o Optional faz retorar ""
        Optional<User> user = this.userRepository.findById(id);

        return user.orElseThrow(() -> new RuntimeException(
                "Usuario não encontrado! id: " + id + ", tipo: " + User.class.getName()
        ));//Nesse return eu digo ou retorna o user ou retorna o Throw

    }

    //Criar um usuario
    @Transactional
    public User create(User obj){

        obj.setId(null);//Garante que se o usuario tentar criar um obj com alguma id ja existente no banco ele vai
        //limpar a id exixtente e criar os novos dados

        obj = this.userRepository.save(obj);
        this.taskRepository.saveAll(obj.getTasks());
        return obj;
    }

    //Atualiza um usuario
    @Transactional
    public User update(User obj){

        User newObj = findById(obj.getId());//Vai pegar o id do usuario
        newObj.setPassword(obj.getPassword());//Permite que o usuario atualize apenas a senha
        return this.userRepository.save(newObj);//Reda a query para salvar o objeto
    }

    public void delete(Long id){

        findById(id);//Procura a id
        try {
            this.userRepository.deleteById(id);
        }catch (Exception e){
            throw new RuntimeException("Não é possível deletar pois há entidades relacionadas à essa id");
        }
    }

    //region teste
    public List<User> findAll(User id){
        List<User> allUsers = this.userRepository.findAll();
        return allUsers;
    }
    //endregion
}

package com.vinineves.simple.services;

import com.vinineves.simple.exceptions.AuthorizationException;
import com.vinineves.simple.models.User;
import com.vinineves.simple.models.enuns.ProfileEnum;
import com.vinineves.simple.repositories.UserRepository;
import com.vinineves.simple.security.UserSpringSecurity;
import com.vinineves.simple.services.exceptions.DataBindingViolationException;
import com.vinineves.simple.services.exceptions.ObjectNotFoundException;
import jakarta.transaction.Transactional;
import org.hibernate.grammars.hql.HqlParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class UserService {

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private UserRepository userRepository;

    //Procurar o usuario pela id
    public User findById(Long id){

        UserSpringSecurity userSpringSecurity = authenticated();
        if (!Objects.nonNull(userSpringSecurity) || !userSpringSecurity.hasRole(ProfileEnum.ADMIN) && !id.equals(userSpringSecurity.getId())){
            throw new AuthorizationException("Acesso Negado");
        }
        //O Optional quer dizer que posso receber esse objeto User ou posso não receber e ficar vazio, entao pra
        //não dar erro na aplicação o Optional faz retorar ""
        Optional<User> user = this.userRepository.findById(id);

        return user.orElseThrow(() -> new ObjectNotFoundException(
                "Usuario não encontrado! id: " + id + ", tipo: " + User.class.getName()
        ));//Nesse return eu digo ou retorna o user ou retorna o Throw

    }

    //Criar um usuario
    @Transactional
    public User create(User obj){

        obj.setId(null);//Garante que se o usuario tentar criar um obj com alguma id ja existente no banco ele vai
        //limpar a id exixtente e criar os novos dados
        //Encripta a senha
        obj.setPassword(this.bCryptPasswordEncoder.encode(obj.getPassword()));
        //Garante que quando o usuario for criado ele seja criado como codigo numero 2 ou seja um USER do ProfileEnum
        obj.setProfiles(Stream.of(ProfileEnum.USER.getCode()).collect(Collectors.toSet()));
        obj = this.userRepository.save(obj);
        return obj;
    }

    //Atualiza um usuario
    @Transactional
    public User update(User obj){

        User newObj = findById(obj.getId());//Vai pegar o id do usuario
        newObj.setPassword(obj.getPassword());//Permite que o usuario atualize apenas a senha
        //Encripta a senha
        newObj.setPassword(this.bCryptPasswordEncoder.encode(obj.getPassword()));
        return this.userRepository.save(newObj);//Reda a query para salvar o objeto
    }

    public void delete(Long id){

        findById(id);//Procura a id
        try {
            this.userRepository.deleteById(id);
        }catch (Exception e){
            throw new DataBindingViolationException("Não é possível deletar pois há entidades relacionadas à essa id");
        }
    }

    //Verifica se tem alguem logado
    public static UserSpringSecurity authenticated(){
        try {
            return (UserSpringSecurity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        }catch (Exception e){
            return null;
        }
    }

    //region teste
    public List<User> findAll(User id){
        List<User> allUsers = this.userRepository.findAll();
        return allUsers;
    }
    //endregion
}

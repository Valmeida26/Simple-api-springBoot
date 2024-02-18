package com.vinineves.simple.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.vinineves.simple.models.enuns.ProfileEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


//è tratato como uma tabela
@Entity
@Table(name = User.TABLE_NAME)
@AllArgsConstructor
//Construtor vazio
@NoArgsConstructor
//faz todos os getters
@Getter
//faz todos os setters
@Setter
//gera o equals and hashcodes
@EqualsAndHashCode
public class User {
    public interface CreateUser{}
    public interface UpdateUser{}

    public static final String TABLE_NAME = "user";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true)
    private Long id;

    @Column(name = "username", length = 100, nullable = false, unique = true)
    @NotNull(groups = CreateUser.class)
    @NotEmpty(groups = CreateUser.class)
    @Size(groups = CreateUser.class, min = 2, max = 100)
    private String username;

    //Para a senha não ser retornada no front end
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "password", length = 60, nullable = false)
    @NotNull(groups = {CreateUser.class, UpdateUser.class})
    @NotEmpty(groups = {CreateUser.class, UpdateUser.class})
    @Size(groups = {CreateUser.class, UpdateUser.class}, min = 8, max = 60)
    private String password;

    //Tasks de usuarios
    @OneToMany(mappedBy = "user")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<Task> tasks = new ArrayList<Task>();

    //Garante que sempre que buscar o usuario traga os perfis do usuario junto
    @ElementCollection(fetch = FetchType.EAGER)
    //Garante que nao retorne ao usuario quais sao seus perfis
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @CollectionTable(name = "user_profile")
    @Column(name = "profile", nullable = false)
    //O set cria listas que não podem ter valores repetidos
    private Set<Integer> profiles = new HashSet<>();

    public Set<ProfileEnum> getProfiles(){
        return this.profiles.stream().map(x -> ProfileEnum.toEnum(x)).collect(Collectors.toSet());
    }

    public void addProfile(ProfileEnum profileEnum){
        this.profiles.add(profileEnum.getCode());
    }

    //Construtor vazio
//    public User(){
//
//    }
//
//    //Construtor com os campos
//    public User(Long id, String username, String password ){
//        this.id = id;
//        this.username = username;
//        this.password = password;
//    }
//
//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }
//
//    public String getUsername() {
//        return username;
//    }
//
//    public void setUsername(String username) {
//        this.username = username;
//    }
//
//    public String getPassword() {
//        return password;
//    }
//
//    public void setPassword(String password) {
//        this.password = password;
//    }
//
//    @JsonIgnore
//    public List<Task> getTasks() {
//        return tasks;
//    }
//
//    public void setTasks(List<Task> tasks) {
//        this.tasks = tasks;
//    }
//
//    //Faz algumas verificações do objeto
//    @Override
//    public boolean equals(Object obj) {
//        //Se o objeto for igal a esse == true
//        if (obj == this){
//            return true;
//        }
//        if (obj == null){
//            return false;
//        }
//        //Se o objeto não for uma instancia de usuario == false
//        if (!(obj instanceof User )){
//            return false;
//        }
//        User other = (User) obj;
//        if (this.id == null) {
//            if (other.id != null){
//                return false;
//            }else if (!this.id.equals(other.id)){
//                return false;
//            }
//        }
//        return Objects.equals(this.id, other.id) && Objects.equals(this.username, other.username) &&
//                Objects.equals(this.password, other.password);
//    }
//
//    @Override
//    public int hashCode() {
//        final int prime = 31;
//        int result = 1;
//        result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());
//        return result;
//    }
}

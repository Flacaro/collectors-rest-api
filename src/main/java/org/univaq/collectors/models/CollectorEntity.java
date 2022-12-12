package org.univaq.collectors.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.univaq.collectors.UserView;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity(name = "collector")
@Table(
        indexes = {
                @Index(name = "collector_email_index", columnList = "email", unique = true),
                @Index(name = "collector_username_index", columnList = "username", unique = true)
        }
)
public class CollectorEntity {

    @JsonView(UserView.Private.class)
    @Id
    @GeneratedValue
    private Long id;

    @JsonView(UserView.Public.class)
    private String name;

    @JsonView(UserView.Public.class)
    private String surname;


    @JsonView(UserView.Public.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate birthday;

    @JsonView(UserView.Public.class)
    @Column(unique = true, nullable = false)
    @NotBlank
    private String username;

    @JsonView(UserView.Public.class)
    @Email
    @NotBlank
    @Column(unique = true, nullable = false)
    private String email;


    @JsonView(UserView.Private.class)
    @NotBlank
    @Column(nullable = false)
    private String password;

    @JsonView(UserView.Private.class)
    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JsonBackReference
    private List<CollectionEntity> favourites = new ArrayList<>();


    public CollectorEntity(Long id, String name, String surname, LocalDate birthday, String username, String email, String password, List<CollectionEntity> favourites) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.birthday = birthday;
        this.username = username;
        this.email = email;
        this.password = new BCryptPasswordEncoder().encode(password);
        this.favourites = favourites;
    }

    public CollectorEntity() {
    }


    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public String getSurname() {
        return surname;
    }


    public void setSurname(String surname) {
        this.surname = surname;
    }


    public LocalDate getBirthday() {
        return birthday;
    }


    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }


    public String getUsername() {
        return username;
    }


    public void setUsername(String username) {
        this.username = username;
    }


    public String getEmail() {
        return email;
    }


    public void setEmail(String email) {
        this.email = email;
    }


    public String getPassword() {
        return password;
    }


    public void setPassword(String password) {
        this.password = password;
    }

    public List<CollectionEntity> getFavourites() {
        return favourites;
    }

    public void addCollectionToFavourites(CollectionEntity collection) {
        this.favourites.add(collection);
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setFavourites(List<CollectionEntity> favourites) {
        this.favourites = favourites;
    }

    public void deleteCollectionFromFavourites(CollectionEntity collection) {
        this.favourites.remove(collection);
    }

    @Override
    public String toString() {
        return "CollectorEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", birthday=" + birthday +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", favourites=" + favourites +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CollectorEntity that = (CollectorEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(surname, that.surname) && Objects.equals(birthday, that.birthday) && Objects.equals(username, that.username) && Objects.equals(email, that.email) && Objects.equals(password, that.password) && Objects.equals(favourites, that.favourites);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, surname, birthday, username, email, password, favourites);
    }
}



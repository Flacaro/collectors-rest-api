package org.univaq.collectors.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.Objects;

@Entity(name = "collector")
public class CollectorEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = true)
    private String name;

    @Column(nullable = true)
    private String surname;

    @JsonFormat(pattern="yyyy-MM-dd")
    @Column(nullable = true)
    private LocalDate birthday;

    @Column(unique = true, nullable = false)
    @NotBlank
    private String username;

    @Email
    @NotBlank
    @Column(unique=true, nullable = false)
    private String email;


    @NotBlank
    @Column(nullable = false)
    private String password;

    public CollectorEntity(Long id, String name, String surname, LocalDate birthday, String username, String email, String password) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.birthday = birthday;
        this.username = username;
        this.email = email;
        this.password = new BCryptPasswordEncoder().encode(password);
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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CollectorEntity that = (CollectorEntity) o;
        return id.equals(that.id) && Objects.equals(name, that.name) && Objects.equals(surname, that.surname) && Objects.equals(birthday, that.birthday) && Objects.equals(username, that.username) && Objects.equals(email, that.email) && Objects.equals(password, that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, surname, birthday, username, email, password);
    }
}

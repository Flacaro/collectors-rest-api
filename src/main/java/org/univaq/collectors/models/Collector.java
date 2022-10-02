package org.univaq.collectors.models;

import java.sql.Date;
import java.time.LocalDate;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity(name = "collectors")
public class Collector {

    @Id
    @GeneratedValue
    private Long collectorId;

    @Column(nullable = true)
    private String name;

    @Column(nullable = true)
    private String surname;

    @JsonFormat(pattern="yyyy-MM-dd")
    @Column(unique=true, nullable = false)
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


    public Collector(Long collectorId, String name, String surname, LocalDate birthday, String username, String email, String password) {
        this.collectorId = collectorId;
        this.name = name;
        this.surname = surname;
        this.birthday = birthday;
        this.username = username;
        this.email = email;
        this.password = new BCryptPasswordEncoder().encode(password);
    }

    public Collector() {}


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Long getCollectorId() {
        return collectorId;
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



}

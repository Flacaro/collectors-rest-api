package org.univaq.collectors.models;

import java.sql.Date;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import org.springframework.lang.NonNull;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Entity
public class Collector {

    @Id
    @GeneratedValue
    private Long id;

    @Basic(optional=true)
    private String name;

    @Basic(optional=true)
    private String surname;

    @Basic(optional=true)
    private Date date;

    @NonNull
    private String username;

    @Email
    @NotBlank
    private String email;


    @NotBlank
    private String password;


    public Collector(Long id, String name, String surname, Date date, String email, String password) {
        this.id = id;
        this.name = name;
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

    public Long getId() {
        return id;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }
    
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

}

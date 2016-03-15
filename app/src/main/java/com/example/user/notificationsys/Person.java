package com.example.user.notificationsys;

import java.io.Serializable;

/**
 * Created by User on 2/18/2016.
 */
public class Person  extends NullPerson implements Serializable{

    private String email, pass, name, category;

    public Person(String email, String name, String pass, String category ){
        setCategory(category);
        setEmail(email);
        setName(name);
        setPass(pass);
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCategory(String category) {
        this.category = category;
    }
    @Override
    public String getCategory() {
        return category;
    }
    @Override
    public String getEmail() {
        return email;
    }
    @Override
    public String getName() {
        return name;
    }
    @Override
    public String getPass() {
        return pass;
    }


    @Override
    public String toString() {
        return  email+" "+name+" "+pass+" "+category;
    }
}

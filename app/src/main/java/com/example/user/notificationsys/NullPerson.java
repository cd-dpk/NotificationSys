package com.example.user.notificationsys;

import java.io.Serializable;

/**
 * Created by User on 2/22/2016.
 */
public class NullPerson implements Serializable{
    private String email, pass, name, category;



    public String getCategory() {
        return "null";
    }

    public String getEmail() {
        return "null";
    }

    public String getName() {
        return "null";
    }

    public String getPass() {
        return "null";
    }
}

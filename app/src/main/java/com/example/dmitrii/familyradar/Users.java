package com.example.dmitrii.familyradar;

import java.util.HashMap;
import java.util.Map;

public class Users {

    public String uid, name, email, image;

    public Users(){

    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Users(String uid, String name, String email, String image) {

        this.uid = uid;
        this.name = name;
        this.email = email;
        this.image = image;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", this.getName());
        result.put("email",this.getEmail());
        result.put("image",this.getImage());
        return result;
    }
}

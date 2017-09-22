package com.lendingapp.neighborly;

/**
 * Created by kishan on 4/17/17.
 */

public class Item {

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String id;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    private String userid;
    private String name;
    private String description;
    private boolean active;

    public Item () {

    }

    public Item(String id, String userid, String name, String description, boolean active) {
        this.id = id;
        this.userid = userid;
        this.name = name;
        this.description = description;
        this.active = active;
    }


}

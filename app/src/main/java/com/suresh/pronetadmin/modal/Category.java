package com.suresh.pronetadmin.modal;

public class Category {

    private int id;
    private String catName;
    private String image;

    public Category(String catName, String image) {
        this.catName = catName;
        this.image = image;
    }

    public Category() {
    }

    public String getCatName() {
        return catName;
    }

    public String getImage() {
        return image;
    }

}

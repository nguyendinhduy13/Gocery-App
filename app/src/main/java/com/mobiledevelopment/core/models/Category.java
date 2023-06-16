package com.mobiledevelopment.core.models;

import com.google.firebase.firestore.DocumentId;

public class Category {
    @DocumentId

    private String id;

    private String name;

    private String image;

    public String getId() {
        return id;
    }

    public String getName(){
        return name;
    }

    public String getImage(){
        return image;
    }

    public void setId(String id){
        this.id = id;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setImage(String image){
        this.image = image;
    }

    public Category(){}

    public Category(String id,String image,String name){
        this.id = id;
        this.image =image;
        this.name =name;
    }
}

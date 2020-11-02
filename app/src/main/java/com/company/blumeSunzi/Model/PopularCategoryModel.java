package com.company.blumeSunzi.Model;

public class PopularCategoryModel {
    private String menu_id,flower_id,name,image;

    public PopularCategoryModel(){

    }
    public PopularCategoryModel(String menu_id, String flower_id, String name, String image)
    {
        this.menu_id=menu_id;
        this.flower_id=flower_id;
        this.name=name;
        this.image=image;
    }

    public String getMenu_id() {
        return menu_id;
    }

    public void setMenu_id(String menu_id) {
        this.menu_id = menu_id;
    }

    public String getFlower_id() {
        return flower_id;
    }

    public void setFlower_id(String food_id) {
        this.flower_id = food_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}

package com.company.blumeserver.Model;

import java.util.List;

public class CategoryModel {
    private String menu_id, name, image;
    List<FlowerModel> flowers;

    public CategoryModel() {

    }

    public CategoryModel(String menu_id) {
        this.menu_id = menu_id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMenu_id() {
        return menu_id;
    }

    public void setMenu_id(String menu_id) {
        this.menu_id = menu_id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<FlowerModel> getFlowers() {
        return flowers;
    }

    public void setFlowers(List<FlowerModel> flowers) {
        this.flowers = flowers;
    }

    public String getName() {
        return name;
    }
}
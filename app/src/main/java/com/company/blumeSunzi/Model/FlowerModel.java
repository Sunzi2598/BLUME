package com.company.blumeSunzi.Model;

import java.util.List;

public class FlowerModel {
    private String key;
    private String name,image,id,description;
    private Long price;
    private List<AddonModel> addon;
    private Double ratingValue;
    private Long ratingCount;
    private List<AddonModel> userSelectedAddon;
    public FlowerModel(){

    }

    public Double getRatingValue() {
        return ratingValue;
    }

    public void setRatingValue(Double ratingValue) {
        this.ratingValue = ratingValue;
    }

    public Long getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(Long ratingCount) {
        this.ratingCount = ratingCount;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public List<AddonModel> getAddon() {
        return addon;
    }

    public void setAddon(List<AddonModel> addon) {
        this.addon = addon;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public List<AddonModel> getUserSelectedAddon() {
        return userSelectedAddon;
    }

    public void setUserSelectedAddon(List<AddonModel> userSelectedAddon) {
        this.userSelectedAddon = userSelectedAddon;
    }
}

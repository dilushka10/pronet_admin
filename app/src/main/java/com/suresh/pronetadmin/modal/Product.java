package com.suresh.pronetadmin.modal;

public class Product {
    private String ProductName;
    private String category;
    private String price;
    private String qty;
    private String description;
    private String image;

    public Product(String productName, String category, String price, String qty, String description, String image) {
        ProductName = productName;
        this.category = category;
        this.price = price;
        this.qty = qty;
        this.description = description;
        this.image = image;
    }

    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}

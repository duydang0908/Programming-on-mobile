package com.example.duyda.onlinesaleshop.Models;

public class Rating {
    private String UId;
    private String ProductId;
    private String rateValure;
    private String comment;

    public Rating() {
    }

    public Rating(String UId, String productId, String rateValure, String comment) {
        this.UId = UId;
        ProductId = productId;
        this.rateValure = rateValure;
        this.comment = comment;
    }

    public String getUId() {
        return UId;
    }

    public void setUId(String UId) {
        this.UId = UId;
    }

    public String getProductId() {
        return ProductId;
    }

    public void setProductId(String productId) {
        ProductId = productId;
    }

    public String getRateValure() {
        return rateValure;
    }

    public void setRateValure(String rateValure) {
        this.rateValure = rateValure;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}

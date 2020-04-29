package com.cloudlevi;

import android.net.Uri;
import android.widget.ImageView;

public class AddFragmentModel {

    private ImageView imageModel;

    private String titleModel;
    private String descriptionModel;
    private String categoryModel;
    private String brandModel;
    private String conditionModel;
    private String priceModel;

    private String imageURL;
    private Uri imageUri;

    private int scrollPositionY;

    public AddFragmentModel(){

    }

    public AddFragmentModel(String mtitleModel, String mimageURL, String mdescriptionModel, String mcategoryModel, String mbrandModel, String mconditionModel, String mpriceModel){

        titleModel = mtitleModel;
        imageURL = mimageURL;
        descriptionModel = mdescriptionModel;
        categoryModel = mcategoryModel;
        brandModel = mbrandModel;
        conditionModel = mconditionModel;
        priceModel = mpriceModel;

    }


    public String getTitleModel() {
        return titleModel;
    }

    public void setTitleModel(String titleModel) {
        this.titleModel = titleModel;
    }

    public String getDescriptionModel() {
        return descriptionModel;
    }

    public void setDescriptionModel(String descriptionModel) {
        this.descriptionModel = descriptionModel;
    }

    public String getCategoryModel() {
        return categoryModel;
    }

    public void setCategoryModel(String categoryModel) {
        this.categoryModel = categoryModel;
    }

    public String getBrandModel() {
        return brandModel;
    }

    public void setBrandModel(String brandModel) {
        this.brandModel = brandModel;
    }

    public String getConditionModel() {
        return conditionModel;
    }

    public void setConditionModel(String conditionModel) {
        this.conditionModel = conditionModel;
    }

    public ImageView getImageModel() {
        return imageModel;
    }

    public void setImageModel(ImageView imageModel) {
        this.imageModel = imageModel;
    }

    public String getPriceModel() {
        return priceModel;
    }

    public void setPriceModel(String priceModel) {
        this.priceModel = priceModel;
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public int getScrollPositionY() {
        return scrollPositionY;
    }

    public void setScrollPositionY(int scrollPositionY) {
        this.scrollPositionY = scrollPositionY;
    }
}

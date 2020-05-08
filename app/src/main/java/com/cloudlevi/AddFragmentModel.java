package com.cloudlevi;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ImageView;

import com.google.firebase.database.Exclude;

public class AddFragmentModel implements Parcelable {

    private ImageView imageModel;

    private String titleModel;
    private String descriptionModel;
    private String categoryModel;
    private String brandModel;
    private String sizeModel;
    private String conditionModel;
    private String priceModel;

    private String uploadIDModel;

    private String imageURL;
    private Uri imageUri;

    private int scrollPositionY;

    //User data:
    private String usernameModel;
    private String userIdModel;

    public AddFragmentModel(){
    }

    public AddFragmentModel(String mtitleModel, String mimageURL, String mdescriptionModel, String mcategoryModel, String mbrandModel, String msizeModel, String mconditionModel, String mpriceModel, String musernameModel, String muserIdModel, String muploadIDModel){

        titleModel = mtitleModel;
        imageURL = mimageURL;
        descriptionModel = mdescriptionModel;
        categoryModel = mcategoryModel;
        brandModel = mbrandModel;
        sizeModel = msizeModel;
        conditionModel = mconditionModel;
        priceModel = mpriceModel;
        usernameModel = musernameModel;
        userIdModel = muserIdModel;
        uploadIDModel = muploadIDModel;

    }


    protected AddFragmentModel(Parcel in) {
        titleModel = in.readString();
        descriptionModel = in.readString();
        categoryModel = in.readString();
        brandModel = in.readString();
        sizeModel = in.readString();
        conditionModel = in.readString();
        priceModel = in.readString();
        imageURL = in.readString();
        imageUri = in.readParcelable(Uri.class.getClassLoader());
        scrollPositionY = in.readInt();
        usernameModel = in.readString();
        userIdModel = in.readString();
        uploadIDModel = in.readString();
    }

    public static final Creator<AddFragmentModel> CREATOR = new Creator<AddFragmentModel>() {
        @Override
        public AddFragmentModel createFromParcel(Parcel in) {
            return new AddFragmentModel(in);
        }

        @Override
        public AddFragmentModel[] newArray(int size) {
            return new AddFragmentModel[size];
        }
    };

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

    public String getSizeModel() {
        return sizeModel;
    }

    public void setSizeModel(String sizeModel) {
        this.sizeModel = sizeModel;
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

    @Exclude
    public int getScrollPositionY() {
        return scrollPositionY;
    }

    @Exclude
    public void setScrollPositionY(int scrollPositionY) {
        this.scrollPositionY = scrollPositionY;
    }

    public String getUsernameModel() {
        return usernameModel;
    }

    public void setUsernameModel(String usernameModel) {
        this.usernameModel = usernameModel;
    }

    public String getUserIdModel() {
        return userIdModel;
    }

    public void setUserIdModel(String userIdModel) {
        this.userIdModel = userIdModel;
    }

    public String getUploadIDModel() {
        return uploadIDModel;
    }

    public void setUploadIDModel(String uploadIDModel) {
        this.uploadIDModel = uploadIDModel;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(titleModel);
        dest.writeString(descriptionModel);
        dest.writeString(categoryModel);
        dest.writeString(brandModel);
        dest.writeString(sizeModel);
        dest.writeString(conditionModel);
        dest.writeString(priceModel);
        dest.writeString(imageURL);
        dest.writeParcelable(imageUri, flags);
        dest.writeInt(scrollPositionY);
        dest.writeString(usernameModel);
        dest.writeString(userIdModel);
        dest.writeString(uploadIDModel);
    }
}

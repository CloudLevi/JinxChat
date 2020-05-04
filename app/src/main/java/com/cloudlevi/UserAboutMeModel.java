package com.cloudlevi;

public class UserAboutMeModel {
    private String bioModel;
    private String genderModel;
    private String birthdayModel;
    private String countryModel;
    private String cityModel;

    public UserAboutMeModel(){
    }

    public UserAboutMeModel(String bioModel, String genderModel, String birthdayModel, String countryModel, String cityModel) {
        this.bioModel = bioModel;
        this.genderModel = genderModel;
        this.birthdayModel = birthdayModel;
        this.countryModel = countryModel;
        this.cityModel = cityModel;
    }

    public String getBioModel() {
        return bioModel;
    }

    public void setBioModel(String bioModel) {
        this.bioModel = bioModel;
    }

    public String getGenderModel() {
        return genderModel;
    }

    public void setGenderModel(String genderModel) {
        this.genderModel = genderModel;
    }

    public String getBirthdayModel() {
        return birthdayModel;
    }

    public void setBirthdayModel(String birthdayModel) {
        this.birthdayModel = birthdayModel;
    }

    public String getCountryModel() {
        return countryModel;
    }

    public void setCountryModel(String countryModel) {
        this.countryModel = countryModel;
    }

    public String getCityModel() {
        return cityModel;
    }

    public void setCityModel(String cityModel) {
        this.cityModel = cityModel;
    }
}

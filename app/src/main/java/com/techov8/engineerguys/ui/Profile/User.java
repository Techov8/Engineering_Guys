package com.techov8.engineerguys.ui.Profile;

public class User {

    private String name;
    private String email;

    private String fcmtoken;
    private String no_of_coins;
    private String mobile;
    private String imageurl;
    private String id;
    private String refer_id;



    public User() {
    }

    public User(String name, String email, String fcmtoken, String no_of_coins, String mobile, String imageurl, String id, String refer_id) {
        this.name = name;
        this.email = email;
        this.fcmtoken = fcmtoken;
        this.no_of_coins = no_of_coins;
        this.mobile = mobile;
        this.imageurl = imageurl;
        this.id = id;
        this.refer_id = refer_id;
    }

    public String getFcmtoken() {
        return fcmtoken;
    }

    public void setFcmtoken(String fcmtoken) {
        this.fcmtoken = fcmtoken;
    }

    public String getNo_of_coins() {
        return no_of_coins;
    }

    public void setNo_of_coins(String no_of_coins) {
        this.no_of_coins = no_of_coins;
    }

    public String getRefer_id() {
        return refer_id;
    }

    public void setRefer_id(String refer_id) {
        this.refer_id = refer_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }



    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

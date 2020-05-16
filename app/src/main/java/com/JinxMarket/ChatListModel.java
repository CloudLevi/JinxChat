package com.JinxMarket;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatListModel {
    private String mImageURL;
    private String mUsername;
    private String mLastMessage;
    private String mUserStatus;

    private String mChatID;
    private String mFirstUserID;
    private String mSecondUserID;

    public ChatListModel(){
    }

    public ChatListModel(String mChatID, String mFirstUserID, String mSecondUserID, String mImageURL, String mUsername, String mLastMessage, String mUserStatus) {
        this.mChatID = mChatID;
        this.mFirstUserID = mFirstUserID;
        this.mSecondUserID = mSecondUserID;
        this.mImageURL = mImageURL;
        this.mUsername = mUsername;
        this.mLastMessage = mLastMessage;
        this.mUserStatus = mUserStatus;
    }

    public String getImageURL() {
        return mImageURL;
    }

    public void setImageURL(String mImageURL) {
        this.mImageURL = mImageURL;
    }

    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String mUsername) {
        this.mUsername = mUsername;
    }

    public String getLastMessage() {
        return mLastMessage;
    }

    public void setLastMessage(String mLastMessage) {
        this.mLastMessage = mLastMessage;
    }

    public String getUserStatus() {
        return mUserStatus;
    }

    public void setUserStatus(String mUserStatus) {
        this.mUserStatus = mUserStatus;
    }

    public String getChatID() {
        return mChatID;
    }

    public void setChatID(String mChatID) {
        this.mChatID = mChatID;
    }

    public String getFirstUserID() {
        return mFirstUserID;
    }

    public void setFirstUserID(String mFirstUserID) {
        this.mFirstUserID = mFirstUserID;
    }

    public String getSecondUserID() {
        return mSecondUserID;
    }

    public void setSecondUserID(String mSecondUserID) {
        this.mSecondUserID = mSecondUserID;
    }
}

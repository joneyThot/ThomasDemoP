package com.rogi.Model;

import org.json.JSONObject;


public class MediaModel {
    String mediaId, mediaType, media, videoThumbImage, mediaDescription, latitude, longitude, street, city,
            state, country, pincode, created_date, is_sync, media_status, project_id, docThumbImage = "";

    boolean selected = false;
    JSONObject mediaObject;

    public MediaModel(String mediaId, String mediaType, String media, String videoThumbImage, String mediaDescription,
                      String latitude, String longitude, String street, String city, String state, String country,
                      String pincode, String created_date, String is_sync, String media_status, String docThumbImage,
                      JSONObject mediaObject) {
        this.mediaId = mediaId;
        this.mediaType = mediaType;
        this.media = media;
        this.videoThumbImage = videoThumbImage;
        this.mediaDescription = mediaDescription;
        this.latitude = latitude;
        this.longitude = longitude;
        this.street = street;
        this.city = city;
        this.state = state;
        this.country = country;
        this.pincode = pincode;
        this.created_date = created_date;
        this.is_sync = is_sync;
        this.media_status = media_status;
        this.docThumbImage = docThumbImage;
        this.mediaObject = mediaObject;

    }

    public MediaModel() {

    }


    public String getMediaId() {
        return mediaId;
    }

    public void setMediaId(String mediaId) {
        this.mediaId = mediaId;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public String getMedia() {
        return media;
    }

    public void setMedia(String media) {
        this.media = media;
    }

    public String getMediaDescription() {
        return mediaDescription;
    }

    public void setMediaDescription(String mediaDescription) {
        this.mediaDescription = mediaDescription;
    }

    public String getVideoThumbImage() {
        return videoThumbImage;
    }

    public void setVideoThumbImage(String videoThumbImage) {
        this.videoThumbImage = videoThumbImage;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getCreated_date() {
        return created_date;
    }

    public void setCreated_date(String created_date) {
        this.created_date = created_date;
    }

    public JSONObject getMediaObject() {
        return mediaObject;
    }

    public void setMediaObject(JSONObject mediaObject) {
        this.mediaObject = mediaObject;
    }

    public String getIs_sync() {
        return is_sync;
    }

    public void setIs_sync(String is_sync) {
        this.is_sync = is_sync;
    }

    public String getMedia_status() {
        return media_status;
    }

    public void setMedia_status(String media_status) {
        this.media_status = media_status;
    }

    public String getProject_id() {
        return project_id;
    }

    public void setProject_id(String project_id) {
        this.project_id = project_id;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getDocThumbImage() {
        return docThumbImage;
    }

    public void setDocThumbImage(String docThumbImage) {
        this.docThumbImage = docThumbImage;
    }
}

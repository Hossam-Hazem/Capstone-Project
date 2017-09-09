package com.example.pc.dishfinder.utils;


import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.Uri;

import com.example.pc.dishfinder.R;
import com.example.pc.dishfinder.config.MyConfig;
import com.example.pc.dishfinder.database.PlaceContract;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;

public class Place implements Serializable {
    private String id;
    private String name;
    private Boolean isOpen;
    private String rating;
    private String lat;
    private String lng;
    private String phoneNumber;
    private String address;
    private String website;
    private ArrayList<String> type;
    private ArrayList<String> PhotoIds;
    private ArrayList<Review> reviews;
    private String logoId;

    private final String PHOTO_BASE_URL = "https://maps.googleapis.com/maps/api/place/photo";
    public Place(String id, String name, boolean isOpen, String rating, String lat, String lng, String phoneNumber, String address, String website, ArrayList<String> type, ArrayList<String> photoIds, ArrayList<Review> reviews) {
        this.id = id;
        this.name = name;
        this.isOpen = isOpen;
        this.rating = rating;
        this.type = type;
        this.lat = lat;
        this.lng = lng;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.website = website;
        this.PhotoIds = photoIds;
        this.reviews = reviews;
        this.logoId = photoIds.get(0);
    }

    public Place(String id, String name, Boolean isOpen, String rating, String lat, String lng, ArrayList<String> type, ArrayList<String> photoIds) {
        this.id = id;
        this.name = name;
        this.isOpen = isOpen;
        this.rating = rating;
        this.lat = lat;
        this.lng = lng;
        this.type = type;
        this.PhotoIds = photoIds;
        this.logoId = !photoIds.isEmpty()? photoIds.get(0) : null;
    }

    public Place(String id, String name, String lat, String lng, String phoneNumber, String address, String rating, String website) {
        this.id = id;
        this.name = name;
        this.lat = lat;
        this.lng = lng;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.rating = rating;
        this.website = website;
    }
    public Place(String id, String name, String lat, String lng, String phoneNumber, String address, String rating, String website, String logo) {
        this.id = id;
        this.name = name;
        this.lat = lat;
        this.lng = lng;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.rating = rating;
        this.website = website;
        this.logoId = logo;
    }

    public void setAdditionalInfo(String phoneNumber, String address, String website, ArrayList<Review> reviews, ArrayList<String> photosId, ArrayList<String> types){
        this.address = address;
        this.website = website;
        this.phoneNumber = phoneNumber;
        this.reviews = reviews;
        this.PhotoIds = photosId;
        this.type = types;
        this.logoId = !photosId.isEmpty()? photosId.get(0) : null;
    }

    public String getName() {
        return name;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public ArrayList<String> getPhotoIds() {
        return PhotoIds;
    }

    public String getId() {
        return id;
    }

    public String getRating() {
        return rating;
    }

    public String getLat() {
        return lat;
    }

    public String getLng() {
        return lng;
    }

    public String getLoc(){
        //TODO
        return null;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public String getWebsite() {
        return website;
    }

    public ArrayList<String> getType() {
        return type;
    }

    public ArrayList<Review> getReviews(){
        return reviews;
    }

    public String getTypeString(){
        if(type == null)
            return null;
        Iterator<String> i = type.iterator();
        String result = "";
        if(i.hasNext())
            result += i.next();
        while(i.hasNext()){
            result += ", "+i.next();
        }
        return result;
    }

    public String getLogo() {
        return logoId != null ? logoId : "";
    }

    public String getLogoImageURL(){
        if(logoId != null && !logoId.isEmpty()) {
            return getImage("400", null, logoId);
        }
        return null;
    }

    public static int getPlaceHolderImage(){
        return R.drawable.image_not_available;
    }


    public String getDefaultImageURL() {
        if(PhotoIds!= null && !PhotoIds.isEmpty()) {
            return getImage("1700",null,getPhotoIds().get(0));
        }
        return null;
    }

    public String getImage(String maxWidth,String maxHeight, String ref){
        final String API_KEY = MyConfig.GOOGLE_PLACES_API_KEY;
        final String API_PARAM = "key";
        final String MAX_WIDTH_PARAM = "maxwidth";
        final String PHOTO_REFERENCE_PARAM = "photoreference";
        final String MAX_HEIGHT_PARAM = "maxheight";
        Uri.Builder uriBuilder = Uri.parse(PHOTO_BASE_URL).buildUpon()
                .appendQueryParameter(API_PARAM, API_KEY)
                .appendQueryParameter(PHOTO_REFERENCE_PARAM, ref);
        if(maxWidth != null)
            uriBuilder.appendQueryParameter(MAX_WIDTH_PARAM, maxWidth);
        if(maxHeight != null)
            uriBuilder.appendQueryParameter(MAX_HEIGHT_PARAM, maxHeight);
        return uriBuilder.build().toString();
    }

    public boolean isFavorite(Context context){
        return PlaceContract.FavoriteEntry.checkPlaceExistsById(context,id);
    }

    public boolean setFavorite(Context context){
        return PlaceContract.FavoriteEntry.insert(
                context,
                this
        );
    }
    public boolean removeFavorite(Context context){
        return PlaceContract.FavoriteEntry.delete(
                context,
                this.id
        );
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOpen(Boolean open) {
        isOpen = open;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public void setType(ArrayList<String> type) {
        this.type = type;
    }

    public void setPhotoIds(ArrayList<String> photoIds) {
        PhotoIds = photoIds;
    }

    public void setReviews(ArrayList<Review> reviews) {
        this.reviews = reviews;
    }

    public void setLogoId(String logoId) {
        this.logoId = logoId;
    }

    //return distance in meters
    public float getDistance(float lat, float lng){

        Location startPoint=new Location("locationA");
        startPoint.setLatitude(lat);
        startPoint.setLongitude(lng);

        Location endPoint=new Location("locationA");
        endPoint.setLatitude(Float.parseFloat(this.lat));
        endPoint.setLongitude(Float.parseFloat(this.lng));

        return startPoint.distanceTo(endPoint);
    }

    public String getDistanceFormatted(float lat, float lng){
        double distance = getDistance(lat, lng);
        String distanceFormatted;
        if(distance < 100){
            NumberFormat formatter = new DecimalFormat("#0");
            return formatter.format(distance)+" meters";
        }
        else{
            distance = distance/1000;
            NumberFormat formatter = new DecimalFormat("#0.00");
            return formatter.format(distance)+" kms";
        }
    }

    public static Comparator getComparater(SortType sortType, float lat, float lng) {
        switch (sortType){
            case DISTANCE: return getDistanceComparator(lat, lng);
            case NAME: return getNameComparator();
            case RATING: return getRatingComparator();
        }
        return null;
    }

    public enum SortType{
        NAME, DISTANCE, RATING
    }

    public static DistanceComparator getDistanceComparator(float lat, float lng){
        return new DistanceComparator(lat, lng);
    }

    public static NameComparator getNameComparator(){
        return new NameComparator();
    }

    public static RatingComparator getRatingComparator(){
        return new RatingComparator();
    }

    private static class DistanceComparator implements Comparator<Place> {

        private float lat;
        private float lng;

        DistanceComparator(float lat, float lng){
            this.lat = lat;
            this.lng = lng;
        }

        @Override
        public int compare(Place o1, Place o2) {
            Float distance1 = o1.getDistance(lat, lng);
            Float distance2 = o2.getDistance(lat, lng);
            return distance1.compareTo(distance2);
        }

    }

    private static class NameComparator implements Comparator<Place> {

        @Override
        public int compare(Place o1, Place o2) {
            return o1.getName().compareTo(o2.getName());
        }
    }

    private static class RatingComparator implements Comparator<Place> {

        @Override
        public int compare(Place o1, Place o2) {
            return o2.getRating().compareTo(o1.getRating());
        }
    }

}



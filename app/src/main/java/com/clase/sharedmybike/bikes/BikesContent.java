package com.clase.sharedmybike.bikes;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.Toast;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class BikesContent {
    //List of all the bikes to be listed in the RecyclerView
    public static List<Bike> ITEMS = new ArrayList<Bike>();
    public static String selectedDate; //Store the selected date
    public static void loadBikesFromJSON(Context c) {
        String json = null;
        try {
            InputStream is =
                    c.getAssets().open("bikeList.json");
            int size = is.available();
            if(size==0){
                Toast.makeText(c, "El JSON está vacio", Toast.LENGTH_SHORT).show();
                return;
            }
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
            JSONObject jsonObject = new JSONObject(json);
            JSONArray couchList = jsonObject.getJSONArray("bike_list");

            for (int i = 0; i < couchList.length(); i++) {
                JSONObject jsonCouch = couchList.getJSONObject(i);
                String owner = jsonCouch.getString("owner");
                String description = jsonCouch.getString("description");
                String city=jsonCouch.getString("city");
                String location=jsonCouch.getString("location");
                String email=jsonCouch.getString("email");
                Bitmap photo=null;

                try {
                    photo= BitmapFactory.decodeStream(
                            c.getAssets().open("images/"+
                                    jsonCouch.getString("image")));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                ITEMS.add(new BikesContent.Bike(
                        photo,owner,description,city,location,email));
            }

        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }

    }

    public static List<Bike> getITEMS() {
        return ITEMS;
    }

    public static class Bike implements Parcelable {
        public Bitmap photo;
        public String owner;
        public String description;
        public String city;
        public String location;
        public String email;
        //Setters and getters...
        public Bike(Bitmap photo, String owner, String description,
                    String city, String location, String email) {
            this.photo = photo;
            this.owner = owner;
            this.description = description;
            this.city = city;
            this.location = location;
            this.email= email;
        }
        @Override
        public String toString() {
            return owner+" "+description;
        }

        protected Bike(Parcel in) {
            owner = in.readString();
            description = in.readString();
            city = in.readString();
            location = in.readString();
            photo = in.readParcelable(Bitmap.class.getClassLoader());
        }

        public static final Creator<Bike> CREATOR = new Creator<Bike>() {
            @Override
            public Bike createFromParcel(Parcel in) {
                return new Bike(in);
            }

            @Override
            public Bike[] newArray(int size) {
                return new Bike[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(owner);
            dest.writeString(description);
            dest.writeString(city);
            dest.writeString(location);
            dest.writeParcelable(photo, flags);
        }
    }
}
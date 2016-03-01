package io.github.inesescin.nucleus.models;

/**
 * Created by danielmaida on 24/02/16.
 */
public class Nucleus {

    private String id;
    private int value;
    private String latitude;
    private String longitude;

    public Nucleus(String id, int value)
    {
        this.id = id;
        this.value = value;
    }

    public Nucleus (String id, int value, String latitude, String longitude)
    {
        this.id = id;
        this.value = value;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Nucleus(){}


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
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





}

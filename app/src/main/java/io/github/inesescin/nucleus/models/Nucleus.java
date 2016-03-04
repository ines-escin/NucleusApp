package io.github.inesescin.nucleus.models;

/**
 * Created by danielmaida on 24/02/16.
 */
public class Nucleus {

    private String id;
    private double value;
    private String coordinates;

    public Nucleus(String id, double value)
    {
        this.id = id;
        this.value = value;
    }

    public Nucleus (String id, double value, String coordinates)
    {
        this.id = id;
        this.value = value;
        this.coordinates = coordinates;
    }

    public Nucleus(){}

    public void setCoordinates(String coordinates)
    {
        this.coordinates = coordinates;
    }

    public void setValue(double value)
    {
        this.value = value;
    }

    public double getLatitude()
    {
        String [] coord = coordinates.split(",");
        double lat = Double.parseDouble(coord[0]);
        return lat;
    }

    public double getLongitude()
    {
        String [] coord = coordinates.split(",");
        double longitude = Double.parseDouble(coord[1]);
        return longitude;
    }
}

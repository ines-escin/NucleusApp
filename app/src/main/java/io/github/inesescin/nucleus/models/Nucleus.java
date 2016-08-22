package io.github.inesescin.nucleus.models;

/**
 * Created by danielmaida on 24/02/16.
 */
public class Nucleus {

    private String id;
    private double value;
    private String coordinates;
    private boolean status;

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

    public double getValue()
    {
        return value;
    }

    public boolean getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        if(status.equals("broken"))
        {
            this.status = true;
        }
        else
        {
            this.status = false;
        }
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getId()
    {
        return id;
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

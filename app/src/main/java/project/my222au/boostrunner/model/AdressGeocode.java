package project.my222au.boostrunner.model;


import java.util.ArrayList;
import java.util.List;

// using google maps api to the convert logitudes, latitudes to address  form json url;
// NOT USING  2016, 10  , GOOD TO HAVE IT FOR LATTER.
public class AdressGeocode {
    private double Latitude;
    private double Longitude;

    private String formatedAdress;
    private List<String> Adresses = new ArrayList<String>();

    public AdressGeocode(double latitude, double longitude) {
        Latitude = latitude;
        Longitude = longitude;
        createURL(latitude,longitude);
    }

    private String URL;

    public double getLatitude() {
        return Latitude;
    }

    public void setLatitude(double latitude) {
        Latitude = latitude;
    }

    public double getLongitude() {
        return Longitude;
    }

    public void setLongitude(double longitude) {
        Longitude = longitude;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public List<String> getAdresses() {
        return Adresses;
    }

    public void setAdresses(List<String> adresses) {
        Adresses = adresses;
    }

    public String getFormatedAdress() {
        return formatedAdress;
    }

    public void setFormatedAdress(String formatedAdress) {
        this.formatedAdress = formatedAdress;
    }

    private void   createURL(double latitude, double longitude){
        String URL = "https://maps.googleapis.com/maps/api/geocode/json?latlng="+latitude+","+longitude+"&key=AIzaSyAjlBovRmm_9rNVKMUELIsOCBmcJlC6pUw";
        this.URL = URL;

    }
}

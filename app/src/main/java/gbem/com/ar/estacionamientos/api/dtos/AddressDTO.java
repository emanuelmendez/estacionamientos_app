package gbem.com.ar.estacionamientos.api.dtos;

import java.io.Serializable;

public class AddressDTO implements Serializable {

    private static final long serialVersionUID = 1874821223106118028L;
    private long id;
    private double latitude;
    private double longitude;
    private String streetAddress;
    private String city;
    private String state;
    private String country;
    private String postalCode;

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public double getLatitude() {
        return latitude;
    }
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    public double getLongitude() {
        return longitude;
    }
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
    public String getStreetAddress() {
        return streetAddress;
    }
    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
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
    public String getPostalCode() {
        return postalCode;
    }
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (id ^ (id >>> 32));
        long temp;
        temp = Double.doubleToLongBits(latitude);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(longitude);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        AddressDTO other = (AddressDTO) obj;
        if (id != other.id) {
            return false;
        }
        if (Double.doubleToLongBits(latitude) != Double.doubleToLongBits(other.latitude)) {
            return false;
        }

        return Double.doubleToLongBits(longitude) == Double.doubleToLongBits(other.longitude);
    }

    @Override
    public String toString() {
        return "Address: [ streetAddres= "+streetAddress+", city= "+city+", country= "+country+
                ", postalCode= "+postalCode+", latitude= "+latitude+", longitude= "+longitude+"]";
    }
}

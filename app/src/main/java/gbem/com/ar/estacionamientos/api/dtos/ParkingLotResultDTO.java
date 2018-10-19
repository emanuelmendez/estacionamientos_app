package gbem.com.ar.estacionamientos.api.dtos;

import java.io.Serializable;

/**
 * @author pielreloj
 * Created: 15/09/18.
 */
public class ParkingLotResultDTO implements Serializable {

    private static final long serialVersionUID = -2650522845717940780L;

    private long id;
    private String description;
    private String coordinates;
    private long addressId;
    private int lotNumber;
    private String streetAddress;
    private long userId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
    }

    public long getAddressId() {
        return addressId;
    }

    public void setAddressId(long addressId) {
        this.addressId = addressId;
    }

    public int getLotNumber() {
        return lotNumber;
    }

    public void setLotNumber(int lotNumber) {
        this.lotNumber = lotNumber;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (id ^ (id >>> 32));
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
        ParkingLotResultDTO other = (ParkingLotResultDTO) obj;
        return id == other.id;
    }

    @Override
    public String toString() {
        return "ParkingLotResultDTO [id=" + id + ", coordinates=" + coordinates + ", addressId=" + addressId
                + ", lotNumber=" + lotNumber + ", userId=" + userId + "]";
    }

}

package gbem.com.ar.estacionamientos.search;

import java.io.Serializable;

/**
 * @author pielreloj
 * Created: 12/11/18.
 */
public class ReservationOptionsDTO implements Serializable {

    private static final long serialVersionUID = -8387368811760938425L;

    private long parkingLotId;
    private long dateFrom;
    private long dateTo;

    long getParkingLotId() {
        return parkingLotId;
    }

    void setParkingLotId(long parkingLotId) {
        this.parkingLotId = parkingLotId;
    }

    long getDateFrom() {
        return dateFrom;
    }

    void setDateFrom(long dateFrom) {
        this.dateFrom = dateFrom;
    }

    long getDateTo() {
        return dateTo;
    }

    void setDateTo(long dateTo) {
        this.dateTo = dateTo;
    }

    @Override
    public String toString() {
        return "ReservationOptionsDTO [parkingLotId=" + parkingLotId + ", dateFrom=" + dateFrom + ", dateTo=" + dateTo
                + "]";
    }

}

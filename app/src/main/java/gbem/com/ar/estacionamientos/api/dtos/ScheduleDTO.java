package gbem.com.ar.estacionamientos.api.dtos;

import java.io.Serializable;

public class ScheduleDTO implements Serializable {

    private static final long serialVersionUID = 5069950243344057573L;
    private ParkingLotDTO parkingLotDTO;
    private boolean monday;
    private boolean tuesday;
    private boolean wednesday;
    private boolean thursday;
    private boolean friday;
    private boolean saturday;
    private boolean sunday;
    private int fromHour;
    private int toHour;

    public ParkingLotDTO getParkingLotDTO() {
        return parkingLotDTO;
    }

    public void setParkingLotDTO(ParkingLotDTO parkingLotDTO) {
        this.parkingLotDTO = parkingLotDTO;
    }

    public boolean isMonday() {
        return monday;
    }

    public void setMonday(boolean monday) {
        this.monday = monday;
    }

    public boolean isTuesday() {
        return tuesday;
    }

    public void setTuesday(boolean tuesday) {
        this.tuesday = tuesday;
    }

    public boolean isWednesday() {
        return wednesday;
    }

    public void setWednesday(boolean wednesday) {
        this.wednesday = wednesday;
    }

    public boolean isThursday() {
        return thursday;
    }

    public void setThursday(boolean thursday) {
        this.thursday = thursday;
    }

    public boolean isFriday() {
        return friday;
    }

    public void setFriday(boolean friday) {
        this.friday = friday;
    }

    public boolean isSaturday() {
        return saturday;
    }

    public void setSaturday(boolean saturday) {
        this.saturday = saturday;
    }

    public boolean isSunday() {
        return sunday;
    }

    public void setSunday(boolean sunday) {
        this.sunday = sunday;
    }

    public int getFromHour() {
        return fromHour;
    }

    public void setFromHour(int fromHour) {
        this.fromHour = fromHour;
    }

    public int getToHour() {
        return toHour;
    }

    public void setToHour(int toHour) {
        this.toHour = toHour;
    }

    @Override
    public String toString() {
        return "Schedule [monday=" + monday + ", tuesday=" + tuesday + ", wednesday=" + wednesday + ", thursday="
                + thursday + ", friday=" + friday + ", saturday=" + saturday + ", sunday=" + sunday + ", fromHour="
                + fromHour + ", toHour=" + toHour + "]";
    }
}

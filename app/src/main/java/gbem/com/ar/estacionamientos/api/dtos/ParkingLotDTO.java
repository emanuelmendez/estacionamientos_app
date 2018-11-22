package gbem.com.ar.estacionamientos.api.dtos;

import java.io.Serializable;

public class ParkingLotDTO implements Serializable {

    private static final long serialVersionUID = -3955385936065796663L;
    private long id;
    private int lotNumber;
    private AddressDTO addressDTO;
    private String description;
    private boolean active;
    private long value;
    private ScheduleDTO scheduleDTO;

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public int getLotNumber() {
        return lotNumber;
    }
    public void setLotNumber(int lotNumber) {
        this.lotNumber = lotNumber;
    }
    public AddressDTO getAddressDTO() {
        return addressDTO;
    }
    public void setAddressDTO(AddressDTO addressDTO) {
        this.addressDTO = addressDTO;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public boolean isActive() {
        return active;
    }
    public void setActive(boolean active) {
        this.active = active;
    }
    public long getValue() { return value; }
    public void setValue(long value) { this.value = value; }
    public ScheduleDTO getScheduleDTO() { return scheduleDTO; }
    public void setScheduleDTO(ScheduleDTO scheduleDTO) { this.scheduleDTO = scheduleDTO; }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (id ^ (id >>> 32));
        result = prime * result + ((addressDTO == null) ? 0 : addressDTO.hashCode());
        result = prime * result + lotNumber;
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
        ParkingLotDTO other = (ParkingLotDTO) obj;
        if (id != other.id) {
            return false;
        }
        if (lotNumber != other.lotNumber) {
            if (addressDTO == null) {
                if (other.addressDTO != null) {
                    return false;
                }
            } else if (!addressDTO.equals(other.addressDTO)) {
                return false;
            }
            return false;
        }
        return true;
    }

    public int compareTo(ParkingLotDTO o) {
        if (this.id != o.id || !this.addressDTO.equals(o.addressDTO)) {
            return 0;
        } else {
            return Integer.compare(this.lotNumber, o.lotNumber);
        }
    }

    @Override
    public String toString() {
        return "ParkingLotDTO [id=" + id + ", address=" + addressDTO + ", scheduleDTO=" + scheduleDTO
                + ", lotNumber=" + lotNumber + ", rate=" + value + ", description=" + description + "]";
    }
}

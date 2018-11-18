package gbem.com.ar.estacionamientos.api.dtos;

/**
 * @author pielreloj
 * Created: 04/11/18.
 */
public enum Status {
    PENDING(1, "Pending"),
    APPROVED(2, "Approved"),
    IN_PROGRESS(3, "In Progress"),
    DONE(4, "Done"),
    CANCELLED(5, "Cancelled");

    private int key;
    private String description;

    Status(int key, String description) {
        this.key = key;
        this.description = description;
    }

    public int key() {
        return key;
    }

    public String description() {
        return description;
    }

    public static Status get(String statusName) {
        for (Status s : values()) {
            if (s.description.equals(statusName)) {
                return s;
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return description;
    }
}

package gbem.com.ar.estacionamientos.history;

import java.io.Serializable;
import java.util.Date;

/**
 * @author pielreloj
 * Created: 23/11/18.
 */
final class ReviewDTO implements Serializable {

    private static final long serialVersionUID = 3683909515846673760L;

    private long id;
    private int score;
    private String comment;
    private Date dateReviewed;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getDateReviewed() {
        return dateReviewed;
    }

    public void setDateReviewed(Date dateReviewed) {
        this.dateReviewed = dateReviewed;
    }
}

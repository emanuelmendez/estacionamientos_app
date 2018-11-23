package gbem.com.ar.estacionamientos.history;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import gbem.com.ar.estacionamientos.R;
import gbem.com.ar.estacionamientos.dashboard.ReservationDTO;
import gbem.com.ar.estacionamientos.dashboard.ReviewDTO;
import gbem.com.ar.estacionamientos.utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReviewActivity extends AppCompatActivity {

    @BindView(R.id.txtTitle)
    TextView txtTitle;
    @BindView(R.id.ratingBar)
    RatingBar ratingBar;
    @BindView(R.id.editText2)
    EditText etComment;
    @BindView(R.id.btnConfirmar)
    Button btnConfirmar;
    private ReservationDTO reservation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        ButterKnife.bind(this);

        if (getIntent() == null
                || getIntent().getExtras() == null
                || getIntent().getExtras().get("reservation") == null) {
            super.onBackPressed();
        }

        reservation = (ReservationDTO) getIntent().getExtras().get("reservation");
        if (reservation == null) return;

        if (reservation.getReview() != null) {
            ratingBar.setRating(reservation.getReview().getScore());
            etComment.setText(reservation.getReview().getComment());
        }

        ratingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            if (rating < 1) {
                ratingBar.setRating(1);
                ratingBar.invalidate();
            }
        });

    }

    @OnClick(R.id.btnConfirmar)
    public void onClickConfirmar() {
        if (etComment.getText().length() == 0) {
            etComment.setError("Es obligatorio completar este campo");
            return;
        } else {
            etComment.setError(null);
        }

        ReviewDTO review = new ReviewDTO();
        review.setScore(Math.round(ratingBar.getRating()));
        review.setComment(etComment.getText().toString());

        Utils.getService(IReservationHistoryService.class)
                .postReview(Utils.getIdToken(this), reservation.getId(), review)
                .enqueue(new Callback<Void>() {
                    @Override

                    public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                        if (response.isSuccessful()) {
                            reservation.setReview(review);
                            getIntent().putExtra("reservation", reservation);
                            setResult(RESULT_OK, getIntent());
                            finish();
                        } else {
                            Toast.makeText(ReviewActivity.this,
                                    "Hubo un error, intente nuevamente", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                        Toast.makeText(ReviewActivity.this,
                                "Error de conexión", Toast.LENGTH_SHORT).show();
                    }
                });

    }
}

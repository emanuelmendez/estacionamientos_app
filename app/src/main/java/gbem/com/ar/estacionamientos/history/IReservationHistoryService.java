package gbem.com.ar.estacionamientos.history;

import java.util.List;

import gbem.com.ar.estacionamientos.dashboard.ReservationDTO;
import gbem.com.ar.estacionamientos.dashboard.ReviewDTO;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface IReservationHistoryService {

    @GET("reservations/driver/history")
    Call<List<ReservationDTO>> getReservationsHistoryByDriver(@Header("api_token") String token);


    @PUT("reservations/driver/{id}")
    Call<Void> postReview(@Header("api_token") String token, @Path("id") long reservationId, @Body ReviewDTO review);
}

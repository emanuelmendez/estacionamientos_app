package gbem.com.ar.estacionamientos.reservations;

import java.util.List;

import gbem.com.ar.estacionamientos.dashboard.ReservationDTO;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * @author pielreloj
 * Created: 17/11/18.
 */
public interface LenderReservationsService {

    @GET("reservations/lender")
    Call<List<ReservationDTO>> getLenderReservations(@Header("api_token") String token);

    @DELETE("reservations/lender/{id}")
    Call<Void> rejectOrCancelLenderReservation(@Header("api_token") String token, @Path("id") long reservationId);

    @POST("reservations/lender/{id}")
    Call<Void> acceptReservation(@Header("api_token") String token, @Path("id") long reservationId);
}

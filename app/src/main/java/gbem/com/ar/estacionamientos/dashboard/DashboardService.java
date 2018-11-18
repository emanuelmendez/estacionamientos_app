package gbem.com.ar.estacionamientos.dashboard;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

/**
 * @author pielreloj
 * Created: 28/10/18.
 */
interface DashboardService {

    @GET("reservations/driver/current")
    Call<ReservationDTO> getDriverCurrentReservation(@Header("api_token") String token);

    @DELETE("reservations/driver/{id}")
    Call<Void> cancelCurrentReservation(@Header("api_token") String token, @Path("id") long reservationId);

    @GET("reservations/lender/pending")
    Call<List<ReservationDTO>> getPendingLenderReservations(@Header("api_token") String token);

}

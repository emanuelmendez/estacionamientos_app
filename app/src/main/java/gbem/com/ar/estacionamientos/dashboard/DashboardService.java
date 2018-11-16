package gbem.com.ar.estacionamientos.dashboard;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
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

    // TODO usar para la pantalla de reservas del prestador, donde se ven todas
    @GET("reservations/lender")
    Call<List<ReservationDTO>> getLenderReservations(@Header("api_token") String token);

    @GET("reservations/lender/pending")
    Call<List<ReservationDTO>> getPendingLenderReservations(@Header("api_token") String token);

    @DELETE("reservations/lender/{id}")
    Call<Void> rejectOrCancelLenderReservation(@Header("api_token") String token, @Path("id") long reservationId);

    @POST("reservations/lender/{id}")
    Call<Void> acceptReservation(@Header("api_token") String token, @Path("id") long reservationId);

}

package gbem.com.ar.estacionamientos.dashboard;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

/**
 * @author pielreloj
 * Created: 28/10/18.
 */
interface DashboardService {

    @GET("reservations/driver")
    Call<List<ReservationDTO>> getDriverReservations(@Header("api_token") String token);

    @GET("reservations/lender")
    Call<List<ReservationDTO>> getLenderReservations(@Header("api_token") String token);

}

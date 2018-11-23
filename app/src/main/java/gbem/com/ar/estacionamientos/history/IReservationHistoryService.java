package gbem.com.ar.estacionamientos.history;

import java.util.List;

import gbem.com.ar.estacionamientos.dashboard.ReservationDTO;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface IReservationHistoryService {

    @GET("reservations/driver/history")
    Call<List<ReservationDTO>> getReservationsHistoryByDriver(@Header("api_token") String token);
}

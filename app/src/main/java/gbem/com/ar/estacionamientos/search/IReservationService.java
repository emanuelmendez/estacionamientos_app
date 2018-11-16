package gbem.com.ar.estacionamientos.search;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * @author pielreloj
 * Created: 12/11/18.
 */
interface IReservationService {

    @POST("reservations/driver")
    Call<Void> makeReservation(@Header("api_token") String token, @Body ReservationOptionsDTO options);
}

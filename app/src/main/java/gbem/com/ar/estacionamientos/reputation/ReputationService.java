package gbem.com.ar.estacionamientos.reputation;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

/**
 * @author pielreloj
 * Created: 23/11/18.
 */
interface ReputationService {

    @GET("reservations/lender/score")
    Call<Float> getAverageScore(@Header("api_token") String token);

}

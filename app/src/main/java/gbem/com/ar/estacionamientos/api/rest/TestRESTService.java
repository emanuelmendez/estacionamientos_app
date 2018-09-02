package gbem.com.ar.estacionamientos.api.rest;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;

/**
 * @author pielreloj
 * Created: 02/09/18.
 */
public interface TestRESTService {

    @GET("testservice")
    Call<ResponseBody> getDBStringObjects();

}

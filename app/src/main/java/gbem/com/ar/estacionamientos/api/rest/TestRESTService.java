package gbem.com.ar.estacionamientos.api.rest;

import java.util.List;

import gbem.com.ar.estacionamientos.api.dtos.TestObjectDTO;
import retrofit2.Call;
import retrofit2.http.GET;

/**
 * @author pielreloj
 * Created: 02/09/18.
 */
public interface TestRESTService {

    @GET("testservice")
    Call<List<TestObjectDTO>> getDBStringObjects();

}

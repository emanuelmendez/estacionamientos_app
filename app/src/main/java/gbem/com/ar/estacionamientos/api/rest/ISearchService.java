package gbem.com.ar.estacionamientos.api.rest;

import java.util.List;

import gbem.com.ar.estacionamientos.api.dtos.ParkingLotResultDTO;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * @author pielreloj
 * Created: 02/09/18.
 */
public interface ISearchService {

    @GET("search")
    Call<List<ParkingLotResultDTO>> searchNear(@Query("latitude") double latitude,
                                               @Query("longitude") double longitude,
                                               @Query("ratio") int ratio);

}

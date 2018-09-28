package gbem.com.ar.estacionamientos.api.rest;

import java.util.List;

import gbem.com.ar.estacionamientos.api.dtos.VehicleDTO;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface IVehicleService {

    @GET("vehicle/{user}")
    Call<List<VehicleDTO>> getVehicleByUserId(@Path("user") long user);
}

package gbem.com.ar.estacionamientos.vehicles;

import java.util.List;

import gbem.com.ar.estacionamientos.api.dtos.VehicleDTO;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface IVehicleService {

    @GET("users/vehicles")
    Call<List<VehicleDTO>> getVehicleByUser(@Header("api_token") String token);

    @DELETE("users/vehicles/{idVehicle}")
    Call<ResponseBody> deleteVehicleById(@Header("api_token") String token,
                                         @Path("idVehicle") long idVehicle);

    @POST("users/vehicles")
    Call<ResponseBody> saveNewVehicle(@Header("api_token") String token,
                                      @Body VehicleDTO jsonData);

    @PATCH("users/vehicles/{idVehicle}")
    Call<ResponseBody> editVehicle(@Header("api_token") String token,
                                   @Path("idVehicle") long idVehicle,
                                   @Body VehicleDTO jsonData);
}

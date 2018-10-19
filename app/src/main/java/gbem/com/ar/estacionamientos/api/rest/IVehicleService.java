package gbem.com.ar.estacionamientos.api.rest;

import java.util.List;

import gbem.com.ar.estacionamientos.api.dtos.VehicleDTO;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface IVehicleService {

    @GET("users/{user}/vehicles")
    Call<List<VehicleDTO>> getVehicleByUserId(@Path("user") long id);

    @DELETE("users/{user}/vehicles/{idVehicle}")
    Call<ResponseBody> deleteVehicleById(@Path("user") long idUser, @Path("idVehicle") long idVehicle);

    @POST("users/{user}/vehicles")
    Call<ResponseBody> saveNewVehicle(@Path("user") long idUser, @Body VehicleDTO jsonData);

    @PATCH("users/{user}/vehicles/{idVehicle}")
    Call<ResponseBody> editVehicle(@Path("user") long idUser, @Path("idVehicle") long idVehicle, @Body VehicleDTO jsonData);
}

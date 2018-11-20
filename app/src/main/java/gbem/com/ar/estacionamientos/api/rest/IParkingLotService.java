package gbem.com.ar.estacionamientos.api.rest;

import java.util.List;

import gbem.com.ar.estacionamientos.api.dtos.ParkingLotDTO;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface IParkingLotService {

    @GET("users/{user}/parkinglot")
    Call<List<ParkingLotDTO>> getParkingLotsByUser(@Header("api_token") String token,
                                                   @Path("user") long id);

    @POST("users/{user}/parkinglot")
    Call<ResponseBody> addNewParkingLot(@Header("api_token") String token,
                                        @Path("user") long idUser,
                                        @Body List<ParkingLotDTO> jsonData);
}

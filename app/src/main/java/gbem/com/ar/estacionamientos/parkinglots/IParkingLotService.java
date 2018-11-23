package gbem.com.ar.estacionamientos.parkinglots;

import java.util.List;

import gbem.com.ar.estacionamientos.api.dtos.ParkingLotDTO;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface IParkingLotService {

    @GET("users/parkinglot")
    Call<List<ParkingLotDTO>> getParkingLotsByUser(@Header("api_token") String token);

    @POST("users/parkinglot")
    Call<ResponseBody> addNewParkingLot(@Header("api_token") String token,
                                        @Body List<ParkingLotDTO> jsonData);

    @PATCH("users/parkinglot/{idParkingLot}")
    Call<ResponseBody> editParkingLot(@Header("api_token") String token,
                                   @Path("idParkingLot") long idParkingLot,
                                   @Body ParkingLotDTO jsonData);

    @DELETE("users/parkinglot/{idParkingLot}")
    Call<ResponseBody> deleteLotById(@Header("api_token") String token,
                                     @Path("idParkingLot") long idParkingLot);
}

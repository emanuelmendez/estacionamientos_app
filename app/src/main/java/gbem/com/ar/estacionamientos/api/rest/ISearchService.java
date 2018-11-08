package gbem.com.ar.estacionamientos.api.rest;

import java.util.List;

import gbem.com.ar.estacionamientos.api.dtos.ParkingLotResultDTO;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

/**
 * @author pielreloj
 * Created: 02/09/18.
 */
public interface ISearchService {

    /**
     * Llamado a la API de búsquedas.
     * Devuelve una lista de resultados de 0..n de los lugares donde la distancia comprendida entre ellos
     * y el punto indicado es menor o igual al radio circular indicado
     * @param token token de autenticación obligatorio
     * @param latitude latitud de la coordenada de referencia
     * @param longitude longitud de la coordenada de referencia
     * @param ratio radio máximo en kilómetros en donde buscar
     * @return Un Call listo para ser ejecutado asincrónicamente.
     * Si la llamada es correcta (200), en el body se encuentra la lista de espacios que cumplen con el criterio,
     * de lo contrario se recibe un mensaje de error (http status codes >= 400)
     */
    @GET("search")
    Call<List<ParkingLotResultDTO>> searchNear(@Header("api_token") String token,
                                               @Query("latitude") double latitude,
                                               @Query("longitude") double longitude,
                                               @Query("ratio") double ratio);

}

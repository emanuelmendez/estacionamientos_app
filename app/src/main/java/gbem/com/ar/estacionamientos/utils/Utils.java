package gbem.com.ar.estacionamientos.utils;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import gbem.com.ar.estacionamientos.EstacionamientosApp;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author pielreloj
 * Created: 16/10/18.
 */
public final class Utils {

    public static final String USER_DATA_KEY = "user_data";
    public static final String RESERVATION_LOCATION = "reservation_location";
    private static final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.US);

    // ip a cambiar para pruebas
    private static final String API_BASE_URL = "http://10.0.2.2:8080/web/";
    private static final Retrofit RETROFIT = new Retrofit.Builder()
            .baseUrl(API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    /**
     * Método para reducir el código necesario para obtener la instancia de la app
     *
     * @param activity contexto en el que estamos
     * @return la instancia de la App
     */
    public static @NonNull
    EstacionamientosApp getApp(@NonNull Activity activity) {
        return (EstacionamientosApp) activity.getApplication();
    }

    public static String getIdToken(@NonNull Activity activity) {
        return getApp(activity).getLastSignedInAccount().getIdToken();
    }

    /**
     * Construye un objeto LatLng desde un string de coordenadas
     *
     * @param coordinates coordenadas con formato "lat,lng"
     * @return LatLng usado en Google Maps
     */
    public static LatLng createLatLng(@NonNull String coordinates) {
        final String[] split = coordinates.split(",");
        final double lat = Double.parseDouble(split[0]);
        final double lng = Double.parseDouble(split[1]);

        return new LatLng(lat, lng);
    }

    public static String parse(Date date) {
        return sdf.format(date);
    }

    public static Date parse(String text) throws ParseException {
        return sdf.parse(text);
    }

    /**
     * Método genérico para obtener un service para los request con retrofit
     *
     * @param service servicio a utilzar
     * @param <T>     Clase del servicio (.class)
     * @return Una instancia de una clase que implementa la interfaz recibida por parámetro,
     * capaz de realizar los requests
     */
    public static <T> T getService(Class<T> service) {
        return RETROFIT.create(service);
    }


}

package gbem.com.ar.estacionamientos.utils;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import gbem.com.ar.estacionamientos.EstacionamientosApp;

/**
 * @author pielreloj
 * Created: 16/10/18.
 */
public final class Utils {

    public static final String USER_DATA_KEY = "user_data";
    public static final String RESERVATION_LOCATION = "reservation_location";
    private static final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.US);

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


}

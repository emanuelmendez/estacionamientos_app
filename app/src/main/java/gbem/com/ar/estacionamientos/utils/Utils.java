package gbem.com.ar.estacionamientos.utils;

import android.app.Activity;
import android.support.annotation.NonNull;

import gbem.com.ar.estacionamientos.EstacionamientosApp;

/**
 * @author pielreloj
 * Created: 16/10/18.
 */
public final class Utils {

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

    public static final String USER_DATA_KEY = "user_data";

}

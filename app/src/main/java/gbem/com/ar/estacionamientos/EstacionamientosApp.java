package gbem.com.ar.estacionamientos;

import android.app.Application;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author pielreloj
 * Created: 02/09/18.
 */
public class EstacionamientosApp extends Application {

    // ip a cambiar para pruebas
    private static final String API_BASE_URL = "http://10.0.2.2:8080/web/";
    private static final String CLIENT_ID = "349020659959-ah8n75k13u1ekbgu59tfioqkgipc46mv.apps.googleusercontent.com";

    private Retrofit retrofit;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    public void onCreate() {
        super.onCreate();

        this.retrofit = new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        final GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestProfile()
                .requestEmail()
                .requestIdToken(CLIENT_ID)
                .build();

        this.mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    /**
     * Método genérico para obtener un service para los request con retrofit
     *
     * @param service servicio a utilzar
     * @param <T>     Clase del servicio (.class)
     * @return Una instancia de una clase que implementa la interfaz recibida por parámetro,
     * capaz de realizar los requests
     */
    public <T> T getService(Class<T> service) {
        return retrofit.create(service);
    }

    public GoogleSignInClient getGoogleSignInClient() {
        return mGoogleSignInClient;
    }

    public GoogleSignInAccount getLastSignedInAccount() {
        return GoogleSignIn.getLastSignedInAccount(this);
    }
}

package gbem.com.ar.estacionamientos;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

/**
 * @author pielreloj
 * Created: 02/09/18.
 */
public class EstacionamientosApp extends Application {

    private static final String CLIENT_ID = "349020659959-ah8n75k13u1ekbgu59tfioqkgipc46mv.apps.googleusercontent.com";

    private GoogleSignInClient mGoogleSignInClient;

    @Override
    public void onCreate() {
        super.onCreate();

        final GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestProfile()
                .requestEmail()
                .requestIdToken(CLIENT_ID)
                .build();

        this.mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            final CharSequence name = getString(R.string.notif_channel);
            final String description = "Notificaciones cuando una reserva se solicita, cancela o confirma";
            final int importance = NotificationManager.IMPORTANCE_DEFAULT;

            final NotificationChannel channel = new NotificationChannel("estacionamientos_notif_service", name, importance);
            channel.setDescription(description);

            final NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    public GoogleSignInClient getGoogleSignInClient() {
        return mGoogleSignInClient;
    }

    public GoogleSignInAccount getLastSignedInAccount() {
        return GoogleSignIn.getLastSignedInAccount(this);
    }
}

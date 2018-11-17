package gbem.com.ar.estacionamientos.notifications;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import gbem.com.ar.estacionamientos.EstacionamientosApp;
import gbem.com.ar.estacionamientos.R;
import gbem.com.ar.estacionamientos.signin.ISessionService;
import gbem.com.ar.estacionamientos.utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * @author pielreloj
 * Created: 14/11/18.
 */
public class NotificationService extends FirebaseMessagingService {

    private static final String TAG = NotificationService.class.getSimpleName();

    public static String getToken(Context context) {
        return context.getSharedPreferences("_", MODE_PRIVATE).getString("fb", null);
    }

    public static void updateDeviceToken(String signInToken, String deviceToken) {
        Utils.getService(ISessionService.class)
                .updateDeviceToken(signInToken, deviceToken)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                        if (response.isSuccessful()) Log.i(TAG, "Device token updated");
                        else Log.e(TAG, "Device token update failed");
                    }

                    @Override
                    public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                        Log.e(TAG, "Device token update failed", t);
                    }
                });
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getNotification() == null)
            Log.d(TAG, "onMessageReceived: null notification body");
        else {
            Log.d(TAG, "onMessageReceived: " + remoteMessage.getNotification().getBody());
            showNotification(remoteMessage.getNotification());
        }
    }

    private void showNotification(RemoteMessage.Notification notification) {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, getString(R.string.notif_channel))
                        .setSmallIcon(R.drawable.ic_parking)
                        .setContentTitle(notification.getTitle())
                        .setContentText(notification.getBody())
                        .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat manager = NotificationManagerCompat.from(this);
        manager.notify(1, builder.build());
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        getSharedPreferences("_", MODE_PRIVATE).edit().putString("fb", s).apply();

        final GoogleSignInAccount lastSignedInAccount = ((EstacionamientosApp) getApplication()).getLastSignedInAccount();
        if (lastSignedInAccount != null) {
            String signInToken = lastSignedInAccount.getIdToken();
            updateDeviceToken(signInToken, s);
        }
    }
}

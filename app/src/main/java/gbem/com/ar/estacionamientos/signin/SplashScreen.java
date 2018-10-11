package gbem.com.ar.estacionamientos.signin;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import butterknife.BindView;
import butterknife.ButterKnife;
import gbem.com.ar.estacionamientos.EstacionamientosApp;
import gbem.com.ar.estacionamientos.MainActivity;
import gbem.com.ar.estacionamientos.R;
import gbem.com.ar.estacionamientos.api.dtos.UserDataDTO;
import gbem.com.ar.estacionamientos.api.rest.ISessionService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashScreen extends AppCompatActivity {

    private static final String TAG = SplashScreen.class.getSimpleName();

    @BindView(R.id.splash_screen_progress_bar)
    ProgressBar progressBar;

    @BindView(R.id.splash_screen_layout)
    CoordinatorLayout layout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        ButterKnife.bind(this);

        new Thread(this::silentSignIn).start();
    }

    private void silentSignIn() {
        if (((EstacionamientosApp) getApplication()).getLastSignedInAccount() == null) {
            redirectTo(MainActivity.class);
        } else {
            ((EstacionamientosApp) getApplication()).getGoogleSignInClient().silentSignIn()
                    .addOnSuccessListener(this::onSuccess)
                    .addOnFailureListener(this::onFailure);
        }
    }

    private void onSuccess(GoogleSignInAccount account) {
        if (account == null) {
            // No debería suceder si el lastSignedInAccount retornó distinto a null
            redirectTo(SignInActivity.class);
        } else {
            final ISessionService sessionService =
                    ((EstacionamientosApp) getApplication()).getService(ISessionService.class);
            final Call<UserDataDTO> call = sessionService.getUserData(account.getIdToken());
            call.enqueue(new SessionServiceCallback());
        }
    }

    private void onFailure(Exception e) {
        Log.e(TAG, "Silent SingIn Failed. Redirected to SignInActivity", e);
        redirectTo(SignInActivity.class);
    }

    private void redirectTo(Class<? extends AppCompatActivity> activityClass) {
        this.redirectTo(activityClass, null);
    }

    private void redirectTo(Class<? extends AppCompatActivity> activityClass, final UserDataDTO userData) {
        final Intent intent = new Intent(SplashScreen.this, activityClass);
        if (userData != null) {
            intent.putExtra("user_data", userData);
        }
        startActivity(intent);
        finish();
    }

    private final class SessionServiceCallback implements Callback<UserDataDTO> {

        @Override
        public void onResponse(@NonNull Call<UserDataDTO> call, @NonNull Response<UserDataDTO> response) {
            if (response.isSuccessful()) {
                redirectTo(MainActivity.class, response.body());
            } else if (response.code() == 403) {
                // el usuario no está registrado pero hizo signin (i.e. abandonó el proceso de registración)
                redirectTo(SignUpActivity.class);
            } else {
                Log.e(TAG, String.format("Unsuccessful response: %s %d", response.message(), response.code()));
                showRetryAction();
            }
        }

        @Override
        public void onFailure(@NonNull Call<UserDataDTO> call, @NonNull Throwable t) {
            // no hay conexion o algun error previo a la llamada
            Log.e(TAG, "SessionService error", t);
            showRetryAction();
        }

        private void showRetryAction() {
            progressBar.setVisibility(View.GONE);

            final Snackbar snackbar = Snackbar.make(layout, "Connectivity error", Snackbar.LENGTH_INDEFINITE);

            snackbar.setAction("Retry", v -> {
                snackbar.dismiss();
                progressBar.setVisibility(View.VISIBLE);
                silentSignIn();
            });

            snackbar.show();
        }
    }
}
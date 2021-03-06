package gbem.com.ar.estacionamientos.signin;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ProgressBar;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import gbem.com.ar.estacionamientos.R;
import gbem.com.ar.estacionamientos.api.dtos.UserDataDTO;
import gbem.com.ar.estacionamientos.dashboard.NavigationDrawerActivity;
import gbem.com.ar.estacionamientos.notifications.NotificationService;
import gbem.com.ar.estacionamientos.utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static gbem.com.ar.estacionamientos.utils.Utils.USER_DATA_KEY;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private static final int REQUEST_CODE = 1234;

    @BindView(R.id.sign_in_button)
    SignInButton signInButton;

    @BindView(R.id.splash_screen_progress_bar)
    ProgressBar progressBar;

    @BindView(R.id.splash_screen_layout)
    ConstraintLayout layout;

    private ISessionService sessionService;
    private SessionServiceCallback sessionServiceCallback;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        ButterKnife.bind(this);

        sessionService = Utils.getService(ISessionService.class);

        new Thread(this::getDeviceToken).start();
        new Thread(this::silentSignIn).start();
    }

    private void getDeviceToken() {
        final String token = NotificationService.getToken(this);
        Log.i(TAG, "getDeviceToken: " + token);
        if (token == null) {
            final Task<InstanceIdResult> task = FirebaseInstanceId.getInstance().getInstanceId();
            task.addOnSuccessListener(result ->
                    getSharedPreferences("_", MODE_PRIVATE)
                            .edit()
                            .putString("fb", result.getToken())
                            .apply());
        } else {
            getSharedPreferences("_", MODE_PRIVATE)
                    .edit()
                    .putString("fb", token)
                    .apply();
        }
    }


    private void silentSignIn() {
        if (Utils.getApp(this).getLastSignedInAccount() == null) {
            showSignInButton();
        } else {
            Utils.getApp(this).getGoogleSignInClient().silentSignIn()
                    .addOnSuccessListener(this::onSuccess)
                    .addOnFailureListener(this::onFailure);
        }
    }

    private void onSuccess(GoogleSignInAccount account) {
        if (account == null) {
            // No debería suceder si el lastSignedInAccount retornó distinto a null
            showSignInButton();
        } else {
            sendGoogleAccountToBackend(account);
        }
    }

    private void sendGoogleAccountToBackend(@NonNull GoogleSignInAccount account) {
        final Call<UserDataDTO> call = sessionService.getUserData(account.getIdToken());

        if (sessionServiceCallback == null) {
            sessionServiceCallback = new SessionServiceCallback(account);
        }
        call.enqueue(sessionServiceCallback);
    }

    private void onFailure(Exception e) {
        Log.e(TAG, "Silent SingIn Failed. Redirected to SignInActivity", e);
        showSignInButton();
    }

    private void redirectTo(Class<? extends AppCompatActivity> activityClass, final UserDataDTO userData) {
        final Intent intent = new Intent(LoginActivity.this, activityClass);
        if (userData != null) {
            userData.setDeviceToken(NotificationService.getToken(getApplication()));
            intent.putExtra(USER_DATA_KEY, userData);
        }
        startActivity(intent);
        finish();
    }

    private void showSignInButton() {
        progressBar.setVisibility(INVISIBLE);
        signInButton.setVisibility(VISIBLE);
    }

    private void showProgressBar() {
        progressBar.setVisibility(VISIBLE);
        signInButton.setVisibility(INVISIBLE);
    }

    @OnClick(R.id.sign_in_button)
    void signIn() {
        final Intent signInIntent = Utils.getApp(this).getGoogleSignInClient().getSignInIntent();
        startActivityForResult(signInIntent, REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            showProgressBar();
            final GoogleSignInAccount account = GoogleSignIn.getSignedInAccountFromIntent(data).getResult();
            sendGoogleAccountToBackend(Objects.requireNonNull(account));
        }
    }

    private final class SessionServiceCallback implements Callback<UserDataDTO> {

        private final GoogleSignInAccount account;

        private SessionServiceCallback(GoogleSignInAccount account) {
            this.account = account;
        }

        @Override
        public void onResponse(@NonNull Call<UserDataDTO> call, @NonNull Response<UserDataDTO> response) {
            if (response.isSuccessful()) {
                redirectTo(NavigationDrawerActivity.class, response.body());
            } else if (response.code() == 403 || response.code() == 404) {
                // el usuario no está registrado pero hizo signin (i.e. abandonó el proceso de registración)
                UserDataDTO u = createUserDataFromAccount();
                redirectTo(SignUpActivity.class, u);
            } else {
                Log.e(TAG, String.format("Unsuccessful response: %s %d", response.message(), response.code()));
                showRetryAction();
            }
        }

        @NonNull
        private UserDataDTO createUserDataFromAccount() {
            final UserDataDTO u = new UserDataDTO();
            u.setName(account.getGivenName());
            u.setSurname(account.getFamilyName());
            u.setActive(false);
            final String email = Objects.requireNonNull(account.getEmail());
            u.setEmail(email);
            u.setDeviceToken(NotificationService.getToken(getApplication()));
            return u;
        }

        @Override
        public void onFailure(@NonNull Call<UserDataDTO> call, @NonNull Throwable t) {
            // no hay conexion o algun error previo a la llamada
            Log.e(TAG, "SessionService error", t);
            showRetryAction();
        }

        private void showRetryAction() {
            progressBar.setVisibility(INVISIBLE);

            final Snackbar snackbar = Snackbar.make(layout, R.string.error_no_internet, Snackbar.LENGTH_INDEFINITE);

            snackbar.setAction(R.string.retry_button, v -> {
                snackbar.dismiss();
                showProgressBar();
                silentSignIn();
            });

            snackbar.show();
        }
    }
}
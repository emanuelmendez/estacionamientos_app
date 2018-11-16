package gbem.com.ar.estacionamientos.signin;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import gbem.com.ar.estacionamientos.R;
import gbem.com.ar.estacionamientos.api.dtos.UserDataDTO;
import gbem.com.ar.estacionamientos.dashboard.NavigationDrawerActivity;
import gbem.com.ar.estacionamientos.notifications.NotificationService;
import gbem.com.ar.estacionamientos.utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static gbem.com.ar.estacionamientos.utils.Utils.USER_DATA_KEY;
import static gbem.com.ar.estacionamientos.utils.Utils.getApp;

public class SignUpActivity extends AppCompatActivity {

    @BindView(R.id.sign_up_layout)
    ConstraintLayout layout;

    @BindView(R.id.txtHolaLabel)
    TextView txtHolaLabel;

    @BindView(R.id.txtPhone)
    TextInputEditText txtPhone;

    @BindView(R.id.tLayoutPhone)
    TextInputLayout tLayoutPhone;

    @BindView(R.id.btn_continuar)
    Button btnContinuar;

    private UserDataDTO userData;

    private SignUpCallback signUpCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);

        userData = (UserDataDTO) Objects.requireNonNull(getIntent().getExtras()).get(USER_DATA_KEY);

        txtHolaLabel.setText(getString(R.string.signup_hola, Objects.requireNonNull(userData)));
    }


    @OnTextChanged(R.id.txtPhone)
    public void onChange() {
        txtPhone.setError(null);
        btnContinuar.setEnabled(true);
    }

    @OnClick(R.id.btn_continuar)
    public void onContinuar() {
        if (txtPhone.getText().toString().isEmpty()) {
            txtPhone.setError(getString(R.string.error_phone_empty));
            return;
        } else {
            txtPhone.setError(null);
        }

        txtPhone.setEnabled(false);
        btnContinuar.setEnabled(false);
        final ISessionService service = Utils.getService(ISessionService.class);
        final GoogleSignInAccount account = getApp(this).getLastSignedInAccount();

        if (account != null) {
            userData.setPhone(txtPhone.getText().toString());
            userData.setDeviceToken(NotificationService.getToken(this));

            final Call<UserDataDTO> call = service.signUp(account.getIdToken(), userData);

            if (signUpCallback == null) {
                signUpCallback = new SignUpCallback();
            }

            call.enqueue(signUpCallback);
        } else {
            final Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private class SignUpCallback implements Callback<UserDataDTO> {

        @Override
        public void onResponse(@NonNull Call<UserDataDTO> call, @NonNull Response<UserDataDTO> response) {
            if (response.isSuccessful()) {
                final Intent intent = new Intent(SignUpActivity.this, NavigationDrawerActivity.class);
                intent.putExtra(USER_DATA_KEY, response.body());
                startActivity(intent);
                finish();
            } else {
                if (response.code() == 409) {
                    txtPhone.setError("Este número ya fue utilizado por otra cuenta");
                }
                showRetryAction();
            }
        }

        @Override
        public void onFailure(@NonNull Call<UserDataDTO> call, @NonNull Throwable t) {
            showRetryAction();
        }

        private void showRetryAction() {
            txtPhone.setEnabled(true);
            final Snackbar snackbar = Snackbar.make(layout, R.string.error_signup_failed, Snackbar.LENGTH_INDEFINITE);

            snackbar.setAction(R.string.retry_button, v -> {
                snackbar.dismiss();
                onContinuar();
            });

            snackbar.show();
        }
    }

}

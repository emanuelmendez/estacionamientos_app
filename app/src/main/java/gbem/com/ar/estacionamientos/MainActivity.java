package gbem.com.ar.estacionamientos;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.SignInButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.testButton)
    Button testButton;

    @BindView(R.id.sign_in_button)
    SignInButton signInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.sign_in_button)
    void signIn() {
        Intent signInIntent = ((EstacionamientosApp) getApplication()).getGoogleSignInClient().getSignInIntent();
        startActivityForResult(signInIntent, 9001);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (GoogleSignIn.getLastSignedInAccount(this) != null) {
            signInButton.setVisibility(View.INVISIBLE);
        }
    }

    @OnClick(R.id.testButton)
    void callAPI() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            startActivity(new Intent(MainActivity.this, MapsActivity.class));
        } else {
            Toast.makeText(this, "You need to sign in to continue", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.testButtonContentNav)
    void callNavMenu() {
        Intent i = new Intent(MainActivity.this, NavigationDrawerActivity.class);
        startActivity(i);
    }

}



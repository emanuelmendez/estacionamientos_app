package gbem.com.ar.estacionamientos;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import gbem.com.ar.estacionamientos.signin.LoginActivity;
import gbem.com.ar.estacionamientos.utils.Utils;

public class IpConfigActivity extends AppCompatActivity {

    public static final String DEFAULT_IP = "10.0.2.2";

    @BindView(R.id.ip1)
    EditText ip1;
    @BindView(R.id.ip2)
    EditText ip2;
    @BindView(R.id.ip3)
    EditText ip3;
    @BindView(R.id.ip4)
    EditText ip4;
    @BindView(R.id.btn_ignore)
    Button btnIgnore;
    @BindView(R.id.btn_ok)
    Button btnOk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ip_config);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_ignore)
    public void onBtnIgnoreClicked() {
        final String savedIp =
                getSharedPreferences("_", MODE_PRIVATE).getString("default_ip", DEFAULT_IP);
        goToLoginActivity(savedIp);
    }

    @OnClick(R.id.btn_ok)
    public void onBtnOkClicked() {
        String ip = ip1.getText().toString() +
                "." +
                ip2.getText().toString() +
                "." +
                ip3.getText().toString() +
                "." +
                ip4.getText().toString();

        goToLoginActivity(ip);
    }

    private void goToLoginActivity(String ip) {
        Log.d("IpConfigActivity", "IP is: " + ip);

        getSharedPreferences("_", MODE_PRIVATE).edit().putString("default_ip", ip).apply();
        Utils.setApiBaseUrl(ip);

        startActivity(new Intent(IpConfigActivity.this, LoginActivity.class));
        finish();
    }
}

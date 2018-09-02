package gbem.com.ar.estacionamientos;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;

import gbem.com.ar.estacionamientos.api.rest.TestRESTService;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {

    public static final String LOCALHOST_API = "http://10.0.2.2:8080/web/";
    private static final String TAG = "EST_APP";

    private TestRESTService service;

    private Button testButton;
    private TextView txtResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // FIXME los llamados deben hacerse en otro hilo. Esto es solamente para poc
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // FIXME inyectar
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(LOCALHOST_API)
                .build();

        service = retrofit.create(TestRESTService.class);

        txtResult = findViewById(R.id.txtResult);
        testButton = findViewById(R.id.testButton);

        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callAPI();
            }
        });

    }

    private void callAPI() {
        // esta siendo sincronico, por eso en EstacionamientosApp se tuvo que poner el thread policy
        final Call<ResponseBody> stringCall = service.getDBStringObjects();
        try {
            final Response<ResponseBody> response = stringCall.execute();
            Log.d(TAG, "Status code:" + response.code());
            if (response.isSuccessful()) {
                txtResult.setText(response.body().string());
            } else {
                txtResult.setText("Error: " + response.message());
            }
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            txtResult.setText("Error IO al realizar el llamado, ver log");
        }
    }
}

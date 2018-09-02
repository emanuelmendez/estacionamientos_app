package gbem.com.ar.estacionamientos;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import gbem.com.ar.estacionamientos.api.dtos.TestObjectDTO;
import gbem.com.ar.estacionamientos.api.rest.TestRESTService;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private static final String LOCALHOST_API = "http://192.168.0.17:8080/web/";
    private static final String TAG = "EST_APP";
    @BindView(R.id.testButton)
    Button testButton;
    @BindView(R.id.txtResult)
    TextView txtResult;
    private TestRESTService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        // FIXME los llamados deben hacerse en otro hilo. Esto es solamente para poc
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // FIXME inyectar
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(LOCALHOST_API)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(TestRESTService.class);
    }

    @OnClick(R.id.testButton)
    void callAPI() {
        // esta siendo sincronico, por eso en el onCreate se tuvo que poner el thread policy
        final Call<List<TestObjectDTO>> stringCall = service.getDBStringObjects();
        try {
            final Response<List<TestObjectDTO>> response = stringCall.execute();
            Log.d(TAG, "Status code:" + response.code());
            if (response.isSuccessful()) {
                final List<TestObjectDTO> dtos = response.body();
                txtResult.setText(dtos.toString());
            } else {
                txtResult.setText("Error: " + response.message());
            }
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            txtResult.setText("Error IO al realizar el llamado, ver log");
        }
    }
}

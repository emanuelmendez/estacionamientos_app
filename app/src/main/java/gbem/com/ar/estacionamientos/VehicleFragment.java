package gbem.com.ar.estacionamientos;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentManagerNonConfig;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import gbem.com.ar.estacionamientos.api.dtos.VehicleDTO;
import gbem.com.ar.estacionamientos.api.rest.IVehicleService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class VehicleFragment extends Fragment {

    //TODO
    private static final String API_BASE_URL = "http://192.168.0.1:8080/web/";

    private Button addVehicleButton;
    private RecyclerView mRVVehicleList;
    private AdapterVehicle mAdapter;
    private IVehicleService iVehicleService;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_vehicle, null, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Formulario de alta de vehículo
        view.findViewById(R.id.button_add_vehicle).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                VehicleDialogFragment vehicleDialogFragment = new VehicleDialogFragment();
                vehicleDialogFragment.show(getFragmentManager(),"");
            }
        });

        //Listado de vehículos
        getVehicleJson();
    }

    public void getVehicleJson(){
        /*String json = "[{\"id\":1, \"plate\":\"ASD123\",\"brand\":\"Ford\",\"model\":\"Ka\",\"color\":\"Blanco\"},\n" +
                "{\"id\":\"2\",\"plate\":\"ZXC555\",\"brand\":\"Fiat\",\"model\":\"Palio\",\"color\":\"Rojo\"},\n" +
                "{\"id\":\"3\",\"plate\":\"TGB444\",\"brand\":\"Renault\",\"model\":\"Clio\",\"color\":\"Azul claro\"},\n" +
                "{\"id\":\"4\",\"plate\":\"YHN885\",\"brand\":\"Ford\",\"model\":\"Fiesta\",\"color\":\"Turquesa\"},\n" +
                "{\"id\":\"5\",\"plate\":\"ESG413\",\"brand\":\"Audi\",\"model\":\"C4\",\"color\":\"Gris claro\"},\n" +
                "{\"id\":\"6\",\"plate\":\"LSK953\",\"brand\":\"Peugeot\",\"model\":\"206\",\"color\":\"Negro\"}]";

        return json;*/


        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        iVehicleService = retrofit.create(IVehicleService.class);
        Call<List<VehicleDTO>> call = iVehicleService.getVehicleByUserId(1); //TODO debe tomar el id de usuario loggeado

        call.enqueue(new Callback<List<VehicleDTO>>() {
            @Override
            public void onResponse(Call<List<VehicleDTO>> call, Response<List<VehicleDTO>> response) {
                switch (response.code()) {
                    case 200:
                        generateDataList(response.body());
                        break;
                    case 401:
                        Log.i("TAG","ENTRO POR 401");
                        break;
                    default:
                        Log.i("TAG","ENTRO POR DEFAULT");
                        break;
                }
            }

            @Override
            public void onFailure(Call<List<VehicleDTO>> call, Throwable t) {
                Toast.makeText(getActivity(), "Error al cargar listado de vehículos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void generateDataList(List<VehicleDTO> result) {

        List<VehicleDTO> data = new ArrayList<>();

        try {
            JSONArray jArray = new JSONArray(result);

            for(int i=0;i<jArray.length();i++){
                JSONObject json_data = jArray.getJSONObject(i);
                VehicleDTO vehicleData = new VehicleDTO();
                vehicleData.setId(json_data.getLong("id"));
                vehicleData.setPlate(json_data.getString("plate"));
                vehicleData.setBrand(json_data.getString("brand"));
                vehicleData.setModel(json_data.getString("model"));
                vehicleData.setColor(json_data.getString("color"));
                data.add(vehicleData);
            }

            mRVVehicleList = (RecyclerView)getView().findViewById(R.id.vehicleList);
            //LinearLayoutManager manager = new LinearLayoutManager(getContext());
            //mRVVehicleList.setLayoutManager(manager);
            //mRVVehicleList.setHasFixedSize(true);
            mAdapter = new AdapterVehicle(getActivity(), data);
            mRVVehicleList.setAdapter(mAdapter);
            mRVVehicleList.setLayoutManager(new LinearLayoutManager(getActivity()));

        } catch (JSONException e) {
            Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
        }
    }
}

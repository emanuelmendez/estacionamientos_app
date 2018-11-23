package gbem.com.ar.estacionamientos.vehicles;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import gbem.com.ar.estacionamientos.R;
import gbem.com.ar.estacionamientos.api.dtos.VehicleDTO;
import gbem.com.ar.estacionamientos.utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static gbem.com.ar.estacionamientos.utils.Utils.getIdToken;

public class VehicleFragment extends Fragment implements IDialogDismissListener{

    private RecyclerView mRVVehicleList;
    private AdapterVehicle mAdapter;
    private IVehicleService iVehicleService;

    @BindView(R.id.button_add_vehicle)
    ImageButton addVehicle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_vehicle, null, false);
        ButterKnife.bind(this,view);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Formulario de alta de vehículo
        addVehicle.setOnClickListener(v -> {
            VehicleDialogFragment vehicleDialogFragment = new VehicleDialogFragment();
            Bundle bundle = new Bundle();
            bundle.putString("BRAND_TO_SET","");
            bundle.putString("COLOR_TO_SET","");
            bundle.putString("PLATE_TO_SET","");
            bundle.putString("MODEL_TO_SET","");
            bundle.putLong("VEHICLE_ID",0);
            vehicleDialogFragment.setArguments(bundle);
            vehicleDialogFragment.setListener(this);

            vehicleDialogFragment.show(getFragmentManager(),"");
        });

        //Listado de vehículos
        getVehicleJson();
    }

    public void getVehicleJson(){

        if (iVehicleService == null) {
            iVehicleService = Utils.getService(IVehicleService.class);
        }

        Call<List<VehicleDTO>> call = iVehicleService.getVehicleByUser(getIdToken(this.getActivity()));

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
                        Log.i("TAG","ENTRO POR DEFAULT "+response.code());
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
            for(int i=0;i<result.size();i++){
                VehicleDTO vehicleData = new VehicleDTO();

                vehicleData.setId(result.get(i).getId());
                vehicleData.setPlate(result.get(i).getPlate());
                vehicleData.setBrand(result.get(i).getBrand());
                vehicleData.setModel(result.get(i).getModel());
                vehicleData.setColor(result.get(i).getColor());
                data.add(vehicleData);
            }

            mRVVehicleList = getView().findViewById(R.id.vehicleList);
            mAdapter = new AdapterVehicle(getActivity(), getActivity(), data);
            mRVVehicleList.setAdapter(mAdapter);
            mRVVehicleList.setLayoutManager(new LinearLayoutManager(getActivity()));

        } catch (Exception e) {
            Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDismissClick() {
        final Handler handler = new Handler();
        handler.postDelayed(this::getVehicleJson, 500);
    }
}
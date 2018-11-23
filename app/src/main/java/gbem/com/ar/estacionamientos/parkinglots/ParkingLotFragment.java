package gbem.com.ar.estacionamientos.parkinglots;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import gbem.com.ar.estacionamientos.R;
import gbem.com.ar.estacionamientos.api.dtos.AddressDTO;
import gbem.com.ar.estacionamientos.api.dtos.ParkingLotDTO;
import gbem.com.ar.estacionamientos.api.dtos.UserDataDTO;
import gbem.com.ar.estacionamientos.utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static gbem.com.ar.estacionamientos.utils.Utils.getIdToken;

public class ParkingLotFragment extends Fragment {

    private RecyclerView mRVAddLotsList;
    private AdapterParkingLot mAdapter;
    private IParkingLotService iParkingLotService;
    List<ParkingLotDTO> lotList = new ArrayList<>();
    AddressDTO currentAddress = new AddressDTO();
    private UserDataDTO userData;

    @BindView(R.id.lbl_full_address)
    TextView fullAddress;

    @BindView(R.id.btn_add_new_lot)
    Button addNewLot;

    @BindView(R.id.button_add_parking_lot)
    ImageButton addParkingLot;

    @BindView(R.id.lbl_add_parking_lot)
    TextView lblAddParkingLot;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_parking_lot, container, false);
        ButterKnife.bind(this,view);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getParkingLotJson();

        //Formulario de alta de estacionamiento
        addParkingLot.setOnClickListener(v -> {
            Fragment fragment = new AddParkingLotFragment();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction ft = fragmentManager.beginTransaction();

            ft.replace(R.id.screen_area, fragment).commit();
        });

        addNewLot.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("addressData",currentAddress);
            Fragment fragment = new AddNewLotFragment();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction ft = fragmentManager.beginTransaction();
            fragment.setArguments(bundle);

            ft.replace(R.id.screen_area, fragment).commit();
        });
    }

    public void getParkingLotJson(){

        if (iParkingLotService == null) {
            iParkingLotService = Utils.getService(IParkingLotService.class);
        }

        Call<List<ParkingLotDTO>> call = iParkingLotService.getParkingLotsByUser(getIdToken(this.getActivity()));

        call.enqueue(new Callback<List<ParkingLotDTO>>() {
            @Override
            public void onResponse(Call<List<ParkingLotDTO>> call, Response<List<ParkingLotDTO>> response) {
                switch (response.code()) {
                    case 200:
                        lblAddParkingLot.setVisibility(View.GONE);
                        addParkingLot.setVisibility(View.GONE);
                        fullAddress.setVisibility(View.VISIBLE);
                        addNewLot.setVisibility(View.VISIBLE);
                        generateDataList(response.body());
                        setAddress(response.body());
                        lotList = response.body();
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
            public void onFailure(Call<List<ParkingLotDTO>> call, Throwable t) {
                Toast.makeText(getActivity(), "Error al cargar listado de vehículos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void generateDataList(List<ParkingLotDTO> result) {

        try {
            mRVAddLotsList = getView().findViewById(R.id.parkingLotList);
            mAdapter = new AdapterParkingLot(getContext(), result);
            mRVAddLotsList.setAdapter(mAdapter);
            mRVAddLotsList.setLayoutManager(new LinearLayoutManager(getContext()));

        } catch (Exception e) {
            Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    private void setAddress(List<ParkingLotDTO> result){

        currentAddress = result.get(0).getAddressDTO();
        String address = "";
        address += result.get(0).getAddressDTO().getStreetAddress()+", ";
        address += result.get(0).getAddressDTO().getState()+", ";
        address += result.get(0).getAddressDTO().getCity()+", ";
        address += result.get(0).getAddressDTO().getCountry();

        fullAddress.setText(address);
    }
}

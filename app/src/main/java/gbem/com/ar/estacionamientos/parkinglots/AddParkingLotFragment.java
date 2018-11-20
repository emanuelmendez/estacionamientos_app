package gbem.com.ar.estacionamientos.parkinglots;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import gbem.com.ar.estacionamientos.R;
import gbem.com.ar.estacionamientos.api.dtos.AddressDTO;
import gbem.com.ar.estacionamientos.api.dtos.ParkingLotDTO;
import gbem.com.ar.estacionamientos.api.rest.IParkingLotService;
import gbem.com.ar.estacionamientos.utils.Utils;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static gbem.com.ar.estacionamientos.utils.Utils.getIdToken;

public class AddParkingLotFragment extends Fragment {

    private RecyclerView mRVAddLotsList;
    private AdapterAddParkingLot mAdapter;
    private IParkingLotService iParkingLotService;
    List<ParkingLotDTO> tempList = new ArrayList<>();
    AddressDTO tempAddress = new AddressDTO();

    @BindView(R.id.txt_street_address)
    EditText streetAddress;

    @BindView(R.id.spinner_cities)
    Spinner spinnerCity;

    @BindView(R.id.spinner_country)
    Spinner spinnerCountry;

    @BindView(R.id.btnVerEnMapa)
    Button showAddressInMap;

    @BindView(R.id.txt_lot_number)
    TextView lotNumber;

    @BindView(R.id.num_lot_rate)
    TextView lotRate;

    @BindView(R.id.txt_lot_description)
    TextView lotDescription;

    @BindView(R.id.btn_add_lot)
    Button addLot;

    @BindView(R.id.btn_save_parking_lot)
    Button saveParkingLot;

    @BindView(R.id.btn_cancel_parking_lot)
    Button cancelParkingLot;

    @BindView(R.id.add_parking_lot_layout)
    FrameLayout layout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_parking_lot, container, false);
        ButterKnife.bind(this,view);

        //City spinner
        ArrayAdapter<CharSequence> adapterCity = ArrayAdapter.createFromResource(getActivity(),
                R.array.cities_array, android.R.layout.simple_spinner_item);
        adapterCity.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCity.setAdapter(adapterCity);

        //Country spinner
        ArrayAdapter<CharSequence> adapterCountry = ArrayAdapter.createFromResource(getActivity(),
                R.array.countries_array, android.R.layout.simple_spinner_item);
        adapterCountry.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCountry.setAdapter(adapterCountry);

        addLotToList();
        return view;
    }

    @OnClick(R.id.btn_add_lot)
    public void addLotToList(){

        ParkingLotDTO pl1 = new ParkingLotDTO();

        if(lotNumber.getText().length() == 0){
            lotNumber.setError("Campo requerido");
            return;
        }else if(lotRate.getText().length() == 0){
            lotRate.setError("Campo requerido");
            return;
        }else{
            lotNumber.setError(null);
            lotRate.setError(null);
        }

        pl1.setActive(true);
        pl1.setDescription(lotDescription.getText().toString());
        pl1.setLotNumber(Integer.parseInt(lotNumber.getText().toString()));
        pl1.setValue(Long.parseLong(lotRate.getText().toString()));
        tempList.add(pl1);

        resetLotForm();
        generateDataList();
    }

    private void generateDataList() {

        try {
            mRVAddLotsList = getView().findViewById(R.id.parkingLotList);
            mAdapter = new AdapterAddParkingLot(getContext(), tempList);
            mRVAddLotsList.setAdapter(mAdapter);
            mRVAddLotsList.setLayoutManager(new LinearLayoutManager(getContext()));

        } catch (Exception e) {
            Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    private void resetLotForm(){
        lotNumber.setText("");
        lotRate.setText("");
        lotDescription.setText("");
    }

    @OnClick(R.id.btn_save_parking_lot)
    public void saveParkingLot(){

        if(streetAddress.getText().length() == 0){
            streetAddress.setError("Campo requerido");
            return;
        }else if(tempList.size() == 0){
            final Snackbar snackbar = Snackbar.make(layout, R.string.error_no_lot, Snackbar.LENGTH_LONG);
            snackbar.show();
            return;
        }

        streetAddress.setError(null);

        final Geocoder geocoder = new Geocoder(getContext());
        final List<Address> addresses;
        String fullAddress = streetAddress.getText().toString()
                +", "+spinnerCity.getSelectedItem().toString()
                +", "+spinnerCountry.getSelectedItem().toString();
        try {
            addresses = geocoder.getFromLocationName(fullAddress, 1);
        } catch (IOException e) {
            Log.e("Error", "Geocoder exception: " + e.getMessage(), e);
            Toast.makeText(getContext(), R.string.location_search_error, Toast.LENGTH_SHORT).show();
            return;
        }
        if (addresses.isEmpty()) {
            streetAddress.setError(getString(R.string.location_search_not_found));
            return;
        }

        final Address address = addresses.get(0);

        tempAddress.setLatitude(address.getLatitude());
        tempAddress.setLongitude(address.getLongitude());
        tempAddress.setStreetAddress(streetAddress.getText().toString());
        tempAddress.setCity(spinnerCity.getSelectedItem().toString());
        tempAddress.setState(spinnerCity.getSelectedItem().toString());
        tempAddress.setCountry(spinnerCountry.getSelectedItem().toString());
        tempAddress.setPostalCode(address.getPostalCode());

        for(ParkingLotDTO pl : tempList){
            pl.setAddressDTO(tempAddress);
        }

        saveData();
    }

    @OnClick(R.id.btn_cancel_parking_lot)
    public void cancelParkingLot(){

        Fragment fragment = new ParkingLotFragment();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();

        ft.replace(R.id.screen_area, fragment).commit();
    }

    public void saveData(){

        if (iParkingLotService == null) {
            iParkingLotService = Utils.getService(IParkingLotService.class);
        }

        Call<ResponseBody> call = iParkingLotService.addNewParkingLot(getIdToken(this.getActivity()),1, tempList); //TODO debe tomar el id de usuario loggeado

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                switch (response.code()) {
                    case 201:
                        Toast.makeText(getContext(), "Estacionamiento guardado con éxito", Toast.LENGTH_SHORT).show();
                        Log.i("TAG","ENTRO POR 201");
                        cancelParkingLot();
                        break;
                    case 200:
                        Toast.makeText(getView().getContext(), "Estacionamiento editado con éxito", Toast.LENGTH_SHORT).show();
                        Log.i("TAG","ENTRO POR 200");
                        cancelParkingLot();
                        break;
                    case 401:
                        Log.i("TAG","ENTRO POR 401");
                        break;
                    default:
                        Log.i("TAG","ENTRO POR DEFAULT  "+response.code());
                        break;
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("ERRORLOTE",t.getMessage(),t);
                Toast.makeText(getContext(), "Error al intentar guardar el estacionamiento", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

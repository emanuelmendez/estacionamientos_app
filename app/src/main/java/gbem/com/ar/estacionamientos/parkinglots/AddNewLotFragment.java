package gbem.com.ar.estacionamientos.parkinglots;


import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import gbem.com.ar.estacionamientos.R;
import gbem.com.ar.estacionamientos.api.dtos.AddressDTO;
import gbem.com.ar.estacionamientos.api.dtos.ParkingLotDTO;
import gbem.com.ar.estacionamientos.api.dtos.ScheduleDTO;
import gbem.com.ar.estacionamientos.utils.Utils;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static gbem.com.ar.estacionamientos.utils.Utils.getIdToken;

public class AddNewLotFragment extends Fragment {

    private IParkingLotService iParkingLotService;
    List<ParkingLotDTO> tempList = new ArrayList<>();
    AddressDTO tempAddress = new AddressDTO();

    @BindView(R.id.txt_lot_number)
    TextView lotNumber;

    @BindView(R.id.num_lot_rate)
    TextView lotRate;

    @BindView(R.id.txt_lot_description)
    TextView lotDescription;

    @BindView(R.id.edit_parking_lot_layout2)
    FrameLayout layout;

    @BindView(R.id.chk_monday_lot)
    CheckBox chkMonday;

    @BindView(R.id.chk_tuesday_lot)
    CheckBox chkTuesday;

    @BindView(R.id.chk_wednesday_lot)
    CheckBox chkWednesday;

    @BindView(R.id.chk_thursday_lot)
    CheckBox chkThursday;

    @BindView(R.id.chk_friday_lot)
    CheckBox chkFriday;

    @BindView(R.id.chk_saturday_lot)
    CheckBox chkSaturday;

    @BindView(R.id.chk_sunday_lot)
    CheckBox chkSunday;

    @BindView(R.id.spinner_time_from)
    Spinner timeFrom;

    @BindView(R.id.spinner_time_to)
    Spinner timeTo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_parking_lot2, container, false);
        ButterKnife.bind(this,view);

        //TimeFrom and TimeTo spinner
        ArrayAdapter<CharSequence> adapterTime = ArrayAdapter.createFromResource(getActivity(),
                R.array.schedule_time_array, android.R.layout.simple_spinner_item);
        adapterTime.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeFrom.setAdapter(adapterTime);
        timeTo.setAdapter(adapterTime);

        Bundle args = getArguments();
        tempAddress = (AddressDTO) args.getSerializable("addressData");

        return view;
    }

    @OnClick(R.id.btn_save_parking_lot)
    public void saveParkingLot(){

        ParkingLotDTO pl1 = new ParkingLotDTO();
        ScheduleDTO sch = new ScheduleDTO();

        if(lotNumber.getText().length() == 0){
            lotNumber.setError("Campo requerido");
            return;
        }else if(lotRate.getText().length() == 0) {
            lotRate.setError("Campo requerido");
            return;
        }else if(!chkMonday.isChecked() && !chkTuesday.isChecked() && !chkWednesday.isChecked() && !chkThursday.isChecked()
                && !chkFriday.isChecked() && !chkSaturday.isChecked() && !chkSunday.isChecked()){
            final Snackbar snackbar = Snackbar.make(layout, R.string.error_no_day_selected, Snackbar.LENGTH_LONG);
            snackbar.show();
            return;
        }else{
            lotNumber.setError(null);
            lotRate.setError(null);
        }

        if(chkMonday.isChecked()){ sch.setMonday(true); }
        if(chkTuesday.isChecked()){ sch.setTuesday(true); }
        if(chkWednesday.isChecked()){ sch.setWednesday(true); }
        if(chkThursday.isChecked()){ sch.setThursday(true); }
        if(chkFriday.isChecked()){ sch.setFriday(true); }
        if(chkSaturday.isChecked()){ sch.setSaturday(true); }
        if(chkSunday.isChecked()){ sch.setSunday(true); }
        sch.setFromHour(Integer.parseInt(timeFrom.getSelectedItem().toString().split(":")[0]));
        sch.setToHour(Integer.parseInt(timeTo.getSelectedItem().toString().split(":")[0]));

        pl1.setActive(true);
        pl1.setScheduleDTO(sch);
        pl1.setDescription(lotDescription.getText().toString());
        pl1.setLotNumber(Integer.parseInt(lotNumber.getText().toString()));
        pl1.setValue(Long.parseLong(lotRate.getText().toString()));
        pl1.setAddressDTO(tempAddress);
        tempList.add(pl1);

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

        Call<ResponseBody> call = iParkingLotService.addNewParkingLot(getIdToken(this.getActivity()),tempList);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                switch (response.code()) {
                    case 201:
                        Toast.makeText(getContext(), "Estacionamiento guardado con éxito", Toast.LENGTH_SHORT).show();
                        Log.i("TAG","ENTRO POR 201");
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

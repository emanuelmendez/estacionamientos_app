package gbem.com.ar.estacionamientos.vehicles;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import gbem.com.ar.estacionamientos.EstacionamientosApp;
import gbem.com.ar.estacionamientos.R;
import gbem.com.ar.estacionamientos.api.dtos.VehicleDTO;
import gbem.com.ar.estacionamientos.api.rest.IVehicleService;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VehicleDialogFragment extends AppCompatDialogFragment {

    private IVehicleService iVehicleService;
    private DialogInterface.OnDismissListener onDismissListener;
    private IDialogDismissListener iDialogDismissListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Bundle args = getArguments();
        String brandOptionVal = args.getString("BRAND_TO_SET");
        String colorOptionVal = args.getString("COLOR_TO_SET");
        String plateVal = args.getString("PLATE_TO_SET");
        String modelVal = args.getString("MODEL_TO_SET");

        //Create View
        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_vehicle_input_form, null);

        if (iVehicleService == null) {
            iVehicleService = ((EstacionamientosApp) v.getContext().getApplicationContext()).getService(IVehicleService.class);
        }

        //Brand spinner
        Spinner spinnerBrand = v.findViewById(R.id.spinner_brand);
        ArrayAdapter<CharSequence> adapterBrand = ArrayAdapter.createFromResource(getActivity(),
                R.array.brands_array, android.R.layout.simple_spinner_item);
        adapterBrand.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBrand.setAdapter(adapterBrand);
        spinnerBrand.setSelection(((ArrayAdapter)spinnerBrand.getAdapter()).getPosition(brandOptionVal));

        //Color spinner
        Spinner spinnerColor = v.findViewById(R.id.spinner_color);
        ArrayAdapter<CharSequence> adapterColor = ArrayAdapter.createFromResource(getActivity(),
                R.array.colors_array, android.R.layout.simple_spinner_item);
        adapterColor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerColor.setAdapter(adapterColor);
        spinnerColor.setSelection(((ArrayAdapter)spinnerColor.getAdapter()).getPosition(colorOptionVal));

        //Set Plate and Model
        EditText plate = v.findViewById(R.id.txt_plate);
        EditText model = v.findViewById(R.id.txt_model);
        plate.setText(plateVal);
        model.setText(modelVal);

        //Create button listener
        DialogInterface.OnClickListener saveData = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                VehicleDTO jsonData = new VehicleDTO();

                jsonData.setPlate(plate.getText().toString());
                jsonData.setBrand(spinnerBrand.getSelectedItem().toString());
                jsonData.setModel(model.getText().toString());
                jsonData.setColor(spinnerColor.getSelectedItem().toString());

                Call<ResponseBody> saveNewVehicle = iVehicleService.saveNewVehicle(1,jsonData);//TODO reemplazar con id de usuario loggeado
                saveNewVehicle.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        switch (response.code()) {
                            case 201:
                                Log.i("TAG",response.body().toString());
                                Toast.makeText(v.getContext(), "Vehículo guardado con éxito", Toast.LENGTH_SHORT).show();
                                Log.i("TAG","ENTRO POR 201");
                                dialogInterface.cancel();
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
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(getContext(), "Error al intentar guardar el vehículo", Toast.LENGTH_SHORT).show();
                    }
                });

                Log.i("TAG","Se clickeó guardar!!");
            }
        };

        DialogInterface.OnClickListener cancel = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
                Log.i("TAG","Se clickeó cancelar!!");
            }
        };

        //Build
        return new AlertDialog.Builder(getActivity())
                .setTitle("Complete el formulario")
                .setView(v)
                .setPositiveButton(R.string.save,saveData)
                .setNegativeButton(R.string.cancel,cancel)
                .create();
    }
    public void setListener(IDialogDismissListener listener) {
        iDialogDismissListener = listener;
    }

    /*
    public void onClick(View v) {
        iDialogDismissListener.onDismissClick();
        getDialog().dismiss();
    }
*/
    @Override
    public void onStop() {
        super.onStop();
        if(iDialogDismissListener!=null)
            iDialogDismissListener.onDismissClick();
    }

}

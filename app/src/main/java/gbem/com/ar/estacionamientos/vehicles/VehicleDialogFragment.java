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

import gbem.com.ar.estacionamientos.R;

public class VehicleDialogFragment extends AppCompatDialogFragment {

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
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.i("TAG","Se clickeó botón!!");
            }
        };

        //Build
        return new AlertDialog.Builder(getActivity())
                .setTitle("Complete el formulario")
                .setView(v)
                .setPositiveButton(R.string.cancel,listener)
                .create();
    }
}

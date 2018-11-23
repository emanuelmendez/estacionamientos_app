package gbem.com.ar.estacionamientos.vehicles;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import gbem.com.ar.estacionamientos.R;
import gbem.com.ar.estacionamientos.api.dtos.VehicleDTO;
import gbem.com.ar.estacionamientos.utils.Utils;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static gbem.com.ar.estacionamientos.utils.Utils.getIdToken;

public class AdapterVehicle extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements IDialogDismissListener {
    List<VehicleDTO> data;
    private Context context;
    private LayoutInflater inflater;
    private IVehicleService iVehicleService;
    private Activity activity;

    // create constructor to innitilize context and data sent from MainActivity
    AdapterVehicle(Activity activity, Context context, List<VehicleDTO> data) {
        this.activity = activity;
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
    }

    // Inflate the layout when viewholder created
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.container_vehicle, parent, false);
        MyHolder holder = new MyHolder(view);

        if (iVehicleService == null) {
            iVehicleService = Utils.getService(IVehicleService.class);
        }

        //EDIT
        ImageButton btnEditVehicle = view.findViewById(R.id.button_edit_vehicle);
        btnEditVehicle.setOnClickListener(v -> {
            //Formulario de edición de vehículo
            String currentBrand = ((TextView) view.findViewById(R.id.textBrand)).getText().toString().split(" ")[1];
            String currentColor = ((TextView) view.findViewById(R.id.textColor)).getText().toString().split(" ")[1];
            String currentPlate = ((TextView) view.findViewById(R.id.textPlate)).getText().toString();
            String currentModel = ((TextView) view.findViewById(R.id.textModel)).getText().toString().split(" ")[1];

            FragmentActivity activity = (FragmentActivity) (context);
            FragmentManager fm = activity.getSupportFragmentManager();
            VehicleDialogFragment vehicleDialogFragment = new VehicleDialogFragment();
            Bundle bundle = new Bundle();
            bundle.putString("BRAND_TO_SET", currentBrand);
            bundle.putString("COLOR_TO_SET", currentColor);
            bundle.putString("PLATE_TO_SET", currentPlate);
            bundle.putString("MODEL_TO_SET", currentModel);
            bundle.putLong("VEHICLE_ID", Long.parseLong((v.getTag().toString()).split("_")[1]));
            vehicleDialogFragment.setArguments(bundle);
            vehicleDialogFragment.setListener(this);
            vehicleDialogFragment.show(fm, "Editar vehículo");
            vehicleDialogFragment.getView();

            Log.i("EDIT", "Posición!! " + (v.getTag().toString()).split("_")[0]);
            Log.i("EDIT", "ID !! " + (v.getTag().toString()).split("_")[1]);
        });

        //DELETE
        ImageButton btnDeleteVehicle = view.findViewById(R.id.button_delete_vehicle);
        btnDeleteVehicle.setOnClickListener(v -> {
            //Delete alert
            AlertDialog.Builder alert = new AlertDialog.Builder(v.getContext());
            alert.setTitle("Eliminar vehículo");
            alert.setMessage("¿Está seguro que quiere eliminar este vehículo?");
            //Confirma eliminación
            alert.setPositiveButton(android.R.string.yes, (dialog, which) -> {

                String idVehicle = (v.getTag().toString()).split("_")[1];
                String rowPosition = (v.getTag().toString()).split("_")[0];
                Call<ResponseBody> deleteVehicleRequest = iVehicleService.deleteVehicleById(getIdToken(activity), Long.parseLong(idVehicle));
                deleteVehicleRequest.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                        switch (response.code()) {
                            case 204:
                            case 200:
                                data.remove(Integer.parseInt(rowPosition));
                                notifyDataSetChanged();
                                dialog.cancel();
                                Toast.makeText(v.getContext(), "Vehículo eliminado", Toast.LENGTH_SHORT).show();
                                break;
                            case 401:
                                Log.i("TAG", "ENTRO POR 401");
                                break;
                            default:
                                Log.i("TAG", "ENTRO POR DEFAULT " + response.code());
                                break;
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                        Toast.makeText(v.getContext(), "Error al eliminar vehículo", Toast.LENGTH_SHORT).show();
                    }
                });
            });

            alert.setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.cancel());
            alert.show();
        });

        return holder;
    }

    // Bind data
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        // Get current position of item in recyclerview to bind data and assign values from list
        MyHolder myHolder = (MyHolder) holder;
        VehicleDTO current = data.get(position);
        myHolder.textPlate.setText(current.getPlate());
        myHolder.textBrand.setText("Marca: " + current.getBrand());
        myHolder.textModel.setText("Modelo: " + current.getModel());
        myHolder.textColor.setText("Color: " + current.getColor());
        myHolder.editButton.setTag(position + "_" + current.getId());
        myHolder.deleteButton.setTag(position + "_" + current.getId());
    }

    @Override
    public int getItemCount() {
        if (data != null)
            return data.size();
        return 0;
    }

    @Override
    public void onDismissClick() {
        final Handler handler = new Handler();
        handler.postDelayed(() -> {

            //TODO: instancia un nuevo VehicleFragment para actualizar vista, mejorar!
            FragmentActivity activity = (FragmentActivity) (context);
            Fragment fragment = new VehicleFragment();
            FragmentManager fragmentManager = activity.getSupportFragmentManager();
            FragmentTransaction ft = fragmentManager.beginTransaction();

            ft.replace(R.id.screen_area, fragment);
            ft.commit();
        }, 500);
    }

    class MyHolder extends RecyclerView.ViewHolder {

        TextView textPlate;
        TextView textBrand;
        TextView textModel;
        TextView textColor;
        ImageButton editButton;
        ImageButton deleteButton;

        // create constructor to get widget reference
        MyHolder(View itemView) {
            super(itemView);
            textPlate = itemView.findViewById(R.id.textPlate);
            textBrand = itemView.findViewById(R.id.textBrand);
            textModel = itemView.findViewById(R.id.textModel);
            textColor = itemView.findViewById(R.id.textColor);
            editButton = itemView.findViewById(R.id.button_edit_vehicle);
            deleteButton = itemView.findViewById(R.id.button_delete_vehicle);
        }

    }

}

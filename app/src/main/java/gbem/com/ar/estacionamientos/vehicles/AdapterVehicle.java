package gbem.com.ar.estacionamientos.vehicles;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collections;
import java.util.List;

import gbem.com.ar.estacionamientos.EstacionamientosApp;
import gbem.com.ar.estacionamientos.R;
import gbem.com.ar.estacionamientos.api.dtos.VehicleDTO;
import gbem.com.ar.estacionamientos.api.rest.IVehicleService;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdapterVehicle extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private LayoutInflater inflater;
    List<VehicleDTO> data= Collections.emptyList();
    VehicleDTO current;
    int currentPos=0;
    private IVehicleService iVehicleService;

    // create constructor to innitilize context and data sent from MainActivity
    public AdapterVehicle(Context context, List<VehicleDTO> data){
        this.context=context;
        inflater= LayoutInflater.from(context);
        this.data=data;
    }

    // Inflate the layout when viewholder created
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.container_vehicle, parent,false);
        MyHolder holder = new MyHolder(view);

        if (iVehicleService == null) {
            iVehicleService = ((EstacionamientosApp) view.getContext().getApplicationContext()).getService(IVehicleService.class);
        }

        //EDIT
        ImageButton btnEditVehicle = (ImageButton) view.findViewById(R.id.button_edit_vehicle);
        btnEditVehicle.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Formulario de edición de vehículo
                String currentBrand = ((TextView) view.findViewById(R.id.textBrand)).getText().toString().split(" ")[1];
                String currentColor = ((TextView) view.findViewById(R.id.textColor)).getText().toString().split(" ")[1];
                String currentPlate = ((TextView) view.findViewById(R.id.textPlate)).getText().toString();
                String currentModel = ((TextView) view.findViewById(R.id.textModel)).getText().toString().split(" ")[1];
                FragmentActivity activity = (FragmentActivity)(context);
                FragmentManager fm = activity.getSupportFragmentManager();
                VehicleDialogFragment vehicleDialogFragment = new VehicleDialogFragment();
                Bundle bundle = new Bundle();
                bundle.putString("BRAND_TO_SET",currentBrand);
                bundle.putString("COLOR_TO_SET",currentColor);
                bundle.putString("PLATE_TO_SET",currentPlate);
                bundle.putString("MODEL_TO_SET",currentModel);
                vehicleDialogFragment.setArguments(bundle);
                vehicleDialogFragment.show(fm,"Editar vehículo");
                vehicleDialogFragment.getView();
                String currentPosition = (v.getTag().toString()).split("_")[0];
                Log.i("EDIT","Posición!! "+(v.getTag().toString()).split("_")[0]);
                Log.i("EDIT","ID !! "+(v.getTag().toString()).split("_")[1]);
            }
        });

        //DELETE
        ImageButton btnDeleteVehicle = (ImageButton) view.findViewById(R.id.button_delete_vehicle);
        btnDeleteVehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Delete alert
                AlertDialog.Builder alert = new AlertDialog.Builder(v.getContext());
                alert.setTitle("Eliminar vehículo");
                alert.setMessage("¿Está seguro que quiere eliminar éste vehículo?");
                //Confirma eliminación
                alert.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //TODO debe tomar el id de usuario loggeado
                        String idVehicle = (v.getTag().toString()).split("_")[1];
                        String rowPosition = (v.getTag().toString()).split("_")[0];
                        Call<ResponseBody> deleteVehicleRequest = iVehicleService.deleteVehicleById(1,Long.parseLong(idVehicle));
                        deleteVehicleRequest.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                switch (response.code()) {
                                    case 200:
                                        data.remove(Integer.parseInt(rowPosition));
                                        notifyDataSetChanged();
                                        dialog.cancel();
                                        Toast.makeText(v.getContext(), "Vehículo eliminado", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(v.getContext(), "Error al eliminar vehículo", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                //Cancela
                alert.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // close dialog
                        dialog.cancel();
                    }
                });
                alert.show();
            }
        });

        return holder;
    }

    // Bind data
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        // Get current position of item in recyclerview to bind data and assign values from list
        MyHolder myHolder= (MyHolder) holder;
        VehicleDTO current=data.get(position);
        myHolder.textPlate.setText(current.getPlate());
        myHolder.textBrand.setText("Marca: " + current.getBrand());
        myHolder.textModel.setText("Modelo: " + current.getModel());
        myHolder.textColor.setText("Color: " + current.getColor());
        myHolder.editButton.setTag(position+"_"+current.getId());
        myHolder.deleteButton.setTag(position+"_"+current.getId());
    }

    // return total item from List
    /*@Override
    public int getItemCount() {
        return data.size();
    }*/
    @Override
    public int getItemCount() {
        if (data != null)
            return data.size();
        return 0;
    }


    class MyHolder extends RecyclerView.ViewHolder{

        TextView textPlate;
        TextView textBrand;
        TextView textModel;
        TextView textColor;
        ImageButton editButton;
        ImageButton deleteButton;

        // create constructor to get widget reference
        public MyHolder(View itemView) {
            super(itemView);
            textPlate= (TextView) itemView.findViewById(R.id.textPlate);
            textBrand = (TextView) itemView.findViewById(R.id.textBrand);
            textModel = (TextView) itemView.findViewById(R.id.textModel);
            textColor = (TextView) itemView.findViewById(R.id.textColor);
            editButton = (ImageButton) itemView.findViewById(R.id.button_edit_vehicle);
            deleteButton = (ImageButton) itemView.findViewById(R.id.button_delete_vehicle);
        }

    }

    public void addNewVehicleToList(VehicleDTO newVehicle) {
        this.data.add(newVehicle);
        notifyDataSetChanged();
    }
}

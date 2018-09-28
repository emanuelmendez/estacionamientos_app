package gbem.com.ar.estacionamientos;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import gbem.com.ar.estacionamientos.api.dtos.VehicleDTO;

public class AdapterVehicle extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private LayoutInflater inflater;
    List<VehicleDTO> data= Collections.emptyList();
    VehicleDTO current;
    int currentPos=0;

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

        // create constructor to get widget reference
        public MyHolder(View itemView) {
            super(itemView);
            textPlate= (TextView) itemView.findViewById(R.id.textPlate);
            textBrand = (TextView) itemView.findViewById(R.id.textBrand);
            textModel = (TextView) itemView.findViewById(R.id.textModel);
            textColor = (TextView) itemView.findViewById(R.id.textColor);
        }

    }

}

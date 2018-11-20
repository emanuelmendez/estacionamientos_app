package gbem.com.ar.estacionamientos.parkinglots;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import gbem.com.ar.estacionamientos.R;
import gbem.com.ar.estacionamientos.api.dtos.ParkingLotDTO;

public class AdapterAddParkingLot extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    List<ParkingLotDTO> data= Collections.emptyList();

    @BindView(R.id.btn_remove_lot)
    ImageButton removeLot;

    // create constructor to innitilize context and data sent from MainActivity
    public AdapterAddParkingLot(Context context, List<ParkingLotDTO> data){
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data=data;
    }

    // Inflate the layout when viewholder created
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.container_add_parking_lot, parent, false);
        MyHolder holder = new MyHolder(view);
        ButterKnife.bind(this,view);

        removeLotFromList();

        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        // Get current position of item in recyclerview to bind data and assign values from list
        MyHolder myHolder= (MyHolder) holder;
        ParkingLotDTO current = data.get(position);

        myHolder.textLotNumber.setText("Lote: " + String.valueOf(current.getLotNumber()));
        myHolder.textLotDescription.setText(current.getDescription());
        myHolder.textLotRate.setText("ARS " + current.getValue());
        myHolder.btnRemove.setTag(position);
    }

    class MyHolder extends RecyclerView.ViewHolder {

        TextView textLotNumber;
        TextView textLotDescription;
        TextView textLotRate;
        ImageButton btnRemove;

        // create constructor to get widget reference
        public MyHolder(View itemView) {
            super(itemView);
            textLotNumber = (TextView) itemView.findViewById(R.id.textLotNumber);
            textLotDescription = (TextView) itemView.findViewById(R.id.textLotDescription);
            textLotRate = (TextView) itemView.findViewById(R.id.textLotRate);
            btnRemove = (ImageButton) itemView.findViewById(R.id.btn_remove_lot);
        }
    }

    @Override
    public int getItemCount() {
        if (data != null)
            return data.size();
        return 0;
    }

    private void removeLotFromList(){

        removeLot.setOnClickListener(v -> {
            int p = Integer.parseInt(v.getTag().toString());
            data.remove(p);
            notifyDataSetChanged();
        });
    }
}
package gbem.com.ar.estacionamientos.parkinglots;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import gbem.com.ar.estacionamientos.R;
import gbem.com.ar.estacionamientos.api.dtos.ParkingLotDTO;

public class AdapterParkingLot extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    List<ParkingLotDTO> data= Collections.emptyList();

    // create constructor to innitilize context and data sent from MainActivity
    public AdapterParkingLot(Context context, List<ParkingLotDTO> data){
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data=data;
    }

    // Inflate the layout when viewholder created
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.container_parking_lot, parent, false);
        MyHolder holder = new MyHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        // Get current position of item in recyclerview to bind data and assign values from list
        AdapterParkingLot.MyHolder myHolder= (AdapterParkingLot.MyHolder) holder;
        ParkingLotDTO current = data.get(position);

        myHolder.textLotNumber.setText(String.valueOf(current.getLotNumber()));
        myHolder.textLotDescription.setText(current.getDescription());
        myHolder.textLotRate.setText("ARS " + current.getValue());

        myHolder.btnEditLot.setTag(position);
        myHolder.btnEditSchedule.setTag(position);
    }

    class MyHolder extends RecyclerView.ViewHolder {

        TextView textLotNumber;
        TextView textLotDescription;
        TextView textLotRate;
        Button btnEditLot;
        Button btnEditSchedule;
        CardView cv;

        // create constructor to get widget reference
        public MyHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            textLotNumber = (TextView) itemView.findViewById(R.id.txt_lot_number);
            textLotDescription = (TextView) itemView.findViewById(R.id.txt_lot_description);
            textLotRate = (TextView) itemView.findViewById(R.id.txt_lot_rate);
            btnEditLot = (Button) itemView.findViewById(R.id.btn_edit_lot);
            btnEditSchedule = (Button) itemView.findViewById(R.id.btn_schedule);
        }
    }

    @Override
    public int getItemCount() {
        if (data != null)
            return data.size();
        return 0;
    }
}

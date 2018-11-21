package gbem.com.ar.estacionamientos.parkinglots;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import gbem.com.ar.estacionamientos.R;
import gbem.com.ar.estacionamientos.api.dtos.ParkingLotDTO;
import gbem.com.ar.estacionamientos.dashboard.NavigationDrawerActivity;

public class AdapterParkingLot extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    List<ParkingLotDTO> data= Collections.emptyList();

    @BindView(R.id.btn_edit_lot)
    Button editLot;

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
        ButterKnife.bind(this,view);
        editParkingLot(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        // Get current position of item in recyclerview to bind data and assign values from list
        AdapterParkingLot.MyHolder myHolder= (AdapterParkingLot.MyHolder) holder;
        ParkingLotDTO current = data.get(position);
        String schedule = context.getString(R.string.no_schedule_set);

        if(current.getScheduleDTO() != null){
            String days = "";
            if(current.getScheduleDTO().isMonday()){ days += context.getString(R.string.monday)+", "; }
            if(current.getScheduleDTO().isTuesday()){ days += context.getString(R.string.tuesday)+", ";}
            if(current.getScheduleDTO().isWednesday()){ days += context.getString(R.string.wednesday)+", ";}
            if(current.getScheduleDTO().isThursday()){ days += context.getString(R.string.thursday)+", ";}
            if(current.getScheduleDTO().isFriday()){ days += context.getString(R.string.friday)+", ";}
            if(current.getScheduleDTO().isSaturday()){ days += context.getString(R.string.saturday)+", ";}
            if(current.getScheduleDTO().isSunday()){ days += context.getString(R.string.sunday)+", ";}
            days += "de "+current.getScheduleDTO().getFromHour()+":00 a "+current.getScheduleDTO().getToHour()+":00hs";
            schedule = days;
        }

        myHolder.textLotNumber.setText(String.valueOf(current.getLotNumber()));
        myHolder.textLotDescription.setText(current.getDescription());
        myHolder.textLotRate.setText("ARS " + current.getValue());
        myHolder.textLotSchedule.setText(schedule);

        myHolder.btnEditLot.setTag(position+"_"+current.getId());
    }

    class MyHolder extends RecyclerView.ViewHolder {

        TextView textLotNumber;
        TextView textLotDescription;
        TextView textLotRate;
        TextView textLotSchedule;
        Button btnEditLot;
        CardView cv;

        // create constructor to get widget reference
        public MyHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            textLotNumber = (TextView) itemView.findViewById(R.id.txt_lot_number);
            textLotDescription = (TextView) itemView.findViewById(R.id.txt_lot_description);
            textLotRate = (TextView) itemView.findViewById(R.id.txt_lot_rate);
            textLotSchedule = (TextView) itemView.findViewById(R.id.txt_lot_schedule);
            btnEditLot = (Button) itemView.findViewById(R.id.btn_edit_lot);
        }
    }

    @Override
    public int getItemCount() {
        if (data != null)
            return data.size();
        return 0;
    }

    public void editParkingLot(View view){

        editLot.setOnClickListener(v -> {
            //Formulario de edición de vehículo
            String currentLotNumber = ((TextView) view.findViewById(R.id.txt_lot_number)).getText().toString();
            String currentRate = ((TextView) view.findViewById(R.id.txt_lot_rate)).getText().toString();
            String currentDescription = ((TextView) view.findViewById(R.id.txt_lot_description)).getText().toString();
            String currentSchedule = ((TextView) view.findViewById(R.id.txt_lot_schedule)).getText().toString();

            Fragment fragment = new EditParkingLotFragment();

            Bundle bundle = new Bundle();
            bundle.putString("LOTNUMBER_TO_SET",currentLotNumber);
            bundle.putString("RATE_TO_SET",currentRate);
            bundle.putString("DESCRIPTION_TO_SET",currentDescription);
            bundle.putString("SCHEDULE_TO_SET",currentSchedule);
            bundle.putLong("PARKING_ID",Long.parseLong((v.getTag().toString()).split("_")[1]));
            fragment.setArguments(bundle);

            Log.i("EDIT", v.getTag().toString());
            Log.i("EDIT", currentSchedule);
            ((NavigationDrawerActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.screen_area, fragment).commit();
        });
    }
}

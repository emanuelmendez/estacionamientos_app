package gbem.com.ar.estacionamientos.history;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

import gbem.com.ar.estacionamientos.R;
import gbem.com.ar.estacionamientos.dashboard.ReservationDTO;

public class AdapterReservationHistory extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    @SuppressLint("SimpleDateFormat")
    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM HH:mm");

    private List<ReservationDTO> reservations;
    private OnReviewClickListener listener;


    AdapterReservationHistory(List<ReservationDTO> data, OnReviewClickListener listener) {
        this.reservations = data;
        this.listener = listener;
    }

    // Inflate the layout when viewholder created
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.container_reservation_history, parent, false);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        AdapterReservationHistory.MyHolder myholder = (AdapterReservationHistory.MyHolder) holder;
        ReservationDTO item = reservations.get(position);
        myholder.textLender.setText(item.getLenderName());
        myholder.textLotDesc.setText(item.getParkingLot().getDescription());

        String desde = "Inicio: " + sdf.format(item.getFrom());
        String hasta = "Fin: " + sdf.format(item.getTo());
        myholder.textFrom.setText(desde);
        myholder.textTo.setText(hasta);
        myholder.btnReview.setTag(item.getId());
        myholder.btnReview.setOnClickListener(v -> {
            listener.onReviewButtonClicked(item);
        });

    }

    @Override
    public int getItemCount() {
        if (reservations != null)
            return reservations.size();
        return 0;
    }

    class MyHolder extends RecyclerView.ViewHolder {

        TextView textLender;
        TextView textLotDesc;
        TextView textFrom;
        TextView textTo;
        Button btnReview;
        CardView cv;

        // create constructor to get widget reference
        MyHolder(View itemView) {
            super(itemView);
            cv = itemView.findViewById(R.id.cv);
            textLender = itemView.findViewById(R.id.txt_lender);
            textLotDesc = itemView.findViewById(R.id.txt_parkinglot);
            textFrom = itemView.findViewById(R.id.txt_from);
            textTo = itemView.findViewById(R.id.txt_to);
            btnReview = itemView.findViewById(R.id.btn_review);
        }
    }
}

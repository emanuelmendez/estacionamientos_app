package gbem.com.ar.estacionamientos.history;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import gbem.com.ar.estacionamientos.R;
import gbem.com.ar.estacionamientos.dashboard.ReservationDTO;

public class AdapterReservationHistory extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    @SuppressLint("SimpleDateFormat")
    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM HH:mm");
    private Context context;
    private LayoutInflater inflater;
    private List<ReservationDTO> reservations;
    private Activity activity;

    @BindView(R.id.btn_review)
    Button btnReview;

    public AdapterReservationHistory(Context context, List<ReservationDTO> data, Activity activity){
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.reservations=data;
        this.activity = activity;
    }

    // Inflate the layout when viewholder created
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.container_reservation_history, parent, false);
        MyHolder holder = new MyHolder(view);
        ButterKnife.bind(this,view);

        btnReview.setOnClickListener(v -> {
            //TODO calificar
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        AdapterReservationHistory.MyHolder myholder= (AdapterReservationHistory.MyHolder) holder;
        ReservationDTO item = reservations.get(position);
        myholder.textLender.setText(item.getLenderName());
        myholder.textLotDesc.setText(item.getParkingLot().getDescription());

        String desde = "Inicio: " + sdf.format(item.getFrom());
        String hasta = "Fin: " + sdf.format(item.getTo());
        myholder.textFrom.setText(desde);
        myholder.textTo.setText(hasta);
        myholder.btnReview.setTag(item.getId());

    }

    class MyHolder extends RecyclerView.ViewHolder {

        TextView textLender;
        TextView textLotDesc;
        TextView textFrom;
        TextView textTo;
        Button btnReview;
        CardView cv;

        // create constructor to get widget reference
        public MyHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            textLender = (TextView) itemView.findViewById(R.id.txt_lender);
            textLotDesc = (TextView) itemView.findViewById(R.id.txt_parkinglot);
            textFrom = (TextView) itemView.findViewById(R.id.txt_from);
            textTo = (TextView) itemView.findViewById(R.id.txt_to);
            btnReview = (Button) itemView.findViewById(R.id.btn_review);
        }
    }

    @Override
    public int getItemCount() {
        if (reservations != null)
            return reservations.size();
        return 0;
    }
}

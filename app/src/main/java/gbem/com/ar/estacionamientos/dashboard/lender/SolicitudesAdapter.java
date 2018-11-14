package gbem.com.ar.estacionamientos.dashboard.lender;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
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

/**
 * @author pielreloj
 * Created: 04/11/18.
 */
public class SolicitudesAdapter extends RecyclerView.Adapter<SolicitudesAdapter.ViewHolder> {

    @SuppressLint("SimpleDateFormat")
    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM HH:mm");
    private List<ReservationDTO> solicitudes;
    private SolicitudListener listener;

    public SolicitudesAdapter(List<ReservationDTO> solicitudes, SolicitudListener listener) {
        this.solicitudes = solicitudes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.lender_requests_rv_item, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ReservationDTO item = solicitudes.get(position);
        holder.txtDriver.setText(String.format("%s - %s", item.getDriverName(), item.getVehicleDescription()));
        holder.txtParkingLot.setText(item.getParkingLot().getDescription());

        String desde = "Inicio: " + sdf.format(item.getFrom());
        String hasta = "Fin: " + sdf.format(item.getTo());
        holder.txtDesde.setText(desde);
        holder.txtHasta.setText(hasta);
        holder.txtStatus.setText(R.string.pending_status);
    }

    @Override
    public int getItemCount() {
        return solicitudes.size();
    }

    public void setData(List<ReservationDTO> solicitudes) {
        this.solicitudes = solicitudes;
        notifyDataSetChanged();
    }

    private void showButtons(View item) {
        final View btnConfirmar = item.findViewById(R.id.btnConfirmar);
        final View btnCancelar = item.findViewById(R.id.btnCancelar);

        if (btnCancelar.getVisibility() == View.GONE) {
            btnCancelar.setVisibility(View.VISIBLE);
            btnConfirmar.setVisibility(View.VISIBLE);
        } else {
            btnConfirmar.setVisibility(View.GONE);
            btnCancelar.setVisibility(View.GONE);
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView txtDriver, txtDesde, txtHasta, txtParkingLot, txtStatus;
        Button btnConfirmar, btnCancelar;

        ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            btnConfirmar = itemView.findViewById(R.id.btnConfirmar);
            btnConfirmar.setOnClickListener(this);

            btnCancelar = itemView.findViewById(R.id.btnCancelar);
            btnCancelar.setOnClickListener(this);

            txtDesde = itemView.findViewById(R.id.txtDesde);
            txtHasta = itemView.findViewById(R.id.txtHasta);
            txtDriver = itemView.findViewById(R.id.txtDriver);
            txtParkingLot = itemView.findViewById(R.id.txtParkingLot);
            txtStatus = itemView.findViewById(R.id.txtStatus);
        }

        @Override
        public void onClick(View v) {
            ReservationDTO r = solicitudes.get(this.getAdapterPosition());
            if (v.getId() == btnConfirmar.getId()) {
                listener.onConfirmar(r);
                showButtons(v.getRootView());
            } else if (v.getId() == btnCancelar.getId()) {
                listener.onRechazar(r);
                showButtons(v.getRootView());
            } else {
                showButtons(v);
            }
        }
    }

}

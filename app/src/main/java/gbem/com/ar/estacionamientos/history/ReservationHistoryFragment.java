package gbem.com.ar.estacionamientos.history;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import gbem.com.ar.estacionamientos.R;
import gbem.com.ar.estacionamientos.dashboard.ReservationDTO;
import gbem.com.ar.estacionamientos.utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static gbem.com.ar.estacionamientos.utils.Utils.getIdToken;

public class ReservationHistoryFragment extends Fragment {

    IReservationHistoryService iReservationHistoryService;
    private RecyclerView mRVAddLotsList;
    private AdapterReservationHistory mAdapter;
    private List<ReservationDTO> reservations;

    @BindView(R.id.cv_no_reservations)
    CardView cvNoReservations;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reservation_history, container, false);
        ButterKnife.bind(this,view);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getParkingLotJson();
    }

    public void getParkingLotJson(){

        if (iReservationHistoryService == null) {
            iReservationHistoryService = Utils.getService(IReservationHistoryService.class);
        }

        Call<List<ReservationDTO>> call = iReservationHistoryService.getReservationsHistoryByDriver(getIdToken(this.getActivity()));

        call.enqueue(new Callback<List<ReservationDTO>>() {
            @Override
            public void onResponse(Call<List<ReservationDTO>> call, Response<List<ReservationDTO>> response) {
                switch (response.code()) {
                    case 204:
                    case 200:
                        reservations = response.body();

                        if (reservations == null) {
                            cvNoReservations.setVisibility(VISIBLE);
                        } else {
                            cvNoReservations.setVisibility(GONE);
                        }
                        generateDataList(response.body());
                        break;
                    case 401:
                        Log.i("TAG","ENTRO POR 401");
                        break;
                    default:
                        Log.i("TAG","ENTRO POR DEFAULT "+response.code());
                        break;
                }
            }

            @Override
            public void onFailure(Call<List<ReservationDTO>> call, Throwable t) {
                Toast.makeText(getActivity(), "Error al cargar el historial de reservas", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void generateDataList(List<ReservationDTO> result) {

        try {
            mRVAddLotsList = getView().findViewById(R.id.reservationRecordList);
            mAdapter = new AdapterReservationHistory(getContext(), result, getActivity());
            mRVAddLotsList.setAdapter(mAdapter);
            mRVAddLotsList.setLayoutManager(new LinearLayoutManager(getContext()));

        } catch (Exception e) {
            Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
        }
    }

}

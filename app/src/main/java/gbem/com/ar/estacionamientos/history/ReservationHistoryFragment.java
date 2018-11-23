package gbem.com.ar.estacionamientos.history;


import android.os.Bundle;
import android.support.annotation.NonNull;
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
import butterknife.Unbinder;
import gbem.com.ar.estacionamientos.R;
import gbem.com.ar.estacionamientos.dashboard.ReservationDTO;
import gbem.com.ar.estacionamientos.utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static gbem.com.ar.estacionamientos.utils.Utils.getIdToken;

public class ReservationHistoryFragment extends Fragment implements OnReviewClickListener {

    IReservationHistoryService iReservationHistoryService;
    @BindView(R.id.cv_no_reservations)
    CardView cvNoReservations;
    private RecyclerView mRVAddLotsList;
    private AdapterReservationHistory mAdapter;
    private List<ReservationDTO> reservations;
    private Unbinder unbinder;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reservation_history, container, false);
        unbinder = ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getParkingLotJson();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public void getParkingLotJson() {

        if (iReservationHistoryService == null) {
            iReservationHistoryService = Utils.getService(IReservationHistoryService.class);
        }

        Call<List<ReservationDTO>> call = iReservationHistoryService.getReservationsHistoryByDriver(getIdToken(getActivity()));

        call.enqueue(new Callback<List<ReservationDTO>>() {
            @Override
            public void onResponse(@NonNull Call<List<ReservationDTO>> call, @NonNull Response<List<ReservationDTO>> response) {
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
                        Log.i("TAG", "ENTRO POR 401");
                        break;
                    default:
                        Log.i("TAG", "ENTRO POR DEFAULT " + response.code());
                        break;
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ReservationDTO>> call, @NonNull Throwable t) {
                Toast.makeText(getActivity(), "Error al cargar el historial de reservas", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void generateDataList(List<ReservationDTO> result) {

        try {
            mRVAddLotsList = getView().findViewById(R.id.reservationRecordList);
            mAdapter = new AdapterReservationHistory(result, this);
            mRVAddLotsList.setAdapter(mAdapter);
            mRVAddLotsList.setLayoutManager(new LinearLayoutManager(getContext()));

        } catch (Exception e) {
            Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onReviewButtonClicked(ReservationDTO item) {

    }
}

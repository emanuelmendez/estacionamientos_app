package gbem.com.ar.estacionamientos.reputation;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import gbem.com.ar.estacionamientos.R;
import gbem.com.ar.estacionamientos.utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReputationFragment extends Fragment {

    @BindView(R.id.txtScore)
    TextView txtScore;
    @BindView(R.id.frameLayout2)
    ConstraintLayout layout;
    private Unbinder unbinder;
    private ReputationService reputationService;

    public ReputationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_reputation, container, false);
        unbinder = ButterKnife.bind(this, view);

        if (reputationService == null) {
            reputationService = Utils.getService(ReputationService.class);
        }

        reputationService.getAverageScore(Utils.getIdToken(Objects.requireNonNull(getActivity()))).enqueue(new Callback<Float>() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onResponse(@NonNull Call<Float> call, @NonNull Response<Float> response) {
                if (response.code() == 200) {
                    txtScore.setText(String.format("%.2f", response.body()));
                } else if (response.code() == 204) {
                    txtScore.setText(R.string.no_data);
                } else {
                    Snackbar.make(view, "No se pudo obtener el puntaje", Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Float> call, @NonNull Throwable t) {
                Snackbar.make(view, "No se pudo obtener el puntaje", Snackbar.LENGTH_LONG).show();
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}

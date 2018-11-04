package gbem.com.ar.estacionamientos.dashboard.lender;

import gbem.com.ar.estacionamientos.dashboard.ReservationDTO;

/**
 * @author pielreloj
 * Created: 04/11/18.
 */
public interface SolicitudListener {

    void onConfirmar(ReservationDTO reservation);

    void onCancelar(ReservationDTO reservation);

}

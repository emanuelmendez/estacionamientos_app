package gbem.com.ar.estacionamientos.signin;

import gbem.com.ar.estacionamientos.api.dtos.UserDataDTO;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * @author pielreloj
 * Created: 08/10/18.
 */
public interface ISessionService {

    /**
     * Solicita datos del usuario a través del token de Google.
     * Una vez que se ejecuta el Call, el token es verificado y se obtiene el subject del payload.
     * Si este subject existe en la base se obtiene un status code 200
     * y en el body los datos básicos del usuario,
     * de lo contrario se puede obtener:
     * - 400 si el token no es válido para ser verificado
     * - 401 si el token no fue enviado (null o "") o si no pasó la verificación de Google
     * - 403 si el token es válido pero el usuario no está registrado en la aplicación
     * - 500 si hay un error interno del servidor al procesar esta solicitud
     *
     * @param token idToken obtenido por el GoogleSignInClient
     * @return Un Call listo para ser ejecutado asincrónicamente
     */
    @GET("session/signin")
    Call<UserDataDTO> getUserData(@Header("api_token") String token);

    /**
     * Realiza la registración con los datos del usuario.
     * Si el status code es 200 o 201 devuelve los datos del usuario
     *
     * @param token idToken obtenido idToken obtenido por el GoogleSignInClient
     * @param data  información del usuario para hacer el registro
     * @return un Call listo para ser ejecutado asincrónicamente
     */
    @POST("session/signup")
    Call<UserDataDTO> signUp(@Header("api_token") String token, @Body UserDataDTO data);

    @POST("session/device")
    Call<Void> updateDeviceToken(@Header("api_token") String token, @Body String deviceToken);



}

package com.example.geo.Views.Inicio;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.geo.APIs.GeoAPIService;
import com.example.geo.MainActivity;
import com.example.geo.Models.Usuario;
import com.example.geo.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    private Retrofit retrofit;
    private Button btn_iniciarSesion, btn_registrase;
    private ImageView btn_show_pass;
    private EditText et_mail, et_password;
    private TextView tv_mensajeError;
    public static final String user = "usuario";
    private String auxRol;
    public static final String userpassword = "password";
    public static final String userRol = "rol";


    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences("shared_login_data", Context.MODE_PRIVATE);
        boolean sesionActiva = isSesionActiva();

        //Condicion que verifica si hay una sesion activa que te mandara al main si hay datos en SharedPreference
        if (sesionActiva) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        setContentView(R.layout.activity_login);
        btn_iniciarSesion = findViewById(R.id.btnLoginIniciarSesion);
        btn_registrase = findViewById(R.id.btnLoginRegistrarse);
        btn_show_pass = findViewById(R.id.btnLoginShowPass);
        et_mail = findViewById(R.id.editLoginMail);
        et_password = findViewById(R.id.editLoginPassword);

        tv_mensajeError = findViewById(R.id.tvLoginErrorMessage);


        retrofit = new Retrofit.Builder()
                .baseUrl("https://segu10-001-site1.gtempurl.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    //Verifica si hay sesion Activa en nuestra app
    private boolean isSesionActiva() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared_login_data", Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("sesionActiva", false);
    }

    // Botón autenticar usuario registrado en base de datos
    public void IniciarSesion(View view) {
        if (this.validarCamposVacios() == true) {
            GeoAPIService servicioGeo = retrofit.create(GeoAPIService.class);
            Call<Usuario> usuarioCall = servicioGeo.validarUsuario("" + et_mail.getText().toString(), "" + et_password.getText().toString());
            usuarioCall.enqueue(new Callback<Usuario>() {
                @Override
                public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                    if (response.code() == 200) {
                        auxRol = String.valueOf(response.body().getRol());
                        acceder();
                    } else {
                        tv_mensajeError.setTextColor(getColor(android.R.color.holo_red_dark));
                        tv_mensajeError.setText("Usuario o contraseña incorrectos");
                    }
                }

                @Override
                public void onFailure(Call<Usuario> call, Throwable t) {
                    Log.e("Error: ", " onFailure: " + t.getMessage());
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }


    // Método para abrir la activity de página principal
    public void acceder() {
        obtenerCredenciales();
        Intent intento = new Intent(this, MainActivity.class);
        startActivity(intento);

    }


    //Estos métodos nos permiten guardar los valores ingresados en password y user si se pulsa el botón home accidentalmente
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        String user, password;
        user = et_mail.getText().toString();
        password = et_password.getText().toString();
        outState.putString("usuario", user);
        outState.putString("password", password);
        outState.putString("rol", auxRol);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String user, password;
        user = et_mail.getText().toString();
        password = et_password.getText().toString();
        et_mail.setText(user);
        et_password.setText(password);
    }
    //--------------------------------------------------------------------------------------------------------------------------

    //Guardaremos datos de sesión;
    void obtenerCredenciales() {

        //Mediante este bloque de código vamos a guardar los datos de usuario
        String usuario = et_mail.getText().toString();
        String password = et_password.getText().toString();
        sharedPreferences.edit()
                .putString(user, usuario)
                .putString(userpassword, password)
                .putString(userRol, auxRol)
                .putBoolean("sesionActiva",true)
                .apply();
    }

    // Método para abrir la activity para registrar un nuevo usuario
    public void RegistrarNuevoUsuario(View view) {
        Intent intento = new Intent(this, CreateAccountActivity.class);
        startActivity(intento);
    }

    // función para validar los campos vacios
    private boolean validarCamposVacios() {
        boolean camposLlenos = true;
        if (et_mail.getText().toString().trim().equalsIgnoreCase("")) {
            et_mail.setError("Ingresa tu correo");
            camposLlenos = false;
        }
        if (et_password.getText().toString().trim().equalsIgnoreCase("")) {
            et_password.setError("Ingresa tu contraseña");
            camposLlenos = false;
        }
        return camposLlenos;

    }


    // Método para mostrar y ocultar la contraseña
    public void ShowHidePass(View view) {
        if (view.getId() == R.id.btnLoginShowPass) {
            if (et_password.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())) {
                ((ImageView) (view)).setImageResource(R.drawable.ic_off_visibility_icon);
                //Show Password
                et_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                ((ImageView) (view)).setImageResource(R.drawable.ic_visibility_icon);
                //Hide Password
                et_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        }
    }


}
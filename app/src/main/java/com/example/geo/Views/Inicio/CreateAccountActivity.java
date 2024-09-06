package com.example.geo.Views.Inicio;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

public class CreateAccountActivity extends AppCompatActivity {
    
    private EditText et_nombre, et_apellidos, et_mail, et_password, et_confirmar_password;
    private RadioButton rb_masculino, rb_femenino;
    private TextView tv_mensajeError;
    //private Button btn_Continuar;
    private Retrofit retrofit;
    private Usuario nuevoUsuario;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        et_nombre = findViewById(R.id.editRegistroUsuarioNombre);
        et_apellidos = findViewById(R.id.editRegistroUsuarioApellidos);
        et_mail = findViewById(R.id.editRegistroUsuarioMail);
        et_password = findViewById(R.id.editRegistroUsuarioPassword);
        et_confirmar_password = findViewById(R.id.editRegistroUsuarioConfirmarPassword);

        tv_mensajeError = findViewById(R.id.tvRegistrarUsuarioErrorMensaje);

        rb_masculino = findViewById(R.id.rbRegistroUsuarioMasculino);
        rb_femenino = findViewById(R.id.rbRegistroUsuarioFemenino);


        retrofit = new Retrofit.Builder()
                .baseUrl("https://segu10-001-site1.gtempurl.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

    }


    // Botón registrar usuario en la base de datos
    public void RegistrarUsuarionuevo(View view) {
        if(this.validarCamposVacios() == true) {
            ObtenerDatosDelFormulario();
            GeoAPIService servicioGeo = retrofit.create(GeoAPIService.class);
            Call<Boolean> registrarUsuarioCall = servicioGeo.RegistrarUsuario(nuevoUsuario);
            registrarUsuarioCall.enqueue(new Callback<Boolean>() {
                @Override
                public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                    if(response.code()==200) {
                        tv_mensajeError.setTextColor(getColor(android.R.color.holo_green_dark));
                        tv_mensajeError.setText("¡Registrado con exito!"+response.code());
                        acceder();
                        finish();
                    }
                    else {
                        tv_mensajeError.setTextColor(getColor(android.R.color.holo_red_dark));
                        tv_mensajeError.setText("Ocurrió un error. Intentelo nuevamente :(");
                    }
                }

                @Override
                public void onFailure(Call<Boolean> call, Throwable t) {
                    tv_mensajeError.setText(t.getMessage());
                    Log.e("Error: ", " onFailure: " + t.getMessage());
                }
            });
        }
    }

    //
    private void ObtenerDatosDelFormulario() {
        char auxSexo = ' ';
        if(rb_masculino.isChecked()) {
            auxSexo = 'M';
        }
        if(rb_femenino.isChecked()) {
            auxSexo = 'F';
        }
        nuevoUsuario = new Usuario(
                "",
                "" + et_nombre.getText().toString() + " " + et_apellidos.getText().toString(),
                "" + et_mail.getText().toString(),
                ""+auxSexo,
                ""+ et_password.getText().toString(),
                2
        );
        /*nuevoUsuario = new Usuario(
                "",
                "user01" ,
                "user@gmail.com",
                "F",
                "12345",
                2
        );
         */
    }

    public void acceder() {
        Intent intento = new Intent(this, LoginActivity.class);
        startActivity(intento);
    }

    private boolean validarCamposVacios() {
        boolean camposLlenos = true;
        if (et_nombre.getText().toString().trim().equalsIgnoreCase("")) {
            et_nombre.setError("Ingresa tu nombre(s)");
            camposLlenos = false;
        }
        if (et_apellidos.getText().toString().trim().equalsIgnoreCase("")) {
            et_apellidos.setError("Ingresa tus apellidos");
            camposLlenos = false;
        }
        if(rb_masculino.isChecked() == false && rb_femenino.isChecked() == false) {
            rb_femenino.setError("Debe seleccionar una opción");
            camposLlenos = false;
        }
        if(et_mail.getText().toString().trim().equalsIgnoreCase("" )){
            et_mail.setError("Ingresa tu correo");
            camposLlenos = false;
        }
        if (et_password.getText().toString().trim().equalsIgnoreCase("")) {
            et_password.setError("Ingresa tu contraseña");
            camposLlenos = false;
        }
        if (et_confirmar_password.getText().toString().trim().equalsIgnoreCase( ""+et_password.getText().toString().trim()) == false) {
            et_confirmar_password.setError("Las contraseñas no coinciden");
            camposLlenos = false;
        }
        if (et_confirmar_password.getText().toString().trim().equalsIgnoreCase("")) {
            et_confirmar_password.setError("Repite la contraseña");
            camposLlenos = false;
        }
        return camposLlenos;
    }


}
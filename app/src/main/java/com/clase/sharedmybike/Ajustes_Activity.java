package com.clase.sharedmybike;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.w3c.dom.Text;

/**
 * @author Manuel
 * @version 1.0*/

public class Ajustes_Activity extends AppCompatActivity {
    Button volver=null; // Botón para volver a la pantalla principal
    Button elegir=null; // Botón para confirmar el cambio de estilo
    RadioButton claro=null; // RadioButton de la opción de estilo claro
    RadioButton oscuro=null; // RadioButton de la opción de estilo oscuro
    TextView titulo=null; // Textview del titulo de la actividad
    TextView descrip=null; // Texview de la descripción de la actividad
    TextView orden=null; // Textview de la orden de la actividad
    ConstraintLayout main=null; // Constraint de la actividad
    SharedPreferences sharedPreferences=null; // Variable para poder guardar y cargar las preferencias del usuario
    String estilo=null; // Variable auxiliar para detectar y aplicar el estilo elegido

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ajustes);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Cargamos los sharedPreferences
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        // Le establecemos a la variable auxiliar el valor que tengamos guardado, en caso de no tener ninguno le ponemos Claro
        estilo = sharedPreferences.getString("estiloApp", "Claro");

        // Obtenemos todos los componentes de la actividad por si se cambia el estilo
        claro = (RadioButton) findViewById(R.id.rbBlanco);
        oscuro = (RadioButton) findViewById(R.id.rbNegro);
        main = (ConstraintLayout) findViewById(R.id.main);
        titulo = (TextView) findViewById(R.id.txtTituA);
        descrip = (TextView) findViewById(R.id.txtExpli);
        orden = (TextView) findViewById(R.id.txtTitu2A);

        // Obtenemos el botón de volver a la MainActivity
        volver = (Button) findViewById(R.id.btnVolve);
        // Le otorgamos un evento para que cuando se pulse realice una acción
        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Indicamos la actividad actual y a la que vamos a pasar
                Intent i = new Intent(Ajustes_Activity.this, MainActivity.class);
                startActivity(i); // Iniciamos la nueva actividad
                finish(); // Finalizamos está actividad
                // Establecemos una animación al pasar a la nueva actividad (deslizar hacia la derecha y hacia la izquierda)
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            }
        });

        // Comprobamos el estilo para elegir cuando se cree la actividad un RadioButton u otro
        if(estilo.equals("Claro")){
            claro.setChecked(true);
        }else if(estilo.equals("Oscuro")){
            oscuro.setChecked(true);
        }

        // Obtenemos el botón para elegir el estilo que queremos
        elegir = (Button) findViewById(R.id.btnElegirEstilo);
        // Le asignamos un evento para que cuando sea pulsado realice una acción
        elegir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(claro.isChecked()){ // En caso de que el rb de claro este seleccionado
                    // Establecemos la variable auxiliar como claro
                    estilo = "Claro";
                    // Obtenemos el editor de las sharedPreferences
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    // Establecemos el estilo
                    editor.putString("estiloApp", estilo);
                    // Y guardamos los cambios
                    editor.apply();
                    // Llamamos al método para que repinte la activida y cambie los colores y si es necesario
                    establecerEstilo();
                }else if(oscuro.isChecked()){ // En caso de que el rb de oscuro este seleccionado
                    // Establecemos la variable auxiliar como oscuro
                    estilo = "Oscuro";
                    // Obtenemos el editor de las sharedPreferences
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    // Establecemos el estilo
                    editor.putString("estiloApp", estilo);
                    // Y guardamos los cambios
                    editor.apply();
                    // Llamamos al método para que repinte la activida y cambie los colores y si es necesario
                    establecerEstilo();
                }else{ // En caso de que ninguno este seleccionado y se pulse el botón
                    // Mandamos un Toast para avisar el usuario que tiene que elegir un estilo
                    Toast.makeText(Ajustes_Activity.this, "Eliga una opción", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Es importante que en el onCreate llamemos a este método una vez ya declarados todos los elementos
        // de la pantalla, por que sino, puede producirse un error y cerrarse la app
        establecerEstilo();
    }

    /**
     * Método que nos permite establecer el estilo de la pantalla, en este caso
     * entre dos estilos posibles, o claro o oscuro*/
    public void establecerEstilo(){
        if(estilo.equals("Claro")){ // En caso de que el estilo seá claro
            main.setBackgroundColor(Color.WHITE); // Establecemos el fondo en blanco
            titulo.setTextColor(Color.BLACK); // Ponemos los textview y radiobutton en negro
            descrip.setTextColor(Color.BLACK); // Ponemos los textview y radiobutton en negro
            orden.setTextColor(Color.BLACK); // Ponemos los textview y radiobutton en negro
            claro.setTextColor(Color.BLACK); // Ponemos los textview y radiobutton en negro
            oscuro.setTextColor(Color.BLACK); // Ponemos los textview y radiobutton en negro
        }else if(estilo.equals("Oscuro")){ // En caso de que el estilo seá oscuro
            main.setBackgroundColor(Color.BLACK); // Establecemos el fondo en oscuro
            titulo.setTextColor(Color.WHITE); // Ponemos los textview y radiobutton en blanco
            descrip.setTextColor(Color.WHITE); // Ponemos los textview y radiobutton en blanco
            orden.setTextColor(Color.WHITE); // Ponemos los textview y radiobutton en blanco
            claro.setTextColor(Color.WHITE); // Ponemos los textview y radiobutton en blanco
            oscuro.setTextColor(Color.WHITE); // Ponemos los textview y radiobutton en blanco
        }
    }
}
package com.clase.sharedmybike;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import com.clase.sharedmybike.bikes.BikesContent;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.clase.sharedmybike.databinding.ActivityBikeBinding;

import org.w3c.dom.Text;

import java.util.List;

/**
 * @author Manuel
 * @version 1.0*/

public class BikeActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityBikeBinding binding;
    SharedPreferences sharedPreferences=null; // Variable para poder guardar y cargar las preferencias del usuario
    String estilo = ""; // Variable donde obtendremos el estilo de la aplicacion
    CoordinatorLayout main = null; // Variable del fondo de la actividad para poder cambiar el color según el estilo

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityBikeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Establecemos el toolbar como actionBar
        setSupportActionBar(binding.toolbar);

        // Obtenemos el componente visual que es nuestro fondo de la actividad
        main = (CoordinatorLayout) findViewById(R.id.coordinator_layout);

        // Procedemos a cargar el primer fragmento
        // Declaramos el NavController haciendo referencia a nuestro contenido de fragmentos
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_bike);
        navController.setGraph(R.navigation.nav_graph); // Establecemos el grafico de navegación que tenemos creado
        NavigationUI.setupActionBarWithNavController(this, navController); // Establecemos el titulo del fragmento en el toolbar

        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();

        // Llamamos al método en donde leemos y cargamos todas las bicicletas del JSON
        BikesContent.loadBikesFromJSON(this);

        // Cargamos los sharedPreferences
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        // Guardamos en la avriable global el estilo elegido, si no hay ninguno guardo, establecemos el claro
        estilo = sharedPreferences.getString("estiloApp", "Claro");

        // Llamamos al método para establecer el estilo, una vez ya definidos e inicializados todos los componenetes
        establecerEstilo();
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_bike);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    /**
     * Método para establecer el estilo de la app basandonos en las sharedPreferences*/
    public void establecerEstilo(){
        if(estilo.equals("Claro")){ // En caso de que el estilo seá claro
            main.setBackground(new ColorDrawable(Color.WHITE)); // Ponemos el fondo de color blanco
        }else if(estilo.equals("Oscuro")){ // En caso de que el estilo seá oscuro
            main.setBackground(new ColorDrawable(Color.BLACK)); // Ponemos el fondo de color negro
        }
    }
}
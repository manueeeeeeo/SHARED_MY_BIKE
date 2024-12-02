package com.clase.sharedmybike;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.clase.sharedmybike.bikes.BikesContent;

/**
 * @author Manuel
 * @version 1.0*/

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ItemFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ItemFragment extends Fragment {

    // Variable que pertenece a la clave para obtener la bicicleta elegida
    private static final String ARG_BIKE = "selected_bike";

    private BikesContent.Bike bike; // Variable para poder obtener los atributos de las bicis
    SharedPreferences sharedPreferences=null; // Variable para poder guardar y cargar las preferencias del usuario
    String estilo=null; // Variable donde cargamos el estilo desde los SharedPreferences
    ConstraintLayout main=null; // Variable para obtener el fondo
    ImageView imageView=null; // Variable para obtener la imagen de la bici
    TextView textoDueno=null; // Variable para obtener el texto donde pondremos el dueño de la bici
    TextView textoDescrip=null; // Variable para obtener el texto donde pondremos la descripción
    TextView textoCiudad=null; // Variable para obtener la ciudad de la bici
    TextView textoUbicacion=null; // Variable para obtener la ubicación exacata de la bici
    Button volver=null; // Variable para obtener el botón para volver

    public ItemFragment() {
    }

    /**
     * Método de fábrica para crear una nueva instancia del fragmento
     * @param bike Objeto Bike que será mostrado en el fragmento.
     * @return Una nueva instancia de ItemFragment.
     */
    public static ItemFragment newInstance(BikesContent.Bike bike) {
        ItemFragment fragment = new ItemFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_BIKE, bike); // Guardamos el objeto Bike en los argumentos
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Cuando se crea el fragmento
        if (getArguments() != null) { // En caso de que obtengamos algo
            // Recuperamos en una variable de tipo Bike el objeto que Parceamos anteriormente
            bike = getArguments().getParcelable(ARG_BIKE);
        }else{ // En caso de que no obtengamos ningún argumento
            // Lanzamos un Toast informando del error
            Toast.makeText(getContext(), "No hemos encontrado ningún argumento para cargar", Toast.LENGTH_SHORT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflamos el layout del fragmento
        View view = inflater.inflate(R.layout.fragment_item, container, false);

        // Recupero el objeto de la imagen
        imageView = view.findViewById(R.id.imagenDatalle);
        // Recupero el objeto del texto del dueño
        textoDueno = view.findViewById(R.id.duenoDetalle);
        // Recupero el objeto del texto de la descripción
        textoDescrip = view.findViewById(R.id.descDetalle);
        // Recupero el objeto del texto de la ciudad
        textoCiudad = view.findViewById(R.id.ciudadDetalle);
        // Recupero el objeto para el texto de la localización de la bici
        textoUbicacion = view.findViewById(R.id.locaDetalle);
        // Recupero el objeto del botón para volver
        volver = view.findViewById(R.id.btnVolverLista);

        // Verificamos  que tengamos información en el objeto de bici que hemos pasado
        if (bike != null) { // En caso de que este lleno
            // Comprobamos si pasamos foto
            if (bike.photo != null) { // En caso de que tengamos foto
                imageView.setImageBitmap(bike.photo); // Cargamos la foto y se la establecemos a la imagen que obtuvimos antes
            } else { // En caso de no tener foto
                imageView.setImageResource(R.drawable.bici_prede); // Establezco una imagen por defecto para que no salga ningún error
            }

            // Establezco el Textview con la información del objeto acerca del dueño
            textoDueno.setText(bike.owner);
            // Establezco el Textview con la información del objeto acerca de la descripción
            textoDescrip.setText(bike.description);
            // Establezco el Textview con la información del objeto acerca de la ciudad
            textoCiudad.setText(bike.city);
            // Establezco el Textview con la información del objeto acerca de la localización
            textoUbicacion.setText(bike.location);
        }

        // Le otorgamos al botón para volver un evento que ejecute cuando seá clicado
        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Uso FragmentManager para volver al fragmento anterior
                //getParentFragmentManager().popBackStack();
                NavController navController = Navigation.findNavController(view);
                navController.navigate(R.id.action_ThirdFragment_to_SecondFragment);
                NavigationUI.setupActionBarWithNavController((AppCompatActivity) getActivity(), navController);
            }
        });

        // Obtenemos las SharedPreferences del usuario para poder guardar las configuraciones del mismo
        sharedPreferences = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        // Obtenemos el estilo que tiene la app
        estilo = sharedPreferences.getString("estiloApp", "Claro");

        // Y una vez declarado todos los componentes para que no surjan errores llamamos al método en donde aplicamos el estilo
        establecerEstilo();

        // Y devolvemos la vista
        return view;
    }

    /**
     * Método para establecer el estilo de la app basandonos en las sharedPreferences*/
    public void establecerEstilo(){
        // Comprobamos que tipo de estilo tenemos
        if(estilo.equals("Claro")){ // En caso de que sea el estilo claro
            textoDueno.setTextColor(Color.BLACK); // Establecemos el dueño de color negro
            textoDescrip.setTextColor(Color.BLACK); // Establecemos la descripción de color negro
            textoCiudad.setTextColor(Color.BLACK); // Establecemos la ciudad de color negro
            textoUbicacion.setTextColor(Color.BLACK); // Establecemos la localización de la bici de color negro
        }else if(estilo.equals("Oscuro")){ // En caso de que sea el estilo oscuro
            textoDueno.setTextColor(Color.WHITE); // Establecemos el dueño de color blanco
            textoDescrip.setTextColor(Color.WHITE); // Establecemos la descripción de color blanco
            textoCiudad.setTextColor(Color.WHITE); // Establecemos la ciudad de color blanco
            textoUbicacion.setTextColor(Color.WHITE); // Establecemos la localización de la bici de color blanco
        }
    }
}
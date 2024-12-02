package com.clase.sharedmybike;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.clase.sharedmybike.bikes.BikesContent;
import com.clase.sharedmybike.databinding.FragmentSecondBinding;
import com.clase.sharedmybike.placeholder.PlaceHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Manuel
 * @version 1.0*/

public class SecondFragment extends Fragment {
    RecyclerView recyclerView=null; // Variable donde declararemos el recyclerview para la lista de bicis
    EditText valorFiltro=null; // Variable para luego poder buscar por un filtro
    Button buscarPorFiltro=null; // Variable para confirmar la busqueda con un filtro
    Spinner spinnerFiltro=null; // Variable para elegir si no queremos filtros o si y cual usar
    SharedPreferences sharedPreferences=null; // Variable para poder guardar y cargar las preferencias del usuario
    private FragmentSecondBinding binding;
    String tipoBusqueda=null; // Variable para saber que tipo de busqueda vamos a realizar
    String busqueda=null; // Variable en donde cargaremos lo buscado en el filtro
    String estilo = ""; // Variable para establecer el estilo de este fragmento

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentSecondBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Cargamos los sharedPreferences y en caso establecemos la bombaElegida por defecto si no hay ningun dato guardado
        sharedPreferences = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        estilo = sharedPreferences.getString("estiloApp", "Claro");

        // Obtenemos el EditText de la búsqueda gracias al binding
        valorFiltro = (EditText) binding.editTextFiltro;
        // Obtenemos el Button de confirmar busqueda gracias al binding
        buscarPorFiltro = (Button) binding.btnBuscar;
        // Obtenemos el Spinner gracias al binding
        spinnerFiltro = (Spinner) binding.spinnerFiltrarPor;
        // Obtenemos el recyclerView gracias al binding
        recyclerView = binding.recycler;

        // Comprobamos si el RecyclerView está vacío o no
        if (recyclerView != null) { // En caso de que no esté vacío
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

            // Inicializo el adaptador del recycler, una nueva lista vacía, en donde después cargaremos las diferentes listas
            MyItemRecyclerViewAdapter adapter = new MyItemRecyclerViewAdapter(new ArrayList<>(), getContext());
            // Le establecemos el adaptador al recycler
            recyclerView.setAdapter(adapter);

            // Creamos una nueva lista de bicicletas que nos servirá de placeholder
            List<BikesContent.Bike> placeholders = PlaceHolder.generatePlaceholders(12);
            // Llamamos al método del adaptador para actualizar el recyclerView pasandole la lista de los placeholder
            adapter.actualizarRecyclerConNuevaLista(placeholders);

            // Activamos como true que los datos están siendo cargados, esto nos sirve para mostrar los placeholder mientras
            adapter.setLoading(true);

            // Simulamos la carga de los datos
            new Handler().postDelayed(() -> {
                // Obtenemos ya la lista completa de las bicicletas desde el BikesContent
                List<BikesContent.Bike> bikes = BikesContent.getITEMS();

                // Verificamos que la lista no seá nula o que esté vacía
                if (bikes != null && !bikes.isEmpty()) { // En caso de que la lista tenga algo
                    // Marcamos como que ya se han cargado los datos
                    adapter.setLoading(false);
                    // Actualizamos el recyclerView con la lista de las bicicletas completa
                    adapter.actualizarRecyclerConNuevaLista(bikes);
                    // Notificamos al adaptador que va ha haber un cambio de datos en el recyclerView
                    adapter.notifyDataSetChanged();
                } else { // En caso de que la lista este vacia
                    // Lanzamos un toast notificando al usuario de lo ocurrido
                    Toast.makeText(getContext(), "No se han encontrado bicicletas", Toast.LENGTH_SHORT).show();
                }
            }, 1200); // Simulamos unos 1,2 segundos para mostrar los placeholders

        } else { // En caso de que no se encuentre el RecyclerView
            // Lanzamos un Toast  al usuario notificandole el error
            Toast.makeText(getContext(), "No hemos podido encontrar el Recycler", Toast.LENGTH_SHORT).show();
        }

        // Configuro el spinner para filtrar dependiendo de la elección
        spinnerFiltro.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                // Hago un switch para poder acceder a la posición del elemento elegido
                switch (i) {
                    case 0: // En caso de elegir la opción 0, que es sin filtro
                        valorFiltro.setVisibility(View.GONE); // Mantenemos el editText oculto
                        buscarPorFiltro.setVisibility(View.GONE); // Mantenemos el button oculto
                        tipoBusqueda = "SinFiltro"; // Establecemos el tipo de busqueda como sinFiltro
                        filtrarLista(""); // Llamamos al método de filtrar la lista sin pasarle nada en el string
                        break;
                    case 1: // En caso de elegir la opción 1, que es ciltrar por ciudades
                        valorFiltro.setVisibility(View.VISIBLE); // Mostramos el editText
                        buscarPorFiltro.setVisibility(View.VISIBLE); // Mostramos el button
                        tipoBusqueda = "Ciudad"; // Establecemos el tipo de busqueda en "Ciudad"
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        // Configuración del botón para ejecutar la búsqueda
        buscarPorFiltro.setOnClickListener(view1 -> {
            busqueda = valorFiltro.getText().toString().trim();
            if (!busqueda.isEmpty()) { // En caso de que la variable de la busqueda tenga algo
                filtrarLista(busqueda); // Llamamos al método de filtrar lista y le agregamos el parametro de la busqueda que queremos hacer
            } else { // En caso de que la vriable de busqueda esté vacío
                // Lanzamos un Toast para notificar al usuario que tiene que introducir algo para buscar
                Toast.makeText(getContext(), "Introduce algo para buscar", Toast.LENGTH_SHORT).show();
            }
        });

        // Llamamos al estilo para establecer el estilo a la aplicación
        establecerEstilo();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /**
     * @param busqueda
     * Método en donde obtenemos la lista completa de bicicletas que hay en el JSON, obtenemos también el adaptador
     * del recyclerView que declaramos anteriormente y en caso de que haya salido bien la obtención de información
     * procedemos a detectar que tipo de busqueda es, si es busqueda por ciudad lo que hacemos es crear una nueva
     * lista de bicicletas y recorremos la que tiene todas, en caso de que una contenga el nombre de la ciudad que hemos
     * introducido nos la agrega a la nueva lista, y una vez recorrida toda la lista completa de bicis, actualizamos el
     * adaptador del recycler para que solo cargue las bicis necesarias. Con el tipo de bicicleta pasa exactamente igual.
     * Además para comparar las ciudades o tipo de bicis, convierto todas las palabras a minusculas y elimino los esapcios
     * de delante y de atras y además contemplo que contenga la palabra no que seá igual a esa palabra*/
    public void filtrarLista(String busqueda){
        // Obtengo las lista de bicicletas
        List<BikesContent.Bike> bikes = BikesContent.getITEMS();

        // Declaro un adaptador y le introduzco el que ya tiene otorgado el recyclerView del Fragmento
        MyItemRecyclerViewAdapter adapter = (MyItemRecyclerViewAdapter) recyclerView.getAdapter();

        // Comprobamos que se haya cargado bien el adaptador y no este vacio
        if (adapter != null) { // En caso de que el adaptador no esté vacío
            // Si no hemos rellenado nada de la busqueda o el tipo de busqueda en null o es SinFiltro
            if (busqueda.isEmpty() || tipoBusqueda == null || tipoBusqueda.equals("SinFiltro")) {
                // Mostramos todas las bicis sin aplicar ningún filtro
                adapter.actualizarRecyclerConNuevaLista(bikes);
            } else if (tipoBusqueda.equals("Ciudad")) { // En caso de que el tipo de Busqueda seá por Ciudad
                // Filtro las bicicletas por la ciudad en donde están
                // Creo una nueva lista de bicicletas vacias
                List<BikesContent.Bike> filtradas = new ArrayList<>();
                for (BikesContent.Bike bike : bikes) { // Voy recorriendo una por una todas las bicis de la lista completa
                    // En caso de que la ciudad esté rellena y poniendola en minusculas y eliminando los espacios de adelante
                    // y atras contenga el valor de la busqueda
                    if (bike.city != null && bike.city.toLowerCase().trim().contains(busqueda.toLowerCase().trim())) {
                        filtradas.add(bike); // La agrego a la lista de bicicletas filtradas
                    }
                }
                adapter.actualizarRecyclerConNuevaLista(filtradas); // Actualizo el adaptador con la nueva lista de bicis
            }
        }
    }

    /**
     * Método para establecer el estilo de la app basandonos en las sharedPreferences*/
    public void establecerEstilo(){
        // Comprobamos si el estilo guardado en claro u oscuro
        if(estilo.equals("Claro")){ // En caso de que seá estilo claro
            valorFiltro.setTextColor(Color.BLACK); // Establecemos el color del texto del EdiText en negro
            valorFiltro.setHintTextColor(Color.GRAY); // Establecemos el color del hint del EdiText en gris
        }else if(estilo.equals("Oscuro")){ // En caso de que seá estilo oscuro
            valorFiltro.setTextColor(Color.WHITE); // Establecemos el color del texto del EdiText en blanco
            valorFiltro.setHintTextColor(Color.DKGRAY); // Establecemos el color del hint del EdiText en gris clarito
        }
    }
}
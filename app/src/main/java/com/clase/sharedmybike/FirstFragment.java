package com.clase.sharedmybike;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.clase.sharedmybike.databinding.FragmentFirstBinding;

import java.util.Calendar;

/**
 * @author Manuel
 * @version 1.0*/

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;
    CalendarView calendario=null; // Variable para obtener el CalendarView de la app
    TextView fecha=null; // Variable para establecer la fecha según vayamos cambiando la selección
    Button elegir=null; // Variable para elegir una fecha y pasar al siguiente fragmento
    int dia = 0; // Variable para saber el día
    int mes = 0; // Variable para saber el número de mes
    int ano = 0; // Variable para saber el año
    String estilo = ""; // Variable donde cargaremos el estilo de la app
    String fechaPasar = ""; // Variable para pasar y guardar la fecha elegida
    SharedPreferences sharedPreferences=null; // Variable para poder guardar y cargar las preferencias del usuario

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflamos el layout y configuramos el binding
        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Cargamos los sharedPreferences
        sharedPreferences = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        // Inicializo el calendario usando el binding
        calendario = binding.calendarView;
        // Inicializo el textview donde saldrá la fecha usando el binding
        fecha = binding.textDia;
        // Inicializo el botón de elegir el día usando el binding
        elegir = binding.btnElegirDia;

        // Configuramos el calendario con la fecha actual
        Calendar hoy = Calendar.getInstance();
        dia = hoy.get(Calendar.DAY_OF_MONTH);
        mes = hoy.get(Calendar.MONTH);
        ano = hoy.get(Calendar.YEAR);

        // Estableczco la fecha mínima seleccionable para no poder seleccionar fechas pasadas
        calendario.setMinDate(hoy.getTimeInMillis());

        // Declaro una nueva variable y la cargo la información del sharedPreferences correspondiente a la fecha
        String fechaGuardada = sharedPreferences.getString("fecha", null);
        if (fechaGuardada != null) { // En caso de que la fecha si que tenga contenido
            // Parseamos la fecha (día, mes, año) desde el formato guardado (dd/MM/yyyy), lo que hacemos es dividir las 3 partes de la fecha
            String[] partesFecha = fechaGuardada.split("/");
            if (partesFecha.length == 3) { // En caso de que salga correcto tendremos 3 partes en el array creado anteriormente
                dia = Integer.parseInt(partesFecha[0]); // Obtenemos el día
                mes = Integer.parseInt(partesFecha[1]) - 1; // Obtenemos el mes y le restamos 1 porque en el CalendarView los meses van del 0 al 11
                ano = Integer.parseInt(partesFecha[2]); // Obtenemos el año

                // Creo un objeto calendar para poder crear una fecha que seá legible por el CalendarView
                Calendar calendar = Calendar.getInstance();
                // Establecemos la variable con el año, el més y el día
                calendar.set(ano, mes, dia);
                // Ahora establecemos el CalendarView pasandole el Candelar antes creado
                calendario.setDate(calendar.getTimeInMillis(), true, true);

                // Mostrar la fecha en el TextView
                fecha.setText(fechaGuardada);
            }
        }else{ // En caso de que no tengamos ninguna fecha guardada anteriormente
            // Formateo la fecha para que tenga el formato dia/mes/año
            String fechaHoy = String.format("%02d/%02d/%04d", dia, (mes + 1), ano);
            sharedPreferences.edit().putString("fecha", fechaHoy).apply();

            // Establecer la fecha actual en el CalendarView
            calendario.setDate(hoy.getTimeInMillis(), true, true);

            // Mostrar la fecha actual en el TextView
            fecha.setText(fechaHoy);
        }

        // Configuramos el listener para el CalendarView
        calendario.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int dayOfMonth) {
                // Tenemos que ajustar el més, ya que los meses van del 0 al 11 y no del 1 al 12
                // Mostramos la fecha seleccionada en el TextView
                fecha.setText(dayOfMonth + "/" + (month+1) + "/" + year);
                // Y guardamos en las variables globales el día, mes y año
                dia = dayOfMonth;
                mes = month+1;
                ano = year;
            }
        });

        // Le otorgamos un evento al botón elegir para que haga una acción cuando le clickemos
        elegir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Validar si la fecha seleccionada es válida
                Calendar seleccionada = Calendar.getInstance();
                seleccionada.set(ano, mes - 1, dia);

                Calendar hoy = Calendar.getInstance();
                if (seleccionada.before(hoy)) {
                    Toast.makeText(getContext(), "Elija una fecha y que no seá posterior a hoy!!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Primero, la variable globable que utilizamos para pasar la fecha al otro fragmento le damos su valor
                fechaPasar = dia+"/"+mes+"/"+ano;
                // Obtenemos el editor de las sharedPreferences
                SharedPreferences.Editor editor = sharedPreferences.edit();
                // Establecemos la última fecha elegida
                editor.putString("fecha", fechaPasar);
                // Y guardamos los cambios
                editor.apply();

                // Creamos el Bundle para pasarselo al siguiente fragmento
                Bundle args = new Bundle();
                args.putString("fecha", fechaPasar); // Pasamos la fecha

                // Creamos un NavController haciendo referencia a la vista en donde estamos
                NavController navController = Navigation.findNavController(view);
                // Llamamos a la acción de pasar del primer fragmento al segundo
                navController.navigate(R.id.action_FirstFragment_to_SecondFragment, args);
                // Establecemos el titulo del toolbar
                NavigationUI.setupActionBarWithNavController((AppCompatActivity) getActivity(), navController);
            }
        });

        // Cargamos en la variables estilo el estilo de la app, sino hay nada guardado, le ponemos el claro que es el de por defecto
        estilo = sharedPreferences.getString("estiloApp", "Claro");

        // Y una vez declarado todos los componentes para que no surjan errores llamamos al método en donde aplicamos el estilo
        establecerEstilo();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /**
     * Método para establecer el estilo de la app basandonos en las sharedPreferences*/
    public void establecerEstilo(){
        // Comprobamos que tipo de estilo tenemos
        if(estilo.equals("Claro")){ // Si el estilo es claro
            fecha.setTextColor(Color.BLACK); // Ponemos el Textview de la fecha de color negro
        }else if(estilo.equals("Oscuro")){ // Si el estilo es oscuro
            fecha.setTextColor(Color.WHITE); // Ponemos el Textview de la fecha de color blanco
        }
    }
}
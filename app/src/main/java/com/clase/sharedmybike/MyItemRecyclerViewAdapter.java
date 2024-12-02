package com.clase.sharedmybike;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.RecyclerView;

import com.clase.sharedmybike.bikes.BikesContent;

import java.util.ArrayList;
import java.util.List;

import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author Manuel
 * @version 1.0*/

public class MyItemRecyclerViewAdapter extends RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder>{
    private final List<BikesContent.Bike> bikes; // Lista con todas las bicicletas
    private List<BikesContent.Bike> listaFiltrada; // Lista con las bicicletas filtras
    private final Context context; // Contexto de la actividad en donde le llamamos
    SharedPreferences sharedPreferences=null; // Variable para poder guardar y cargar las preferencias del usuario
    String estilo=null; // Variable para obtener el estilo de la app
    String fecha=null; // Variable para obtener la fecha que elegimos anteriormente
    boolean isLoading = true; // Variable para comprobar si se están cargando los datos o ya están cargados

    /**
     * @param bikes
     * @param context
     * Método que es el constructor al que le pasamos la lista con todas las biciletas
     * y el contexto de la actividad en donde llamamos al fragmento*/
    public MyItemRecyclerViewAdapter(List<BikesContent.Bike> bikes, Context context) {
        this.bikes = bikes;
        this.listaFiltrada = new ArrayList<>(); // Creamos una nueva lista para la listaFiltrada de bicicletas
        this.context = context;

        // Inicializar SharedPreferences
        sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        // Cargamos el estilo, en caso de no tener ninguno guardado, ponemos el claro
        estilo = sharedPreferences.getString("estiloApp", "Claro");
        // Obtenemos la fecha si esque hemos guardado alguna ya
        fecha = sharedPreferences.getString("fecha", null);
    }

    /**
     * @param nuevaLista
     * Método para actualizar el recycler con la nueva lista que le pasamos, que es la lista friltrada,
     * ya sea con algun filtro aplicado o sin ninguno filtro aplicado. Limpiamos la lista que tenemos en la clase
     * le introducimos la lista completa que le pasamos por parametro y notifiacmos al adaptador y al recycler
     * que se han producido cambios en los datos*/
    public void actualizarRecyclerConNuevaLista(List<BikesContent.Bike> nuevaLista) {
        listaFiltrada.clear(); // Primero, limpiamos la lista para no ocasionar errores ni cargas bicis que no se requieren
        listaFiltrada.addAll(nuevaLista); // Agregamos la lista que le pasamos a la listaFiltrada
        notifyDataSetChanged(); // Notificamos al adaptador que los datos han cambiado
    }

    /**
     * @param loading
     * Método en donde establecemos la variable global como el parametro que le pasamos a la función
     * y además notificamos el cambio al adaptador y al recycler*/
    public void setLoading(boolean loading) {
        isLoading = loading;
        notifyDataSetChanged();
    }

    /**
     * @param parent
     * @param viewType*/
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_bike, parent, false);
        return new ViewHolder(view);
    }

    /**
     * @param holder
     * @param position*/
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        // Estilo según el tema (Claro u Oscuro)
        if (estilo.equals("Claro")) { // En caso de que sea estilo claro
            holder.cardView.setCardBackgroundColor(Color.WHITE);
            holder.itemView.setBackgroundColor(Color.WHITE);
            holder.duenoBici.setTextColor(Color.BLACK);
            holder.descripcionBici.setTextColor(Color.BLACK);
            holder.ciudad.setTextColor(Color.BLACK);
            holder.dire.setTextColor(Color.BLACK);
        } else if (estilo.equals("Oscuro")) { // En caso de que sea estilo oscuro
            holder.cardView.setCardBackgroundColor(Color.BLACK);
            holder.itemView.setBackgroundColor(Color.BLACK);
            holder.duenoBici.setTextColor(Color.WHITE);
            holder.descripcionBici.setTextColor(Color.WHITE);
            holder.ciudad.setTextColor(Color.WHITE);
            holder.dire.setTextColor(Color.WHITE);
        }
        // Verificamos si estamos en proceso de carga de datos o ya se han cargado
        if (isLoading) { // En caso de que se estén cargando los datos aun
            // Mostramos una especie de placeholder
            holder.imagenBici.setBackgroundColor(Color.LTGRAY);
            holder.duenoBici.setText("Cargando Dueño...");
            holder.descripcionBici.setText("Cargando Descripción...");
            holder.ciudad.setText("Cargando Ciudad...");
            holder.dire.setText("Cargando Ciudad...");
            holder.botonCorreo.setBackgroundColor(Color.LTGRAY);
        } else { // En caso de que ya se hayan cargado todos los datos
            // Obtenemos la listaFiltrada con las bicis
            BikesContent.Bike bike = listaFiltrada.get(position);

            // Volvemos a poner los fondos transparentes y el icono a la imagen del botón de correo
            holder.imagenBici.setBackgroundColor(Color.TRANSPARENT);
            holder.duenoBici.setBackgroundColor(Color.TRANSPARENT);
            holder.descripcionBici.setBackgroundColor(Color.TRANSPARENT);
            holder.ciudad.setBackgroundColor(Color.TRANSPARENT);
            holder.dire.setBackgroundColor(Color.TRANSPARENT);
            holder.botonCorreo.setBackgroundColor(Color.TRANSPARENT);
            holder.botonCorreo.setImageResource(R.drawable.icono_correo);

            // Comprobamos si la bici en esa posición tiene foto o no
            if (bike.photo != null) { // En caso de que si que tenga foto
                holder.imagenBici.setImageBitmap(bike.photo); // Le establecemos la foto al objeto especifico
            } else { // En caso de que la bicicleta no tenga foto
                holder.imagenBici.setImageResource(R.drawable.bici_prede); // Procedemos a poner una bici por defecto para que no ser pierda el atractivo
            }

            // Establezco el dueño en el el componente del dueño
            holder.duenoBici.setText(bike.owner);
            // Establezco la descripción en el el componente de la descripción
            holder.descripcionBici.setText(bike.description);
            // Establezco la ciudad en el el componente de la ciudad
            holder.ciudad.setText(bike.city);
            // Establezco la dirección en el el componente de la dirección
            holder.dire.setText(bike.location);

            // Establecemos al botonCorreo un evento para cuando le pulsemos realice una acción
            holder.botonCorreo.setOnClickListener(v -> {
                // En este caso vamos a crear un String con formato de correo para enviar un correo al dueño de la bici que queremos
                // para así enviarle la información necesaria
                String uriText = "mailto:" + bike.email +
                        "?subject=" + Uri.encode("Bike Reservation Request") +
                        "&body=" + Uri.encode(
                        "Dear Mr/Mrs " + bike.owner + ",\n\n" +
                                "I'd like to use your bike at " + bike.location + " (" + bike.city + ") " +
                                "for the following date: " + fecha + ".\n\n" +
                                "Can you confirm its availability? \n" +
                                "Kindest regards "
                );
                // Parseamos el String que creamos antes para poder pasarselo al Intent
                Uri uri = Uri.parse(uriText);
                // Creamos un nuevo intent para enviar el correo en donde le pasamos el formato uri del correo que deseamos enviar
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, uri);
                // Iniciamos la actividad de enviar el correo
                v.getContext().startActivity(emailIntent);
            });

            // Otorgamos un evento al tocar el item de una bicicleta del recycler
            holder.itemView.setOnClickListener(v -> {
                // Obtenemos el NavController asociado al contexto actual
                NavController navController = Navigation.findNavController((AppCompatActivity) v.getContext(), R.id.nav_host_fragment_content_bike);

                // Creamos el Bundle para poder pasarle la bicicleta elegida al siguiente fragmento
                Bundle bundle = new Bundle();
                bundle.putParcelable("selected_bike", bike); // Establecemos la clave de recuperación y pasamos la bici parceada

                // Llamamos a la acción para pasar del fragmento actual al de la información de las bicis y le pasamos el objeto Bundle
                navController.navigate(R.id.action_SecondFragment_to_ThirdFragment, bundle);
            });
        }
    }

    /**
     * @return
     * Método para devolver el tamaño de la lista filtrada*/
    @Override
    public int getItemCount() {
        // Lo que hacemos, es, comprobar si la variable que comprueba si estamos cargando los datos o ya los cargamos es verdaderá
        // devuevle una longitud de 12 y si es falsa por lo cuál ya hemos cargado todos los datos, devolvemos la longitud de la lista normal
        return isLoading ? 12 : listaFiltrada.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final CardView cardView;
        public final ImageView imagenBici;
        public final TextView duenoBici;
        public final TextView descripcionBici;
        public final ImageView botonCorreo;
        public final TextView ciudad;
        public final TextView dire;

        public ViewHolder(View view) {
            super(view);
            cardView = itemView.findViewById(R.id.cardid);
            imagenBici = view.findViewById(R.id.imageBike);
            ciudad = view.findViewById(R.id.textCiudad);
            duenoBici = view.findViewById(R.id.textDueno);
            dire = view.findViewById(R.id.textDir);
            descripcionBici = view.findViewById(R.id.textDes);
            botonCorreo = view.findViewById(R.id.iconoEmail);
        }
    }
}
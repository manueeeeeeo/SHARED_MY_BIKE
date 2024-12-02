package com.clase.sharedmybike.placeholder;

import com.clase.sharedmybike.bikes.BikesContent;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Manuel
 * @version 1.0*/

public class PlaceHolder {
    /**
     * @param count
     * @return
     * Método para generar una nueva lista de bicicletas que nos servirá para
     * mostrarla en el rato que cargamos las bicicletas de verdad, para que el usuario
     * veá que se está procesando el cargado de bicicletas*/
    public static List<BikesContent.Bike> generatePlaceholders(int count) {
        // Creamos una nueva lista de bicicletas que la llamaremos placeholder
        List<BikesContent.Bike> placeholders = new ArrayList<>();
        // Creamos el número de bicicletas que nos pasan como parametros
        for (int i = 0; i < count; i++) {
            // Vamos creando una por una todas las bicicletas con todos los datos de cargar
            BikesContent.Bike placeholder = new BikesContent.Bike(
                    null, // Sin foto
                    "Cargando Dueño...", // Ejemplo de cargando dueño...
                    "Cargando Descripción...", // Ejemplo de cargando descripción...
                    "Cargando Ciudad...", // Ejemplo de cargando ciudad...
                    "Cargando Ubicación...", // Ejemplo de cargando ubicación...
                    "Cargando correo..." // Ejemplo de cargando correo...
            );
            // Vamos aregando a la lista anteriormente creada una por una todas las bicicletas ya rellenas
            placeholders.add(placeholder);
        }

        // Devolvemos la lista de bicicletas auxiliar
        return placeholders;
    }
}
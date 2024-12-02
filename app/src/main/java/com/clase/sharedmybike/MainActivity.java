package com.clase.sharedmybike;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import java.io.IOException;
import java.util.List;

/**
 * @author Manuel
 * @version 1.0*/

public class MainActivity extends AppCompatActivity {
    // Declaramos e inicializamos las variables necesarias
    TextView cp=null; // Variable del textview del código postal
    Button iniciar=null; // Variable del botón para iniciar la app
    ImageButton obtenerCP=null; // Variable del imagenButton para obtener el cp
    ImageView btnAjustes=null; // Imagen para acceder a los ajustes
    ConstraintLayout main=null; // Constraint de la pantalla principal
    TextView descrip=null; // Texto de la descripción de la app
    TextView correoString=null; // Texto del correo que aparece
    ImageButton btnCorreo=null; // ImagenButon para configurar el correo
    EditText introCorreo=null; // Variable para obtener el correo que introduce el usuario
    double latitud=0.0; // Variable de la latitud
    double longitud=0.0; // Variable de la longitud
    private FusedLocationProviderClient fusedLocationProviderClient=null; // Variable para obtener la ubicación del usuario utilziando la API Fused Location Provider
    public final int LOCATION_PERMISSION_REQUEST_CODE = 1; // Varaible para comprobar los permisos de la app
    SharedPreferences sharedPreferences=null; // Los sharedPreferences para guardar la configuración del usuario
    String estilo=null; // Para obtener el estilo de la app
    Toast mensajeToast=null; // Variable para manejar los Toast y evitar toast en cola infinita

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Obtenemos los servicios de localización con la API Fused Location Provider
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Cargamos los sharedPreferences y en caso establecemos la bombaElegida por defecto si no hay ningun dato guardado
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        estilo = sharedPreferences.getString("estiloApp", "Claro");

        // Obtengo el Constraint Layout de la pantalla actual
        main = (ConstraintLayout) findViewById(R.id.main);
        // Obtenemos la descripción de la app de la pantalla actual
        descrip = (TextView) findViewById(R.id.textIntro);
        // Obtenemos el imagenButton de la pantalla actual
        btnCorreo = (ImageButton) findViewById(R.id.btnCorreo);
        // Obtenemos el texto donde pone el correo de la pantalla actual
        //correoString = (TextView) findViewById(R.id.textGmail);
        // Obtenemos el EditText donde ponemos el correo del usuario
        introCorreo = (EditText) findViewById(R.id.editEmail);

        // Obtenemos el TextView para posteriormente colocar el código postal del usuario
        cp = (TextView) findViewById(R.id.textPostal);

        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Permiso ya concedido, puedes proceder a obtener la ubicación
            showToast("Permisos de ubicación concedidos");
        } else {
            // Si no se ha concedido el permiso, solicita el permiso
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }

        // Obtenemos el botón para pasar a la siguiente pantalla
        iniciar = (Button) findViewById(R.id.btnLogin);
        // Le agregamos un evento para cuando seá clicado
        iniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isValidEmail(introCorreo.getText().toString()) && comprobarCP(cp.getText().toString())){
                    // Indicamos la actividad actual y a la que vamos a pasar
                    Intent i = new Intent(MainActivity.this, BikeActivity.class);
                    startActivity(i); // Iniciamos la nueva actividad
                    finish(); // Finalizamos está actividad
                    // Establecemos una animación al pasar a la nueva actividad (deslizar hacia la derecha y hacia la izquierda)
                    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                }else{
                    if(!isValidEmail(introCorreo.getText().toString())){
                        //Toast.makeText(MainActivity.this, "Te falta configurar el correo", Toast.LENGTH_SHORT).show();
                        showToast("Te falta configurar el correo");
                    }else if(!comprobarCP(cp.getText().toString())){
                        //Toast.makeText(MainActivity.this, "Te falta configurar el código postal", Toast.LENGTH_SHORT).show();
                        showToast("Te falta configurar el código postal");
                    }else{
                        //Toast.makeText(MainActivity.this, "Te falta configurar el correo y el código postal", Toast.LENGTH_SHORT).show();
                        showToast("Te falta configurar el correo y el código postal");
                    }
                }
            }
        });

        // Obtenemos el botón que al ser pulsado obtenemos el código postal del usuario
        obtenerCP = (ImageButton) findViewById(R.id.btnLocation);
        // Le damos un evento para cuando sea pulsado
        obtenerCP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Llamo al método para obtener y comprobar el gps
                verificarYActivarGPS();
                // Volvemos a comprobar si el usuario tiene el permiso de ubicación activado
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
                }else { // En caso de que si que le tenga llamamos al método para obtener la ubicación
                    obtenerUbiacion();
                }
            }
        });

        // Obtenemos la imagen de ajustes de la pantalla inicial
        btnAjustes = (ImageView) findViewById(R.id.btnAjustes);
        // Le otorgamos un evento para que cuando sea clicada pase algo
        btnAjustes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Indicamos la actividad actual y a la que vamos a pasar
                Intent i = new Intent(MainActivity.this, Ajustes_Activity.class);
                startActivity(i); // Iniciamos la nueva actividad
                finish(); // Finalizamos está actividad
                // Establecemos una animación al pasar a la nueva actividad (deslizar hacia la derecha y hacia la izquierda)
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            }
        });

        // Llamamos al método para establecer el estilo a la app, ya seá oscuro o claro
        establecerEstilo();
    }

    /**
     * @param email
     * @return
     * Método en donde le pasamos un supuesto email y nos devuelve un booleando
     * indicandonos si el supuesto email respeta o no la estructura que siguen
     * los correos electronicos*/
    public static boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    /**
     * @param cod
     * @return
     * Método en donde le pasamos un String que es el contenido del TextView donde se va a cargar el código postal
     * lo que hacemos es comprobar si el String que le pasamos es igual al mensaje que tiene el TextView al princio
     * como este TextView solo cambia en caso de que se obtenga el código postal, pues si es igual no se ha obtenido
     * el código postal por lo tanto no puede continuar, en caso de que seá diferente si que puede continuar*/
    public boolean comprobarCP(String cod){
        boolean bien = false;
        if(cod.equals("Tu dirección postal")){
            bien = false;
        }else{
            bien = true;
        }
        return bien;
    }

    /**
     * Método en donde comprobamos si tenemos los permisos otorgados, en caso de si tenerlos otorgados
     * procedemos a obtener la latitud y la longitud del usuario, posteriormente creamos un string
     * al cuál le damos formato para que nos abrá la aplicación de Google Maps con esa latitud y
     * longitud, además de abrir el Google Maps en esa ubicación, también llamo al método para obtener
     * el código postal del usuario*/
    public void obtenerUbiacion(){
        // Comprobamos que tenemos activos todos los permisos necesarios para obtener la ubicación, utilizamos el ACCESS_FINE_LOCATION
        // para la ubicación precisa y el ACCESS_COARSE_LOCATION para la localización aproximada
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // En caso de no tener alguno de los dos se para la ejecucción del proyecto y volvemos
            return;
        }
        // Utilziamos el provedor de localziación del cliente para conocer la última ubicación del dispositvo
        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(this, location -> { // Aquí lo que hacemos es programar que hacer si obtiene la ubiación correctamente
                    if (location != null) { // En caso de la localización no seá nula
                        latitud = location.getLatitude(); // Obtenemos la latitud del usuario
                        longitud = location.getLongitude(); // Obtenemos la longitud del usuario

                        // Llamo al método para obtener el código postal
                        obtenerCodigoPostal(latitud, longitud);

                        // Llamo al método para abrir el Google Maps
                        abrirGoogleMaps();
                    } else { // En caso de que la localización seá nula o no se pueda acceder
                        // Lanzamos el Toast diciendo que no se ha podido obtener la ubi
                        //Toast.makeText(this, "No se pudo obtener la ubicación", Toast.LENGTH_LONG).show();
                        //showToast("No se pudo obtener la ubicación");
                        // Llamo al método para forzar una ubicación en tiempo real
                        solicitarUbicacionActiva();
                    }
                });
    }

    /**
     * Método en donde abro el Google Maps en la ubicación obtenida*/
    public void abrirGoogleMaps(){
        String uri = String.format("geo:%f,%f", latitud, longitud);
        Intent intent = new Intent();
        // Establezco la acción que va ha realizar el intent
        intent.setAction(Intent.ACTION_VIEW);
        // Establecemos los datos que le vamos a pasar que en este caso es la variable a la que dimos formato para abrir Maps
        intent.setData(Uri.parse(uri));
        if (intent.resolveActivity(getPackageManager()) != null){
            startActivity(intent);
        }else{
            showToast("No se encontró una aplicación para abrir mapas");
        }
    }

    /**
     * Este método es llamado en caso de que aun el usuario teniendo el GPS activado, no seamos capaces de obtener su ubicación,
     * ya que al utilizar la API está lo que hacemos es coger la última ubicación guardada, por lo que si por cualquier
     * cosa no existe o se queda el GPS congelado, no podremos obtener el CP
     * https://developer.android.com/reference/android/location/LocationRequest
     * */
    private void solicitarUbicacionActiva() {
        // Creo una instancia de LocationRequest, en donde establezco, la priporidad y el intervalo para forzar ubicaciones
        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000) // Intervalo normal de 5 segundos
                .setMinUpdateIntervalMillis(2000) // Intervalo más rápido de 2 segundos
                .setMaxUpdates(1) // Establecemos que solo necesitamos una actualización
                .build(); // Construimos y confirmamos todos los parametros

        // Creamos un callback para manejar la respuesta de la solicitud de forzar la ubicaci´n
        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                // Comprobamos si ya si que hemos podido obtener la ubicación o no
                if (locationResult != null && locationResult.getLastLocation() != null) { // En caso de que si que hayamos podido
                    // Obtenemos la ubicación
                    Location location = locationResult.getLastLocation();
                    latitud = location.getLatitude(); // Establecemos la latitud
                    longitud = location.getLongitude(); // Establecemos la longitud

                    // Llamamos al método para obtener el código postal
                    obtenerCodigoPostal(latitud, longitud);

                    // Llamo al método para abrir el Google Maps
                    abrirGoogleMaps();
                } else { // En caso de que sigamos sin poder obtener la ubicación
                    // Lanzamos un Toast para notificar de la incidencia al usuario
                    showToast("No se pudo obtener la ubicación activa");
                }
            }
        };

        // Comprobamos los permisos
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        // Iniciamos la solicitud de ubicación con el nuevo LocationRequest
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    /**
     * @param latitud
     * @param longitud
     * Método al que le pasamos la latitud y la longitud donde se encuentra el usuario y gracias al
     * objeto Geocoder obtemos un lista de las posciones del usuario, pero solo obtenemos la primero dirección.
     * Si la lista no está vacia procedemos a obtener el código postal de la dirección, en caso de que esté
     * vacia la dirección por el logcat imprimimos un mensaje indicando que no se encontro la dirección
     * para esas coordenadas y esto dentro de un try catch por si acaso ocurre un error que sepas cuál es*/
    public void obtenerCodigoPostal(double latitud, double longitud){
        // Creamos un nuevo objeto Geocoder para poder pasar coordenadas a una ubicación fisica
        Geocoder geocoder = new Geocoder(this);
        try {
            // Creamos una lista de direcciones basandonos en el objeto Geocoder donde utilziamos getFromLocation para transformar
            // las coordenadas en una lista de address, le establecemos la latitud y la longtiud y limitamos la lista a solo un resultado
            List<Address> addresses = geocoder.getFromLocation(latitud, longitud, 1);
            // Comprobamos que hemos obtenido bien la Address
            if (addresses != null && !addresses.isEmpty()) { // En caso de que la Address esté llena
                Address address = addresses.get(0); // Obtenemos el primer elemento de la lista
                String codigoPostal = address.getPostalCode(); // Obtenemos el código postal con el método getPostalCode
                cp.setText(codigoPostal); // Modificamos el valor del TextView de código postal
            } else { // En caso que no hayamos obtenido bien la Address
                // Lanzamos un Toast para comunicar el error
                showToast("Error: No pudimos encontrar la dirección de las coodernadas");
                //Log.d("Geocoder", "No se encontró la dirección para las coordenadas");
            }
        } catch (IOException e) { // En caso de que surja alguna escepción
            e.printStackTrace();
            //Log.d("Geocoder", "Error al obtener el código postal");
        }
    }

    /**
     * @param requestCode
     * @param grantResults
     * @param permissions
     * Método que nos permite manejar los permisos de la aplicación, saber si se ha aceptado, sino, ect
     * en este caso estamos manejando y comprobando el permiso de acceso a la ubicación del usuario*/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // En caso de que el permiso se haya aceptado
                showToast("Permiso de ubicación aceptado");
            } else {
                // Si el usuario marca que no permite que la app acceda a su ubicación, le lanzamos un Toast
                // diciendo, permiso de ubicación denegado, para que sepá lo que ha hecho
                //Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show();
                showToast("El usuario ha rechazado los permisos de ubicación permanentemente, acceda a ajustes para cambiarlo");
            }
        }
    }

    /**
     * Método en el cuál comprobamos si el usuario tiene activado el GPS del telefono, en caso de
     * no tenerlo activado, abrimos la pantalla de ajustes de ubicación y le decimos que tiene que
     * activar el GPS*/
    public void verificarYActivarGPS() {
        // Creamos un LocationManager para poder gestionar la ubicación del dispotivo, con ello podemos acceder a servicios
        // como el GPS que es el que necesitamos
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // Comprobamos si el GPS está activado
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) { // Si el GPS no está activado
            // Le lanzamos un Toast para que sepa lo que tiene que hacer en la nueva pantalla que se ha abierto
            //Toast.makeText(this, "El GPS está desactivado. Debe activarlo", Toast.LENGTH_SHORT).show();
            showToast("El GPS está desactivado. Debe activarlo");

            // Creamos un nuevo intent para hacer que se abrá la configuración del GPS del usuario
            // y que este la pueda activar
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            // Iniciamos la actividad
            startActivity(intent);
        } else { // Si el GPS está activado
            // Le lanzamos un Toast para que sepá que tiene el GPS ya activado
            //Toast.makeText(this, "El GPS ya está activado.", Toast.LENGTH_SHORT).show();
            showToast("El GPS ya está activado.");
        }
    }

    /**
     * Método para establecer el estilo de la app basandonos en las sharedPreferences*/
    public void establecerEstilo(){
        if(estilo.equals("Claro")){ // En caso de que el estilo seá claro
            main.setBackgroundColor(Color.WHITE); // Establecemos el fondo en blanco
            descrip.setTextColor(Color.BLACK); // Ponemos los textview en negro
            btnCorreo.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFFFFF"))); // El fondo de los imageButton le ponemos blanco
            //correoString.setTextColor(Color.BLACK); // Ponemos los textview en negro
            cp.setTextColor(Color.BLACK); // Ponemos los textview en negro
            introCorreo.setTextColor(Color.BLACK); // Establecemos el color del texto que se escribe en el Edit en negro
            introCorreo.setHintTextColor(Color.GRAY); // Establecemos el color del hint en gris
            obtenerCP.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFFFFF")));
            obtenerCP.setImageResource(R.drawable.location_black);
            btnCorreo.setImageResource(R.drawable.correo_black);
        }else if(estilo.equals("Oscuro")){ // En caso de que el estilo seá oscuro
            main.setBackgroundColor(Color.BLACK); // Establecemos el fondo en negro
            descrip.setTextColor(Color.WHITE); // Ponemos los textview en blanco
            btnCorreo.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#000000"))); // El fondo de los imageButton le ponemos negro
            //correoString.setTextColor(Color.WHITE); // Ponemos los textview en blanco
            obtenerCP.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#000000"))); // El fondo de los imageButton le ponemos negro
            cp.setTextColor(Color.WHITE); // Ponemos los textview en blanco
            introCorreo.setTextColor(Color.WHITE); // Establecemos el color del texto que se escribe en el Edit en negro
            introCorreo.setHintTextColor(Color.LTGRAY); // Establecemos el color del hint en gris blanquecino
            obtenerCP.setImageResource(R.drawable.location_white);
            btnCorreo.setImageResource(R.drawable.correo_white);
        }
    }

    /**
     * @param message
     * Método para ir matando los Toast y mostrar todos en el mismo para evitar
     * colas de Toasts y que se ralentice el dispositivo*/
    public void showToast(String message) {
        // Comprobamos si existe algun toast cargado en el toast de la variable global
        if (mensajeToast != null) { // En caso de que si que exista
            mensajeToast.cancel(); // Le cancelamos, es decir le "matamos"
        }

        // Creamos un nuevo Toast con el mensaje que nos dan de argumento en el método
        mensajeToast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        // Mostramos dicho Toast
        mensajeToast.show();
    }
}
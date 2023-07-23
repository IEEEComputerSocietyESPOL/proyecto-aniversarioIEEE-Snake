import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HistorialPuntajes{
    private String RutaArchivo;
    private List<Puntaje> puntajes;



public EncargoPuntaje(String RutaArchivo) {
    this.RutaArchivo = RutaArchivo;
    this.puntajes = new ArrayList<>();
}
    

public void guardarPuntajes() {
    try (FileWriter escribir = new FileWriter(RutaArchivo)) {
        for (Puntaje puntaje : puntajes) {
            escribir.write(puntaje.getName() + "," + puntaje.getScore() + "\n");
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
}


/**
 * @param puntaje
 */
public void agregarPuntaje(Puntaje puntaje) {
    puntajes.add(puntaje);
    Collections.sort(scores);
}




public void cargarPuntajes() {
    puntajes.clear();
    try (BufferedReader lector = new BufferedReader(new FileReader(RutaArchivo))) {
        String line;
        while ((line = reader.readLine()) != null) {
            String[] data = line.split(",");
            if (data.length == 2) {
                String nombre = data[0];
                int puntaje = Integer.parseInt(data[1]);
                puntajes.add(new Score(nombre, puntaje));
            }
        }
    } catch (IOException | NumberFormatException e) {
        e.printStackTrace();
    }
}


public Score PuntajeMax() {
    if (puntajes.isEmpty()) {
        return null;
    }
    return puntajes.get(0);
}


}

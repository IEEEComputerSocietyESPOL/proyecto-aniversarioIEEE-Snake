package proyecto;

public class Puntaje implements Comparable<Puntaje> {
    private String nombre;
    private int puntaje;

    public Puntaje(String nombre, int puntaje) {
        this.nombre = nombre;
        this.puntaje = puntaje;
    }

    public String getNombre() {
        return nombre;
    }

    public int getPuntaje() {
        return puntaje;
    }

    @Override
    public int compareTo(Puntaje otroPuntaje) {
        // Ordenamos los puntajes de mayor a menor.
        return Integer.compare(otroPuntaje.puntaje, this.puntaje);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Puntaje) {
            Puntaje otroPuntaje = (Puntaje) obj;
            return this.nombre.equals(otroPuntaje.nombre);
        }
        return false;
    }
}
package logic;

/**
 * Clase Producto - modelo básico para un producto de la agroveterinaria.
 *
 * Campos: id (int), nombre (String), precio (double)
 * Métodos: constructores, getters, setters, toString y serializador para guardar en TXT.
 */
public class Producto {
    private int id;
    private String nombre;
    private double precio;

    public Producto() {
    }

    public Producto(int id, String nombre, double precio) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    @Override
    public String toString() {
        return "Producto{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", precio=" + precio +
                '}';
    }

    /**
     * Serializa el producto en una línea para guardar en archivo TXT.
     * Formato: id;nombre;precio
     */
    public String toTxtLine() {
        return id + ";" + nombre.replace(";", " ") + ";" + precio;
    }

    /**
     * Crea un objeto Producto a partir de una línea leída del TXT.
     * Espera formato: id;nombre;precio
     */
    public static Producto fromTxtLine(String line) {
        if (line == null || line.trim().isEmpty()) return null;
        String[] parts = line.split(";");
        if (parts.length < 3) return null;
        try {
            int id = Integer.parseInt(parts[0].trim());
            String nombre = parts[1].trim();
            double precio = Double.parseDouble(parts[2].trim());
            return new Producto(id, nombre, precio);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return null;
        }
    }
}

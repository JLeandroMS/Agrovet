package logic;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Factura {

    private String cedulaCliente;
    private Producto[] productos;
    private LocalDateTime fecha;

    public Factura(String cedulaCliente, Producto[] productos) {
        this.cedulaCliente = cedulaCliente;
        this.productos = productos;
        this.fecha = LocalDateTime.now(); // Fecha automática de la factura
    }

    public String getCedulaCliente() {
        return cedulaCliente;
    }

    public Producto[] getProductos() {
        return productos;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    // Calcula el total usando recursividad
    public double calcularTotal() {
        return sumaRecursiva(0);
    }

    private double sumaRecursiva(int index) {
        if (index == productos.length) return 0;
        return productos[index].getPrecio() + sumaRecursiva(index + 1);
    }

    // Serializa la factura para guardarla en TXT
    public String toTxt() {
        StringBuilder sb = new StringBuilder();

        sb.append("CEDULA: ").append(cedulaCliente).append("\n");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        sb.append("FECHA: ").append(fecha.format(formatter)).append("\n");

        sb.append("PRODUCTOS:\n");

        for (Producto p : productos) {
            sb.append(" - ").append(p.getNombre()).append(" | PRECIO: ").append(p.getPrecio()).append("\n");
        }

        sb.append("TOTAL: ").append(calcularTotal()).append("\n");
        sb.append("-------------------------\n");

        return sb.toString();
    }

    // Crea una factura a partir de texto leído del archivo TXT
    public static Factura fromTxt(String[] bloque) {
        try {
            String cedula = bloque[0].split(":")[1].trim();
            LocalDateTime fecha = LocalDateTime.parse(
                bloque[1].split(": ", 2)[1].trim(),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            );

            // Los productos vienen desde la línea 3 hasta la penúltima
            Producto[] lista = new Producto[bloque.length - 4];
            int index = 0;

            for (int i = 2; i < bloque.length - 2; i++) {
                String linea = bloque[i].trim().replace(" - ", "");
                // Formato: Nombre | PRECIO: X
                String[] partes = linea.split("\\| PRECIO: ");
                String nombre = partes[0].trim();
                double precio = Double.parseDouble(partes[1].trim());

                lista[index++] = new Producto(i, nombre, precio);
            }

            Factura f = new Factura(cedula, lista);
            f.fecha = fecha;
            return f;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

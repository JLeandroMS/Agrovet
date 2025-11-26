package logic;

import java.util.ArrayList;

public class Carrito {

    private ArrayList<Producto> productos;

    public Carrito() {
        productos = new ArrayList<>();
    }

    public void agregarProducto(Producto producto) {
        if (producto != null) {
            productos.add(producto);
        }
    }

    // ---------------- Nuevo método ----------------
    public void eliminar(int indice) {
        if (indice >= 0 && indice < productos.size()) {
            productos.remove(indice);
        }
    }
    // ----------------------------------------------

    public void limpiar() {
        productos.clear();
    }

    public double calcularTotal() {
        double total = 0;

        for (Producto p : productos) {
            total += p.getPrecio();
        }

        return total;
    }

    public Producto[] obtenerProductosComoArray() {
        return productos.toArray(new Producto[0]);
    }

    public ArrayList<Producto> getProductos() {
        return productos;
    }
}

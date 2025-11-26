package logic;

import java.util.ArrayList;

public class Carrito {

    private ArrayList<Producto> productos;

    public Carrito() {
        productos = new ArrayList<>();
    }

    // ------------------------------------------------------------
    // Agregar producto al carrito
    // ------------------------------------------------------------
    public void agregarProducto(Producto producto) {
        productos.add(producto);
    }

    // ------------------------------------------------------------
    // Quitar producto por índice (opcional)
    // ------------------------------------------------------------
    public void eliminarProducto(int index) {
        if (index >= 0 && index < productos.size()) {
            productos.remove(index);
        }
    }

    // ------------------------------------------------------------
    // Obtener productos como array (para factura)
    // ------------------------------------------------------------
    public Producto[] obtenerProductosComoArray() {
        return productos.toArray(new Producto[0]);
    }

    // ------------------------------------------------------------
    // Calcular total usando recursividad
    // ------------------------------------------------------------
    public double calcularTotal() {
        return sumaRecursiva(0);
    }

    private double sumaRecursiva(int indice) {
        if (indice == productos.size()) return 0;
        return productos.get(indice).getPrecio() + sumaRecursiva(indice + 1);
    }

    // ------------------------------------------------------------
    // Limpiar carrito
    // ------------------------------------------------------------
    public void limpiar() {
        productos.clear();
    }

    // ------------------------------------------------------------
    // Obtener productos como lista (para mostrar en JTable)
    // ------------------------------------------------------------
    public ArrayList<Producto> getProductos() {
        return productos;
    }
}

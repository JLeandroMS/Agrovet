package repository;

import logic.Producto;

public class ProductoRepository {

    private Producto[] productos;

    public ProductoRepository() {
        productos = new Producto[]{
                new Producto(1, "Concentrado Perro", 12000, 10),
                new Producto(2, "Concentrado Gato", 9500, 8),
                new Producto(3, "Pipeta Antipulgas", 7500, 15),
                new Producto(4, "Shampoo Mascota", 6000, 12),
                new Producto(5, "Collar Mascota", 3500, 6),
                new Producto(6, "Juguete Pelota", 2500, 20),
                new Producto(7, "Garrapaticida", 8200, 5),
                new Producto(8, "Vitaminas", 5000, 9),
                new Producto(9, "Cortaúñas", 3000, 7),
                new Producto(10, "Plato Mascota", 2800, 11)
        };
    }

    public Producto[] getAll() {
        return productos;
    }

    public Producto buscarPorId(int id) {
        for (Producto p : productos) {
            if (p.getId() == id) return p;
        }
        return null;
    }

    // --------------------------------------------------------------
    // 🔥 MÉTODO NUEVO: REDUCIR STOCK
    // --------------------------------------------------------------
    public boolean reducirStock(int id, int cantidad) {
        Producto p = buscarPorId(id);
        if (p == null) return false;

        int actual = p.getStock();
        if (actual < cantidad) return false;

        p.setStock(actual - cantidad);
        return true;
    }

    // --------------------------------------------------------------
    // 🔥 MÉTODO NUEVO: AUMENTAR STOCK
    // --------------------------------------------------------------
    public boolean aumentarStock(int id, int cantidad) {
        Producto p = buscarPorId(id);
        if (p == null) return false;

        p.setStock(p.getStock() + cantidad);
        return true;
    }
}

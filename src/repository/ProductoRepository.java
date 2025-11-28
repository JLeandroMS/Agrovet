package repository;

import logic.Producto;

public class ProductoRepository {

    private Producto[] productos;

    public ProductoRepository() {

        productos = new Producto[]{
                new Producto(1, "Concentrado Perro", 12000),
                new Producto(2, "Concentrado Gato", 9500),
                new Producto(3, "Pipeta Antipulgas", 7500),
                new Producto(4, "Shampoo Mascota", 6000),
                new Producto(5, "Collar Mascota", 3500),
                new Producto(6, "Juguete Pelota", 2500),
                new Producto(7, "Garrapaticida", 8200),
                new Producto(8, "Vitaminas", 5000),
                new Producto(9, "Cortaúñas", 3000),
                new Producto(10, "Plato Mascota", 2800)
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
}

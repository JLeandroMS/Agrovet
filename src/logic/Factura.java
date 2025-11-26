package logic;

public class Factura {

    private String cedula;
    private Producto[] productos;

    public Factura(String cedula, Producto[] productos) {
        this.cedula = cedula;
        this.productos = productos;
    }

    public String getCedula() {
        return cedula;
    }

    public Producto[] getProductos() {
        return productos;
    }

    public double total() {
        double t = 0;
        for (Producto p : productos) {
            t += p.getPrecio();
        }
        return t;
    }
}

package logic;

public class Producto {

    private int id;
    private String nombre;
    private double precio;
    private int stock;  // 🟢 Nuevo

  public Producto(int id, String nombre, double precio, int stock) {
    this.id = id;
    this.nombre = nombre;
    this.precio = precio;
    this.stock = stock;
}
public Producto(int id, String nombre, double precio) {
    this(id, nombre, precio, 0); // stock por defecto
}



    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public double getPrecio() { return precio; }
    public int getStock() { return stock; }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public void reducirStock(int cantidad) {
        this.stock -= cantidad;
        if (this.stock < 0) this.stock = 0;
    }
}

package repository;

import logic.Producto;

import java.io.*;
import java.util.ArrayList;

public class ProductoRepository {

    private final String FILE_NAME = "productos.txt";
    private ArrayList<Producto> productos = new ArrayList<>();

    public ProductoRepository() {
        cargar();
    }

    private void cargar() {
        File archivo = new File(FILE_NAME);

        if (!archivo.exists()) {
            cargarProductosPorDefecto();
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String linea;

            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split(";");
                int id = Integer.parseInt(partes[0]);
                String nombre = partes[1];
                double precio = Double.parseDouble(partes[2]);

                productos.add(new Producto(id, nombre, precio));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cargarProductosPorDefecto() {
        productos.add(new Producto(1, "Concentrado Perros 20kg", 25.50));
        productos.add(new Producto(2, "Concentrado Gatos 10kg", 18.75));
        productos.add(new Producto(3, "Alimento Gallinas 40kg", 32.00));
        productos.add(new Producto(4, "Alimento Caballos 25kg", 45.00));
        productos.add(new Producto(5, "Pala Agrícola", 12.99));
        productos.add(new Producto(6, "Rastrillo Metálico", 9.99));
        productos.add(new Producto(7, "Guantes de Trabajo", 3.50));
        productos.add(new Producto(8, "Vacuna Antipulgas", 6.75));
        productos.add(new Producto(9, "Desparasitante Bovino", 14.30));
        productos.add(new Producto(10, "Vitaminas Mascotas", 7.20));

        guardar();
    }

    private void guardar() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_NAME))) {
            for (Producto p : productos) {
                pw.println(p.getId() + ";" + p.getNombre() + ";" + p.getPrecio());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Producto[] getAll() {
        return productos.toArray(new Producto[0]);
    }

    public Producto buscarPorId(int id) {
        for (Producto p : productos) {
            if (p.getId() == id) return p;
        }
        return null;
    }
}

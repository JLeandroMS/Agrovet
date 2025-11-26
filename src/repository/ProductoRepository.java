package repository;

import logic.Producto;

import java.io.*;
import java.util.ArrayList;

public class ProductoRepository {

    private final String FILE_PATH = "data/productos.txt";
    private Producto[] productos;

    public ProductoRepository() {
        cargarDesdeArchivo();
    }

    private void cargarDesdeArchivo() {
        ArrayList<Producto> listaTemporal = new ArrayList<>();
        File archivo = new File(FILE_PATH);

        try {
            // Si no existe el archivo, lo crea
            if (!archivo.exists()) {
                archivo.getParentFile().mkdirs();
                archivo.createNewFile();
            }

            BufferedReader br = new BufferedReader(new FileReader(archivo));
            String line;

            // Lee línea por línea
            while ((line = br.readLine()) != null) {
                Producto p = Producto.fromTxtLine(line);
                if (p != null) listaTemporal.add(p);
            }
            br.close();

            // Convierte el ArrayList a un array estático
            productos = listaTemporal.toArray(new Producto[0]);

        } catch (IOException e) {
            e.printStackTrace();
            productos = new Producto[0];
        }
    }

    public Producto[] getAll() {
        return productos;
    }

    public Producto buscarPorId(int id) {
        for (Producto p : productos) {
            if (p.getId() == id) {
                return p;
            }
        }
        return null;
    }

    public void agregarProducto(Producto nuevo) {
        // Expande el array manualmente (manejo dinámico de memoria)
        Producto[] nuevoArray = new Producto[productos.length + 1];

        for (int i = 0; i < productos.length; i++) {
            nuevoArray[i] = productos[i];
        }

        nuevoArray[productos.length] = nuevo;
        productos = nuevoArray;

        // Guarda los cambios en el archivo TXT
        guardarTodosEnArchivo();
    }

    private void guardarTodosEnArchivo() {
        File archivo = new File(FILE_PATH);
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(archivo));

            for (Producto p : productos) {
                bw.write(p.toTxtLine());
                bw.newLine();
            }
            bw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

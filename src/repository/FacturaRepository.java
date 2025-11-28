package repository;

import logic.Factura;
import logic.Producto;

import java.io.*;
import java.util.ArrayList;

public class FacturaRepository {

    private final String FILE_PATH = "facturas.txt";

    public FacturaRepository() {
        File f = new File(FILE_PATH);
        try {
            if (!f.exists()) f.createNewFile();
        } catch (IOException e) {
            System.out.println("Error creando facturas.txt");
        }
    }

    // ========================================================
    //                     GUARDAR FACTURA
    // ========================================================
    public void guardar(Factura factura) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH, true))) {

            bw.write("CEDULA:" + factura.getCedula());
            bw.newLine();

            for (Producto p : factura.getProductos()) {
                bw.write("PRODUCTO:" + p.getId() + "," + p.getNombre() + "," + p.getPrecio());
                bw.newLine();
            }

            bw.write("END");
            bw.newLine();
            bw.newLine();

        } catch (IOException e) {
            System.out.println("ERROR guardando factura: " + e.getMessage());
        }
    }

    // Alias opcional (si tu código llama guardarFactura)
    public void guardarFactura(Factura f) {
        guardar(f);
    }

    // ========================================================
    //               BUSCAR FACTURAS POR CÉDULA
    // ========================================================
    public ArrayList<String> buscarPorCedula(String cedulaBuscada) {
        ArrayList<String> encontrados = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {

            String linea;
            StringBuilder facturaActual = new StringBuilder();
            boolean coincideCedula = false;

            while ((linea = br.readLine()) != null) {

                if (linea.startsWith("CEDULA:")) {
                    if (linea.equals("CEDULA:" + cedulaBuscada)) {
                        coincideCedula = true;
                        facturaActual = new StringBuilder();
                        facturaActual.append(linea).append("\n");
                    } else {
                        coincideCedula = false;
                    }
                } else if (linea.equals("END")) {
                    if (coincideCedula) {
                        encontrados.add(facturaActual.toString());
                    }
                } else {
                    if (coincideCedula) {
                        facturaActual.append(linea).append("\n");
                    }
                }
            }

        } catch (IOException e) {
            System.out.println("ERROR leyendo facturas: " + e.getMessage());
        }

        return encontrados;
    }
}

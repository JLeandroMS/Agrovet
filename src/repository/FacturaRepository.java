package repository;

import logic.Factura;
import logic.Producto;

import java.io.*;
import java.util.ArrayList;

public class FacturaRepository {

    private final String FILE_NAME = "facturas.txt";

    public FacturaRepository() {
        File f = new File(FILE_NAME);

        try {
            if (!f.exists()) {
                f.createNewFile();
            }
        } catch (Exception e) {
            System.out.println("Error creando archivo de facturas: " + e.getMessage());
        }
    }

    // =====================================================
    // GUARDAR FACTURA
    // =====================================================
    public void guardarFactura(Factura factura) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, true))) {

            writer.write("CEDULA:" + factura.getCedula());
            writer.newLine();

            for (Producto p : factura.getProductos()) {
                writer.write(p.getId() + "," + p.getNombre() + "," + p.getPrecio());
                writer.newLine();
            }

            writer.write("TOTAL:" + factura.total());
            writer.newLine();
            writer.write("---");
            writer.newLine();

        } catch (Exception e) {
            System.out.println("Error guardando factura: " + e.getMessage());
        }
    }

    // =====================================================
    // BUSCAR FACTURAS POR CEDULA
    // =====================================================
    public ArrayList<String> buscarPorCedula(String cedula) {

        ArrayList<String> resultados = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String linea;
            boolean facturaEncontrada = false;
            StringBuilder facturaCompleta = new StringBuilder();

            while ((linea = reader.readLine()) != null) {

                if (linea.startsWith("CEDULA:" + cedula)) {
                    facturaEncontrada = true;
                    facturaCompleta = new StringBuilder();
                    facturaCompleta.append(linea).append("\n");
                } else if (linea.equals("---")) {
                    if (facturaEncontrada) {
                        resultados.add(facturaCompleta.toString());
                        facturaEncontrada = false;
                    }
                } else if (facturaEncontrada) {
                    facturaCompleta.append(linea).append("\n");
                }
            }

        } catch (Exception e) {
            System.out.println("Error buscando facturas: " + e.getMessage());
        }

        return resultados;
    }
}

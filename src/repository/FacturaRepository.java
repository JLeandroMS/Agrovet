package repository;

import logic.Factura;

import java.io.*;
import java.util.ArrayList;

public class FacturaRepository {

    private final String FILE_PATH = "data/facturas.txt";
    private Factura[] facturas;

    public FacturaRepository() {
        cargarFacturas();
    }

    // ------------------------------------------------------------
    // Cargar todas las facturas desde el TXT
    // ------------------------------------------------------------
    private void cargarFacturas() {
        ArrayList<String> bloque = new ArrayList<>();
        ArrayList<Factura> lista = new ArrayList<>();

        File archivo = new File(FILE_PATH);

        try {
            if (!archivo.exists()) {
                archivo.getParentFile().mkdirs();
                archivo.createNewFile();
            }

            BufferedReader br = new BufferedReader(new FileReader(archivo));
            String line;

            while ((line = br.readLine()) != null) {
                if (line.startsWith("-------------------------")) {
                    // Se terminó una factura → convertir bloque a factura
                    Factura f = Factura.fromTxt(bloque.toArray(new String[0]));
                    if (f != null) lista.add(f);
                    bloque.clear();
                } else {
                    bloque.add(line);
                }
            }

            br.close();

            // Convertir ArrayList → array
            facturas = lista.toArray(new Factura[0]);

        } catch (IOException e) {
            e.printStackTrace();
            facturas = new Factura[0];
        }
    }

    public Factura[] getAll() {
        return facturas;
    }

    // ------------------------------------------------------------
    // Guardar UNA sola factura al final del archivo
    // ------------------------------------------------------------
    public void guardarFactura(Factura factura) {
        File archivo = new File(FILE_PATH);
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(archivo, true));
            bw.write(factura.toTxt());
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Actualizamos el array interno
        cargarFacturas();
    }

    // ------------------------------------------------------------
    // Buscar facturas por cédula usando recursividad
    // ------------------------------------------------------------
    public Factura[] buscarPorCedula(String cedula) {
        ArrayList<Factura> resultado = new ArrayList<>();
        buscarRecursivo(cedula, 0, resultado);
        return resultado.toArray(new Factura[0]);
    }

    private void buscarRecursivo(String cedula, int index, ArrayList<Factura> lista) {
        if (index == facturas.length) return;

        if (facturas[index].getCedulaCliente().equalsIgnoreCase(cedula)) {
            lista.add(facturas[index]);
        }

        buscarRecursivo(cedula, index + 1, lista);
    }
}

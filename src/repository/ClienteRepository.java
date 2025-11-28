package repository;

import logic.Cliente;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteRepository {

    private final List<Cliente> clientes = new ArrayList<>();
    private final String FILE_NAME = "clientes.txt";

    public ClienteRepository() {
        cargarDesdeTXT();
    }

    // CREATE (con validación de duplicados)
    public boolean add(Cliente cliente) {
        for (Cliente c : clientes) {
            if (c.getCedula().equalsIgnoreCase(cliente.getCedula())) {
                return false; // Ya existe
            }
        }

        clientes.add(cliente);
        guardarEnTXT();
        return true;
    }

    // READ ALL
    public List<Cliente> getAll() {
        return clientes;
    }

    // READ ONE
    public Cliente getById(String cedula) {
        for (Cliente c : clientes) {
            if (c.getCedula().equalsIgnoreCase(cedula)) {
                return c;
            }
        }
        return null;
    }

    // UPDATE
    public boolean update(Cliente actualizado) {
        for (int i = 0; i < clientes.size(); i++) {
            if (clientes.get(i).getCedula().equalsIgnoreCase(actualizado.getCedula())) {
                clientes.set(i, actualizado);
                guardarEnTXT();
                return true;
            }
        }
        return false;
    }

    // DELETE
    public boolean delete(String cedula) {
        boolean eliminado = clientes.removeIf(c -> c.getCedula().equalsIgnoreCase(cedula));
        if (eliminado) {
            guardarEnTXT();
        }
        return eliminado;
    }

    // =========================================================
    // GUARDAR AUTOMÁTICAMENTE EN TXT
    // =========================================================
    private void guardarEnTXT() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_NAME))) {

            for (Cliente c : clientes) {
                writer.println(c.getCedula() + "|" +
                               c.getNombre() + "|" +
                               c.getTelefono());
            }

        } catch (Exception ex) {
            System.out.println("Error guardando clientes.txt: " + ex.getMessage());
        }
    }

    // =========================================================
    // CARGAR AUTOMÁTICAMENTE DESDE TXT
    // =========================================================
    private void cargarDesdeTXT() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            return; // no existe aún
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String linea;

            while ((linea = reader.readLine()) != null) {
                String[] partes = linea.split("\\|");

                if (partes.length == 3) {
                    String cedula = partes[0];
                    String nombre = partes[1];
                    String telefono = partes[2];

                    clientes.add(new Cliente(cedula, nombre, telefono));
                }
            }

        } catch (Exception ex) {
            System.out.println("Error cargando clientes.txt: " + ex.getMessage());
        }
    }
}

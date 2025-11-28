package repository;

import logic.Empleado;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class EmpleadoRepository {

    private final List<Empleado> empleados = new ArrayList<>();
    private final String FILE_NAME = "empleados.txt";

    public EmpleadoRepository() {
        cargarDesdeTXT();
    }

    // CREATE
    public boolean add(Empleado empleado) {
        // Validar duplicados por cédula
        for (Empleado e : empleados) {
            if (e.getCedula().equalsIgnoreCase(empleado.getCedula())) {
                return false;
            }
        }

        empleados.add(empleado);
        guardarEnTXT();
        return true;
    }

    // READ ALL
    public List<Empleado> getAll() {
        return empleados;
    }

    // READ ONE
    public Empleado getById(String cedula) {
        for (Empleado e : empleados) {
            if (e.getCedula().equalsIgnoreCase(cedula)) {
                return e;
            }
        }
        return null;
    }

    // UPDATE
    public boolean update(Empleado actualizado) {
        for (int i = 0; i < empleados.size(); i++) {
            if (empleados.get(i).getCedula().equalsIgnoreCase(actualizado.getCedula())) {
                empleados.set(i, actualizado);
                guardarEnTXT();
                return true;
            }
        }
        return false;
    }

    // DELETE
    public boolean delete(String cedula) {
        boolean eliminado = empleados.removeIf(e -> e.getCedula().equalsIgnoreCase(cedula));
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

            for (Empleado e : empleados) {
                writer.println(e.getCedula() + "|" +
                               e.getNombre() + "|" +
                               e.getTelefono() + "|" +
                               e.getOcupacion() + "|" +
                               e.getTipo() + "|" +
                               e.getSalario());
            }

        } catch (Exception ex) {
            System.out.println("Error guardando empleados.txt: " + ex.getMessage());
        }
    }

    // =========================================================
    // CARGAR AUTOMÁTICAMENTE DESDE TXT
    // =========================================================
    private void cargarDesdeTXT() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            return; // No hay archivo aún
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String linea;

            while ((linea = reader.readLine()) != null) {
                String[] partes = linea.split("\\|");

                if (partes.length == 6) {
                    String cedula = partes[0];
                    String nombre = partes[1];
                    String telefono = partes[2];
                    String ocupacion = partes[3];
                    String tipo = partes[4];
                    double salario = Double.parseDouble(partes[5]);

                    empleados.add(new Empleado(cedula, nombre, telefono, ocupacion, tipo, salario));
                }
            }

        } catch (Exception ex) {
            System.out.println("Error cargando empleados.txt: " + ex.getMessage());
        }
    }
}

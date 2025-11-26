package repository;

import logic.Empleado;
import java.util.ArrayList;
import java.util.List;

public class EmpleadoRepository {

    private final List<Empleado> empleados = new ArrayList<>();

    // CREATE
    public void add(Empleado empleado) {
        empleados.add(empleado);
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
    public boolean update(Empleado empleadoActualizado) {
        for (int i = 0; i < empleados.size(); i++) {
            if (empleados.get(i).getCedula().equalsIgnoreCase(empleadoActualizado.getCedula())) {
                empleados.set(i, empleadoActualizado);
                return true;
            }
        }
        return false;
    }

    // DELETE
    public boolean delete(String cedula) {
        return empleados.removeIf(e -> e.getCedula().equalsIgnoreCase(cedula));
    }
}

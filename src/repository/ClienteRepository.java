package repository;

import logic.Cliente;
import java.util.ArrayList;
import java.util.List;

public class ClienteRepository {

    private final List<Cliente> clientes = new ArrayList<>();

    // CREATE
    public void add(Cliente cliente) {
        clientes.add(cliente);
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
    public boolean update(Cliente clienteActualizado) {
        for (int i = 0; i < clientes.size(); i++) {
            if (clientes.get(i).getCedula().equalsIgnoreCase(clienteActualizado.getCedula())) {
                clientes.set(i, clienteActualizado);
                return true;
            }
        }
        return false;
    }

    // DELETE
    public boolean delete(String cedula) {
        return clientes.removeIf(c -> c.getCedula().equalsIgnoreCase(cedula));
    }
}

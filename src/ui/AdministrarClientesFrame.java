package ui;

import logic.Cliente;
import repository.ClienteRepository;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class AdministrarClientesFrame extends JFrame {

    private ClienteRepository clienteRepo;
    private JTable tablaClientes;
    private JTextField txtCedula, txtNombre, txtEdad, txtTelefono;
    private JButton btnAgregar, btnModificar, btnEliminar;

    public AdministrarClientesFrame(ClienteRepository repo) {
        this.clienteRepo = repo;
        setTitle("Administrar Clientes");
        setSize(600, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        initComponents();
        cargarTabla();
    }

    private void initComponents() {
        // Panel formulario con GridBagLayout
        JPanel panelFormulario = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        int y = 0;

        // Cédula
        gbc.gridx = 0;
        gbc.gridy = y;
        panelFormulario.add(new JLabel("Cédula:"), gbc);
        gbc.gridx = 1;
        txtCedula = new JTextField(15);
        panelFormulario.add(txtCedula, gbc);

        // Nombre
        y++;
        gbc.gridx = 0;
        gbc.gridy = y;
        panelFormulario.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1;
        txtNombre = new JTextField(15);
        panelFormulario.add(txtNombre, gbc);

        // Edad
        y++;
        gbc.gridx = 0;
        gbc.gridy = y;
        panelFormulario.add(new JLabel("Edad:"), gbc);
        gbc.gridx = 1;
        txtEdad = new JTextField(15);
        panelFormulario.add(txtEdad, gbc);

        // Teléfono
        y++;
        gbc.gridx = 0;
        gbc.gridy = y;
        panelFormulario.add(new JLabel("Teléfono:"), gbc);
        gbc.gridx = 1;
        txtTelefono = new JTextField(15);
        panelFormulario.add(txtTelefono, gbc);

        // Botones
        y++;
        gbc.gridx = 0;
        gbc.gridy = y;
        btnAgregar = new JButton("Agregar");
        btnAgregar.addActionListener(e -> agregarCliente());
        panelFormulario.add(btnAgregar, gbc);

        gbc.gridx = 1;
        btnModificar = new JButton("Modificar");
        btnModificar.addActionListener(e -> modificarCliente());
        panelFormulario.add(btnModificar, gbc);

        y++;
        gbc.gridx = 0;
        gbc.gridy = y;
        gbc.gridwidth = 2;
        btnEliminar = new JButton("Eliminar");
        btnEliminar.addActionListener(e -> eliminarCliente());
        panelFormulario.add(btnEliminar, gbc);

        add(panelFormulario, BorderLayout.NORTH);

        // Tabla
        tablaClientes = new JTable();
        tablaClientes.setModel(new DefaultTableModel(
                new Object[][]{},
                new String[]{"Cédula", "Nombre", "Edad", "Teléfono"}
        ));

        tablaClientes.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int fila = tablaClientes.getSelectedRow();
                    if (fila != -1) {
                        txtCedula.setText((String) tablaClientes.getValueAt(fila, 0));
                        txtNombre.setText((String) tablaClientes.getValueAt(fila, 1));
                        txtEdad.setText(tablaClientes.getValueAt(fila, 2).toString());
                        txtTelefono.setText((String) tablaClientes.getValueAt(fila, 3));
                    }
                }
            }
        });

        add(new JScrollPane(tablaClientes), BorderLayout.CENTER);
    }

    private void cargarTabla() {
        DefaultTableModel model = (DefaultTableModel) tablaClientes.getModel();
        model.setRowCount(0);
        for (Cliente c : clienteRepo.getAll()) {
            model.addRow(new Object[]{
                    c.getCedula(),
                    c.getNombre(),
                    c.getEdad(),
                    c.getTelefono()
            });
        }
    }

    private void agregarCliente() {
        String cedula = txtCedula.getText().trim();
        String nombre = txtNombre.getText().trim();
        String edadStr = txtEdad.getText().trim();
        String telefono = txtTelefono.getText().trim();

        if (cedula.isEmpty() || nombre.isEmpty() || edadStr.isEmpty() || telefono.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Complete todos los campos.");
            return;
        }

        if (clienteRepo.getById(cedula) != null) {
            JOptionPane.showMessageDialog(this, "La cédula ya existe.");
            return;
        }

        int edad;
        try {
            edad = Integer.parseInt(edadStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Edad inválida.");
            return;
        }

        Cliente cliente = new Cliente(cedula, nombre, edad, telefono);
        clienteRepo.add(cliente);
        cargarTabla();
        limpiarFormulario();
    }

    private void modificarCliente() {
        String cedula = txtCedula.getText().trim();
        Cliente existente = clienteRepo.getById(cedula);
        if (existente == null) {
            JOptionPane.showMessageDialog(this, "Cliente no encontrado.");
            return;
        }

        String nombre = txtNombre.getText().trim();
        String edadStr = txtEdad.getText().trim();
        String telefono = txtTelefono.getText().trim();

        if (nombre.isEmpty() || edadStr.isEmpty() || telefono.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Complete todos los campos.");
            return;
        }

        int edad;
        try {
            edad = Integer.parseInt(edadStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Edad inválida.");
            return;
        }

        existente.setNombre(nombre);
        existente.setEdad(edad);
        existente.setTelefono(telefono);
        clienteRepo.update(existente);
        cargarTabla();
        limpiarFormulario();
    }

    private void eliminarCliente() {
        String cedula = txtCedula.getText().trim();
        if (cedula.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese la cédula para eliminar.");
            return;
        }

        if (clienteRepo.delete(cedula)) {
            cargarTabla();
            limpiarFormulario();
        } else {
            JOptionPane.showMessageDialog(this, "Cliente no encontrado.");
        }
    }

    private void limpiarFormulario() {
        txtCedula.setText("");
        txtNombre.setText("");
        txtEdad.setText("");
        txtTelefono.setText("");
    }
}

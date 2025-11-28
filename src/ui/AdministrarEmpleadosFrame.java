package ui;

import logic.Empleado;
import repository.EmpleadoRepository;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class AdministrarEmpleadosFrame extends JFrame {

    private EmpleadoRepository empleadoRepo;
    private JTable tablaEmpleados;
    private JTextField txtCedula, txtNombre, txtTelefono, txtOcupacion, txtSalario;
    private JComboBox<String> cbTipoEmpleado;
    private JButton btnAgregar, btnModificar, btnEliminar;

    public AdministrarEmpleadosFrame(EmpleadoRepository repo) {
        this.empleadoRepo = repo;
        setTitle("Administrar Empleados");
        setSize(700, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        initComponents();
        cargarTabla();
    }

    private void initComponents() {

        JPanel panelFormulario = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        int y = 0;

        // ===============================
        // CÉDULA
        // ===============================
        gbc.gridx = 0; gbc.gridy = y;
        panelFormulario.add(new JLabel("Cédula:"), gbc);
        gbc.gridx = 1;
        txtCedula = new JTextField(15);
        panelFormulario.add(txtCedula, gbc);

        // ===============================
        // NOMBRE
        // ===============================
        y++;
        gbc.gridx = 0; gbc.gridy = y;
        panelFormulario.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1;
        txtNombre = new JTextField(15);
        panelFormulario.add(txtNombre, gbc);

        // ===============================
        // TELÉFONO
        // ===============================
        y++;
        gbc.gridx = 0; gbc.gridy = y;
        panelFormulario.add(new JLabel("Teléfono:"), gbc);
        gbc.gridx = 1;
        txtTelefono = new JTextField(15);
        panelFormulario.add(txtTelefono, gbc);

        // ===============================
        // OCUPACIÓN
        // ===============================
        y++;
        gbc.gridx = 0; gbc.gridy = y;
        panelFormulario.add(new JLabel("Ocupación:"), gbc);
        gbc.gridx = 1;
        txtOcupacion = new JTextField(15);
        panelFormulario.add(txtOcupacion, gbc);

        // ===============================
        // TIPO EMPLEADO
        // ===============================
        y++;
        gbc.gridx = 0; gbc.gridy = y;
        panelFormulario.add(new JLabel("Tipo:"), gbc);
        gbc.gridx = 1;
        cbTipoEmpleado = new JComboBox<>(new String[]{"Veterinario", "Cashier"});
        panelFormulario.add(cbTipoEmpleado, gbc);

        // ===============================
        // SALARIO
        // ===============================
        y++;
        gbc.gridx = 0; gbc.gridy = y;
        panelFormulario.add(new JLabel("Salario:"), gbc);
        gbc.gridx = 1;
        txtSalario = new JTextField(15);
        panelFormulario.add(txtSalario, gbc);

        // ===============================
        // BOTONES
        // ===============================

        y++;
        gbc.gridx = 0; gbc.gridy = y; gbc.gridwidth = 2;
        JPanel panelBotones = new JPanel(new FlowLayout());

        btnAgregar = new JButton("Agregar");
        btnAgregar.addActionListener(e -> agregarEmpleado());
        panelBotones.add(btnAgregar);

        btnModificar = new JButton("Modificar");
        btnModificar.addActionListener(e -> modificarEmpleado());
        panelBotones.add(btnModificar);

        btnEliminar = new JButton("Eliminar");
        btnEliminar.addActionListener(e -> eliminarEmpleado());
        panelBotones.add(btnEliminar);

        panelFormulario.add(panelBotones, gbc);

        add(panelFormulario, BorderLayout.NORTH);

        // ===============================
        // TABLA
        // ===============================
        tablaEmpleados = new JTable();
        tablaEmpleados.setModel(new DefaultTableModel(
                new Object[][]{},
                new String[]{"Cédula", "Nombre", "Teléfono", "Ocupación", "Tipo", "Salario"}
        ));

        tablaEmpleados.setRowHeight(25);

        tablaEmpleados.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int fila = tablaEmpleados.getSelectedRow();
                if (fila != -1) {
                    txtCedula.setText((String) tablaEmpleados.getValueAt(fila, 0));
                    txtNombre.setText((String) tablaEmpleados.getValueAt(fila, 1));
                    txtTelefono.setText((String) tablaEmpleados.getValueAt(fila, 2));
                    txtOcupacion.setText((String) tablaEmpleados.getValueAt(fila, 3));
                    cbTipoEmpleado.setSelectedItem(tablaEmpleados.getValueAt(fila, 4));
                    txtSalario.setText(tablaEmpleados.getValueAt(fila, 5).toString());
                }
            }
        });

        add(new JScrollPane(tablaEmpleados), BorderLayout.CENTER);
    }

    // ==========================================================
    // CARGAR TABLA
    // ==========================================================
    private void cargarTabla() {
        DefaultTableModel model = (DefaultTableModel) tablaEmpleados.getModel();
        model.setRowCount(0);

        for (Empleado e : empleadoRepo.getAll()) {
            model.addRow(new Object[]{
                    e.getCedula(),
                    e.getNombre(),
                    e.getTelefono(),
                    e.getOcupacion(),
                    e.getTipo(),
                    e.getSalario()
            });
        }
    }

    // ==========================================================
    // VALIDACIONES
    // ==========================================================
    private boolean validarCampos(String cedula, String nombre, String telefono, String salarioStr) {

        if (!cedula.matches("\\d+")) {
            JOptionPane.showMessageDialog(this, "La cédula solo debe contener números.");
            return false;
        }

        if (!nombre.matches("[a-zA-Z ]+")) {
            JOptionPane.showMessageDialog(this, "El nombre solo debe contener letras.");
            return false;
        }

        if (!telefono.matches("\\d+")) {
            JOptionPane.showMessageDialog(this, "El teléfono solo debe contener números.");
            return false;
        }

        try {
            Double.parseDouble(salarioStr);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "El salario debe ser un número válido.");
            return false;
        }

        return true;
    }

    // ==========================================================
    // AGREGAR
    // ==========================================================
    private void agregarEmpleado() {
        String cedula = txtCedula.getText().trim();
        String nombre = txtNombre.getText().trim();
        String telefono = txtTelefono.getText().trim();
        String ocupacion = txtOcupacion.getText().trim();
        String tipo = (String) cbTipoEmpleado.getSelectedItem();
        String salarioStr = txtSalario.getText().trim();

        if (!validarCampos(cedula, nombre, telefono, salarioStr)) return;

        if (empleadoRepo.getById(cedula) != null) {
            JOptionPane.showMessageDialog(this, "Ya existe un empleado con esta cédula.");
            return;
        }

        double salario = Double.parseDouble(salarioStr);

        Empleado empleado = new Empleado(cedula, nombre, telefono, ocupacion, tipo, salario);

        empleadoRepo.add(empleado);
        cargarTabla();

        JOptionPane.showMessageDialog(this, "Empleado agregado con éxito.");
    }

    // ==========================================================
    // MODIFICAR
    // ==========================================================
    private void modificarEmpleado() {
        String cedula = txtCedula.getText().trim();
        String nombre = txtNombre.getText().trim();
        String telefono = txtTelefono.getText().trim();
        String ocupacion = txtOcupacion.getText().trim();
        String tipo = (String) cbTipoEmpleado.getSelectedItem();
        String salarioStr = txtSalario.getText().trim();

        if (!validarCampos(cedula, nombre, telefono, salarioStr)) return;

        if (empleadoRepo.getById(cedula) == null) {
            JOptionPane.showMessageDialog(this, "No se encontró el empleado para modificar.");
            return;
        }

        double salario = Double.parseDouble(salarioStr);

        Empleado empleado = new Empleado(cedula, nombre, telefono, ocupacion, tipo, salario);

        empleadoRepo.update(empleado);
        cargarTabla();

        JOptionPane.showMessageDialog(this, "Empleado modificado con éxito.");
    }

    // ==========================================================
    // ELIMINAR
    // ==========================================================
    private void eliminarEmpleado() {
        String cedula = txtCedula.getText().trim();

        if (cedula.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese la cédula a eliminar.");
            return;
        }

        if (empleadoRepo.delete(cedula)) {
            cargarTabla();
            JOptionPane.showMessageDialog(this, "Empleado eliminado.");
        } else {
            JOptionPane.showMessageDialog(this, "No se encontró el empleado.");
        }
    }
}

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
        setSize(700, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        initComponents();
        cargarTabla();
    }

    private void initComponents() {

        JPanel panelFormulario = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        int y = 0;

        // Cédula
        gbc.gridx = 0; gbc.gridy = y;
        panelFormulario.add(new JLabel("Cédula:"), gbc);
        gbc.gridx = 1;
        txtCedula = new JTextField(15);
        panelFormulario.add(txtCedula, gbc);

        // Nombre
        y++;
        gbc.gridx = 0; gbc.gridy = y;
        panelFormulario.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1;
        txtNombre = new JTextField(15);
        panelFormulario.add(txtNombre, gbc);

        // Teléfono
        y++;
        gbc.gridx = 0; gbc.gridy = y;
        panelFormulario.add(new JLabel("Teléfono:"), gbc);
        gbc.gridx = 1;
        txtTelefono = new JTextField(15);
        panelFormulario.add(txtTelefono, gbc);

        // Ocupación
        y++;
        gbc.gridx = 0; gbc.gridy = y;
        panelFormulario.add(new JLabel("Ocupación:"), gbc);
        gbc.gridx = 1;
        txtOcupacion = new JTextField(15);
        panelFormulario.add(txtOcupacion, gbc);

        // Tipo
        y++;
        gbc.gridx = 0; gbc.gridy = y;
        panelFormulario.add(new JLabel("Tipo:"), gbc);
        gbc.gridx = 1;
        cbTipoEmpleado = new JComboBox<>(new String[]{"Veterinario", "Casher"});
        panelFormulario.add(cbTipoEmpleado, gbc);

        // Salario
        y++;
        gbc.gridx = 0; gbc.gridy = y;
        panelFormulario.add(new JLabel("Salario:"), gbc);
        gbc.gridx = 1;
        txtSalario = new JTextField(15);
        panelFormulario.add(txtSalario, gbc);

        // BOTONES
        y++;
        gbc.gridx = 0; gbc.gridy = y;
        btnAgregar = new JButton("Agregar");
        btnAgregar.addActionListener(e -> agregarEmpleado());
        panelFormulario.add(btnAgregar, gbc);

        gbc.gridx = 1;
        btnModificar = new JButton("Modificar");
        btnModificar.addActionListener(e -> modificarEmpleado());
        panelFormulario.add(btnModificar, gbc);

        y++;
        gbc.gridx = 0; gbc.gridy = y; gbc.gridwidth = 2;
        btnEliminar = new JButton("Eliminar");
        btnEliminar.addActionListener(e -> eliminarEmpleado());
        panelFormulario.add(btnEliminar, gbc);

        add(panelFormulario, BorderLayout.NORTH);

        // TABLA
        tablaEmpleados = new JTable();
        tablaEmpleados.setModel(new DefaultTableModel(
                new Object[][]{},
                new String[]{"Cédula", "Nombre", "Teléfono", "Ocupación", "Tipo", "Salario"}
        ));

        tablaEmpleados.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 1) {
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
            }
        });

        add(new JScrollPane(tablaEmpleados), BorderLayout.CENTER);
    }

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

    private void agregarEmpleado() {
        String cedula = txtCedula.getText().trim();
        String nombre = txtNombre.getText().trim();
        String telefono = txtTelefono.getText().trim();
        String ocupacion = txtOcupacion.getText().trim();
        String tipo = (String) cbTipoEmpleado.getSelectedItem();
        String salarioStr = txtSalario.getText().trim();

        if (cedula.isEmpty() || nombre.isEmpty() || telefono.isEmpty() ||
                ocupacion.isEmpty() || salarioStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Complete todos los campos.");
            return;
        }

        double salario;

        try {
            salario = Double.parseDouble(salarioStr);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "El salario debe ser un número válido.");
            return;
        }

        Empleado empleado = new Empleado(cedula, nombre, telefono, ocupacion, tipo, salario);
        empleadoRepo.add(empleado);
        cargarTabla();

        JOptionPane.showMessageDialog(this, "Empleado agregado con éxito.");
    }

    private void modificarEmpleado() {

        String cedula = txtCedula.getText().trim();
        String nombre = txtNombre.getText().trim();
        String telefono = txtTelefono.getText().trim();
        String ocupacion = txtOcupacion.getText().trim();
        String tipo = (String) cbTipoEmpleado.getSelectedItem();
        String salarioStr = txtSalario.getText().trim();

        if (cedula.isEmpty() || nombre.isEmpty() || telefono.isEmpty() ||
                ocupacion.isEmpty() || salarioStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Complete todos los campos.");
            return;
        }

        double salario;

        try {
            salario = Double.parseDouble(salarioStr);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "El salario debe ser un número válido.");
            return;
        }

        Empleado empleado = new Empleado(cedula, nombre, telefono, ocupacion, tipo, salario);

        if (empleadoRepo.update(empleado)) {
            cargarTabla();
            JOptionPane.showMessageDialog(this, "Empleado modificado con éxito.");
        } else {
            JOptionPane.showMessageDialog(this, "No se encontró el empleado.");
        }
    }

    private void eliminarEmpleado() {
        String cedula = txtCedula.getText().trim();

        if (cedula.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese la cédula del empleado que desea eliminar.");
            return;
        }

        if (empleadoRepo.delete(cedula)) {
            cargarTabla();
            JOptionPane.showMessageDialog(this, "Empleado eliminado con éxito.");
        } else {
            JOptionPane.showMessageDialog(this, "No se encontró el empleado.");
        }
    }
}

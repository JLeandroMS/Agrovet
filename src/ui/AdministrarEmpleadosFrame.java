package ui;

import logic.Empleado;
import repository.EmpleadoRepository;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class AdministrarEmpleadosFrame extends JFrame {

    private EmpleadoRepository empleadoRepo;
    private JTable tablaEmpleados;
    private JTextField txtCedula, txtNombre, txtEdad, txtTelefono, txtOcupacion, txtSalario;
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

        // Ocupación
        y++;
        gbc.gridx = 0;
        gbc.gridy = y;
        panelFormulario.add(new JLabel("Ocupación:"), gbc);
        gbc.gridx = 1;
        txtOcupacion = new JTextField(15);
        panelFormulario.add(txtOcupacion, gbc);

        // Tipo de empleado
        y++;
        gbc.gridx = 0;
        gbc.gridy = y;
        panelFormulario.add(new JLabel("Tipo:"), gbc);
        gbc.gridx = 1;
        cbTipoEmpleado = new JComboBox<>(new String[]{"Veterinario", "Casher"});
        panelFormulario.add(cbTipoEmpleado, gbc);

        // Salario
        y++;
        gbc.gridx = 0;
        gbc.gridy = y;
        panelFormulario.add(new JLabel("Salario:"), gbc);
        gbc.gridx = 1;
        txtSalario = new JTextField(15);
        panelFormulario.add(txtSalario, gbc);

        // Botones
        y++;
        gbc.gridx = 0;
        gbc.gridy = y;
        btnAgregar = new JButton("Agregar");
        btnAgregar.addActionListener(e -> agregarEmpleado());
        panelFormulario.add(btnAgregar, gbc);

        gbc.gridx = 1;
        btnModificar = new JButton("Modificar");
        btnModificar.addActionListener(e -> modificarEmpleado());
        panelFormulario.add(btnModificar, gbc);

        y++;
        gbc.gridx = 0;
        gbc.gridy = y;
        gbc.gridwidth = 2;
        btnEliminar = new JButton("Eliminar");
        btnEliminar.addActionListener(e -> eliminarEmpleado());
        panelFormulario.add(btnEliminar, gbc);

        add(panelFormulario, BorderLayout.NORTH);

        // Tabla
        tablaEmpleados = new JTable();
        tablaEmpleados.setModel(new DefaultTableModel(
                new Object[][]{},
                new String[]{"Cédula", "Nombre", "Edad", "Teléfono", "Ocupación", "Tipo", "Salario"}
        ));

        tablaEmpleados.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int fila = tablaEmpleados.getSelectedRow();
                    if (fila != -1) {
                        txtCedula.setText((String) tablaEmpleados.getValueAt(fila, 0));
                        txtNombre.setText((String) tablaEmpleados.getValueAt(fila, 1));
                        txtEdad.setText(tablaEmpleados.getValueAt(fila, 2).toString());
                        txtTelefono.setText((String) tablaEmpleados.getValueAt(fila, 3));
                        txtOcupacion.setText((String) tablaEmpleados.getValueAt(fila, 4));
                        cbTipoEmpleado.setSelectedItem(tablaEmpleados.getValueAt(fila, 5));
                        txtSalario.setText(tablaEmpleados.getValueAt(fila, 6).toString());
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
                    e.getEdad(),
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
        String edadStr = txtEdad.getText().trim();
        String telefono = txtTelefono.getText().trim();
        String ocupacion = txtOcupacion.getText().trim();
        String tipo = (String) cbTipoEmpleado.getSelectedItem();
        String salarioStr = txtSalario.getText().trim();

        if (cedula.isEmpty() || nombre.isEmpty() || edadStr.isEmpty() || telefono.isEmpty() ||
                ocupacion.isEmpty() || salarioStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Complete todos los campos.");
            return;
        }

        if (empleadoRepo.getById(cedula) != null) {
            JOptionPane.showMessageDialog(this, "La cédula ya existe.");
            return;
        }

        int edad;
        double salario;
        try {
            edad = Integer.parseInt(edadStr);
            salario = Double.parseDouble(salarioStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Edad o salario inválido.");
            return;
        }

        Empleado emp = new Empleado(cedula, nombre, edad, telefono, ocupacion, tipo, salario);
        empleadoRepo.add(emp);
        cargarTabla();
        limpiarFormulario();
    }

    private void modificarEmpleado() {
        String cedula = txtCedula.getText().trim();
        Empleado existente = empleadoRepo.getById(cedula);
        if (existente == null) {
            JOptionPane.showMessageDialog(this, "Empleado no encontrado.");
            return;
        }

        String nombre = txtNombre.getText().trim();
        String edadStr = txtEdad.getText().trim();
        String telefono = txtTelefono.getText().trim();
        String ocupacion = txtOcupacion.getText().trim();
        String tipo = (String) cbTipoEmpleado.getSelectedItem();
        String salarioStr = txtSalario.getText().trim();

        if (nombre.isEmpty() || edadStr.isEmpty() || telefono.isEmpty() ||
                ocupacion.isEmpty() || salarioStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Complete todos los campos.");
            return;
        }

        int edad;
        double salario;
        try {
            edad = Integer.parseInt(edadStr);
            salario = Double.parseDouble(salarioStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Edad o salario inválido.");
            return;
        }

        existente.setNombre(nombre);
        existente.setEdad(edad);
        existente.setTelefono(telefono);
        existente.setOcupacion(ocupacion);
        existente.setTipo(tipo);
        existente.setSalario(salario);

        empleadoRepo.update(existente);
        cargarTabla();
        limpiarFormulario();
    }

    private void eliminarEmpleado() {
        String cedula = txtCedula.getText().trim();
        if (cedula.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese la cédula para eliminar.");
            return;
        }

        if (empleadoRepo.delete(cedula)) {
            cargarTabla();
            limpiarFormulario();
        } else {
            JOptionPane.showMessageDialog(this, "Empleado no encontrado.");
        }
    }

    private void limpiarFormulario() {
        txtCedula.setText("");
        txtNombre.setText("");
        txtEdad.setText("");
        txtTelefono.setText("");
        txtOcupacion.setText("");
        txtSalario.setText("");
        cbTipoEmpleado.setSelectedIndex(0);
    }
}

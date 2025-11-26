package ui;

import logic.Cliente;
import repository.ClienteRepository;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
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
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(15, 15));
        getContentPane().setBackground(new Color(245, 245, 245));

        initComponents();
        cargarTabla();
    }

    private void initComponents() {

        // ============================
        // PANEL CONTENEDOR TIPO TARJETA
        // ============================
        JPanel card = new JPanel(new GridBagLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
                new EmptyBorder(20, 20, 20, 20)
        ));
        card.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        Font labelFont = new Font("Segoe UI", Font.BOLD, 16);
        Font inputFont = new Font("Segoe UI", Font.PLAIN, 16);
        Font buttonFont = new Font("Segoe UI", Font.BOLD, 16);

        int y = 0;

        // CÉDULA
        gbc.gridx = 0; gbc.gridy = y;
        JLabel lblCedula = new JLabel("Cédula:");
        lblCedula.setFont(labelFont);
        card.add(lblCedula, gbc);

        gbc.gridx = 1;
        txtCedula = new JTextField(22);
        txtCedula.setFont(inputFont);
        txtCedula.setBorder(BorderFactory.createLineBorder(new Color(160, 160, 160)));
        card.add(txtCedula, gbc);

        // NOMBRE
        y++;
        gbc.gridx = 0; gbc.gridy = y;
        JLabel lblNombre = new JLabel("Nombre:");
        lblNombre.setFont(labelFont);
        card.add(lblNombre, gbc);

        gbc.gridx = 1;
        txtNombre = new JTextField(22);
        txtNombre.setFont(inputFont);
        txtNombre.setBorder(BorderFactory.createLineBorder(new Color(160, 160, 160)));
        card.add(txtNombre, gbc);

        // EDAD
        y++;
        gbc.gridx = 0; gbc.gridy = y;
        JLabel lblEdad = new JLabel("Edad:");
        lblEdad.setFont(labelFont);
        card.add(lblEdad, gbc);

        gbc.gridx = 1;
        txtEdad = new JTextField(22);
        txtEdad.setFont(inputFont);
        txtEdad.setBorder(BorderFactory.createLineBorder(new Color(160, 160, 160)));
        card.add(txtEdad, gbc);

        // TELÉFONO
        y++;
        gbc.gridx = 0; gbc.gridy = y;
        JLabel lblTelefono = new JLabel("Teléfono:");
        lblTelefono.setFont(labelFont);
        card.add(lblTelefono, gbc);

        gbc.gridx = 1;
        txtTelefono = new JTextField(22);
        txtTelefono.setFont(inputFont);
        txtTelefono.setBorder(BorderFactory.createLineBorder(new Color(160, 160, 160)));
        card.add(txtTelefono, gbc);

        // ============================
        // BOTONES MODERNOS
        // ============================

        Color btnColor = new Color(52, 152, 219);
        Color btnEliminarColor = new Color(231, 76, 60);

        y++;
        gbc.gridx = 0; gbc.gridy = y; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        panelBotones.setBackground(Color.WHITE);

        btnAgregar = new JButton("Agregar");
        btnAgregar.setFont(buttonFont);
        btnAgregar.setBackground(btnColor);
        btnAgregar.setForeground(Color.WHITE);
        btnAgregar.setFocusPainted(false);
        btnAgregar.setBorder(new EmptyBorder(8, 20, 8, 20));
        btnAgregar.addActionListener(e -> agregarCliente());
        panelBotones.add(btnAgregar);

        btnModificar = new JButton("Modificar");
        btnModificar.setFont(buttonFont);
        btnModificar.setBackground(new Color(46, 204, 113));
        btnModificar.setForeground(Color.WHITE);
        btnModificar.setFocusPainted(false);
        btnModificar.setBorder(new EmptyBorder(8, 20, 8, 20));
        btnModificar.addActionListener(e -> modificarCliente());
        panelBotones.add(btnModificar);

        btnEliminar = new JButton("Eliminar");
        btnEliminar.setFont(buttonFont);
        btnEliminar.setBackground(btnEliminarColor);
        btnEliminar.setForeground(Color.WHITE);
        btnEliminar.setFocusPainted(false);
        btnEliminar.setBorder(new EmptyBorder(8, 20, 8, 20));
        btnEliminar.addActionListener(e -> eliminarCliente());
        panelBotones.add(btnEliminar);

        card.add(panelBotones, gbc);

        add(card, BorderLayout.NORTH);

        // ============================
        // TABLA MODERNA
        // ============================

        tablaClientes = new JTable();
        tablaClientes.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tablaClientes.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tablaClientes.setRowHeight(26);

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

        JScrollPane scroll = new JScrollPane(tablaClientes);
        scroll.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(scroll, BorderLayout.CENTER);
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

        int edad;
        try {
            edad = Integer.parseInt(edadStr);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Edad inválida.");
            return;
        }

        if (clienteRepo.getById(cedula) != null) {
            JOptionPane.showMessageDialog(this, "La cédula ya existe.");
            return;
        }

        clienteRepo.add(new Cliente(cedula, nombre, edad, telefono));
        cargarTabla();
        limpiar();
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
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Edad inválida.");
            return;
        }

        existente.setNombre(nombre);
        existente.setEdad(edad);
        existente.setTelefono(telefono);

        clienteRepo.update(existente);
        cargarTabla();
        limpiar();
    }

    private void eliminarCliente() {
        String cedula = txtCedula.getText().trim();

        if (clienteRepo.delete(cedula)) {
            cargarTabla();
            limpiar();
        } else {
            JOptionPane.showMessageDialog(this, "Cliente no encontrado.");
        }
    }

    private void limpiar() {
        txtCedula.setText("");
        txtNombre.setText("");
        txtEdad.setText("");
        txtTelefono.setText("");
    }
}

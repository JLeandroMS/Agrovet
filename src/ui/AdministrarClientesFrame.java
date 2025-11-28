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
    private JTextField txtCedula, txtNombre, txtTelefono;
    private JButton btnAgregar, btnModificar, btnEliminar;

    public AdministrarClientesFrame(ClienteRepository repo) {
        this.clienteRepo = repo;
        setTitle("Administrar Clientes");
        setSize(700, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(15, 15));

        initComponents();
        cargarTabla();
    }

    private void initComponents() {

        JPanel card = new JPanel(new GridBagLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(20, 20, 20, 20)
        ));
        card.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        Font labelFont = new Font("Segoe UI", Font.BOLD, 15);
        Font inputFont = new Font("Segoe UI", Font.PLAIN, 15);
        Font buttonFont = new Font("Segoe UI", Font.BOLD, 15);

        int y = 0;

        // CÉDULA
        gbc.gridx = 0; gbc.gridy = y;
        JLabel lblCedula = new JLabel("Cédula:");
        lblCedula.setFont(labelFont);
        card.add(lblCedula, gbc);

        gbc.gridx = 1;
        txtCedula = new JTextField(20);
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
        txtNombre = new JTextField(20);
        txtNombre.setFont(inputFont);
        txtNombre.setBorder(BorderFactory.createLineBorder(new Color(160, 160, 160)));
        card.add(txtNombre, gbc);

        // TELÉFONO
        y++;
        gbc.gridx = 0; gbc.gridy = y;
        JLabel lblTelefono = new JLabel("Teléfono:");
        lblTelefono.setFont(labelFont);
        card.add(lblTelefono, gbc);

        gbc.gridx = 1;
        txtTelefono = new JTextField(20);
        txtTelefono.setFont(inputFont);
        txtTelefono.setBorder(BorderFactory.createLineBorder(new Color(160, 160, 160)));
        card.add(txtTelefono, gbc);

        // BOTONES
        y++;
        gbc.gridx = 0; gbc.gridy = y; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        panelBotones.setBackground(Color.WHITE);

        btnAgregar = new JButton("Agregar");
        btnAgregar.setFont(buttonFont);
        btnAgregar.setBackground(new Color(52, 152, 219));
        btnAgregar.setForeground(Color.WHITE);
        btnAgregar.setBorder(new EmptyBorder(10, 20, 10, 20));
        btnAgregar.addActionListener(e -> agregarCliente());
        panelBotones.add(btnAgregar);

        btnModificar = new JButton("Modificar");
        btnModificar.setFont(buttonFont);
        btnModificar.setBackground(new Color(46, 204, 113));
        btnModificar.setForeground(Color.WHITE);
        btnModificar.setBorder(new EmptyBorder(10, 20, 10, 20));
        btnModificar.addActionListener(e -> modificarCliente());
        panelBotones.add(btnModificar);

        btnEliminar = new JButton("Eliminar");
        btnEliminar.setFont(buttonFont);
        btnEliminar.setBackground(new Color(231, 76, 60));
        btnEliminar.setForeground(Color.WHITE);
        btnEliminar.setBorder(new EmptyBorder(10, 20, 10, 20));
        btnEliminar.addActionListener(e -> eliminarCliente());
        panelBotones.add(btnEliminar);

        card.add(panelBotones, gbc);

        add(card, BorderLayout.NORTH);

        // TABLA
        tablaClientes = new JTable();
        tablaClientes.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tablaClientes.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tablaClientes.setRowHeight(26);

        tablaClientes.setModel(new DefaultTableModel(
                new Object[][]{},
                new String[]{"Cédula", "Nombre", "Teléfono"}
        ));

        tablaClientes.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int fila = tablaClientes.getSelectedRow();
                    if (fila != -1) {
                        txtCedula.setText((String) tablaClientes.getValueAt(fila, 0));
                        txtNombre.setText((String) tablaClientes.getValueAt(fila, 1));
                        txtTelefono.setText((String) tablaClientes.getValueAt(fila, 2));
                    }
                }
            }
        });

        add(new JScrollPane(tablaClientes), BorderLayout.CENTER);
    }

    // =====================================================================
    // VALIDACIONES
    // =====================================================================

    private boolean validarCedula(String cedula) {
        return cedula.matches("\\d+");
    }

    private boolean validarNombre(String nombre) {
        return nombre.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+");
    }

    private boolean validarTelefono(String telefono) {
        return telefono.matches("\\d{8,12}");
    }

    private boolean error(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
        return false;
    }

    // =====================================================================
    // CRUD
    // =====================================================================

    private void cargarTabla() {
        DefaultTableModel model = (DefaultTableModel) tablaClientes.getModel();
        model.setRowCount(0);
        for (Cliente c : clienteRepo.getAll()) {
            model.addRow(new Object[]{
                    c.getCedula(),
                    c.getNombre(),
                    c.getTelefono()
            });
        }
    }

    private void agregarCliente() {
        String cedula = txtCedula.getText().trim();
        String nombre = txtNombre.getText().trim();
        String telefono = txtTelefono.getText().trim();

        if (cedula.isEmpty() || nombre.isEmpty() || telefono.isEmpty()) {
            error("Complete todos los campos.");
            return;
        }

        if (!validarCedula(cedula)) {
            error("La cédula debe contener solo números.");
            return;
        }

        if (!validarNombre(nombre)) {
            error("El nombre solo puede contener letras y espacios.");
            return;
        }

        if (!validarTelefono(telefono)) {
            error("El teléfono debe tener entre 8 y 12 dígitos numéricos.");
            return;
        }

        if (clienteRepo.getById(cedula) != null) {
            error("Ya existe un cliente con esa cédula.");
            return;
        }

        clienteRepo.add(new Cliente(cedula, nombre, telefono));
        cargarTabla();
        limpiar();
        JOptionPane.showMessageDialog(this, "Cliente agregado con éxito.");
    }

    private void modificarCliente() {
        String cedula = txtCedula.getText().trim();
        Cliente existente = clienteRepo.getById(cedula);

        if (existente == null) {
            error("Cliente no encontrado.");
            return;
        }

        String nombre = txtNombre.getText().trim();
        String telefono = txtTelefono.getText().trim();

        if (!validarNombre(nombre)) {
            error("Nombre inválido.");
            return;
        }

        if (!validarTelefono(telefono)) {
            error("Teléfono inválido.");
            return;
        }

        existente.setNombre(nombre);
        existente.setTelefono(telefono);

        clienteRepo.update(existente);
        cargarTabla();
        limpiar();
        JOptionPane.showMessageDialog(this, "Cliente modificado con éxito.");
    }

    private void eliminarCliente() {
        String cedula = txtCedula.getText().trim();

        if (clienteRepo.delete(cedula)) {
            cargarTabla();
            limpiar();
            JOptionPane.showMessageDialog(this, "Cliente eliminado.");
        } else {
            error("Cliente no encontrado.");
        }
    }

    private void limpiar() {
        txtCedula.setText("");
        txtNombre.setText("");
        txtTelefono.setText("");
    }
}

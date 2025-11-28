package ui;

import logic.Carrito;
import logic.Producto;
import logic.Factura;
import logic.Cliente;
import repository.ProductoRepository;
import repository.FacturaRepository;
import repository.ClienteRepository;
import repository.EmpleadoRepository;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class MainMenu extends javax.swing.JFrame {

    private ProductoRepository productoRepo;
    private FacturaRepository facturaRepo;
    private Carrito carrito;
    private ClienteRepository clienteRepo;
    private EmpleadoRepository empleadoRepo;

    public MainMenu() {
        initComponents();

        productoRepo = new ProductoRepository();
        facturaRepo = new FacturaRepository();
        carrito = new Carrito();
        clienteRepo = new ClienteRepository();
        empleadoRepo = new EmpleadoRepository();

        cargarTablaProductos();
        cargarCarrito();
    }
    

    // ----------------------------------------------------------
    //          CARGAR TABLA DE PRODUCTOS
    // ----------------------------------------------------------
    private void cargarTablaProductos() {
        Producto[] productos = productoRepo.getAll();

        DefaultTableModel model = (DefaultTableModel) tablaProductos.getModel();
        model.setRowCount(0);

        for (Producto p : productos) {
            model.addRow(new Object[]{
                    p.getId(),
                    p.getNombre(),
                    p.getPrecio()
            });
        }
    }

    // ----------------------------------------------------------
    //          CARGAR TABLA DEL CARRITO (AGRUPADA)
    // ----------------------------------------------------------
    private void cargarCarrito() {
        DefaultTableModel model = (DefaultTableModel) tablaCarrito.getModel();
        model.setRowCount(0);

        java.util.List<Producto> productos = carrito.getProductos();

        java.util.Map<String, Double> precios = new java.util.HashMap<>();
        java.util.Map<String, Integer> cantidades = new java.util.HashMap<>();

        for (Producto p : productos) {
            if (p == null) continue;

            String nombre = p.getNombre();
            precios.put(nombre, precios.getOrDefault(nombre, 0.0) + p.getPrecio());
            cantidades.put(nombre, cantidades.getOrDefault(nombre, 0) + 1);
        }

        for (String nombre : precios.keySet()) {
            model.addRow(new Object[]{
                    cantidades.get(nombre),
                    nombre,
                    precios.get(nombre)
            });
        }
    }


    // ----------------------------------------------------------
    //                 MÉTODOS BOTONES
    // ----------------------------------------------------------
    private void agregarAlCarrito() {
        int fila = tablaProductos.getSelectedRow();

        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto.");
            return;
        }

        int id = (int) tablaProductos.getValueAt(fila, 0);
        Producto p = productoRepo.buscarPorId(id);

        if (p != null) {
            carrito.agregarProducto(p);
            cargarCarrito();
        }
    }

    private void eliminarProductoCarrito() {
        int fila = tablaCarrito.getSelectedRow();

        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto del carrito.");
            return;
        }

        carrito.eliminar(fila);
        cargarCarrito();
    }

    private void limpiarCarrito() {
        carrito.limpiar();
        cargarCarrito();
    }

    private void pagar() {
        String cedula = txtCedula.getText().trim();

        if (cedula.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese la cédula del cliente.");
            return;
        }

        Cliente cliente = clienteRepo.getById(cedula);
        if (cliente == null) {
            int opcion = JOptionPane.showConfirmDialog(this, "Cliente no encontrado. ¿Desea agregarlo?",
                    "Cliente no encontrado", JOptionPane.YES_NO_OPTION);
            if (opcion == JOptionPane.YES_OPTION) {
                abrirAdministrarClientes(cedula);
            }
            return;
        }

        if (carrito.getProductos().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El carrito está vacío.");
            return;
        }

        Factura factura = new Factura(cedula, carrito.obtenerProductosComoArray());
        facturaRepo.guardar(factura);


        carrito.limpiar();
        cargarCarrito();
        txtCedula.setText("");

        JOptionPane.showMessageDialog(this, "Pago realizado con éxito. Factura generada.");
    }

    private void abrirBuscadorFacturas() {
        BuscarFacturaFrame b = new BuscarFacturaFrame();
        b.setVisible(true);
    }

    private void abrirAdministrarClientes(String cedula) {
        AdministrarClientesFrame frame = new AdministrarClientesFrame(clienteRepo);
        frame.setVisible(true);
    }

    private void abrirAdministrarEmpleados() {
        AdministrarEmpleadosFrame frame = new AdministrarEmpleadosFrame(empleadoRepo);
        frame.setVisible(true);
    }


    // ----------------------------------------------------------
    //                      INTERFAZ
    // ----------------------------------------------------------
    @SuppressWarnings("unchecked")
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tablaProductos = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        tablaCarrito = new javax.swing.JTable();

        btnAgregar = new POSButton("Agregar");
        btnEliminar = new POSButton("Eliminar");
        btnLimpiar = new POSButton("Limpiar");
        btnPagar = new POSButton("💰 PAGAR");
        btnBuscarFactura = new POSButton("Facturas");
        btnAdministrarClientes = new POSButton("Clientes");
        btnAdministrarEmpleados = new POSButton("Empleados");

        txtCedula = new javax.swing.JTextField();
        lblCedula = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Agroveterinaria - Menú Principal");

        // 👇 ADAPTADO PARA PANTALLA 1024×768
        setSize(1024, 768);
        setResizable(false);
        setLocationRelativeTo(null);

        // TABLA PRODUCTOS
        tablaProductos.setFont(new Font("Dialog", Font.BOLD, 18));
        tablaProductos.setRowHeight(40);

        tablaProductos.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{},
                new String[]{"ID", "Nombre", "Precio"}
        ));

        jScrollPane1.setViewportView(tablaProductos);
        jScrollPane1.setPreferredSize(new Dimension(900, 200));

        // TABLA CARRITO (CORREGIDA)
        tablaCarrito.setFont(new Font("Dialog", Font.BOLD, 18));
        tablaCarrito.setRowHeight(40);

        tablaCarrito.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{},
                new String[]{"Cantidad", "Producto", "Subtotal"}
        ));

        jScrollPane2.setViewportView(tablaCarrito);
        jScrollPane2.setPreferredSize(new Dimension(900, 200));

        // ACCIONES
        btnAgregar.addActionListener(evt -> agregarAlCarrito());
        btnEliminar.addActionListener(evt -> eliminarProductoCarrito());
        btnLimpiar.addActionListener(evt -> limpiarCarrito());
        btnPagar.addActionListener(evt -> pagar());
        btnBuscarFactura.addActionListener(evt -> abrirBuscadorFacturas());
        btnAdministrarClientes.addActionListener(evt -> abrirAdministrarClientes(""));
        btnAdministrarEmpleados.addActionListener(evt -> abrirAdministrarEmpleados());

        lblCedula.setText("Cédula Cliente:");
        lblCedula.setFont(new Font("Dialog", Font.BOLD, 18));

        txtCedula.setFont(new Font("Dialog", Font.BOLD, 18));
        txtCedula.setPreferredSize(new Dimension(200, 35));

        // LAYOUT (AJUSTADO A 1024×768)
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);

        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup().addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 980, Short.MAX_VALUE)
                                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 980, Short.MAX_VALUE)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(btnAgregar)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(btnEliminar)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(btnLimpiar)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(btnBuscarFactura)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(btnAdministrarClientes)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(btnAdministrarEmpleados))
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(lblCedula)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(txtCedula, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(btnPagar)))
                                .addContainerGap())
        );

        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup().addContainerGap()
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(btnAgregar)
                                        .addComponent(btnEliminar)
                                        .addComponent(btnLimpiar)
                                        .addComponent(btnBuscarFactura)
                                        .addComponent(btnAdministrarClientes)
                                        .addComponent(btnAdministrarEmpleados))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(15)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(lblCedula)
                                        .addComponent(txtCedula, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(btnPagar))
                                .addContainerGap(15, Short.MAX_VALUE))
        );
    }


    // VARIABLES
    private javax.swing.JButton btnAgregar;
    private javax.swing.JButton btnEliminar;
    private javax.swing.JButton btnLimpiar;
    private javax.swing.JButton btnPagar;
    private javax.swing.JButton btnBuscarFactura;
    private javax.swing.JButton btnAdministrarClientes;
    private javax.swing.JButton btnAdministrarEmpleados;
    private javax.swing.JLabel lblCedula;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable tablaProductos;
    private javax.swing.JTable tablaCarrito;
    private javax.swing.JTextField txtCedula;

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new MainMenu().setVisible(true));
    }
}


// ===============================================================
//                 BOTÓN PERSONALIZADO ESTILO POS
// ===============================================================
class POSButton extends JButton {

    public POSButton(String text) {
        super(text);
        setFont(new Font("Dialog", Font.BOLD, 18));
        setBackground(new Color(168, 208, 141));
        setForeground(Color.BLACK);
        setFocusPainted(false);
        setBorderPainted(false);
        setOpaque(true);

        // 👇 BOTONES MÁS COMPACTOS (PARA 1024PX DE ANCHO)
        setPreferredSize(new Dimension(150, 55));

        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(120, 150, 100), 2),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));

        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                setBackground(new Color(140, 230, 150));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                setBackground(new Color(168, 208, 141));
            }
        });
    }
}

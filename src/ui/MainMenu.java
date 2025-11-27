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
    //          CARGAR TABLA DEL CARRITO
    // ----------------------------------------------------------
   private void cargarCarrito() {
    DefaultTableModel model = (DefaultTableModel) tablaCarrito.getModel();
    model.setRowCount(0);

    java.util.List<Producto> productos = carrito.getProductos();  // ← AQUÍ EL CAMBIO

    // Mapa temporal para agrupar por nombre
    java.util.Map<String, Double> mapaPrecios = new java.util.HashMap<>();
    java.util.Map<String, Integer> mapaCantidad = new java.util.HashMap<>();

    for (Producto p : productos) {
        if (p == null) continue;

        String nombre = p.getNombre();
        double precio = p.getPrecio();

        // Sumar precios de productos repetidos
        mapaPrecios.put(nombre, mapaPrecios.getOrDefault(nombre, 0.0) + precio);

        // Contar cuántos hay
        mapaCantidad.put(nombre, mapaCantidad.getOrDefault(nombre, 0) + 1);
    }

    // Mostrar productos agrupados
    for (String nombre : mapaPrecios.keySet()) {
        double precioTotal = mapaPrecios.get(nombre);
        int cantidad = mapaCantidad.get(nombre);

        model.addRow(new Object[]{
                cantidad,       // Cantidad
                nombre,
                precioTotal     // Precio total sumado
        });
    }
}


    // ----------------------------------------------------------
    //                 MÉTODOS DE BOTONES
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
            int opcion = JOptionPane.showConfirmDialog(this,
                    "Cliente no encontrado. ¿Desea agregarlo?", "Cliente no encontrado",
                    JOptionPane.YES_NO_OPTION);
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
        facturaRepo.guardarFactura(factura);

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

        // BOTONES ESTILO POS
        btnAgregar = new POSButton("Agregar al Carrito");
        btnEliminar = new POSButton("Eliminar Producto");
        btnLimpiar = new POSButton("Limpiar Carrito");
        btnPagar = new POSButton("💰 PAGAR");
        btnBuscarFactura = new POSButton("Buscar Facturas");
        btnAdministrarClientes = new POSButton("Administrar Clientes");
        btnAdministrarEmpleados = new POSButton("Administrar Empleados");

        txtCedula = new javax.swing.JTextField();
        lblCedula = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Agroveterinaria - Menú Principal");
        setSize(1000, 850);
        setResizable(true);

        //-----------------------------------------------------------
        // TABLA PRODUCTOS (GRANDE + SCROLL)
        //-----------------------------------------------------------
        tablaProductos.setFont(new Font("Dialog", Font.BOLD, 18));
        tablaProductos.setRowHeight(45);

        tablaProductos.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{},
                new String[]{"ID", "Nombre", "Precio"}
        ));

        // Aumentar ancho de columnas
        tablaProductos.getColumnModel().getColumn(0).setPreferredWidth(80);
        tablaProductos.getColumnModel().getColumn(1).setPreferredWidth(300);
        tablaProductos.getColumnModel().getColumn(2).setPreferredWidth(150);

        jScrollPane1.setViewportView(tablaProductos);
        jScrollPane1.setPreferredSize(new Dimension(900, 280));

        //-----------------------------------------------------------
        // TABLA CARRITO (GRANDE + SCROLL)
        //-----------------------------------------------------------
        tablaCarrito.setFont(new Font("Dialog", Font.BOLD, 18));
        tablaCarrito.setRowHeight(45);

        tablaCarrito.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{},
                new String[]{"ID", "Nombre", "Precio"}
        ));

        jScrollPane2.setViewportView(tablaCarrito);
        jScrollPane2.setPreferredSize(new Dimension(900, 280));

        //-----------------------------------------------------------
        // ACCIONES DE BOTONES
        //-----------------------------------------------------------
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

        //-----------------------------------------------------------
        // LAYOUT ORIGINAL (NO ALTERADO)
        //-----------------------------------------------------------
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);

        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup().addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 880, Short.MAX_VALUE)
                                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 880, Short.MAX_VALUE)
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
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(btnAgregar)
                                        .addComponent(btnEliminar)
                                        .addComponent(btnLimpiar)
                                        .addComponent(btnBuscarFactura)
                                        .addComponent(btnAdministrarClientes)
                                        .addComponent(btnAdministrarEmpleados))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(15)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(lblCedula)
                                        .addComponent(txtCedula, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(btnPagar))
                                .addContainerGap(20, Short.MAX_VALUE))
        );

        setLocationRelativeTo(null);
    }

    // ----------------------------------------------------------
    //                  VARIABLES
    // ----------------------------------------------------------
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
        setFont(new Font("Dialog", Font.BOLD, 20));
        setBackground(new Color(168, 208, 141));
        setForeground(Color.BLACK);
        setFocusPainted(false);
        setBorderPainted(false);
        setOpaque(true);
        setPreferredSize(new Dimension(220, 60)); // Botón grande POS
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

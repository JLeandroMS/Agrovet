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
import java.util.*;
import java.util.List;

/**
 * MainMenu - Full screen POS integrado con stock en productos.
 *
 * Requiere que exista la clase Carrito con métodos:
 *   List<Producto> getProductos()
 *   void agregarProducto(Producto p)
 *   void eliminar(int index)  // elimina en la lista interna por índice
 *   void limpiar()
 *
 * Y FacturaRepository.guardar(Factura) (se asume presente).
 */
public class MainMenu extends JFrame {

    private final ProductoRepository productoRepo;
    private final FacturaRepository facturaRepo;
    private final Carrito carrito;
    private final ClienteRepository clienteRepo;
    private final EmpleadoRepository empleadoRepo;

    private JTable tablaProductos, tablaCarrito;
    private JTextField txtCedula;
    private JButton btnAgregar, btnEliminar, btnLimpiar, btnPagar;
    private JButton btnFacturas, btnClientes, btnEmpleados;
    private JLabel lblTotalValue;

    public MainMenu() {
        productoRepo = new ProductoRepository();
        facturaRepo = new FacturaRepository();
        carrito = new Carrito();
        clienteRepo = new ClienteRepository();
        empleadoRepo = new EmpleadoRepository();

        initComponents();

        cargarTablaProductos();
        cargarCarrito();
    }

    // ---------------------------------------------------------------------
    // CARGA DE TABLAS
    // ---------------------------------------------------------------------
    private void cargarTablaProductos() {
        DefaultTableModel model = (DefaultTableModel) tablaProductos.getModel();
        model.setRowCount(0);

        Producto[] productos = productoRepo.getAll();
        if (productos == null) return;

        for (Producto p : productos) {
            model.addRow(new Object[]{
                    p.getId(),
                    p.getNombre(),
                    "₡" + String.format("%,.0f", p.getPrecio()),
                    p.getStock()
            });
        }
    }

    private void cargarCarrito() {
        DefaultTableModel model = (DefaultTableModel) tablaCarrito.getModel();
        model.setRowCount(0);

        List<Producto> lista = carrito.getProductos();
        Map<Integer, Agregado> mapa = new LinkedHashMap<>();

        for (Producto p : lista) {
            if (p == null) continue;
            mapa.putIfAbsent(p.getId(), new Agregado(p.getId(), p.getNombre(), p.getPrecio()));
            mapa.get(p.getId()).add(p);
        }

        double total = 0;
        for (Agregado ag : mapa.values()) {
            model.addRow(new Object[]{
                    ag.cantidad,
                    ag.nombre,
                    "₡" + String.format("%,.0f", ag.subtotal)
            });
            total += ag.subtotal;
        }

        lblTotalValue.setText("₡" + String.format("%,.0f", total));
    }

    private Producto[] buildFacturaProductos() {
        return carrito.getProductos().toArray(new Producto[0]);
    }

    // ---------------------------------------------------------------------
    // ACCIONES
    // ---------------------------------------------------------------------
    private void agregarAlCarrito() {
        int fila = tablaProductos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto.");
            return;
        }

        Object idObj = tablaProductos.getValueAt(fila, 0);
        int id = (idObj instanceof Integer) ? (Integer) idObj : Integer.parseInt(idObj.toString());
        Producto p = productoRepo.buscarPorId(id);
        if (p == null) {
            JOptionPane.showMessageDialog(this, "Producto no encontrado.");
            return;
        }

        if (p.getStock() <= 0) {
            JOptionPane.showMessageDialog(this, "No hay stock disponible de: " + p.getNombre());
            return;
        }

        // agregamos al carrito (la reducción real de stock se hace al pagar)
        carrito.agregarProducto(p);
        cargarCarrito();
    }

    private void eliminarProductoCarrito() {
        int fila = tablaCarrito.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una fila en el carrito (agrupada).");
            return;
        }

        // al mostrar el carrito agrupado solo tenemos nombre; buscamos primer índice en la lista original
        String nombre = tablaCarrito.getValueAt(fila, 1).toString();

        List<Producto> lista = carrito.getProductos();
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getNombre().equals(nombre)) {
                carrito.eliminar(i);
                cargarCarrito();
                return;
            }
        }

        JOptionPane.showMessageDialog(this, "No se encontró el producto en el carrito interno.");
    }

    private void limpiarCarrito() {
        carrito.limpiar();
        cargarCarrito();
    }

    private void pagar() {
        if (carrito.getProductos().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El carrito está vacío.");
            return;
        }

        String ced = txtCedula.getText().trim();
        if (ced.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese la cédula del cliente.");
            return;
        }

        Cliente c = clienteRepo.getById(ced);
        if (c == null) {
            int opt = JOptionPane.showConfirmDialog(this, "Cliente no encontrado. ¿Desea registrarlo?", "Cliente", JOptionPane.YES_NO_OPTION);
            if (opt == JOptionPane.YES_OPTION) {
                new AdministrarClientesFrame(clienteRepo).setVisible(true);
            }
            return;
        }

        // Agrupar para conocer cantidades por producto (id)
        List<Producto> lista = carrito.getProductos();
        Map<Integer, Integer> cantidades = new LinkedHashMap<>();
        for (Producto p : lista) {
            cantidades.put(p.getId(), cantidades.getOrDefault(p.getId(), 0) + 1);
        }

        // Verificar stock suficiente para todos los productos
        for (Map.Entry<Integer, Integer> e : cantidades.entrySet()) {
            int id = e.getKey();
            int qty = e.getValue();
            Producto prod = productoRepo.buscarPorId(id);
            if (prod == null) {
                JOptionPane.showMessageDialog(this, "Producto en carrito no existe (id=" + id + ").");
                return;
            }
            if (prod.getStock() < qty) {
                JOptionPane.showMessageDialog(this, "Stock insuficiente para " + prod.getNombre() + ". Disponible: " + prod.getStock() + ", requerido: " + qty);
                return;
            }
        }

        // Reducir stock en repositorio
        for (Map.Entry<Integer, Integer> e : cantidades.entrySet()) {
            productoRepo.reducirStock(e.getKey(), e.getValue());
        }

        // Crear y guardar factura
        Factura factura = new Factura(ced, buildFacturaProductos());
        try {
            facturaRepo.guardar(factura); // se asume existencia
        } catch (Exception ex) {
            // si falla guardar, revertir stock (intento simple)
            for (Map.Entry<Integer, Integer> e : cantidades.entrySet()) {
                productoRepo.aumentarStock(e.getKey(), e.getValue());
            }
            JOptionPane.showMessageDialog(this, "Error al guardar factura: " + ex.getMessage());
            return;
        }

        JOptionPane.showMessageDialog(this, "Pago completado. Factura guardada.");
        carrito.limpiar();
        cargarCarrito();
        cargarTablaProductos(); // refrescar stock mostrado
    }

    // ---------------------------------------------------------------------
    // UI - FULL SCREEN + BOTONES PEGADOS
    // ---------------------------------------------------------------------
    private void initComponents() {

        // FULL SCREEN
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        setTitle("POS Veterinaria - Full Screen");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        Container cp = getContentPane();
        cp.setLayout(new BorderLayout());
        cp.setBackground(new Color(245, 247, 246));

        // HEADER
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(16, 185, 129));
        header.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));

        JLabel title = new JLabel("Agroveterinaria - POS", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 34));
        title.setForeground(Color.WHITE);
        header.add(title, BorderLayout.CENTER);

        cp.add(header, BorderLayout.NORTH);

        // LEFT MENU — buttons closer together
        JPanel left = new JPanel();
        left.setBackground(new Color(245, 247, 246));
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setPreferredSize(new Dimension(320, 0)); // más ancho para botones grandes

        btnAgregar   = new POSButton("  Agregar", new Color(16, 185, 129));
        btnEliminar  = new POSButton("  Eliminar", new Color(253, 126, 20));
        btnLimpiar   = new POSButton("  Limpiar", new Color(239, 68, 68));

        btnFacturas  = new POSButton("  Facturas", new Color(59, 130, 246));
        btnClientes  = new POSButton("  Registrar", new Color(59, 130, 246));
        btnEmpleados = new POSButton("️ Empleados", new Color(59, 130, 246));

        // ESPACIADO REDUCIDO
        int space = 8;

        left.add(Box.createVerticalStrut(12));
        left.add(btnAgregar);   left.add(Box.createVerticalStrut(space));
        left.add(btnEliminar);  left.add(Box.createVerticalStrut(space));
        left.add(btnLimpiar);   left.add(Box.createVerticalStrut(space + 6));
        left.add(btnFacturas);  left.add(Box.createVerticalStrut(space));
        left.add(btnClientes);  left.add(Box.createVerticalStrut(space));
        left.add(btnEmpleados);
        left.add(Box.createVerticalGlue());

        cp.add(left, BorderLayout.WEST);

        // CENTER CONTENT
        JPanel middle = new JPanel();
        middle.setLayout(new BoxLayout(middle, BoxLayout.Y_AXIS));
        middle.setBackground(new Color(245, 247, 246));
        middle.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // TABLA PRODUCTOS
        tablaProductos = new JTable(new DefaultTableModel(
                new Object[]{"ID", "Producto", "Precio", "Stock"}, 0
        ));

        tablaProductos.setRowHeight(45);
        tablaProductos.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        tablaProductos.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 22));

        JScrollPane spProductos = new JScrollPane(tablaProductos);
        spProductos.setPreferredSize(new Dimension(1200, 420));
        middle.add(spProductos);
        middle.add(Box.createVerticalStrut(10));

        // CARRITO
        tablaCarrito = new JTable(new DefaultTableModel(
                new Object[]{"Cant", "Producto", "Subtotal"}, 0
        ));
        tablaCarrito.setRowHeight(45);
        tablaCarrito.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        tablaCarrito.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 22));

        JScrollPane spCarrito = new JScrollPane(tablaCarrito);
        spCarrito.setPreferredSize(new Dimension(1200, 380));

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.add(spCarrito, BorderLayout.CENTER);

        // FOOTER CARRITO
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(Color.WHITE);

        JLabel lblTotal = new JLabel("TOTAL: ");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 24));

        lblTotalValue = new JLabel("₡0.00");
        lblTotalValue.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTotalValue.setForeground(new Color(16, 185, 129));

        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totalPanel.setBackground(Color.WHITE);
        totalPanel.add(lblTotal);
        totalPanel.add(lblTotalValue);

        footer.add(totalPanel, BorderLayout.NORTH);

        JPanel payPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        payPanel.setBackground(Color.WHITE);

        txtCedula = new JTextField();
        txtCedula.setPreferredSize(new Dimension(260, 40));
        txtCedula.setFont(new Font("Segoe UI", Font.PLAIN, 22));

        btnPagar = new POSButton(" Pagar", new Color(16, 185, 129));
        btnPagar.setPreferredSize(new Dimension(300, 64));

        payPanel.add(new JLabel("Cédula: "));
        payPanel.add(txtCedula);
        payPanel.add(Box.createHorizontalStrut(12));
        payPanel.add(btnPagar);

        footer.add(payPanel, BorderLayout.SOUTH);

        card.add(footer, BorderLayout.SOUTH);

        middle.add(card);
        cp.add(middle, BorderLayout.CENTER);

        // EVENTOS
        btnAgregar.addActionListener(e -> agregarAlCarrito());
        btnEliminar.addActionListener(e -> eliminarProductoCarrito());
        btnLimpiar.addActionListener(e -> limpiarCarrito());
        btnPagar.addActionListener(e -> pagar());

        btnFacturas.addActionListener(e -> new BuscarFacturaFrame().setVisible(true));
        btnClientes.addActionListener(e -> new AdministrarClientesFrame(clienteRepo).setVisible(true));
        btnEmpleados.addActionListener(e -> new AdministrarEmpleadosFrame(empleadoRepo).setVisible(true));
    }

    // ---------------------------------------------------------------------
    // CLASES AUXILIARES
    // ---------------------------------------------------------------------
    private static class Agregado {
        int cantidad = 0;
        String nombre;
        double subtotal = 0;
        int id;

        Agregado(int id, String nombre, double precioUnit) {
            this.id = id;
            this.nombre = nombre;
            this.subtotal = 0;
            this.cantidad = 0;
        }

        void add(Producto p) {
            cantidad++;
            subtotal += p.getPrecio();
        }
    }

    private static class POSButton extends JButton {
        POSButton(String text, Color bg) {
            super(text);
            setFont(new Font("Segoe UI", Font.BOLD, 26));
            setBackground(bg);
            setForeground(Color.WHITE);
            setFocusPainted(false);
            setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
            setPreferredSize(new Dimension(300, 84));
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainMenu m = new MainMenu();
            m.setVisible(true);
        });
    }
}

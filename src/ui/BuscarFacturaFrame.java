package ui;

import repository.FacturaRepository;
import javax.swing.*;

public class BuscarFacturaFrame extends javax.swing.JFrame {

    private FacturaRepository facturaRepo;

    public BuscarFacturaFrame() {
        facturaRepo = new FacturaRepository();
        initComponents();

        setSize(800, 600);
        setResizable(false);
    }

    private void buscar() {
        String cedula = txtCedula.getText().trim();

        if (cedula.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese una cédula.");
            return;
        }

        java.util.ArrayList<String> resultados = facturaRepo.buscarPorCedula(cedula);

        if (resultados.isEmpty()) {
            txtResultados.setText("");
            JOptionPane.showMessageDialog(this, "No se encontraron facturas para la cédula: " + cedula);
            return;
        }

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < resultados.size(); i++) {
            sb.append("FACTURA #").append(i + 1).append("\n");
            sb.append(resultados.get(i)).append("\n");
        }

        txtResultados.setText(sb.toString());
        txtResultados.setCaretPosition(0);
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        txtCedula = new javax.swing.JTextField();
        btnBuscar = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtResultados = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Buscar Facturas");

        jLabel1.setText("Cédula del Cliente:");

        btnBuscar.setText("Buscar");
        btnBuscar.addActionListener(evt -> buscar());

        txtResultados.setEditable(false);
        txtResultados.setColumns(20);
        txtResultados.setRows(10);
        jScrollPane1.setViewportView(txtResultados);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);

        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 780, Short.MAX_VALUE)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(jLabel1)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(txtCedula, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(btnBuscar)
                                                .addGap(0, 340, Short.MAX_VALUE)))
                                .addContainerGap())
        );

        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel1)
                                        .addComponent(txtCedula, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(btnBuscar))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 480, Short.MAX_VALUE)
                                .addContainerGap())
        );

        pack();
        setLocationRelativeTo(null);
    }

    private javax.swing.JButton btnBuscar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField txtCedula;
    private javax.swing.JTextArea txtResultados;
}

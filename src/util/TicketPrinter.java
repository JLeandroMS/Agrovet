package util;

import logic.Factura;
import logic.Producto;

import javax.print.*;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

public class TicketPrinter {

    private String printerName = "POS-80"; // <-- CAMBIAR POR EL NOMBRE DE TU IMPRESORA

    public TicketPrinter(String printerName) {
        this.printerName = printerName;
    }

    // ----------------------------------------------------------------
    //   IMPRIMIR FACTURA COMPLETA (Método principal)
    // ----------------------------------------------------------------
    public void imprimirFactura(Factura factura) {
        try {
            String ticket = construirTicket(factura);
            enviarAImpresora(ticket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ----------------------------------------------------------------
    //   Construye el texto del ticket estilo recibo térmico
    // ----------------------------------------------------------------
    private String construirTicket(Factura factura) {

        StringBuilder sb = new StringBuilder();

        sb.append("     *** AGROVETERINARIA ***\n");
        sb.append("         COMPROBANTE\n");
        sb.append("-------------------------------\n");
        sb.append("CEDULA: ").append(factura.getCedula()).append("\n");
        sb.append("-------------------------------\n");

        double total = 0;

        for (Producto p : factura.getProductos()) {
            sb.append(p.getNombre()).append("\n");
            sb.append("    ₡").append(String.format("%,.0f", p.getPrecio())).append("\n");
            total += p.getPrecio();
        }

        sb.append("-------------------------------\n");
        sb.append("TOTAL: ₡").append(String.format("%,.0f", total)).append("\n");
        sb.append("-------------------------------\n");
        sb.append("Gracias por su compra\n\n\n\n");

        return sb.toString();
    }

    // ----------------------------------------------------------------
    //   ENVÍA EL TEXTO A LA IMPRESORA USB
    // ----------------------------------------------------------------
    private void enviarAImpresora(String data) throws Exception {

        // Buscar impresora instalada por nombre
        PrintService impresora = null;
        PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);

        for (PrintService ps : services) {
            if (ps.getName().equalsIgnoreCase(printerName)) {
                impresora = ps;
                break;
            }
        }

        if (impresora == null) {
            throw new Exception("Impresora no encontrada: " + printerName);
        }

        // Convertir texto a bytes
        byte[] bytes = data.getBytes(StandardCharsets.UTF_8);

        DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
        Doc doc = new SimpleDoc(bytes, flavor, null);

        PrintRequestAttributeSet attrs = new HashPrintRequestAttributeSet();

        // Enviar
        DocPrintJob job = impresora.createPrintJob();
        job.print(doc, attrs);
    }
}

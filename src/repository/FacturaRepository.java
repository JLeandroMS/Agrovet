package repository;

import logic.Factura;
import logic.Producto;

import java.io.*;
import java.nio.file.*;
import java.text.NumberFormat;
import java.time.Instant;
import java.util.*;

/**
 * FacturaRepository
 *
 * - Guarda facturas en data/facturas.txt (una factura por línea).
 * - Carga desde el archivo al construir la clase.
 *
 * Formato por línea:
 *   invoiceId|cedula|timestampISO|prodId1:qty1,prodId2:qty2|totalFormatted
 *
 * Nota: para reconstruir objetos Producto al leer las facturas se utiliza
 * ProductoRepository (busca por id). Si un producto no existe en el repo,
 * se crea un Producto "placeholder" con el id y nombre "Desconocido".
 */
public class FacturaRepository {

    private static final String DATA_DIR = "data";
    private static final String FILE_NAME = "facturas.txt";
    private final Path filePath;
    private final ProductoRepository productoRepo;

    // Cache simple: lista de líneas (no estrictamente necesaria pero práctico)
    private final List<String> lines = new ArrayList<>();

    public FacturaRepository() {
        this.productoRepo = new ProductoRepository();
        this.filePath = Paths.get(DATA_DIR, FILE_NAME);
        cargarDesdeTXT();
    }

    /**
     * Guarda la factura (añade una línea al archivo).
     * El método genera un invoiceId y agrupa productos por id/qty.
     *
     * @param f Factura a guardar
     * @throws RuntimeException en caso de error de IO
     */
    public synchronized void guardar(Factura f) {
        try {
            // asegurar directorio
            Files.createDirectories(filePath.getParent());

            // agrupar productos por id
            Map<Integer, Integer> qtyById = new LinkedHashMap<>();
            for (Producto p : f.getProductos()) {
                if (p == null) continue;
                qtyById.put(p.getId(), qtyById.getOrDefault(p.getId(), 0) + 1);
            }

            // formar la parte products: "id:qty,id:qty"
            StringBuilder sbProd = new StringBuilder();
            boolean first = true;
            for (Map.Entry<Integer, Integer> e : qtyById.entrySet()) {
                if (!first) sbProd.append(",");
                sbProd.append(e.getKey()).append(":").append(e.getValue());
                first = false;
            }

            String invoiceId = "INV-" + Instant.now().toEpochMilli();
            String timestamp = Instant.now().toString();
            NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("es", "CR"));
            String totalFormatted = nf.format(f.total());

            String line = String.join("|",
                    invoiceId,
                    f.getCedula(),
                    timestamp,
                    sbProd.toString(),
                    totalFormatted
            );

            // append to file
            try (BufferedWriter bw = Files.newBufferedWriter(filePath, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
                bw.write(line);
                bw.newLine();
            }

            // update cache
            lines.add(line);

        } catch (IOException ex) {
            throw new RuntimeException("No se pudo guardar la factura: " + ex.getMessage(), ex);
        }
    }

    /**
     * Busca facturas por cédula y devuelve una lista de objetos Factura reconstruidos.
     *
     * @param cedula cédula del cliente
     * @return lista de Factura (vacía si no hay resultados)
     */
    public List<Factura> buscarPorCedula(String cedula) {
        List<Factura> out = new ArrayList<>();
        if (cedula == null || cedula.isEmpty()) return out;

        for (String line : lines) {
            String[] parts = line.split("\\|", -1);
            if (parts.length < 5) continue;
            String ced = parts[1];
            if (ced.equalsIgnoreCase(cedula)) {
                // reconstruir productos desde parte 3 (index 3)
                String prodPart = parts[3];
                List<Producto> prodList = new ArrayList<>();

                if (!prodPart.trim().isEmpty()) {
                    String[] items = prodPart.split(",");
                    for (String it : items) {
                        String[] kv = it.split(":");
                        try {
                            int prodId = Integer.parseInt(kv[0]);
                            int qty = (kv.length > 1) ? Integer.parseInt(kv[1]) : 1;
                            Producto template = productoRepo.buscarPorId(prodId);
                            if (template != null) {
                                // añadir 'qty' veces una copia básica del producto
                                for (int i = 0; i < qty; i++) {
                                    // Si quieres una copia independiente, puedes clonar o crear uno nuevo:
                                    prodList.add(new Producto(template.getId(), template.getNombre(), template.getPrecio()));
                                }
                            } else {
                                // producto no encontrado -> crear placeholder
                                Producto placeholder = new Producto(prodId, "Desconocido (id " + prodId + ")", 0.0);
                                for (int i = 0; i < qty; i++) prodList.add(placeholder);
                            }
                        } catch (NumberFormatException ignored) { }
                    }
                }

                // construir Factura
                Producto[] prodArray = prodList.toArray(new Producto[0]);
                Factura fact = new Factura(ced, prodArray);
                out.add(fact);
            }
        }
        return out;
    }

    /**
     * Busca facturas por cédula y devuelve representaciones de texto (listas),
     * útiles para mostrar en un textarea tal cual.
     *
     * @param cedula cédula
     * @return lista de bloques de texto de facturas
     */
    public List<String> buscarPorCedulaTexto(String cedula) {
        List<String> out = new ArrayList<>();
        if (cedula == null || cedula.isEmpty()) return out;

        NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("es", "CR"));

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            String[] parts = line.split("\\|", -1);
            if (parts.length < 5) continue;
            String ced = parts[1];
            if (!ced.equalsIgnoreCase(cedula)) continue;

            String invoiceId = parts[0];
            String timestamp = parts[2];
            String prodPart = parts[3];
            String total = parts[4];

            StringBuilder sb = new StringBuilder();
            sb.append("Factura ID: ").append(invoiceId).append("\n");
            sb.append("Cédula: ").append(ced).append("\n");
            sb.append("Fecha: ").append(timestamp).append("\n");
            sb.append("Items:\n");

            if (!prodPart.trim().isEmpty()) {
                String[] items = prodPart.split(",");
                for (String it : items) {
                    String[] kv = it.split(":");
                    try {
                        int prodId = Integer.parseInt(kv[0]);
                        int qty = (kv.length > 1) ? Integer.parseInt(kv[1]) : 1;
                        Producto prod = productoRepo.buscarPorId(prodId);
                        String name = (prod != null) ? prod.getNombre() : ("Desconocido (id " + prodId + ")");
                        double price = (prod != null) ? prod.getPrecio() : 0.0;
                        sb.append("  - ").append(name).append(" (id ").append(prodId).append(") x").append(qty)
                                .append(" @ ").append(nf.format(price)).append(" c/u")
                                .append(" -> ").append(nf.format(price * qty)).append("\n");
                    } catch (NumberFormatException ex) {
                        // ignorar
                    }
                }
            }

            sb.append("TOTAL: ").append(total).append("\n");
            out.add(sb.toString());
        }

        return out;
    }

    /**
     * Carga todas las facturas desde el archivo (invocado en el constructor).
     */
    private void cargarDesdeTXT() {
        lines.clear();
        try {
            if (!Files.exists(filePath)) {
                // crear archivo vacío
                Files.createDirectories(filePath.getParent());
                Files.createFile(filePath);
                return;
            }
            try (BufferedReader br = Files.newBufferedReader(filePath)) {
                String l;
                while ((l = br.readLine()) != null) {
                    if (!l.trim().isEmpty()) lines.add(l);
                }
            }
        } catch (IOException ex) {
            // no interrumpir la app: loguear a stderr
            System.err.println("Error cargando facturas desde TXT: " + ex.getMessage());
        }
    }
}

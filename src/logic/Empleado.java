package logic;

/**
 * Representa a un empleado del sistema POS.
 * Contiene información básica personal y laboral.
 */
public class Empleado {

    private String cedula;
    private String nombre;
    private String telefono;
    private String ocupacion;
    private String tipo; // Veterinario o Cashier
    private double salario;

    /**
     * Constructor completo de empleado.
     *
     * @param cedula     Identificación del empleado.
     * @param nombre     Nombre completo del empleado.
     * @param telefono   Número de contacto del empleado.
     * @param ocupacion  Ocupación o rol interno dentro del comercio.
     * @param tipo       Tipo de empleado (por ejemplo: "Veterinario" o "Cashier").
     * @param salario    Salario mensual del empleado.
     */
    public Empleado(String cedula, String nombre, String telefono, String ocupacion, String tipo, double salario) {
        this.cedula = cedula;
        this.nombre = nombre;
        this.telefono = telefono;
        this.ocupacion = ocupacion;
        this.tipo = tipo;
        this.salario = salario;
    }

    // GETTERS Y SETTERS

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getOcupacion() {
        return ocupacion;
    }

    public void setOcupacion(String ocupacion) {
        this.ocupacion = ocupacion;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public double getSalario() {
        return salario;
    }

    public void setSalario(double salario) {
        this.salario = salario;
    }
}

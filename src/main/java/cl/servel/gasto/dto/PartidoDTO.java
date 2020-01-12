package cl.servel.gasto.dto;

import java.io.Serializable;
import java.util.Date;

public class PartidoDTO implements Serializable {

	private int id;
    private String rut;
    private String nombre;
    private String direccion;
    //private String comuna;
    private String ciudad;
    private String rutAdministrador;
    private String digitoVerificadorAdministrador;
    private String nombreAdministrador;
    private String apellidoPaternoAdministrador;
    private String apellidoMaternoAdministrador;
    private String estadoPartido;
    private Date fechaDisolucion;
    private String codigoOrigen;

   
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getRut() {
		return rut;
	}
	public void setRut(String rut) {
		this.rut = rut;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getDireccion() {
		return direccion;
	}
	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}
	public String getCiudad() {
		return ciudad;
	}
	public void setCiudad(String ciudad) {
		this.ciudad = ciudad;
	}
	public String getRutAdministrador() {
		return rutAdministrador;
	}
	public void setRutAdministrador(String rutAdministrador) {
		this.rutAdministrador = rutAdministrador;
	}
	public String getDigitoVerificadorAdministrador() {
		return digitoVerificadorAdministrador;
	}
	public void setDigitoVerificadorAdministrador(String digitoVerificadorAdministrador) {
		this.digitoVerificadorAdministrador = digitoVerificadorAdministrador;
	}
	public String getNombreAdministrador() {
		return nombreAdministrador;
	}
	public void setNombreAdministrador(String nombreAdministrador) {
		this.nombreAdministrador = nombreAdministrador;
	}
	public String getApellidoPaternoAdministrador() {
		return apellidoPaternoAdministrador;
	}
	public void setApellidoPaternoAdministrador(String apellidoPaternoAdministrador) {
		this.apellidoPaternoAdministrador = apellidoPaternoAdministrador;
	}
	public String getApellidoMaternoAdministrador() {
		return apellidoMaternoAdministrador;
	}
	public void setApellidoMaternoAdministrador(String apellidoMaternoAdministrador) {
		this.apellidoMaternoAdministrador = apellidoMaternoAdministrador;
	}
	public String getEstadoPartido() {
		return estadoPartido;
	}
	public void setEstadoPartido(String estadoPartido) {
		this.estadoPartido = estadoPartido;
	}
	public Date getFechaDisolucion() {
		return fechaDisolucion;
	}
	public void setFechaDisolucion(Date fechaDisolucion) {
		this.fechaDisolucion = fechaDisolucion;
	}

	public String getCodigoOrigen() {
		return codigoOrigen;
	}

	public void setCodigoOrigen(String codigoOrigen) {
		this.codigoOrigen = codigoOrigen;
	}
    


}

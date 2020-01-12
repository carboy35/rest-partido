package cl.servel.gasto.dto;

import java.io.Serializable;

public class PartidoRequestDTO implements Serializable{
	
	private static final long serialVersionUID = 1L;

	private int eventoId;
	private int tipoEventoId;
	private int tipoEleccionId;
	public int getEventoId() {
		return eventoId;
	}
	public void setEventoId(int eventoId) {
		this.eventoId = eventoId;
	}
	public int getTipoEventoId() {
		return tipoEventoId;
	}
	public void setTipoEventoId(int tipoEventoId) {
		this.tipoEventoId = tipoEventoId;
	}
	public int getTipoEleccionId() {
		return tipoEleccionId;
	}
	public void setTipoEleccionId(int tipoEleccionId) {
		this.tipoEleccionId = tipoEleccionId;
	}
	@Override
	public String toString() {
		return "PartidoRequestDTO [eventoId=" + eventoId + ", tipoEventoId=" + tipoEventoId + ", tipoEleccionId="
				+ tipoEleccionId + "]";
	}	
	
	
	
}

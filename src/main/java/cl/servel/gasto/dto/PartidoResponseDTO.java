package cl.servel.gasto.dto;

import java.io.Serializable;
import java.util.List;

public class PartidoResponseDTO implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private List<PartidoDTO> partidos;

	public List<PartidoDTO> getPartidos() {
		return partidos;
	}

	public void setPartidos(List<PartidoDTO> partidos) {
		this.partidos = partidos;
	}

	@Override
	public String toString() {
		return "PartidoResponseDTO [partidos=" + partidos + "]";
	}
	
	

}

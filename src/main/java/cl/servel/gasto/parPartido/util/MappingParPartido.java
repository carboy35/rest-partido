package cl.servel.gasto.parPartido.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.servel.gasto.dto.PartidoDTO;
import cl.servel.gasto.entity.EleEleccion;
import cl.servel.gasto.entity.PacPacto;
import cl.servel.gasto.entity.ParPartido;
import cl.servel.gasto.entity.SpaPacto;
import cl.servel.gasto.model.EleccionLiviano;
import cl.servel.gasto.model.EventoEleccionarioLiviano;
import cl.servel.gasto.model.Pacto;
import cl.servel.gasto.model.Partido;
import cl.servel.gasto.model.PartidoLiviano;
import cl.servel.gasto.model.SubPacto;
import cl.servel.gasto.model.TipoEventoLiviano;
import cl.servel.gasto.repository.AdministradorElectoralRepository;
import cl.servel.gasto.repository.CanCandidatoRepository;
import cl.servel.gasto.repository.EleEleccionRepository;
import cl.servel.gasto.repository.PacPactoRepository;
import cl.servel.gasto.repository.SpaPactoRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MappingParPartido {

	@Autowired
	PacPactoRepository pacPactoRepository;

	@Autowired
	EleEleccionRepository eleEleccionRepository;

	@Autowired
	AdministradorElectoralRepository administradorElectoralRepository;

	@Autowired
	CanCandidatoRepository canCandidatoRepository;

	@Autowired
	SpaPactoRepository spaPactoRepository;

	// De Entity a Model
	public Partido partidoEntityToPartidoModel(ParPartido partidoEntity) {

		Integer pactoID = null;
		Pacto pacto = new Pacto();

		Partido partidoModel = new Partido();

		if (partidoEntity.getPacPacto() != null) {
			Optional<PacPacto> pacPacto = pacPactoRepository.findById(new Integer(partidoEntity.getParId()));
			// valor del pacto ID para el model

			if (pacPacto.isPresent()) {
				pactoID = pacPacto.get().getPacId();
			}

			try {
				pacto.setIdEleccion(partidoEntity.getPacPacto().getEleEleccion().getEleId());
			} catch (Exception e) {
				log.info("parPartido: no tine partidoEntity.getPacPacto().getEleEleccion().getEleId()");
			}

			try {
				pacto.setLista(partidoEntity.getPacPacto().getLista());
			} catch (Exception e) {
				log.info("parPartido: no tiene propiedad");
			}

			try {
				pacto.setPacCodigoOrigen(partidoEntity.getPacPacto().getPacCodigoOrigen());
			} catch (Exception e) {
				log.info("parPartido: no tiene propiedad");
			}

			try {
				pacto.setPacNombre(partidoEntity.getPacPacto().getPacNombre());
			} catch (Exception e) {
				log.info("parPartido: no tiene propiedad");
			}

			try {
				pacto.setPacSigla(partidoEntity.getPacPacto().getPacSigla());
			} catch (Exception e) {
				log.info("parPartido: no tiene propiedad");
			}

			try {
				pacto.setPacId(partidoEntity.getPacPacto().getPacId());
			} catch (Exception e) {
				log.info("parPartido: no tiene propiedad");
			}

			try {
				pacto.setPacCreated(partidoEntity.getPacPacto().getPacCreated());
			} catch (Exception e) {
				log.info("parPartido: no tiene propiedad");
			}

			try {
				pacto.setPacModified(partidoEntity.getPacPacto().getPacModified());
			} catch (Exception e) {
				log.info("parPartido: no tiene propiedad");
			}

			try {
				pacto.setPacEliminado(partidoEntity.getPacPacto().getPacEliminado());
			} catch (Exception e) {
				log.info("parPartido: no tiene propiedad");
			}

			partidoModel.setPacto(pacto);

			partidoModel.setIdPacPacto(partidoEntity.getPacPacto().getPacId());
		}

		if (partidoEntity.getPacPacto() != null) {
			List<SpaPacto> spaPacto = spaPactoRepository.findAllActiveByPacto(partidoEntity.getPacPacto().getPacId());

			SubPacto subPacto = new SubPacto();
			if (spaPacto != null) {

				for (SpaPacto spacto : spaPacto) {
					subPacto.setIdEleccion(partidoEntity.getEleEleccion().getEleId());
					subPacto.setIdPacPacto(partidoEntity.getPacPacto().getPacId());
					subPacto.setSpaNombre(spacto.getSpaNombre());
					subPacto.setSpaSigla(spacto.getSpaSigla());
				}
			}

			if (spaPacto != null) {
				partidoModel.setSubPacto(subPacto);
			}
		}

		if (partidoEntity.getEleEleccion() != null) {
			partidoModel.setIdEleccion(partidoEntity.getEleEleccion().getEleId());
		}
		
		partidoModel.setParCodigoOrigen(partidoEntity.getParCodigoOrigen());
		partidoModel.setParCreated(partidoEntity.getParCreated());
		partidoModel.setParId(partidoEntity.getParId());
		partidoModel.setParNombre(partidoEntity.getParNombre());
		partidoModel.setParSigla(partidoEntity.getParSigla());
		partidoModel.setParNombreAdm(partidoEntity.getParNombreAdm());
		partidoModel.setParRut(partidoEntity.getParRut());

		partidoModel.setParMail(partidoEntity.getParMail());
		partidoModel.setParDireccion(partidoEntity.getParDireccion());
		partidoModel.setParCiudad(partidoEntity.getParCiudad());
		partidoModel.setParTelefonoCelu(partidoEntity.getParTelefonoCelu());
		partidoModel.setParEstado(partidoEntity.getParEstado());
		partidoModel.setParFechaDisolucion(partidoEntity.getParFechaDisolucion());
		partidoModel.setParFechaContitucion(partidoEntity.getParFechaConstitucion());
		partidoModel.setParComuna(partidoEntity.getParComuna());
		if (partidoEntity.getParEliminado() != null) {
			partidoModel.setParEliminado(partidoEntity.getParEliminado());
		} else {
			partidoModel.setParEliminado(false);
		}

		return partidoModel;
	}

	// De Model a Entity
	public ParPartido partidoModelToPartidoEntity(Partido partidoModel) {

		ParPartido partidoEntity = new ParPartido();

		EleEleccion eleEleccion = eleEleccionRepository.findById(partidoModel.getIdEleccion()).get();
		if (partidoModel.getIdPacPacto() != null) {
			PacPacto pp = pacPactoRepository.findById(partidoModel.getIdPacPacto()).get();

			partidoEntity.setPacPacto(pp);
		}
		partidoEntity.setEleEleccion(eleEleccion);
		partidoEntity.setParCodigoOrigen(partidoModel.getParCodigoOrigen());
		partidoEntity.setParCreated(partidoModel.getParCreated());
		partidoEntity.setParEliminado(partidoModel.getParEliminado());
		partidoEntity.setParId(partidoModel.getParId());
		partidoEntity.setParNombre(partidoModel.getParNombre());
		partidoEntity.setParSigla(partidoModel.getParSigla());
		partidoEntity.setParNombreAdm(partidoModel.getParNombreAdm());
		partidoEntity.setParRut(partidoModel.getParRut());
		partidoEntity.setParMail(partidoModel.getParMail());
		partidoEntity.setParDireccion(partidoModel.getParDireccion());
		partidoEntity.setParCiudad(partidoModel.getParCiudad());
		partidoEntity.setParTelefonoCelu(partidoModel.getParTelefonoCelu());
		partidoEntity.setParEstado(partidoModel.getParEstado());
		partidoEntity.setParFechaDisolucion(partidoModel.getParFechaDisolucion());
		partidoEntity.setParFechaConstitucion(partidoModel.getParFechaContitucion());
		partidoEntity.setParComuna(partidoModel.getParComuna());
		// partidoEntity.setRlcPartidoNivels(rlcPartidoNivels);

		return partidoEntity;

	}

	// De Model a DTO
	public PartidoDTO partidoModelToPartidoDTO(Partido partidoModel) {
		PartidoDTO pdto = new PartidoDTO();

		// pdto.setAdministradorid(999);// Falta sacar de algún lado.
//		pdto.setBancCuentaBancariaPartido(partidoModel.getParBcoCtaBca());
//		pdto.setCuentaBancariaPartido(partidoModel.getParCtaBca());
//		pdto.setCuentaElectoralBancaria("999"); //Falta sacar de algún lado
//		pdto.setCuentaElectoralPartido(partidoModel.getParCtaEleBca());
		pdto.setId(partidoModel.getParId());
		pdto.setRut(partidoModel.getParRut());
		pdto.setNombre(partidoModel.getParNombre());
		pdto.setDireccion(partidoModel.getParDireccion());
		pdto.setCiudad(partidoModel.getParCiudad());
		pdto.setEstadoPartido(partidoModel.getParEstado());
		pdto.setFechaDisolucion(partidoModel.getParFechaDisolucion());
		pdto.setCodigoOrigen(partidoModel.getParCodigoOrigen());
		return pdto;
	}

	public PartidoLiviano partidoEntityToPartidoLiviano(ParPartido entity) {

		return PartidoLiviano.builder().parId(entity.getParId()).parEliminado(entity.getParEliminado())
				.parSigla(entity.getParSigla()).parNombre(entity.getParNombre()).parRut(entity.getParRut())
				.evento(EventoEleccionarioLiviano.builder()
						.id(entity.getEleEleccion().getEveEventoEleccionario().getEveId())
						.nombre(entity.getEleEleccion().getEveEventoEleccionario().getEveNombre())
						.tipoEvento(TipoEventoLiviano.builder()
								.id(entity.getEleEleccion().getEveEventoEleccionario().getIdTipoEvento())
								.nombre(entity.getEleEleccion().getTpoEvento().getTpoEventoNombre()).build())
						.elecciones(new ArrayList<EleccionLiviano>() {
							{
								add(EleccionLiviano.builder().id(entity.getEleEleccion().getEleId())
										.nombre(entity.getEleEleccion().getEleNombre()).build());
							}
						}).build())
				.build();
	}
}

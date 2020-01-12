package cl.servel.gasto.parPartido.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cl.servel.gasto.dto.PartidoDTO;
import cl.servel.gasto.dto.PartidoResponseDTO;
import cl.servel.gasto.entity.CanCandidato;
import cl.servel.gasto.entity.ParPartido;
import cl.servel.gasto.enumerated.MensajesRespuestaRest;
import cl.servel.gasto.enumerated.OrigenesBusqueda;
import cl.servel.gasto.exception.CustomErrorType;
import cl.servel.gasto.model.Candidato;
import cl.servel.gasto.model.Partido;
import cl.servel.gasto.model.PartidoLiviano;
import cl.servel.gasto.model.PartidosHash;
import cl.servel.gasto.model.RestResponse;
import cl.servel.gasto.parPartido.util.MappingParPartido;
import cl.servel.gasto.repository.ParPartidoRepository;

@SuppressWarnings({ "rawtypes", "unchecked" })
@RestController("crudParPartido")
public class CrudParPartido {

	private static final Log LOG = LogFactory.getLog(CrudParPartido.class);

	@Autowired
	MappingParPartido mappingParPartido;

	@Autowired
	private ParPartidoRepository parPartidoRepository;

//Met�do que implementa la respuesta GET para un id.
	@GetMapping("/parpartido/{id}")
	public Partido leerParPartido(@PathVariable Integer id) {

		Optional<ParPartido> obj = parPartidoRepository.findById(id);
		Partido empty = new Partido();
		empty.setParId(-1);

		if (!obj.isPresent()) {
			return empty;
		}

		if (obj.get().getParEliminado()) {
			return empty;
		}

		return mappingParPartido.partidoEntityToPartidoModel(obj.get());

	}

//M�todo que implementa la respuesta GET para todos.
	@GetMapping("/parpartido")
	public List<Partido> leerTodosParPartido(@RequestParam("idEventoAnterior") Optional<Integer> idEventoAnterior,
			@RequestParam("idEventoActual") Optional<Integer> idEventoActual,
			@RequestParam("idPacto") Optional<Integer> idPacto,
			@RequestParam("idTipoEleccion") Optional<Integer> idTipoEleccion,
			@RequestParam("eleccionId") Optional<Integer> eleccionId) {
		List<Partido> listPartido = new ArrayList<Partido>();
		if (eleccionId.isPresent()) {
			parPartidoRepository.findByEleccion(eleccionId.get())
					.forEach(e -> listPartido.add(mappingParPartido.partidoEntityToPartidoModel(e)));
		} else if (idEventoAnterior.isPresent() && idEventoActual.isPresent()) {
			parPartidoRepository.getPartidosNuevosEntreEventos(idEventoAnterior.get(), idEventoActual.get())
					.forEach(e -> listPartido.add(mappingParPartido.partidoEntityToPartidoModel(e)));
		} else if (idEventoActual.isPresent() && idPacto.isPresent() && idTipoEleccion.isPresent()) {
			parPartidoRepository
					.getPartidosXEventoPactoTipoEleccion(idEventoActual.get(), idPacto.get(), idTipoEleccion.get())
					.forEach(e -> listPartido.add(mappingParPartido.partidoEntityToPartidoModel(e)));
		} else {
			parPartidoRepository.findAll()
					.forEach(e -> listPartido.add(mappingParPartido.partidoEntityToPartidoModel(e)));
		}

		return listPartido;

	}

//M�todo que implementa la respuesta GET para todos los activos.
	@GetMapping("/parpartido/activo")
	public List<Partido> leerTodosActivosParPartido(@RequestParam("idEvento") Optional<Integer> idEvento,
			@RequestParam("pactoId") Optional<Integer> pactoId,

			@RequestParam("eleccionId") Optional<Integer> eleccionId) {
		List<Partido> listPartido = new ArrayList<Partido>();
		List<ParPartido> result = null;

		if (pactoId.isPresent() && eleccionId.isPresent()) {
			result = parPartidoRepository.findByPacto(pactoId.get(), eleccionId.get());
		} else if (idEvento.isPresent()) {
			result = parPartidoRepository.findActiveByEvento(idEvento.get());
		} else if (eleccionId.isPresent()) {
			result = parPartidoRepository.findActiveByEleccion(eleccionId.get());
		} else {
			result = parPartidoRepository.findAllActiveParPartido();
		}

		result.forEach(e -> listPartido.add(mappingParPartido.partidoEntityToPartidoModel(e)));

		return listPartido;

	}

//M�todo que implemenat la respuesta POST
	@PostMapping("/parpartido")
	public Integer crearParPartido(@RequestBody Partido partidoModel) {
		partidoModel.setParEliminado(false);
		partidoModel.setParCreated(new Date());
		ParPartido partidoEntity = parPartidoRepository
				.save(mappingParPartido.partidoModelToPartidoEntity(partidoModel));
		return partidoEntity.getParId();
	}

//M�todo que implementa la respuesta PUT
	@PutMapping("/parpartido")
	public boolean actualizarPartido(@RequestBody Partido partidoModel) {
		try {
			parPartidoRepository.save(mappingParPartido.partidoModelToPartidoEntity(partidoModel));
			return true; // mappingParPArtido.entityToParPartido(obj_borrado);
		} catch (Exception e) {
			LOG.error("Ocurrio un error al actualizar el partido.", e);
			return false;
		}
	}

//M�todo que implementa la respuesta DELETE 
	@DeleteMapping("/parpartido")
	public boolean borrarPartido(@RequestBody Partido partidoModel) {
		try {
			partidoModel.setParEliminado(true);
			parPartidoRepository.save(mappingParPartido.partidoModelToPartidoEntity(partidoModel));
			return true; // mappingParPArtido.entityToParPartido(obj_borrado);
		} catch (Exception e) {
			return false;
		}
	}

//Método que implementa la respuesta GET All para front end
	@GetMapping("/parpartido/byEleccionId/{eleccionId}")
	public PartidoResponseDTO traerPatidos(@PathVariable("eleccionId") Integer eleccionId) {

		LOG.info("eleccionId:" + eleccionId);

		List<Partido> listPartidoModel = new ArrayList<Partido>();
		for (ParPartido parPartido : (List<ParPartido>) parPartidoRepository.findActiveByEleccion(eleccionId)) {
			listPartidoModel.add(mappingParPartido.partidoEntityToPartidoModel(parPartido));
		}

		// parce de lista de model a lista de dto
		List<PartidoDTO> listaPartidoDTO = new ArrayList<>();
		for (Partido partidoModel : listPartidoModel) {
			listaPartidoDTO.add(mappingParPartido.partidoModelToPartidoDTO(partidoModel));
		}

		PartidoResponseDTO partidoRes = new PartidoResponseDTO();
		partidoRes.setPartidos(listaPartidoDTO);
		return partidoRes;

	}

	@GetMapping("/parpartido/rutevento/{rutPartido}/{eventoId}")
	public List<Partido> getPatidosByRutAndEvento(@PathVariable("rutPartido") String rutPartido,
			@PathVariable("eventoId") int eventoId) {
		List<Partido> partidos = new ArrayList<>();

		parPartidoRepository.getPartidosByEventoAndRut(eventoId, rutPartido)
				.forEach(e -> partidos.add(mappingParPartido.partidoEntityToPartidoModel(e)));

		return partidos;
	}

	@GetMapping("/parpartido/rutevento/{rutPartido}/{eventoId}/{tpoEventoId}")
	public List<Partido> getPatidosByRutAndEventoAndTpoEvento(@PathVariable("rutPartido") String rutPartido,
			@PathVariable("eventoId") int eventoId, @PathVariable("tpoEventoId") int tpoEventoId) {
		List<Partido> partidos = new ArrayList<>();

		parPartidoRepository.getPartidosByEventoAndTipoEventoAndRut(eventoId, tpoEventoId, rutPartido)
				.forEach(e -> partidos.add(mappingParPartido.partidoEntityToPartidoModel(e)));

		return partidos;
	}

	@GetMapping("/parpartido/eleccionrut/{eleccionId}/{rutPartido}")
	public List<Partido> getPatidosByRutAndEleccion(@PathVariable("eleccionId") int eleccionId,
			@PathVariable("rutPartido") String rutPartido) {
		List<Partido> partidos = new ArrayList<>();

		parPartidoRepository.getPartidosByEleccionAndRut(eleccionId, rutPartido)
				.forEach(e -> partidos.add(mappingParPartido.partidoEntityToPartidoModel(e)));

		return partidos;
	}

	@PostMapping("/parpartido/buscar")
	public PartidosHash getBusqueda(@RequestParam("where") String where, @RequestParam("key") String key,
			@RequestBody List<String> campos, @RequestParam("idEvento") Optional<Integer> idEvento,
			@RequestParam("tipoEvento") Optional<Integer> tipoEvento,
			@RequestParam("idEleccion") Optional<Integer> idEleccion,
			@RequestParam("estado") Optional<Boolean> estado,
			@RequestParam("definicionTarea") Optional<String> definicionTarea) {

		HashMap<String, PartidoLiviano> partidosUnicos = new HashMap<String, PartidoLiviano>();
		List<ParPartido> partidosEntity = null;

		if (where.equalsIgnoreCase(OrigenesBusqueda.PARTIDO.getOrigen()) || where.equalsIgnoreCase(OrigenesBusqueda.CANDIDATO_PARTIDO.getOrigen()) || where.equalsIgnoreCase(OrigenesBusqueda.CANDIDATO_PARTIDO_ADMINISTRADOR.getOrigen())) {
			partidosEntity = parPartidoRepository.buscarPartidos(idEvento.isPresent() ? idEvento.get() : null, tipoEvento.isPresent() ? tipoEvento.get() : null, idEleccion.isPresent() ? idEleccion.get() : null, campos, key.replaceAll("\\s+", " "), estado.isPresent() ? estado.get() : false, definicionTarea.isPresent() ? definicionTarea.get(): null, Arrays.asList(),null);
		} else if (where.equalsIgnoreCase(OrigenesBusqueda.PARTIDO_RENDICION.getOrigen()) || where.equalsIgnoreCase(OrigenesBusqueda.CANDIDATO_PARTIDO_RENDICION.getOrigen())) {

			partidosEntity = parPartidoRepository.buscarPartidos(idEvento.isPresent() ? idEvento.get() : null, tipoEvento.isPresent() ? tipoEvento.get() : null, idEleccion.isPresent() ? idEleccion.get() : null, campos, key.replaceAll("\\s+", " "), estado.isPresent() ? estado.get() : false, definicionTarea.isPresent() ? definicionTarea.get(): null, Arrays.asList(OrigenesBusqueda.RENDICION.getOrigen()),null);
		} else if (where.equalsIgnoreCase(OrigenesBusqueda.RESPUESTA_OBSERVACION.getOrigen())) {
			
			partidosEntity = parPartidoRepository.buscarPartidos(idEvento.isPresent() ? idEvento.get() : null, tipoEvento.isPresent() ? tipoEvento.get() : null, idEleccion.isPresent() ? idEleccion.get() : null, campos, key.replaceAll("\\s+", " "), estado.isPresent() ? estado.get() : false, definicionTarea.isPresent() ? definicionTarea.get(): null, Arrays.asList(OrigenesBusqueda.RESPUESTA_OBSERVACION.getOrigen()),null);
		} else if (where.equalsIgnoreCase(OrigenesBusqueda.INGRESO_RECURSO_PARTIDO.getOrigen()) || where.equalsIgnoreCase(OrigenesBusqueda.INGRESO_RECURSO_CANDIDATO_PARTIDO.getOrigen())) {
			partidosEntity = parPartidoRepository.buscarPartidos(idEvento.isPresent() ? idEvento.get() : null, tipoEvento.isPresent() ? tipoEvento.get() : null, idEleccion.isPresent() ? idEleccion.get() : null, campos, key.replaceAll("\\s+", " "), estado.isPresent() ? estado.get() : true, definicionTarea.isPresent() ? definicionTarea.get(): null, Arrays.asList(OrigenesBusqueda.INGRESO_RECURSO_PARTIDO.getOrigen()),null);
		} else if (where.equalsIgnoreCase(OrigenesBusqueda.INGRESO_SENTENCIAS_TRICEL.getOrigen())) {
			partidosEntity = parPartidoRepository.buscarPartidos(idEvento.isPresent() ? idEvento.get() : null, tipoEvento.isPresent() ? tipoEvento.get() : null, idEleccion.isPresent() ? idEleccion.get() : null, campos, key.replaceAll("\\s+", " "), estado.isPresent() ? estado.get() : true, definicionTarea.isPresent() ? definicionTarea.get(): null, Arrays.asList(OrigenesBusqueda.INGRESO_SENTENCIAS_TRICEL.getOrigen()),null);
		}

		partidosEntity.forEach(p -> {

			PartidoLiviano partido = mappingParPartido.partidoEntityToPartidoLiviano(p);
			if (partido.getParRut() != null) {
				if (partidosUnicos.containsKey(partido.getParRut())) {

					if (!partidosUnicos.get(partido.getParRut()).getEvento().getElecciones().contains(partido.getEvento().getElecciones().get(0))) {
						partidosUnicos.get(partido.getParRut()).getEvento().getElecciones().add(partido.getEvento().getElecciones().get(0));
					}

				} else {
					partidosUnicos.put(partido.getParRut(), partido);
				}

			}

		});

		return PartidosHash.builder().partidos(partidosUnicos).build();

	}
	@PostMapping("/parpartido/busca")
	public List<Partido> getBuscar(@RequestParam("where") String where, @RequestParam("key") String key,
			@RequestBody List<String> campos,
			@RequestParam("idEvento") Integer idEvento,
			@RequestParam("idTipoEvento") Integer idTipoEvento,
			@RequestParam("idTipoEleccion") Integer idTipoEleccion) {

			List<Partido> partidos = new ArrayList<Partido>();
			List<ParPartido> partidosEntity = null;

			partidosEntity = parPartidoRepository.buscarPartidos(idEvento,idTipoEvento, null, campos, key.replaceAll("\\s+", " "),  true,  null, Arrays.asList(OrigenesBusqueda.CUENTA_CANDIDATO_PARTIDO.getOrigen()),idTipoEleccion);
		

			partidosEntity.forEach(p -> {
				partidos.add(mappingParPartido.partidoEntityToPartidoModel(p));
				
			});

		return partidos;

	}
	@GetMapping("/parpartido/instanciaflujo")
	public ResponseEntity<List<Partido>> getPatidosInstanciaFlujo(@RequestParam("eventoId") Optional<Integer> eventoId,@RequestParam("usuarioIds") Optional<List<Integer>> usuarioIds,@RequestParam("dtaId") Optional<Integer> dtaId,@RequestParam("usuarioId") Optional<Integer> usuarioId) {
		
		try {
		List<Partido> partidos = new ArrayList<>();
		List<ParPartido> partidosEntityList= null;
			partidosEntityList= new ArrayList<>();
			Integer eveId=null;
			List<Integer> usuarioList=null;
			Integer idDta=null;
			Integer idUsuario=null;
			if (eventoId.isPresent()) {
				eveId=eventoId.get();
			}
			if (usuarioIds.isPresent()) {
				usuarioList=usuarioIds.get();
			}
			if (dtaId.isPresent()) {
				idDta=dtaId.get();
			}
			if (usuarioId.isPresent()) {
				idUsuario=usuarioId.get();
			}
			
			partidosEntityList=parPartidoRepository.devuelveConsultaPartidosInstanciaFlujo(eveId,usuarioList,idDta,idUsuario);
			
			if (partidosEntityList !=null) {
				partidosEntityList.forEach(e -> partidos.add(mappingParPartido.partidoEntityToPartidoModel(e)));
			}
		return ResponseEntity.ok(partidos);
		}catch(Exception e) {
			LOG.error("Ocurrió un error al consultar los partidos.",e);
			return new ResponseEntity(new CustomErrorType("Ocurrió un error al consultar los partidos.",
					HttpStatus.INTERNAL_SERVER_ERROR.value()), HttpStatus.INTERNAL_SERVER_ERROR);
			
		}
	}
	
	@GetMapping("/parpartido/candidatos/instanciaflujo")
	public ResponseEntity<List<Partido>> getPatidosCandidatoInstanciaFlujo(@RequestParam("eventoId") Optional<Integer> eventoId,@RequestParam("usuarioIds") Optional<List<Integer>> usuarioIds,@RequestParam("dtaId") Optional<Integer> dtaId,@RequestParam("usuarioId") Optional<Integer> usuarioId) {
		
		try {
		List<Partido> partidos = new ArrayList<>();
		List<ParPartido> partidosEntityList= null;
			partidosEntityList= new ArrayList<>();
			Integer eveId=null;
			List<Integer> usuarioList=null;
			Integer idDta=null;
			Integer idUsuario=null;
			if (eventoId.isPresent()) {
				eveId=eventoId.get();
			}
			if (usuarioIds.isPresent()) {
				usuarioList=usuarioIds.get();
			}
			if (dtaId.isPresent()) {
				idDta=dtaId.get();
			}
			if (usuarioId.isPresent()) {
				idUsuario=usuarioId.get();
			}
			
			partidosEntityList=parPartidoRepository.devuelvePartidosCandidatoInstanciaFlujo(eveId,usuarioList,idDta,idUsuario);
			
			if (partidosEntityList !=null) {
				partidosEntityList.forEach(e -> partidos.add(mappingParPartido.partidoEntityToPartidoModel(e)));
				
			}
		return ResponseEntity.ok(partidos);
		}catch(Exception e) {
			LOG.error("Ocurrió un error al consultar los partidos.",e);
			return new ResponseEntity(new CustomErrorType("Ocurrió un error al consultar los partidos.",
					HttpStatus.INTERNAL_SERVER_ERROR.value()), HttpStatus.INTERNAL_SERVER_ERROR);
			
		}
	}
	
	@GetMapping("/parpartido/candidatos/rendicion")
	public ResponseEntity<List<Partido>> getPatidosCandidatoRendicion(@RequestParam("eventoId") Optional<Integer> eventoId) {
		
		try {
		List<Partido> partidos = new ArrayList<>();
		List<ParPartido> partidosEntityList= null;
			partidosEntityList= new ArrayList<>();
			if (eventoId.isPresent()) {
			partidosEntityList=parPartidoRepository.devuelvePartidosCandidatoRendicion(eventoId.get());
			}else {
				partidosEntityList=parPartidoRepository.devuelvePartidosCandidatoRendicion(null);
			}
			if (partidosEntityList !=null) {
				partidosEntityList.forEach(e -> partidos.add(mappingParPartido.partidoEntityToPartidoModel(e)));
				
			}
		return ResponseEntity.ok(partidos);
		}catch(Exception e) {
			LOG.error("Ocurrió un error al consultar los partidos.",e);
			return new ResponseEntity(new CustomErrorType("Ocurrió un error al consultar los partidos.",
					HttpStatus.INTERNAL_SERVER_ERROR.value()), HttpStatus.INTERNAL_SERVER_ERROR);
			
		}
	}
	
	@GetMapping("/parpartido/pacto/{idPacto}")
	public ResponseEntity<RestResponse<List<Partido>>> getByPacto(@PathVariable("idPacto") int idPacto) {
		try {
			List<ParPartido> partidosEntity = parPartidoRepository.findPartidosPorPacto(idPacto);
			List<Partido> partidos = new ArrayList<Partido>();
			
			partidosEntity.forEach(partido -> partidos.add(mappingParPartido.partidoEntityToPartidoModel(partido)));
			
			return ResponseEntity.ok(new RestResponse<List<Partido>>(true, "Consulta exitosa", MensajesRespuestaRest.OK.name(), partidos));
		} catch (Exception e) {
			LOG.error("Ocurrio un error al obtener los partidos del pacto", e);
			
			return new ResponseEntity<RestResponse<List<Partido>>>(new RestResponse<List<Partido>>(false, "Ocurrio un error al obtener los partidos del pacto", MensajesRespuestaRest.ERROR_INTERNO.name()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/parpartido/subpacto/{idSubPacto}")
	public ResponseEntity<RestResponse<List<Partido>>> getBySubPacto(@PathVariable("idSubPacto") int idSubPacto) {
		try {
			List<ParPartido> partidosEntity = parPartidoRepository.findPartidosPorSubPacto(idSubPacto);
			List<Partido> partidos = new ArrayList<Partido>();
			
			partidosEntity.forEach(partido -> partidos.add(mappingParPartido.partidoEntityToPartidoModel(partido)));
			
			return ResponseEntity.ok(new RestResponse<List<Partido>>(true, "Consulta exitosa", MensajesRespuestaRest.OK.name(), partidos));
		} catch (Exception e) {
			LOG.error("Ocurrio un error al obtener los partidos del subpacto", e);
			
			return new ResponseEntity<RestResponse<List<Partido>>>(new RestResponse<List<Partido>>(false, "Ocurrio un error al obtener los partidos del subpacto", MensajesRespuestaRest.ERROR_INTERNO.name()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/parpartido/evento/{eventoId}/tipoevento/{tipoEventoId}")
	public ResponseEntity<RestResponse<List<Partido>>> getByEventoTipoEvento(@PathVariable("eventoId") int eventoId,@PathVariable("tipoEventoId") int tipoEventoId) {
		try {
			List<ParPartido> partidosEntity = parPartidoRepository.findByEventoTipoEvento(eventoId, tipoEventoId);
			List<Partido> partidos = new ArrayList<Partido>();
			if (partidosEntity!=null) {
				partidosEntity.forEach(partido -> partidos.add(mappingParPartido.partidoEntityToPartidoModel(partido)));
			}
			
			return ResponseEntity.ok(new RestResponse<List<Partido>>(true, "Consulta exitosa", MensajesRespuestaRest.OK.name(), partidos));
		} catch (Exception e) {
			LOG.error("Ocurrio un error al obtener los partidos del evento tipo evento", e);
			
			return new ResponseEntity<RestResponse<List<Partido>>>(new RestResponse<List<Partido>>(false, "Ocurrio un error al obtener los partidos del evento tipo evento", MensajesRespuestaRest.ERROR_INTERNO.name()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	@Transactional
	@DeleteMapping("/parpartido/idEvento/{idEvento}/idTipoEvento/{idTipoEvento}")
	public ResponseEntity<RestResponse> deleteByEventoYTipoEvento(@PathVariable Integer idEvento,@PathVariable Integer idTipoEvento) {
		try {
			parPartidoRepository.deleteByEventoYTipo(  idEvento,  idTipoEvento);
			return ResponseEntity.ok(new RestResponse(true, "Eliminación satisfactoria", MensajesRespuestaRest.OK.name(), true));
		} catch (Exception e) {
			LOG.error("Ocurrio un error al eliminar partido", e);
			return new ResponseEntity(new CustomErrorType("Ocurrio un error al eliminar partido", HttpStatus.INTERNAL_SERVER_ERROR.value()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}

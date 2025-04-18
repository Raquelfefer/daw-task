package com.daw.services;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.daw.persistence.entities.Tarea;
import com.daw.persistence.entities.enums.Estado;
import com.daw.persistence.repositories.TareaRepository;
import com.daw.services.exceptions.TareaException;
import com.daw.services.exceptions.TareaNotFoundException;

@Service
public class TareaService {
	
	@Autowired
	private TareaRepository tareaRepository;

	public List<Tarea> findAll(){
		return this.tareaRepository.findAll();
	}
	
	public Tarea findById(int idTarea) {
		if(!this.tareaRepository.existsById(idTarea)) {
			throw new TareaNotFoundException("No existe la tarea con ID: " + idTarea);
		}
		
		return this.tareaRepository.findById(idTarea).get();
	}
	
	public Tarea findByIdFuncional (int idTarea) {
		return this.tareaRepository.findById(idTarea)
				.orElseThrow(() -> new TareaNotFoundException("No existe la tarea con ID: " + idTarea));
	}
	
	public boolean existsById(int idTarea) {
		return this.tareaRepository.existsById(idTarea);
	}
	
	public void deleteById(int idTarea) {
		if(!this.tareaRepository.existsById(idTarea)) {
			throw new TareaNotFoundException("No existe la tarea con ID: " + idTarea);
		}
		
		this.tareaRepository.deleteById(idTarea);
	}
	
	public Tarea create(Tarea tarea) {	
		tarea.setId(0);
		tarea.setFechaCreacion(LocalDate.now());
		tarea.setEstado(Estado.PENDIENTE);
		
		return this.tareaRepository.save(tarea);
	}
	
	public Tarea update(int idTarea, Tarea tarea) {
		if(idTarea != tarea.getId()) {
			throw new TareaException("El ID del path ("+ idTarea +") y el id del body ("+ tarea.getId() +") no coinciden");
		}
		if(!this.tareaRepository.existsById(idTarea)) {
			throw new TareaNotFoundException("No existe la tarea con ID: " + idTarea);
		}
		if(tarea.getFechaCreacion() != null || tarea.getEstado() != null) {
			throw new TareaException("No se permite modificar la fecha de creación y/o el estado. ");
		}
		
		Tarea tareaBD = this.findById(tarea.getId());
		tareaBD.setTitulo(tarea.getTitulo());
		tareaBD.setDescripcion(tarea.getDescripcion());
		tareaBD.setFechaVencimiento(tarea.getFechaVencimiento());
		
		return this.tareaRepository.save(tareaBD);
	}
	
	public boolean deleteDeclarativo (int idTarea) {
		boolean result = false;
		
		if(this.tareaRepository.existsById(idTarea)) {
			this.tareaRepository.deleteById(idTarea);
			result = true;
		}
		
		return result;
	}
	
	public boolean deleteFuncional (int idTarea) {
		return this.tareaRepository.findById(idTarea)
				.map(t -> {
					this.tareaRepository.deleteById(idTarea);
					return true;
				})
				.orElse(false);
	}
	
	//Ejemplos Stream
	//Obtener el número total de tareas completadas
	public long totalTareasCompletadasFuncional () {
		return this.tareaRepository.findAll().stream()
				.filter(t -> t.getEstado() == Estado.COMPLETADA)
				.count();
	}
	
	public long totalTareasCompletadas () {
		return this.tareaRepository.countByEstado(Estado.COMPLETADA);
	}
	
	//Obtener una lista de las fechas de vencimiento de las tareas que estén en progreso
	public List<LocalDate> fechasVencimientoEnProgresoFuncional(){
		return this.tareaRepository.findAll().stream()
				.filter(t -> t.getEstado() == Estado.EN_PROGRESO)
				.map(t -> t.getFechaVencimiento())
				.collect(Collectors.toList());
	}
	
	public List<LocalDate> fechasVencimientoEnProgreso(){
		return this.tareaRepository.findByEstado(Estado.EN_PROGRESO).stream()
				.map(t -> t.getFechaVencimiento())
				.collect(Collectors.toList());
	}
	
	
	//Obtener las tareas vencidas
	public List<Tarea> tareasVencidasFuncional (){
		return this.tareaRepository.findAll().stream()
				.filter(t -> t.getFechaVencimiento().isBefore(LocalDate.now()))
				.collect(Collectors.toList());
	}
	
	public List <Tarea> tareasVencidas(){
		return this.tareaRepository.findByFechaVencimientoBefore(LocalDate.now());
	}
	
	//Obtener las tareas no vencidas
	public List<Tarea> tareasNoVencidasFuncional (){
		return this.tareaRepository.findAll().stream()
				.filter(t -> t.getFechaVencimiento().isAfter(LocalDate.now()))
				.collect(Collectors.toList());
	}
	
	//Obtener las tareas pendientes
	public List<Tarea> tareasPendientesFuncional(){
		return this.tareaRepository.findAll().stream()
				.filter(t -> t.getEstado() == (Estado.PENDIENTE))
				.collect(Collectors.toList());
	}
	
	//Obtener las tareas en progreso
	public List<Tarea> tareasEnProgresoFuncional(){
		return this.tareaRepository.findAll().stream()
				.filter(t -> t.getEstado() == (Estado.EN_PROGRESO))
				.collect(Collectors.toList());
	}
	
	//Obtener las tareas completadas
	public List<Tarea> tareasCompletadasFuncional(){
		return this.tareaRepository.findAll().stream()
				.filter(t -> t.getEstado() == (Estado.COMPLETADA))
				.collect(Collectors.toList());
	}
	
	//Obtener los títulos de las tareas pendientes
	public List<String> titulosPendientesFuncional(){
		return this.tareaRepository.findAll().stream()
				.filter(t -> t.getEstado() == Estado.PENDIENTE)
				.map(t -> t.getTitulo())
				.collect(Collectors.toList());
	}
	
	public List<String> titulosPendientes(){
		return this.tareaRepository.findByEstado(Estado.PENDIENTE).stream()
				.map(t -> t.getTitulo())
				.collect(Collectors.toList());
	}
	
	//Obtener las tareas ordenadas por fecha de vencimiento
	public List<Tarea> ordenadasFechaVencimientoFuncional (){
		return this.tareaRepository.findAll().stream()
			.sorted((t1, t2) -> t1.getFechaVencimiento().compareTo(t2.getFechaVencimiento()))
			.collect(Collectors.toList());
	}
	
	public List<Tarea> ordenadasFechaVencimiento(){
		return this.tareaRepository.findAllByOrderByFechaVencimiento();
	}
	
	
	//Iniciar una tarea
	public Tarea iniciarTarea (int idTarea) {
		if(!this.tareaRepository.existsById(idTarea)) {
			throw new TareaNotFoundException("No existe la tarea con ID: " + idTarea);
		}
		if(this.tareaRepository.findById(idTarea).get().getEstado() != Estado.PENDIENTE) {
			throw new TareaException("Para iniciar la tarea, su estado debe ser pendiente");
		}
		Tarea tarea = this.findById(idTarea);
		tarea.setEstado(Estado.EN_PROGRESO);
		
		return this.tareaRepository.save(tarea);
	}
	
	
	//Completar una tarea
	public Tarea completarTarea (int idTarea) {
		if(!this.tareaRepository.existsById(idTarea)) {
			throw new TareaNotFoundException("No existe la tarea con ID: " + idTarea);
		}
		if(this.tareaRepository.findById(idTarea).get().getEstado() != Estado.EN_PROGRESO) {
			throw new TareaException("Para completar la tarea, su estado debe ser en progreso.");
		}
		Tarea tarea = this.findById(idTarea);
		tarea.setEstado(Estado.COMPLETADA);
		
		return this.tareaRepository.save(tarea);
	}
	
	//Obtener las tareas mediante su titulo
	public List<Tarea> buscadorTareaTitulo(String titulo){
		return this.tareaRepository.findAll().stream()
				.filter(t -> t.getTitulo().contains(titulo))
				.collect(Collectors.toList());
	}
	
	
	
	
	
}

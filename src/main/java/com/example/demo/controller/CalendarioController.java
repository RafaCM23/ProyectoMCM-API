package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Cita;
import com.example.demo.model.Mes;
import com.example.demo.model.Profesional;
import com.example.demo.services.AgendaService;
import com.example.demo.services.ProfesionalService;

@CrossOrigin(origins = "https://rafacm23.github.io")
@RestController
public class CalendarioController {

	//- Este controlador se ocupa de todo lo relacionado con el calendario y las citas previas -//
	
    @Autowired private AgendaService agendaService;
    @Autowired private ProfesionalService profService;
    
    /**
     * Esta llamada devuelve las citas en las proximas 2 semanas del profesional que realiza la llamada.
     * Si no tiene ninguna devuelve notFound.
     * @param verificadas
     * @return ResponseEntity<?>
     */
	@GetMapping("/citasProximas")
    public ResponseEntity<?> getCitasProximas(@RequestParam(required=false) Boolean verificadas){
		if( verificadas ==null) {return ResponseEntity.badRequest().body("Falta datos");}
		String email=(String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return (verificadas? agendaService.getCitasProximasVerificadas(email) : agendaService.getCitasProximasSinVerificar(email));
    }
	
	/**
	 * Esta llamada recibe un id de una cita. Si la encuentra y el que hace la llamada es un profesional, la cita es aceptada.
	 * Si no la encuentra devuelve notFound.
	 * @param idCita
	 * @return ResponseEntity<?>
	 */
	@GetMapping("/acepta/cita/{id}")
    public ResponseEntity<?> aceptaCita(@PathVariable Long id){
		String email=(String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	   	return agendaService.aceptaCita(id,email);
    }
	
	/**
	 * Esta llamada recibe un id de una cita. Si la encuentra y el que hace la llamada es un profesional, la cita es rechazada.
	 * Si no la encuentra devuelve notFound.
	 * @param id
	 * @param motivo
	 * @return ResponseEntity<?>
	 */
	@GetMapping("/rechaza/cita/{id}")
    public ResponseEntity<?> rechazaCita(@PathVariable Long id,@RequestParam int motivo){
		String email=(String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	   	return agendaService.rechazaCita(id,email,motivo);
    }
	
	/**
	 * Esta llamada recibe un id de una cita. Si la encuentra la cita es cancelada por parte del usuario.
	 * Si no la encuentra devuelve not found.
	 * @param idCita
	 * @return ResponseEntity<?>
	 */
	@GetMapping("/cancelar/cita")
    public ResponseEntity<?> userCancelaCIta(@RequestParam int id){
		return agendaService.cancelarCita(id);
    }
	
    /**
     * Esta llamada recibe una idProfesional, un anio y mes y devuelve el mes concreto buscado.
     * Si no lo encuentra lo crea.
     * @param idProfesional
     * @param anio
     * @param mes
     * @return  ResponseEntity<?>
     */
    @GetMapping("/profesional/{id}/agenda/anio/{anio}/mes/{mes}")
    public ResponseEntity<?> getAgenda(@PathVariable int id,@PathVariable int anio,@PathVariable int mes){
    	if(id==0 || anio==0 || mes==0) {return ResponseEntity.badRequest().body("Faltan datos");}
    	Mes resp=agendaService.getAgenda(id,anio,mes);
    	if(resp!=null) {return ResponseEntity.ok(resp);}
    	else {return ResponseEntity.notFound().build();}
    	
    }
    
    /**
     * Esta llamada recibe una idProfesional, un anio y mes y una cita. Si los datos son correctos
     * crea una cita y envia un correo de confirmacion de la peticion de la cita.
     * Si no encuentra el anio, mes o dia, lo crea. Si no encuentra el profesional o la agenda, devuelve el error.
     * @param idProfesional
     * @param anio
     * @param mes
     * @param Cita
     * @return ResponseEntity<?>
     */
    @PostMapping("/profesional/{id}/agenda/anio/{anio}/mes/{mes}")
    public ResponseEntity<?> postCita(@PathVariable int id,@PathVariable int anio,@PathVariable int mes,
    		@RequestBody Cita cita){
    	if(id==0 || anio==0 || mes==0 || cita==null) {return ResponseEntity.badRequest().body("Faltan datos");}
    		String resp=agendaService.nuevaReserva(id,anio,mes,cita);
    		if(resp.equals("Success")) { return ResponseEntity.ok().build(); }
    		else { return ResponseEntity.badRequest().body(resp); }
    }
    
    /**
     * Esta llamada recibe una idProfesional, un anio y mes y un dia. Si encuentra el dia
     * y el que hizo la llamada es un profesional, marca ese dia como ocupado y cancela todas las citas de ese dia.
     * @param id
     * @param anio
     * @param mes
     * @param dia
     * @return ResponseEntity<?>
     */
    @GetMapping("/ocupado/profesional/{id}/agenda/anio/{anio}/mes/{mes}/dia/{dia}")
    public ResponseEntity<?> ocupaDia(@PathVariable Long id,@PathVariable int anio,@PathVariable int mes,@PathVariable int dia){
    	String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    	if(id==0 || anio==0 || mes==0 || dia==0) {return ResponseEntity.badRequest().body("Faltan datos");}
    	Profesional p = profService.getProfesionalByEmail(email);
    	return agendaService.ocupaDia(id,anio,mes,dia,p);

    }
    

    /**
     * Esta llamada recibe una idProfesional, un anio y mes y un dia. Si encuentra el dia
     * y el que hizo la llamada es un profesional, marca ese dia como vacaciones y cancela todas las citas de ese dia.
     * @param id
     * @param anio
     * @param mes
     * @param dia
     * @return ResponseEntity<?>
     */
    @GetMapping("/vacaciones/profesional/{id}/agenda/anio/{anio}/mes/{mes}/dia/{dia}")
    public ResponseEntity<?> vacacionesDia(@PathVariable Long id,@PathVariable int anio,@PathVariable int mes,@PathVariable int dia){
    	String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    	if(id==0 || anio==0 || mes==0 || dia==0) {return ResponseEntity.badRequest().body("Faltan datos");}
    	Profesional p = profService.getProfesionalByEmail(email);
    		return  agendaService.vacacionesDia(id,anio,mes,dia,p);
    }
}

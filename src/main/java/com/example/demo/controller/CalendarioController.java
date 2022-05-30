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

    @Autowired private AgendaService agendaService;
    @Autowired private ProfesionalService profService;
    
	@GetMapping("/citasProximas")
    public ResponseEntity<?> getCitasProximas(@RequestParam(required=false) Boolean verificadas){
		if( verificadas ==null) {return ResponseEntity.badRequest().body("Falta datos");}
	   	ResponseEntity<?> resp;
		String email=(String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	   	resp= (verificadas==true? agendaService.getCitasProximasVerificadas(email) : agendaService.getCitasProximasSinVerificar(email));
	   	return resp;
    }
	
	@GetMapping("/acepta/cita/{id}")
    public ResponseEntity<?> aceptaCita(@PathVariable Long id){
	   	ResponseEntity<?> resp;
		String email=(String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	   	resp= agendaService.aceptaCita(id,email);
	   	return resp;
    }
	
	@GetMapping("/rechaza/cita/{id}")
    public ResponseEntity<?> rechazaCita(@PathVariable Long id,@RequestParam int motivo){
	   	ResponseEntity<?> resp;
		String email=(String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	   	resp= agendaService.rechazaCita(id,email,motivo);
	   	return resp;
    }
	
	@GetMapping("/cancelar/cita")
    public ResponseEntity<?> userCancelaCIta(@RequestParam int id){
	   	
		return agendaService.cancelarCita(id);

    }
	
    
    @GetMapping("/profesional/{id}/agenda/anio/{anio}/mes/{mes}")
    public ResponseEntity<?> getAgenda(@PathVariable int id,@PathVariable int anio,@PathVariable int mes){
    	if(id==0 || anio==0 || mes==0) {return ResponseEntity.badRequest().body("Faltan datos");}
    	Mes resp=agendaService.getAgenda(id,anio,mes);
    	if(resp!=null) {return ResponseEntity.ok(resp);}
    	else {return ResponseEntity.notFound().build();}
    	
    }
    
    @PostMapping("/profesional/{id}/agenda/anio/{anio}/mes/{mes}")
    public ResponseEntity<?> postCita(@PathVariable int id,@PathVariable int anio,@PathVariable int mes,
    		@RequestBody Cita cita){
    	if(id==0 || anio==0 || mes==0 || cita==null) {return ResponseEntity.badRequest().body("Faltan datos");}
    		String resp=agendaService.nuevaReserva(id,anio,mes,cita);
    		if(resp.equals("Success")) { return ResponseEntity.ok().build(); }
    		else { return ResponseEntity.badRequest().body(resp); }
    }
    
    @GetMapping("/ocupado/profesional/{id}/agenda/anio/{anio}/mes/{mes}/dia/{dia}")
    public ResponseEntity<?> ocupaDia(@PathVariable Long id,@PathVariable int anio,@PathVariable int mes,@PathVariable int dia){
    	String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    	if(id==0 || anio==0 || mes==0 || dia==0) {return ResponseEntity.badRequest().body("Faltan datos");}
    	Profesional p = profService.getDatos(email);
    		ResponseEntity<?> resp = agendaService.ocupaDia(id,anio,mes,dia,p);
    		return resp;
    }
    

    @GetMapping("/vacaciones/profesional/{id}/agenda/anio/{anio}/mes/{mes}/dia/{dia}")
    public ResponseEntity<?> vacacionesDia(@PathVariable Long id,@PathVariable int anio,@PathVariable int mes,@PathVariable int dia){
    	String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    	if(id==0 || anio==0 || mes==0 || dia==0) {return ResponseEntity.badRequest().body("Faltan datos");}
    	Profesional p = profService.getDatos(email);
    		ResponseEntity<?> resp = agendaService.vacacionesDia(id,anio,mes,dia,p);
    		return resp;
    }
}

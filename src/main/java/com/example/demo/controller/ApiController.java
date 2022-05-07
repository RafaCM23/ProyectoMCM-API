package com.example.demo.controller;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Cita;
import com.example.demo.model.Comentario;

import com.example.demo.model.Mes;
import com.example.demo.model.Profesional;
import com.example.demo.services.AgendaService;
import com.example.demo.services.ComentarioService;
import com.example.demo.services.ProfesionalService;


@CrossOrigin(origins = "https://rafacm23.github.io")
@RestController
public class ApiController {

	
	
    @Autowired private ComentarioService comentarioService;
    @Autowired private AgendaService agendaService;
    @Autowired private ProfesionalService profService;
    
    
    
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
    		return ResponseEntity.ok().build();
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
    
    
    @GetMapping("/mifoto")
    public ResponseEntity<?> getFoto(){
    	String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    	String resp=profService.getImg(email);
    	if(email.isEmpty()  || resp==null) {return ResponseEntity.notFound().build();}
    	else{  		
    		return ResponseEntity.ok('"'+resp+'"');
    				}
    }
    
    @GetMapping("/misdatos")
    public ResponseEntity<?> getDatos(){
    	String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    	Profesional resp=profService.getDatos(email);
    	if(email.isEmpty()  || resp==null) {return ResponseEntity.notFound().build();}
    	else{  		
    		return ResponseEntity.ok(resp);
    				}
    }
    
   
    @PutMapping("/profesional/{id}")
    public ResponseEntity<?> postDatos(@RequestBody(required=false) Profesional prof,@RequestParam(required=false) Long id){
    	String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    	Profesional resp=profService.getDatos(prof.getEmail());
    	if(!email.equals(prof.getEmail()) && !email.equals("administrador")) {return ResponseEntity.badRequest().body("No puede cambiar los datos de otro");}
    	return profService.putDatos(prof,resp);
    }
    
    @GetMapping("/profesional/{id}")
    public ResponseEntity<?> getProfesional(@PathVariable Long id){
    	Profesional prof=profService.getProfesional(id);
    	if(prof==null) {
    		return ResponseEntity.notFound().build();
    	}
    	else {
    		return ResponseEntity.ok(prof);
    	}
    }
    
    
    @PostMapping("/profesional")
    public ResponseEntity<?> postProfesional(@RequestBody(required=false) Profesional prof){
    	
    	int resp=profService.newProfesional(prof);
    	if(resp==-1) return ResponseEntity.badRequest().body("Faltan datos");
    	else return ResponseEntity.ok(HttpStatus.CREATED);
    }
    
    
    @GetMapping("/profesionales")
    public ResponseEntity<List<Profesional>> getProfesionalesSinConfirmar(@RequestParam(required=false) Boolean verificado){
    	List<Profesional> profs= null;
    	if(verificado==null || verificado==true) {profs=profService.getProfesionalesVerificados();}
    	else {profs =profService.getProfesionalesSinVerificar();}
    	
    	if(profs==null) {
    		return ResponseEntity.notFound().build();
    		}
    	else {
    		return ResponseEntity.ok(profs);
    		}
    }
    
    
    
    @PostMapping("/correoOcupado")
    public ResponseEntity<?> correOcupado(@RequestBody(required=false) String correo){
    	
    	if(correo==null) return ResponseEntity.badRequest().body("Falta correo");
    	int respuesta=profService.correoOcupado(correo);
    	return respuesta==0 ? ResponseEntity.notFound().build() : ResponseEntity.ok().build();
    }
    
    
    
    /**
     * Este metodo recibe un comentario y si todos los campos son válidos se guarda
     * @param comentario
     * @return ResponseEntity
     */
    @PostMapping("/comentario")
    	public ResponseEntity<?> postComentarioServicio(@RequestBody(required=false) Comentario comentario){
    	
    	if( comentario==null || comentario.getAutor()==null || comentario.getAutor()=="" ||comentario.getContenido()==null
    			|| comentario.getContenido()=="" /*|| comentario.getFecha()==null*/) {
    		return ResponseEntity.badRequest().body("Faltan datos");
    	}
    	else {
    		if(comentarioService.postComentario(comentario)==1) {return ResponseEntity.ok(HttpStatus.CREATED);}
    		else {return ResponseEntity.badRequest().body("Ya existe el comentario");}
    		
    	}
    }
    
    //------------------------------------ Admin ------------------------------------//
    
    @GetMapping("/isAdmin")
    public ResponseEntity<?> isAdmin() {
    	String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    	if(email.isEmpty() || email==null || !email.equals("administrador")) {
    		return ResponseEntity.ok(false);
    	}
    	else {
    		return ResponseEntity.ok(true);
    	}
    }
    
    @GetMapping("/allprofesionales")
    public ResponseEntity<?> getAllProfesionales(){
    	String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    	if(!email.equals("administrador") || email.isEmpty()) {return ResponseEntity.badRequest().body("Debe ser admin");}
    	List<Profesional> profs= profService.getProfesionales();    	
    	if(profs==null) {
    		return ResponseEntity.notFound().build();
    		}
    	else {
    		return ResponseEntity.ok(profs);
    		}
    }
    
    
    
    //------------------------------------ Identificación ------------------------------------//
    
    @GetMapping("/whois")
    public ResponseEntity<?> whoIs(){
    	String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    	Profesional resp= profService.getDatos(email);
    	if(resp==null) {return ResponseEntity.notFound().build();}
    	else {return ResponseEntity.ok(resp.getId());}
    }
    
    @GetMapping("/verifica/profesional/{id}")
	public ResponseEntity<?> verificaProf(@PathVariable(required=false) Long id){
	
	ResponseEntity<?> resp= profService.verificaProf(id);
	return resp;
    }
    @GetMapping("/rechaza/profesional/{id}")
	public ResponseEntity<?> rechazaProf(@PathVariable(required=false) Long id){
	
	ResponseEntity<?> resp= profService.rechazaProf(id);
	return resp;
}
    
    @PostMapping("/auth/login")
    	public ResponseEntity<?> login(@RequestBody(required=false) Profesional prof){
    	
    	ResponseEntity<?> resp=profService.login(prof);
    	return resp;
    	
    }

   

	
}


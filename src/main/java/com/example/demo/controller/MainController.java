package com.example.demo.controller;

import java.util.List;

import javax.swing.text.html.HTML;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.model.Profesional;
import com.example.demo.services.CorreoService;
import com.example.demo.services.ProfesionalService;


@CrossOrigin(origins = "https://rafacm23.github.io")
@RestController
public class MainController {

	
	

    @Autowired private ProfesionalService profService;
    
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
    public ResponseEntity<?> putDatos(@RequestBody(required=false) Profesional prof,@PathVariable(required=false) Long id){
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
    
    
    @DeleteMapping("/profesional/{id}")
    public ResponseEntity<?> deleteProfesional(@PathVariable(required=false) Long id){
    	String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    	return profService.borraProfesional(id,email);
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
    
    
    
    
    //------------------------------------ Admin ------------------------------------//
    
    
    
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
    
    @GetMapping("/verifica/profesional/{id}")
	public ResponseEntity<?> verificaProf(@PathVariable(required=false) Long id){
	
		return profService.verificaProf(id);

    }
    
    @GetMapping("/rechaza/profesional/{id}")
	public ResponseEntity<?> rechazaProf(@PathVariable(required=false) Long id){
	
		return profService.rechazaProf(id);

    }
    
    
    //------------------------------------ Identificación ------------------------------------//
    
    @PostMapping("/profesional")//Registro
    public ResponseEntity<?> postProfesional(@RequestBody(required=false) Profesional prof){
    	
    	int resp=profService.newProfesional(prof);
    	if(resp==-1) return ResponseEntity.badRequest().body("Faltan datos");
    	else return ResponseEntity.ok(HttpStatus.CREATED);
    }
    
    
    
    @PostMapping("/auth/login")
    	public ResponseEntity<?> login(@RequestBody(required=false) Profesional prof){
    	
    	ResponseEntity<?> resp=profService.login(prof);
    	return resp;
    	
    }

  //------------------------------------ Utilidades ------------------------------------//
    
    @GetMapping("/whois")
    public ResponseEntity<?> whoIs(){
    	String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    	System.out.println(email);
    	Profesional resp= profService.getDatos(email);
    	System.out.println(resp);
    	if(resp==null) {return ResponseEntity.notFound().build();}
    	else {return ResponseEntity.ok(resp.getId());}
    }
    
    @GetMapping("/isAdmin")
    	public ResponseEntity<?> isAdmin() {
    	String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    	if(email.isEmpty() || !email.equals("administrador")) {
    		return ResponseEntity.ok(false);
    	}

    		return ResponseEntity.ok(true);
    }
    
    @PostMapping("/correoOcupado")
    public ResponseEntity<?> correOcupado(@RequestBody(required=false) String correo){
    	
    	if(correo==null) return ResponseEntity.badRequest().body("Falta correo");
    	int respuesta=profService.correoOcupado(correo);
    	return respuesta==0 ? ResponseEntity.notFound().build() : ResponseEntity.ok().build();
    }
    
    
    @GetMapping(value="/profesional/{id}/imagen",produces= {MediaType.APPLICATION_OCTET_STREAM_VALUE})
    public Resource getImagenProf(@PathVariable(required=false) Long id){
    	Resource resp = profService.getImgProf(id);
        return resp;
    }
    
    @PostMapping("/profesional/{id}/imagen")
    public ResponseEntity<?> subirImagenProf(@RequestBody(required=false) MultipartFile file,@PathVariable(required=false) Long id){
    	String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    		ResponseEntity<?> resp = profService.setImagen(id,email, file);
    		return resp;
    	
    }
    
    
    

	
}


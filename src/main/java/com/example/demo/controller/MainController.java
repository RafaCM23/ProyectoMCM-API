package com.example.demo.controller;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
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
import com.example.demo.services.ProfesionalService;


@CrossOrigin(origins = "https://rafacm23.github.io")
@RestController
public class MainController {

	
	//- Este controlador se ocupa de todo lo relacionado con la identificacion y autenticacion de los profesionales y admin, -//
	//- Asi como la gestion de estos -//

    @Autowired private ProfesionalService profService;
    
    /**
     * Esta llamada devuelve los datos del profesional que realiza la llamada. Si no encuentra el profesional devuelve notFound.
     * @return ResponseEntity<?>
     */
    @GetMapping("/misdatos")
    public ResponseEntity<?> getDatos(){
    	String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    	return profService.getDatos(email);
    	
    }
    
    /**
     * Esta llamada recibe un booleano para indicar si se buscan los profesionales verificados o sin verificar.
     * Segun este valor, y si hay al menos uno en ese grupo, lo devuelve, en caso contrario devuelve notFound.
     * @param verificado
     * @return  ResponseEntity<?>
     */
    @GetMapping("/profesionales")
    public ResponseEntity<List<Profesional>> getProfesionales(@RequestParam(required=false) Boolean verificado){
    	List<Profesional> profs= null;
    	if(verificado==null || verificado==true) {profs=profService.getProfesionalesVerificados();}
    	else {profs =profService.getProfesionalesSinVerificar();}
    	return profs==null ? ResponseEntity.notFound().build() : ResponseEntity.ok(profs);
    }
    
    /**
     * Esta llamada recibe un id de Profesional y si lo encuentra lo devuelve, en caso contrario devuelve notFound.
     * @param idProfesional
     * @return ResponseEntity<?>
     */
    @GetMapping("/profesional/{id}")
    public ResponseEntity<?> getProfesional(@PathVariable(required=false) Long id){
    	Profesional prof=profService.getProfesional(id);
    	return prof==null ? ResponseEntity.notFound().build() : ResponseEntity.ok(prof);
    } 
    
    /**
     * Esta llamada recibe una id de Profesional, y un Profesional nuevo, cuyos datos seran ahora los del profesional buscado (PUT).
     * Si no encuentra el profesional devuelve notFound.
     * @param idProf
     * @return ResponseEntity<?>
     */
    @PutMapping("/profesional/{id}")
    public ResponseEntity<?> putDatos(@RequestBody(required=false) Profesional prof,@PathVariable(required=false) Long id){
    	String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    	return profService.putDatosProfesional(prof,id,email);
    } 
    
    /**
     * Esta llamada recibe un id de Profesional. Si lo encuentra y la llamada la ha realizado el administrador,
     * este profesional es borrado y sus post pasan a ser del administrador. En caso contrario devuelve notFound o falta de
     * permisos respectivamente.
     * @param idProfesional
     * @return ResponseEntity<?>
     */
    @DeleteMapping("/profesional/{id}")
    public ResponseEntity<?> deleteProfesional(@PathVariable(required=false) Long id){
    	String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    	return profService.borraProfesional(id,email);
    }
   
    
    
    
    //------------------------------------ Admin ------------------------------------//
    
    
    /**
     * Esta llamada devuelve todos los profesionales, verificados o sin verificar, que no sea el admin.
     * Si no encuentra ninguno devuelve not found.
     * @return ResponseEntity<?>
     */
    @GetMapping("/allprofesionales")
    public ResponseEntity<?> getAllProfesionales(){
    	String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    	if(!email.equals("administrador") || email.isEmpty()) {return ResponseEntity.badRequest().body("Debe ser admin");}
    	List<Profesional> profs= profService.getProfesionales();    	
    	return profs==null ? ResponseEntity.notFound().build() : ResponseEntity.ok(profs);
    }
    
    /**
     * Esta llamada recibe un id de Profesional, si no esta verificado lo verifica.
     * @param idProfesional
     * @return ResponseEntity<?>
     */
    @GetMapping("/verifica/profesional/{id}")
	public ResponseEntity<?> verificaProf(@PathVariable(required=false) Long id){
    	if(id==null) {return ResponseEntity.badRequest().body("Falta id");}
    	String email=(String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return profService.verificaProf(id,email);

    }
    
    /**
     * Esta llamada recibe un id de Profesional, si lo encuentra lo borra.
     * @param idProfesional
     * @return ResponseEntity<?>
     */
    @GetMapping("/rechaza/profesional/{id}")
	public ResponseEntity<?> rechazaProf(@PathVariable(required=false) Long id){
    	if(id==null) {return ResponseEntity.badRequest().body("Falta id");}
    	String email=(String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return profService.borraProfesional(id,email);

    }
    
    
    //------------------------------------ Identificación ------------------------------------//
    
    /**
     * Esta llamada recibe un profesional, y si no faltan datos lo crea. El administrador debe
     * verificarlo para poder loguear.
     * @param Profesional
     * @return ResponseEntity<?>
     */
    @PostMapping("/profesional")//Registro
    public ResponseEntity<?> postProfesional(@RequestBody(required=false) Profesional prof){
    	return profService.newProfesional(prof);
    }
    
    
    /**
     * Esta llamada recibe un email y contraseña en dentro de un Profesional. Si el correo y contraseña
     * coinciden con los guardados, devuelve un token JWT. En caso contrario indica el error.
     * @param Profesional
     * @return ResponseEntity<?>
     */
    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody(required=false) Profesional prof){
    	return profService.login(prof);
    }

  //------------------------------------ Utilidades ------------------------------------//
    
    /**
     * Esta llamada recibe un email. Si lo encuentra registrado como profeisonal devuelve la id.
     * Si no lo encuentra devuelve notFound.
     * @return
     */
    @GetMapping("/whois")
    public ResponseEntity<?> whoIs(){
    	String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    	return profService.getProfIdByEmail(email);
    }
    
    /**
     * Esta llamada recibe un email en la cabecera y devuelve true o false segun si el profesional es el administrador o no.
     * @return ResponseEntity<Boolean>
     */
    @GetMapping("/isAdmin")
    public ResponseEntity<Boolean> isAdmin() {
    	String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    	return (email.isEmpty() || !email.equals("administrador")) ?  ResponseEntity.ok(false) : ResponseEntity.ok(true);
    }
    
    /**
     * Esta llamada recibe un correo, y comprueba si ya ha sido usado en otro profesional.
     * Devuelve notFound si no existe, y ok si existe.
     * @param correo
     * @return ResponseEntity<?>
     */
    @PostMapping("/correoOcupado")
    public ResponseEntity<?> correOcupado(@RequestBody(required=false) String correo){
    	if(correo==null) return ResponseEntity.badRequest().body("Falta correo");
    	int respuesta=profService.correoOcupado(correo);
    	return respuesta==0 ? ResponseEntity.notFound().build() : ResponseEntity.ok().build();
    }
    
    /**
     * Esta llamada recibe un id de un Profesional. Si lo encuentra devuelve su imagen.
     * Si no lo encuentra devuelve notFound.
     * @param idProfesional
     * @return Resource (PNG)
     */
    @GetMapping(value="/profesional/{id}/imagen",produces= {MediaType.APPLICATION_OCTET_STREAM_VALUE})
    public Resource getImagenProf(@PathVariable(required=false) Long id){
    	return  profService.getImgProf(id);
    }
    
    /**
     * Esta llamada recibe un id de un Profesional y una imagen. Si encuentra el profesional y puede procesar la imagen, entonces
     * se crea la imagen y se vincula al Profesional.
     * @param Imagen 
     * @param id Profesional
     * @return ResponseEntity<?>
     */
    @PostMapping("/profesional/{id}/imagen")
    public ResponseEntity<?> subirImagenProf(@RequestBody(required=false) MultipartFile file,@PathVariable(required=false) Long id){
    	String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    		return  profService.setImagen(id,email, file);
    	
    }
    
    
    

	
}


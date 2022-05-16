package com.example.demo.controller;

import java.util.List;

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

import com.example.demo.model.Categoria;
import com.example.demo.model.Cita;
import com.example.demo.model.Comentario;
import com.example.demo.model.ComentarioPost;
import com.example.demo.model.Mes;
import com.example.demo.model.Persona;
import com.example.demo.model.Post;
import com.example.demo.model.Profesional;
import com.example.demo.services.AgendaService;
import com.example.demo.services.BlogService;
import com.example.demo.services.ComentarioService;
import com.example.demo.services.ProfesionalService;


@CrossOrigin(origins = "https://rafacm23.github.io")
@RestController
public class ApiController {

	
	
    @Autowired private ComentarioService comentarioService;
    @Autowired private AgendaService agendaService;
    @Autowired private ProfesionalService profService;
    @Autowired private BlogService blogService;
    
    
    
    @GetMapping("/misdatos")
    public ResponseEntity<?> getDatos(){
    	String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    	Profesional resp=profService.getDatos(email);
    	if(email.isEmpty()  || resp==null) {return ResponseEntity.notFound().build();}
    	else{  		
    		return ResponseEntity.ok(resp);
    		}
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
    
    @PutMapping("/profesional/{id}")
    public ResponseEntity<?> postDatos(@RequestBody(required=false) Profesional prof,@PathVariable(required=false) Long id){
    	String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    	Profesional resp=profService.getDatos(prof.getEmail());
    	if(!email.equals(prof.getEmail()) && !email.equals("administrador")) {return ResponseEntity.badRequest().body("No puede cambiar los datos de otro");}
    	return profService.putDatos(prof,resp);
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
    
    //------------------------------------ Comentarios ------------------------------------//
    
    /**
     * Este metodo recibe un comentario y si todos los campos son válidos se guarda
     * @param comentario
     * @return ResponseEntity
     */
    @PostMapping("/comentario")
    	public ResponseEntity<?> postComentarioServicio(@RequestBody(required=false) Comentario comentario){
    	
    	ResponseEntity<?> resp = comentarioService.postComentario(comentario);
    	return resp;
    }
    
    @GetMapping("/comentarios")
	public ResponseEntity<?> postComentarioServicio(@RequestParam(required=false) Boolean verificado){
	if(verificado==null) {return ResponseEntity.badRequest().body("Faltan datos");}
	if(verificado==false) {
		String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return comentarioService.getComentariosSinVerificar(email);}
	else {
		return comentarioService.getComentariosVerificados();
		}
	}
	
	@GetMapping("/verifica/comentario/{id}")
	public ResponseEntity<?> verificaComentario(@PathVariable(required=false) Long id){
	if(id==null) {return ResponseEntity.badRequest().body("Faltan datos");}
	
	String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	ResponseEntity<?> resp = comentarioService.verificaComentario(id,email);
	return resp;
	}
	
	@GetMapping("/rechaza/comentario/{id}")
	public ResponseEntity<?> rechazaComentario(@PathVariable(required=false) Long id){
	if(id==null) {return ResponseEntity.badRequest().body("Faltan datos");}
	
	String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	ResponseEntity<?> resp = comentarioService.rechazaComentario(id,email);
	return resp;
	}
	
	@DeleteMapping("/comentario/{id}")
	public ResponseEntity<?> borraComentario(@PathVariable(required=false) Long id){
	if(id==null) {return ResponseEntity.badRequest().body("Faltan datos");}
	
	String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	ResponseEntity<?> resp = comentarioService.rechazaComentario(id,email);
	return resp;
	}
    
  //------------------------------------ Calendario ------------------------------------//
	
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
    public ResponseEntity<?> rechazaCita(@PathVariable Long id){
	   	ResponseEntity<?> resp;
		String email=(String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	   	resp= agendaService.rechazaCita(id,email);
	   	return resp;
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
    
  //------------------------------------ Blog ------------------------------------//
    
    @PostMapping("/post")
    public ResponseEntity<?> nuevoPost(@RequestBody Post p){
    	String email=(String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    	ResponseEntity<?> resp =blogService.creaPost(p,email);
    	return resp;
    }
    
    @GetMapping("/posts")
    public ResponseEntity<?> getAllPostsPag(@RequestParam int page){
    	ResponseEntity<?> resp =blogService.getNextPosts(page);
    	return resp;
    }
   
    
    @GetMapping("/post/{id}")
    public ResponseEntity<?> getPost(@PathVariable Long id){
    	ResponseEntity<?> resp =blogService.getPost(id);
    	return resp;
    }
    
    //--Administracion Blog--//
    
    @DeleteMapping("/post/{id}")
    public ResponseEntity<?> deletePost(@PathVariable Long id){
    	String email=(String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    	ResponseEntity<?> resp =blogService.borraPost(id,email);
    	return resp;
    }
    @PutMapping("/post/{id}")
    public ResponseEntity<?> editaPost(@PathVariable Long id,@RequestBody Post p){
    	String email=(String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    	ResponseEntity<?> resp =blogService.editaPost(id,email,p);
    	return resp;
    }
    
    @GetMapping("/allPosts")
    public ResponseEntity<?> getAllPosts(){
    	String email=(String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    	ResponseEntity<?> resp =blogService.getAllPosts(email);
    	return resp;
    }
    
    
    
    
    
    
    
    @PostMapping("/categoria")
    public ResponseEntity<?> postCategoria(@RequestBody Categoria c){
    	String email=(String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    	ResponseEntity<?> resp=this.blogService.creaCategoria(c.getNombre(), email);
    	return resp;
    }
    
    @GetMapping("/categorias")
    public ResponseEntity<?> getCategorias(){
    	String email=(String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    	ResponseEntity<?> resp=this.blogService.recuperaCategorias(email);
    	return resp;
    }
    
    @PostMapping("/post/{id}/comentario")
    public ResponseEntity<?> postComentario(@PathVariable Long id,@RequestBody ComentarioPost comentario){

    	ResponseEntity<?> resp=this.blogService.creaComentario(id, comentario);
    	return resp;
    }
    
    @DeleteMapping("/post/{idPost}/comentario/{idComent}")
    public ResponseEntity<?> deleteComentario(@PathVariable Long idPost,@PathVariable Long idComent){
    	String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    	ResponseEntity<?> resp=this.blogService.deleteComentario(idPost, idComent,email);
    	return resp;
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
    
    
    //------------------------------------ Identificación ------------------------------------//
    
    @GetMapping("/whois")
    public ResponseEntity<?> whoIs(){
    	String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    	Profesional resp= profService.getDatos(email);
    	if(resp==null) {return ResponseEntity.notFound().build();}
    	else {return ResponseEntity.ok(resp.getId());}
    }
    
    
    
    @PostMapping("/auth/login")
    	public ResponseEntity<?> login(@RequestBody(required=false) Profesional prof){
    	
    	ResponseEntity<?> resp=profService.login(prof);
    	return resp;
    	
    }

  //------------------------------------ Utilidades ------------------------------------//
    
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
    
    @PostMapping("/post/{id}/imagen")
    public ResponseEntity<?> subirImagenPost(@RequestBody(required=false) MultipartFile file,@PathVariable(required=false) Long id){
    	String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    		ResponseEntity<?> resp = blogService.setImagen(id,email, file);
    		return resp;
    	
    }
    @GetMapping(value="/post/{id}/imagen",produces= {MediaType.APPLICATION_OCTET_STREAM_VALUE})
    public Resource getImagenPost(@PathVariable(required=false) Long id){
    	Resource resp = blogService.getImgPost(id);
        return resp;
    }
    
    

	
}


package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Comentario;
import com.example.demo.services.ComentarioService;

@CrossOrigin(origins = "https://rafacm23.github.io")
@RestController
public class ComentarioController {

    @Autowired private ComentarioService comentarioService;
    
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
}

package com.example.demo.services;

import com.example.demo.model.Comentario;
import com.example.demo.model.Profesional;
import com.example.demo.repository.ComentarioRepo;
import com.example.demo.repository.ProfesionalRepo;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class ComentarioService {


	
	@Autowired ComentarioRepo comentRepo;
	@Autowired ProfesionalRepo profRepo;
	
	
	//POST COMENTARIO
	public ResponseEntity<?> postComentario(Comentario comentario) {
		if( comentario==null || comentario.getAutor()==null || comentario.getAutor()=="" ||comentario.getContenido()==null
    			|| comentario.getContenido()=="") {
    		return ResponseEntity.badRequest().body("Faltan datos");
    	}
    	
		Comentario existe=comentRepo.findByCodigo(comentario.hashCode()).orElse(null);
		if( existe == null) {
			comentario.setCodigo(comentario.hashCode());
			comentario.setFecha(new Date());
			comentario.setVerificado(false);
			comentRepo.save(comentario);
			return ResponseEntity.ok(HttpStatus.CREATED);
		}
		else {
			return ResponseEntity.badRequest().body("Ya existe el comentario");
		}
	}
	
	public ResponseEntity<?> getComentariosSinVerificar(String email){
		Profesional p = profRepo.findByEmail(email).orElse(null);
		if(p==null || !p.getEmail().equals("administrador")) {
			return ResponseEntity.badRequest().body("Faltan permisos");
		}
		else {
			List<Comentario> todos = (List<Comentario>) comentRepo.findAllNonVerified();
			if(todos.isEmpty()) {return ResponseEntity.notFound().build();}
			else {return ResponseEntity.ok(todos);}
			
		}
	}
	
	public ResponseEntity<?> getComentariosVerificados(){
			List<Comentario> todos = (List<Comentario>) comentRepo.findAllVerified();
			if(todos.isEmpty()) {return ResponseEntity.notFound().build();}
			else {return ResponseEntity.ok(todos);}
	}
	
	public ResponseEntity<?> verificaComentario(Long id, String email){
		Profesional p = profRepo.findByEmail(email).orElse(null);
		if(p==null || !p.getEmail().equals("administrador")) {
			return ResponseEntity.badRequest().body("Faltan permisos");
		}
		Comentario c = comentRepo.findById(id).orElse(null);
		if(c==null) {
			return ResponseEntity.notFound().build();
		}
		else {
			c.setVerificado(true);
			comentRepo.save(c);
			return ResponseEntity.ok().build();
		}
	}
	
	public ResponseEntity<?> rechazaComentario(Long id, String email){
		Profesional p = profRepo.findByEmail(email).orElse(null);
		if(p==null || !p.getEmail().equals("administrador")) {
			return ResponseEntity.badRequest().body("Faltan permisos");
		}
		Comentario c = comentRepo.findById(id).orElse(null);
		if(c==null) {
			return ResponseEntity.notFound().build();
		}
		else {
			comentRepo.delete(c);
			return ResponseEntity.noContent().build();
		}
	}
	
	
	
	
	
	
	
	
	
}

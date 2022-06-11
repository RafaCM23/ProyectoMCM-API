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

import com.example.demo.model.Comentario;
import com.example.demo.services.ComentarioService;

@CrossOrigin(origins = "https://rafacm23.github.io")
@RestController
public class ComentarioController {

	//- Este controlador se ocupa de todo lo relacionado con los comentarios de la pagina principal -//
	
    @Autowired private ComentarioService comentarioService;
    
	/**
     * Esta llamada recibe un comentario y si todos los campos son válidos se guarda
     * @param Comentario
     * @return ResponseEntity<?>
     */
    @PostMapping("/comentario")
    public ResponseEntity<?> postComentarioServicio(@RequestBody(required=false) Comentario comentario){
    		return  comentarioService.postComentario(comentario);
    }
    
    /**
     * Esta llamada recibe un booleano y devuelve todos los comentarios de la página principal, ya sean confirmados o sin confirmar.
     * Los comentarios sin verificar solo pueden verse si  es admin.
     * @param verificado
     * @return ResponseEntity<?>
     */
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
	
    /**
     * Esta llamada recibe un id de un comentario. Si lo encuentra lo marca como verificado,
     * en caso contrario devuelve notFound.
     * @param idComentario
     * @return ResponseEntity<?>
     */
	@GetMapping("/verifica/comentario/{id}")
	public ResponseEntity<?> verificaComentario(@PathVariable(required=false) Long id){
		if(id==null) {return ResponseEntity.badRequest().body("Faltan datos");}
		
		String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return comentarioService.verificaComentario(id,email);
	}
	
	/**
     * Esta llamada recibe un id de un comentario. Si lo encuentra lo borra,
     * en caso contrario devuelve notFound.
	 * @param idComentario
	 * @return ResponseEntity<?>
	 */
	@GetMapping("/rechaza/comentario/{id}")
	public ResponseEntity<?> rechazaComentario(@PathVariable(required=false) Long id){
		if(id==null) {return ResponseEntity.badRequest().body("Faltan datos");}
		String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return  comentarioService.rechazaComentario(id,email);
	}
	
}

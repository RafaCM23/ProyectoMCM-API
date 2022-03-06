package com.example.demo.controller;

import java.util.Date;
import java.util.List;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

import com.example.demo.model.*;
import com.example.demo.repository.AnuncioRepo;
import com.example.demo.repository.UserRepo;
import com.example.demo.repository.ComentarioRepo;
@CrossOrigin(origins = "http://localhost:4200")
@RestController
public class AnuncioController {

	@Autowired
	private ComentarioRepo comentarioRepo;
	@Autowired
	private AnuncioRepo anuncioRepo;
	
	@Autowired 
	private UserRepo userRepo;
	
	//------------------------------------ API ------------------------------------//
	
	
	/**
	 * Este metodo devuelve todos los anuncios. Si no hay ninguno devuelve not found.
	 * @return list<Anuncio>
	 */
	@GetMapping("/anuncios")
	public ResponseEntity<List<Anuncio>> getAnuncios() {
		
		List<Anuncio> anuncios = anuncioRepo.findAll();
		if(anuncios.isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		else {
		
		return ResponseEntity.ok(anuncios);
		}
	}
	
	/**
	 * Este metodo devuelve un anuncio concreto cuyo id se pasa por parametro. Si lo encuentra devuelve el anuncio
	 * y si no devuelve not found
	 * @param id long
	 * @return anuncio
	 */
	@GetMapping("/anuncio")
	public ResponseEntity<?> anuncio(@RequestParam(required=false) Integer id) {
		if(id==null) {return ResponseEntity.badRequest().body("Faltan datos");}
		Anuncio anuncio = anuncioRepo.findById((long)id).orElse(null);
		
		if(anuncio==null) {
			
			return ResponseEntity.notFound().build();
		}
		else {
			return ResponseEntity.ok(anuncio);
		}
	}
	
	
	/**
	 * Este metodo recibe un anuncio por parametro y un usuario por Token. Si los datos del anuncio son correctos
	 * se crea el anuncio y se le asigna al usuario, devolviendo CREATED. En caso contrario devuelve badRequest
	 * @param anuncio
	 * @return
	 */
	@PostMapping("/anuncios")
	public ResponseEntity<?> postAnuncio(@RequestBody(required=false) Anuncio anuncio){
		String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if(anuncio==null ||anuncio.getImg()==null || anuncio.getDescripcion()==null || anuncio.getTitulo()==null) {
			return ResponseEntity.badRequest().body("Faltan datos");
		}
    	User usuario = userRepo.findByEmail(email).orElse(null);
		Anuncio nuevo = new Anuncio(anuncio.getImg(),usuario,anuncio.getTitulo(),anuncio.getDescripcion());
	
		
		if(nuevo!=null && usuario!=null) {
			
			try {				
				anuncioRepo.save(nuevo);
				usuario.setAnuncio(nuevo);
				userRepo.save(usuario);
				
				return ResponseEntity.ok(HttpStatus.CREATED);
				
			} catch (Exception e) {
				return ResponseEntity.badRequest().build();
			}
						
			
		}
		else {
			return ResponseEntity.badRequest().body("Error al procesar la respuesta");
		}
	}
	
	/**
	 * Este metodo recibe los datos de un anuncio por RequestBody y un usuario por token.
	 * Si el autor del anuncio es el mismo que el usuario del token y los datos son validos, se realizan
	 * los cambios en el anuncio. Si no es asi devuelve not found o badRequest.
	 * @param aux Anuncio
	 * @return ResponseEntity
	 */
    @PutMapping("/misanuncios")
 	public ResponseEntity<?> editarMisAnuncios(@RequestBody(required=false) Anuncio aux){
    	String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    	if(aux==null || aux.getTitulo()==null ||aux.getDescripcion()==null || aux.getImg()==null)
    	{
    		return ResponseEntity.badRequest().body("Faltan datos");
    	}
    	User usuario = userRepo.findByEmail(email).orElse(null);
    	System.out.println(aux.getId());
		Anuncio buscado = anuncioRepo.findById(aux.getId()).orElse(null);
		if(buscado==null || usuario==null) {
 			return ResponseEntity.notFound().build();
 		}
		if( usuario.getId()!=buscado.getAutor().getId()) {
			return ResponseEntity.badRequest().body("No puede editar un anuncio que no es suyo");
		}
 		else {
 	
 			buscado.setTitulo(aux.getTitulo());
 			buscado.setDescripcion(aux.getDescripcion());
			buscado.setImg(aux.getImg());
			anuncioRepo.save(buscado);
			return ResponseEntity.ok().build();
 		}
 	}
    
    
	/**
	 * Este metodo recibe un id de un anuncio y un usuario por token. Si el usuario y el autor del anuncio
	 * son el mismo entonces se borra y se devuelve 204 no content. En caso contrario devuelve notFound
	 * si no encuentra el anuncio o el usuario y badRequest si no recibe id.
	 * @param id long
	 * @return ResponseEntity
	 */
	@DeleteMapping("/misanuncios")
	public ResponseEntity<?> eliminarAnuncio(@RequestParam(required=false) Integer id) {
		String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if(id==null) {return ResponseEntity.badRequest().body("Faltan datos");}
    	User usuario = userRepo.findByEmail(email).orElse(null);
		Anuncio anuncio=anuncioRepo.findById((long)id).orElse(null);
		
		if(anuncio!=null && usuario!=null && usuario.getAnuncios().contains(anuncio)) {
			usuario.getAnuncios().remove(anuncio);
			userRepo.save(usuario);
			anuncioRepo.delete(anuncio);
			return ResponseEntity.noContent().build();
		}
		else {
			return ResponseEntity.badRequest().body("El anuncio no existe o no le pertenece");
			
		}
	}
	
	//------------------------------------ API - Segundo Nivel ------------------------------------//
		
		/**
		 * Este metodo recibe un id de un anuncio por parametro y devuelve sus comentarios. Si no lo encuentra devuelve not found
		 * @param id long
		 * @return Comentario
		 */
		@GetMapping("/anuncios/{id}/comentarios")
		public ResponseEntity<List<Comentario>> getAnuncioPorId(@PathVariable long id){
		Anuncio anuncio=anuncioRepo.findById(id).orElse(null);
		if(anuncio==null) { return ResponseEntity.notFound().build();}
		else {
			return ResponseEntity.ok(anuncio.getComentarios());
		}
		}
		
		//ARREGLAR
		
		/**
		 * Este metodo recibe un id de un anuncio por parametro y un comentario por RequestBody. Si el usuario existe
		 * y el anuncio tambien entonces se publica el comentario y se guarda el comentario en el usuario. En caso contrairo
		 * devuelve badRequest o notFound.
		 * @param id long
		 * @param comentario Comentario
		 * @return ResponseEntity
		 */
		@PostMapping("/anuncios/{id}/comentarios")
		public ResponseEntity<?> postComentario(@PathVariable long id,@RequestBody(required=false) Comentario comentario){
			
			if(comentario==null || comentario.getContenido()==null) {return ResponseEntity.badRequest().body("Faltan datos");}
			Anuncio anuncio=anuncioRepo.findById(id).orElse(null);
			String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	    	User usuario = userRepo.findByEmail(email).orElse(null);
	    	
	    	if(anuncio==null || usuario==null) {
	    		System.out.println(usuario);
	    		return ResponseEntity.notFound().build();
	    	}
	    	else {
	    		Date ahora = new Date();
	    		Comentario nuevo = new Comentario(comentario.getContenido());
	    		nuevo.setAutor(usuario);
	    		nuevo.setFecha(ahora);
	    		
	    		comentarioRepo.save(nuevo);
	    		anuncio.getComentarios().add(nuevo);
	    		anuncioRepo.save(anuncio);
	    		
	    		return ResponseEntity.ok(HttpStatus.CREATED);
	    		
	    	}
		}
		
		/**
		 * Este metodo recoge un id por url, y un comentario por Request Body. Busca un anuncio por esa id, comprueba el usuario
		 * por el token y si todo es correcto publica un anuncio en ese comentario hecho por el usuario.
		 * En caso contrario devuelve BadRequest o not found
		 * @param id long
		 * @param comentario Comentario
		 * @return ResponseEntity
		 */
		@PutMapping("/anuncios/{id}/comentarios")
		public ResponseEntity<?> putComentario(@PathVariable long id,@RequestBody(required=false) Comentario comentario){
			
			Anuncio anuncio=anuncioRepo.findById(id).orElse(null);
		
			String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	    	User usuario = userRepo.findByEmail(email).orElse(null);
	    	if(comentario==null) {return ResponseEntity.badRequest().body("Faltan datos");}
	    	Comentario coment = comentarioRepo.findById(comentario.getId()).orElse(null);
	    	
	    	if(comentario==null || anuncio==null || email==null || coment==null || usuario==null || coment.getAutor()==null) {
	    		return ResponseEntity.notFound().build();
	    	}
	    	if(comentario.getContenido()==null) {
	    		return ResponseEntity.badRequest().body("No hay contenido");
	    	}
	    	if(usuario.getId()!=coment.getAutor().getId()) {
	    		return ResponseEntity.badRequest().body("No puede editar un anucio que no le pertenece");
	    	}
	    
	    	else {
	    		Date ahora = new Date();
	    		coment.setFecha(ahora);coment.setContenido(comentario.getContenido());
	    		comentarioRepo.save(coment);
	    		anuncio.getComentarios().set(anuncio.getComentarios().indexOf(coment), coment);
	    		anuncioRepo.save(anuncio);
	    		return ResponseEntity.ok().build();
	    		
	    	}
		}
		
		/**
		 * Este metodo recibe dos id por url, el primero es el del anuncio y el segundo es del comentario.
		 * Tambien recibe un usuario por token. Si El anuncio existe, el comentario es de ese anuncio
		 * y el autor del anuncio es el usuario entonces el comentario se borra. En caso contrario devuelve
		 * Not found o Bad Request
		 * @param id long
		 * @param comentarioId long
		 * @return ResponseEntity
		 */
		@DeleteMapping("/anuncios/{id}/comentarios/{comentarioId}")
		public ResponseEntity<?> borrarComentario(@PathVariable long id, @PathVariable long comentarioId){
			
			Anuncio anuncio=anuncioRepo.findById(id).orElse(null);
			
			String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			User usuario = userRepo.findByEmail(email).orElse(null);
			Comentario coment = comentarioRepo.findById(comentarioId).orElse(null);
			
			if(coment==null || usuario == null || coment.getAutor()==null || anuncio==null) {
				return ResponseEntity.notFound().build();
			}
			if(coment.getAutor().getId()!=usuario.getId()) {
	
				return ResponseEntity.badRequest().body("No puede borrar un comentario que no es suyo");
			}
			if(!anuncio.getComentarios().contains(coment)) {
			
				return ResponseEntity.badRequest().body("Este comentario no existe");
			}
			else {
				
				anuncio.getComentarios().remove(anuncio.getComentarios().indexOf(coment));
				anuncioRepo.save(anuncio);
				comentarioRepo.delete(coment);
				return ResponseEntity.noContent().build();
			}
		}
	//------------------------------------ Angular ------------------------------------//
	
		/**
		 * Este metodo devuelve el autor del anuncio para ponerlo en el contacto en la pagina del anuncio
		 * @param id long
		 * @return email ( LoginCredentials )
		 */
	@GetMapping("/dequienes")
	public ResponseEntity<LoginCredentials> dequienes(@RequestParam long id){
		String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
    	User buscado = userRepo.findByEmail(email).orElse(null);
    	Anuncio anuncio= anuncioRepo.findById(id).orElse(null);
    	if(buscado!=null && anuncio!=null && anuncio.getAutor()!=null) {
    		
    		LoginCredentials credenciales= new LoginCredentials();
    		credenciales.setEmail(anuncio.getAutor().getEmail());
    		return ResponseEntity.ok(credenciales);
    	}
    	else {
    		return ResponseEntity.notFound().build();
    	}
	}
	
	/**
	 * Este metodo devuelve todos los anuncios de un usuario que se le pasa por token. En caso contrario devuelve not found
	 * @return List<Anuncio>
	 */
	@GetMapping("/misanuncios")
    public ResponseEntity<?> misanuncios(){
		String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
    	User buscado = userRepo.findByEmail(email).orElse(null);
    	if(buscado==null || buscado.getAnuncios().isEmpty()) {
    		
    			return ResponseEntity.notFound().build();
    		}
    	else {
    		return ResponseEntity.ok(buscado.getAnuncios());
    	}
    }
	
	
	
	
	
}

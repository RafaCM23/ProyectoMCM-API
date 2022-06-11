package com.example.demo.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.model.Categoria;
import com.example.demo.model.ComentarioPost;
import com.example.demo.model.Post;
import com.example.demo.model.Profesional;
import com.example.demo.repository.CategoriaRepo;
import com.example.demo.repository.ComentarioPostRepo;
import com.example.demo.repository.PostRepo;
import com.example.demo.repository.ProfesionalRepo;

@Service
public class BlogService {

	@Autowired CategoriaRepo catRepo;
	@Autowired ProfesionalRepo profRepo;
	@Autowired PostRepo postRepo;
	@Autowired ComentarioPostRepo comentarioPostRepo;
	


    public ResponseEntity<?> setImagen(Long id,String email,MultipartFile imagen){
    	if(imagen.isEmpty() || imagen==null) {
    		return ResponseEntity.badRequest().body("Falta imagen");
    	}
		Profesional who = profRepo.findByEmail(email).orElse(null);
    	if(who==null) {return ResponseEntity.badRequest().body("Falta permisos");}
    	else {
    		Path directorioImagenes = Paths.get("src//main//resources//static/blog");
    		String rutaAbsoluta = directorioImagenes.toFile().getAbsolutePath();
    		ResponseEntity<?>resp;
    		try {
				byte[] bytesImg = imagen.getBytes();
				Path rutaCompleta = Paths.get(rutaAbsoluta+"//"+imagen.getOriginalFilename());
				Files.write(rutaCompleta, bytesImg);
				Post buscado = postRepo.findById(id).orElse(null);
		    	if(buscado!=null) {
		    		buscado.setImagen(imagen.getOriginalFilename());
		    		postRepo.save(buscado);
		    		resp= ResponseEntity.ok(HttpStatus.CREATED);
		    	}
		    	else {
		    		resp= ResponseEntity.notFound().build();
		    	}
				
			} catch (Exception e) {
				resp=ResponseEntity.badRequest().body("Error al procesar la imagen");
				
			}
    	return resp;
    }
    	
    }
       
    public Resource getImgPost(Long id) {
		Post p= postRepo.findById(id).orElse(null);
		Resource res=null;
		if(p!=null) {		
			if(p.getImagen()==null) {}
			else {
				Path directorioImagenes = Paths.get("src//main//resources//static/blog");
	    		String rutaAbsoluta = directorioImagenes.toFile().getAbsolutePath();
				Path rutaCompleta = Paths.get(rutaAbsoluta+"//"+p.getImagen());
				try {				
					Resource resource = new UrlResource(rutaCompleta.toUri());
					res= resource;
					} catch (IOException e) {
					res= null;
					}
				
				}
				
				return res;
		}
		return res;
	}

	public ResponseEntity<?> getNextPosts(Integer pagina){
		int cuantos = pagina;

		List<Post> resp= (List<Post>) postRepo.findNext8(cuantos);
		if(!resp.isEmpty()) {
			return ResponseEntity.ok(resp);
		}else {
			return ResponseEntity.notFound().build();
		}
	}
	
	public ResponseEntity<?> getAllPosts(String email){
		Profesional prof = profRepo.findByEmail(email).orElse(null);
		if(prof==null || !prof.getEmail().equals("administrador")) {return ResponseEntity.badRequest().body("Faltan permisos");}
		
		List<Post> todos = postRepo.findAll();
		if(todos.isEmpty()) {return ResponseEntity.notFound().build();}
		else {
			return ResponseEntity.ok(todos);
		}
	}
	
	public ResponseEntity<?> getPost(Long id){
		Post buscado= postRepo.findById(id).orElse(null);
		if(buscado==null) {return ResponseEntity.notFound().build();}
		else {
			if(!buscado.getComentarios().isEmpty()) {
			}
			return ResponseEntity.ok(buscado);
		}
	}
	
	public ResponseEntity<?> borraPost(Long id,String email){
		Profesional prof = profRepo.findByEmail(email).orElse(null);
		if(id == null || prof==null || !prof.getEmail().equals("administrador")){
			return ResponseEntity.badRequest().body("Falta id post o authorizacion");
		}
		
		Post buscado = postRepo.findById(id).orElse(null);
		if(buscado==null) {
			return ResponseEntity.notFound().build();
		}
		else {
			postRepo.delete(buscado);
			return ResponseEntity.noContent().build();
		}
		
	}
	
	public ResponseEntity<?> creaPost(Post p,String emailProf){
		
		if(p==null || emailProf.isEmpty() || p.getNombre().isEmpty() || p.getContenido().isEmpty()) {
			return ResponseEntity.badRequest().body("Faltan Datos");
		}
		Profesional prof = profRepo.findByEmail(emailProf).orElse(null);
		if(prof==null) {return ResponseEntity.notFound().build();}
		else {
			Post nuevo = new Post(prof, p.getNombre(), p.getContenido());
			Post buscado = postRepo.findByNombre(nuevo.getNombre()).orElse(null);
			if(buscado!=null) {return ResponseEntity.badRequest().body("Ya existe un post con ese nombre");}
			if(p.getCategoria()!=null) {nuevo.setCategoria(p.getCategoria());}
			p.setAutor(prof);p.setFecha(new Date());
			postRepo.save(nuevo);
			Post recienCreado = postRepo.findByNombre(nuevo.getNombre()).orElse(null);
			return ResponseEntity.ok(recienCreado.getId());
		}
	}
	
	public ResponseEntity<?> creaCategoria(String categoria,String emailProf){
		
		if(categoria.isEmpty() || emailProf.isEmpty()) {
			return ResponseEntity.badRequest().body("Faltan Datos");
		}
		Profesional p = profRepo.findByEmail(emailProf).orElse(null);
		if(p==null) {return ResponseEntity.notFound().build();}
		
		else {
			Categoria c = new Categoria(categoria);
			this.catRepo.save(c);
			return ResponseEntity.ok(HttpStatus.CREATED);
		}
	}
	
	
	public ResponseEntity<?> recuperaCategorias(){
		
			List<Categoria> todas = catRepo.findAll();
			if(todas.isEmpty()){
				return ResponseEntity.notFound().build();
			}		
			return ResponseEntity.ok(todas);
		
		}
	
	public ResponseEntity<?> getRelacionados(Long idCat){
		
		List<Post> todos = (List<Post>) postRepo.findRelacionados(idCat);
		if(todos.isEmpty()){
			return ResponseEntity.notFound().build();
		}		
		return ResponseEntity.ok(todos);
	
		}
	
	public ResponseEntity<?> getBlogPreview(){
		
		List<Post> todos = (List<Post>) postRepo.findPreview();
		if(todos.isEmpty()){
			return ResponseEntity.notFound().build();
		}		
		return ResponseEntity.ok(todos);
	
		}
	
	public ResponseEntity<?> creaComentario(Long id,ComentarioPost comentario){
		
		Post p = postRepo.findById(id).orElse(null);
		if(p==null) {return ResponseEntity.notFound().build();}
		if(comentario==null ||comentario.getAutor()==null || comentario.getContenido()==null) {
			return ResponseEntity.badRequest().body("Faltan Datos");
		}
		
		else {
			for (ComentarioPost c : p.getComentarios()) {
				if(c.getAutor().equals(comentario.getAutor())) {
					return ResponseEntity.badRequest().body("No puede publicar más comentarios en este post");
				}
			}
			ComentarioPost nuevo = new ComentarioPost(comentario.getAutor(),comentario.getContenido(),new Date());
			p.addComentario(nuevo);
			comentarioPostRepo.save(nuevo);
			postRepo.save(p);
			return ResponseEntity.ok().build();
		}
	}
	
	public ResponseEntity<?> deleteComentario(Long idPost,Long idComent,String email){
		Profesional prof = profRepo.findByEmail(email).orElse(null); 
		if(prof==null || !prof.getEmail().equals("administrador")) {return ResponseEntity.badRequest().body("Faltan permisos");}
		
		Post p = postRepo.findById(idPost).orElse(null);
		if(p==null) {return ResponseEntity.notFound().build();}
		ComentarioPost cp = comentarioPostRepo.findById(idComent).orElse(null);
		if(cp==null) {return ResponseEntity.notFound().build();}
		
		else {
			if(p.getComentarios().contains(cp)) {
				p.deleteComentario(cp);
				postRepo.save(p);
				comentarioPostRepo.delete(cp);
				return ResponseEntity.noContent().build();
			}
			
		}
		
		
		
			return ResponseEntity.ok().build();
		
	}
	

	public ResponseEntity<?> editaPost(Long id, String email,Post p){
		Profesional prof = profRepo.findByEmail(email).orElse(null); 
		if(prof==null || !prof.getEmail().equals("administrador")) {return ResponseEntity.badRequest().body("Faltan Permisos");}
		Post buscado= postRepo.findById(id).orElse(null);
		if(p==null || buscado==null) {return ResponseEntity.notFound().build();}
		else {
			buscado.setNombre(p.getNombre());
			buscado.setContenido(p.getContenido());
			buscado.setCategoria(catRepo.findById(p.getCategoria().getId()).orElse(null));
			postRepo.save(buscado);
			return ResponseEntity.ok().build();
			
		}
	}
	
	public void salvaPosts(Long id) {
		List<Post> todos=(List<Post>) postRepo.findAllByAutor(id);
		if(!todos.isEmpty()) {
			for (Post p : todos) {
				p.setAutor(null);
				postRepo.save(p);
			}
		}
	}



	// -- FILTROS -- //
	
	public ResponseEntity<?> getPostsFitrados(Long categoria,String titulo){
		
		List<Post> res=null;
		if(categoria!=null) {
			Categoria cat = catRepo.findById(categoria).orElse(null);
			res=(List<Post>)postRepo.findAllByCategoria(cat);
			return ResponseEntity.ok(res);
			}
		else if(titulo!=null){
			 res=(List<Post>)postRepo.findAllByTitulo(titulo);
			return ResponseEntity.ok(res);
		}
		else {return ResponseEntity.notFound().build();}
		
	}


}

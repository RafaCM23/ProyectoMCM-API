package com.example.demo.controller;


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

import com.example.demo.model.Categoria;
import com.example.demo.model.ComentarioPost;
import com.example.demo.model.Post;
import com.example.demo.services.BlogService;

@CrossOrigin(origins = "https://rafacm23.github.io")
@RestController
public class BlogController {

	@Autowired private BlogService blogService;

    @PostMapping("/post")
    public ResponseEntity<?> nuevoPost(@RequestBody Post p){
    	String email=(String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    	ResponseEntity<?> resp =blogService.creaPost(p,email);
    	return resp;
    }
    
    @GetMapping("/posts")
    public ResponseEntity<?> getAllPostsPag(@RequestParam(required=false) Integer page,@RequestParam(required=false) Long categoria,@RequestParam(required=false) String titulo){
    	ResponseEntity<?> resp =null;
    	if(page!=null) {resp = blogService.getNextPosts(page);}
    	else if(categoria!=null) {resp=blogService.getPostsFitrados(categoria,null);}
    	else if(titulo!=null) {resp=blogService.getPostsFitrados(null,titulo);}
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
    
    
    
    @GetMapping("/postRelacionados/{idCat}")
    public ResponseEntity<?> getRelacionados(@PathVariable Long idCat){
    	ResponseEntity<?> resp =this.blogService.getRelacionados(idCat);
    	return resp;
    }
    
    @GetMapping("/blogPreview")
    public ResponseEntity<?> getBlogPreview(){
    	ResponseEntity<?> resp =this.blogService.getBlogPreview();
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
    	ResponseEntity<?> resp=this.blogService.recuperaCategorias();
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
    
    //------------------------------------ Utilidades ------------------------------------//
    
    
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

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

	
	//- Este controlador se ocupa de todo lo relacionado con el blog y los posts -//
	
	@Autowired private BlogService blogService;

	
    
    /**
     * Esta llamada recibe una página o un filtro (categoria / nombre). Según los inputs introducidos
     * saca los posts que cumplen esas condiciones y los devuelve. 
     * Si los inputs no coincide con ningun post devuelve notFound.
     * @param page
     * @param categoria
     * @param titulo
     * @return ResponseEntity<?>
     */
    @GetMapping("/posts")
    public ResponseEntity<?> getAllPostsPag(@RequestParam(required=false) Integer page,@RequestParam(required=false) Long categoria,@RequestParam(required=false) String titulo){
    	ResponseEntity<?> resp =null;
    	if(page!=null) {resp = blogService.getNextPosts(page);}
    	else if(categoria!=null) {resp=blogService.getPostsFitrados(categoria,null);}
    	else if(titulo!=null) {resp=blogService.getPostsFitrados(null,titulo);}
    	return resp;
    }
       
    /**
     * Esta llamada recibe un id y devuelve el post con esa id. Si no lo encuentra devuevle notFound.
     * @param idPost
     * @return ResponseEntity<?>
     */
    @GetMapping("/post/{id}")
    public ResponseEntity<?> getPost(@PathVariable Long id){
    	return blogService.getPost(id);
    }
    
    /**
     * Esta llamada recibe un id de categoría y devuelve 4 posts relacionados al azar, si no hay devuelve notFound.
     * @param idCat
     * @return ResponseEntity<?>
     */
    @GetMapping("/postRelacionados/{idCat}")
    public ResponseEntity<?> getRelacionados(@PathVariable Long idCat){
    	return this.blogService.getRelacionados(idCat);
    }
    
    /**
     * Ests llamada devuelve 6 posts al azar. Si no hay ninguno devuelve notFound.
     * @return ResponseEntity<?>
     */
    @GetMapping("/blogPreview")
    public ResponseEntity<?> getBlogPreview(){
    	return this.blogService.getBlogPreview();
    }
    
    
    /**
     * Esta llamada recibe una categoría, y si los datos son correctos la guarda.
     * @param Categoria 
     * @return ResponseEntity<?>
     */
    @PostMapping("/categoria")
    public ResponseEntity<?> postCategoria(@RequestBody Categoria c){
    	String email=(String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    	return this.blogService.creaCategoria(c.getNombre(), email);
    }
    
    /**
     * Esta llamada devuelve todas las categorías existentes, si no hay ninguna devuelve notFound.
     * @return ResponseEntity<?>
     */
    @GetMapping("/categorias")
    public ResponseEntity<?> getCategorias(){
    	return this.blogService.recuperaCategorias();
    }
    
    /**
	 * Esta llamada recibe un post, si esta correcto lo crea y lo asocia al profesional que ha hecho la llamada
	 * Si el post no es correcto, o no encuentra el profesional devuelve error.
	 * @param Post p
	 * @return ResponseEntity<?>
	 */
    @PostMapping("/post")
    public ResponseEntity<?> nuevoPost(@RequestBody Post p){
    	String email=(String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    	return blogService.creaPost(p,email);
    }
    
    /**
     * Esta llamada recibe una id de un post y un comentario. Si los datos son correctos el comentario es guardado
     * dentro del post. Si no encuentra el post devuelve notFound.
     * @param idPost
     * @param ComentarioPost
     * @return ResponseEntity<?>
     */
    @PostMapping("/post/{id}/comentario")
    public ResponseEntity<?> postComentario(@PathVariable Long id,@RequestBody ComentarioPost comentario){

    	return this.blogService.creaComentario(id, comentario);
    }
    
    /**
     * Esta llamada recibe un id de un post y un id de comentario. Si encuentra el post y el comentario dentro, entonces lo borra.
     * Si no encuentra alguno de los dos devuelve notFound.
     * @param idPost
     * @param idComent
     * @return ResponseEntity<?>
     */
    @DeleteMapping("/post/{idPost}/comentario/{idComent}")
    public ResponseEntity<?> deleteComentario(@PathVariable Long idPost,@PathVariable Long idComent){
    	String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    	return this.blogService.deleteComentario(idPost, idComent,email);
    }
    
    //------------------------------------ Administracion Blog ------------------------------------//
    
    /**
     * Esta llamada recibe un id de un post, si lo encuentra y quien hizo la llamada es el administrador, el post es borrado.
     * @param idPost
     * @return ResponseEntity<?>
     */
    @DeleteMapping("/post/{id}")
    public ResponseEntity<?> deletePost(@PathVariable Long id){
    	String email=(String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    	return blogService.borraPost(id,email);
    }
    
    /**
     * Esta llamada recibe un id de un post y nuevos datos para editarlo. Si encuentra el posts y quien hizo la llamada es
     * el administrador, los datos del posts son editados.
     * @param idPost
     * @param Post
     * @return ResponseEntity<?>
     */
    @PutMapping("/post/{id}")
    public ResponseEntity<?> editaPost(@PathVariable Long id,@RequestBody Post p){
    	String email=(String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    	return blogService.editaPost(id,email,p);
    }
    
    /**
     * Esta llamada recupera todos los posts. Si no existe ninguno devuelde notFound.
     * @return  ResponseEntity<?>
     */
    @GetMapping("/allPosts")
    public ResponseEntity<?> getAllPosts(){
    	String email=(String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    	return blogService.getAllPosts(email);
    }
    
    //------------------------------------ Utilidades ------------------------------------//
    
    /**
     * Esta llamada recibe un id de un post y una imagen. Si la imagen es correcta y encuentra el post, se vincula la imagen con el post.
     * @param imagen
     * @param idPost
     * @return  ResponseEntity<?>
     */
    @PostMapping("/post/{id}/imagen")
    public ResponseEntity<?> subirImagenPost(@RequestBody(required=false) MultipartFile file,@PathVariable(required=false) Long id){
    	String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    		return  blogService.setImagen(id,email, file);
    	
    }
    
    /**
     * Esta llamada recibe un id de un post, y si lo encuentra devuelve su imagen asociada.
     * @param idPost
     * @return Resource (.png)
     */
    @GetMapping(value="/post/{id}/imagen",produces= {MediaType.APPLICATION_OCTET_STREAM_VALUE})
    public Resource getImagenPost(@PathVariable(required=false) Long id){
    	return blogService.getImgPost(id);
    }
    
}

package com.example.demo.repository;


import java.util.Collection;
import java.util.Optional;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.model.Categoria;
import com.example.demo.model.Post;


public interface PostRepo extends JpaRepository<Post,Long>{
			
	//Método para obtener un post por nombre
	public Optional<Post> findByNombre(String nombre);
	
	//Método para obtener Posts por Categoria
			public Collection<Post> findAllByCategoria(Categoria cat);
	

	
	@Query(
			  value = "SELECT * FROM post p WHERE p.id NOT IN (SELECT id from (Select * FROM post ORDER BY id DESC LIMIT :ultimos) as t) ORDER BY p.id DESC LIMIT 8", 
			  nativeQuery = true)
			Collection<Post> findNext8(@Param("ultimos") int ultimos);
	
	@Query(
			  value = "SELECT * FROM post p WHERE p.nombre LIKE CONCAT('%',:titulo,'%')",
			  nativeQuery = true)
			Collection<Post> findAllByTitulo(@Param("titulo") String titulo);
	
	@Query(
			  value = "SELECT * FROM post p WHERE p.categoria_id= :idCat ORDER BY RAND() LIMIT 4",
			  nativeQuery = true)
			Collection<Post> findRelacionados(@Param("idCat") Long idCat);
	
	@Query(
			  value = "SELECT * FROM post p ORDER BY RAND() LIMIT 6",
			  nativeQuery = true)
			Collection<Post> findPreview();
	
	@Query(
			  value = "SELECT * FROM post p WHERE autor_id= :idProf",
			  nativeQuery = true)
			Collection<Post> findAllByAutor(@Param("idProf") Long idProf);
	

}
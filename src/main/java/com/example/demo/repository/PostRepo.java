package com.example.demo.repository;


import java.util.Collection;
import java.util.Optional;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.model.Post;


public interface PostRepo extends JpaRepository<Post,Long>{
			
	//Método para obtener un post por nombre
	public Optional<Post> findByNombre(String nombre);
	
	@Query(
			  value = "SELECT * FROM post p WHERE p.id NOT IN (SELECT id from (Select * FROM post ORDER BY id ASC LIMIT :ultimos) as t) ORDER BY p.id ASC LIMIT 8", 
			  nativeQuery = true)
			Collection<Post> findNext8(@Param("ultimos") int ultimos);

}
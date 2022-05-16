package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.ComentarioPost;


public interface ComentarioPostRepo extends JpaRepository<ComentarioPost,Long>{
	
	
}


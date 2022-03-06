package com.example.demo.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Anuncio;

public interface AnuncioRepo extends JpaRepository<Anuncio,Long>{
}




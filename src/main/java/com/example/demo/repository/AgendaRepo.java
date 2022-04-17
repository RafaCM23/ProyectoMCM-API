package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Agenda;

public interface AgendaRepo extends JpaRepository<Agenda, Long>{
 
}

package com.example.demo.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
public class Agenda {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@OneToMany(fetch=FetchType.LAZY)
	private List<Anio> anios;
	
	public Agenda() {
		this.anios=new ArrayList<Anio>();
	}
	
	public void creaAnio(int year) {
		this.anios.add(new Anio(year));
	}
	public void addAnio(Anio anio) {
		this.anios.add(anio);
	}
	
	
	
	public Anio getAnio(int year) {
		for (Anio anio : anios) {
			if(anio.getNumero()==year) {
				return anio;
			}
		}
		return null;
	}
	
}

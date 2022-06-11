package com.example.demo.model;

import java.util.ArrayList;
import java.util.List;


import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Entity
@Getter
@Setter
@ToString

public class Mes {

	public Mes () {
		this.dias=new ArrayList<Dia>();
	}
	public Mes (int numero) {
		this.dias=new ArrayList<Dia>();
		this.numero=numero;
	}
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private int numero;
	@OneToMany(fetch=FetchType.LAZY)
	private List<Dia> dias;
	
	public void addDia(Dia dia) {
		this.dias.add(dia);
	}
	public Dia getDia(int numero) {
		for (Dia dia : dias) {
			if(dia.getNumero()==numero) {
				return dia;
			}
		}
		return null;
	}
}

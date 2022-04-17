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

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString

public class Anio {

	public Anio () {
		this.meses=new ArrayList<Mes>();
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private int numero;
	@OneToMany(fetch=FetchType.LAZY,cascade = CascadeType.PERSIST)
	private List<Mes> meses;
	
	public void addMes(Mes mes) {
		this.meses.add(mes);
	}
	public Mes getMes(int numero) {
		for (Mes mes : meses) {
			if(mes.getNumero()==numero) {
				return mes;
			}
		}
		return null;
	}
}

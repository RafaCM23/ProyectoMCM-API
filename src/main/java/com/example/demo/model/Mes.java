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

public class Mes {

	public Mes () {
		this.dias=new ArrayList<Dia>();
	}
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private int numero;
	@OneToMany(fetch=FetchType.LAZY,cascade = CascadeType.PERSIST)
	private List<Dia> dias;
	
	public void addDia(Dia dia) {
		this.dias.add(dia);
	}
}

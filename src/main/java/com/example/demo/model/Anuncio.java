package com.example.demo.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Anuncio {

	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	private String img;
	@ManyToOne(fetch=FetchType.EAGER)
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private User autor;
	private String titulo;
	private String descripcion;
	@OneToMany(fetch=FetchType.EAGER)
	private List<Comentario> comentarios;
	
	
	public Anuncio(String img, User user,String titulo, String descripcion) {
		this.img=img;
		this.autor=user;
		this.titulo=titulo;
		this.descripcion=descripcion;
		this.comentarios=new ArrayList<Comentario>();
	}
}

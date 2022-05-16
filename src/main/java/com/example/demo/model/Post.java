package com.example.demo.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Post {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@OneToOne
	private Profesional autor;
	private String nombre;
	@OneToMany(cascade = CascadeType.ALL)
	private	List<Categoria> categorias;
	@Column(length = 2000)
	private String contenido;
	private String imagen;
	private Date fecha;
	@OneToMany(cascade = CascadeType.ALL)
	private List<ComentarioPost> comentarios;
	
	public Post(Profesional autor, String nombre, String contenido) {
		super();
		this.autor = autor;
		this.nombre = nombre;
		this.contenido = contenido;
		this.fecha = new Date();
		this.comentarios=new ArrayList<ComentarioPost>();
	}
	
	public void addComentario(ComentarioPost comentario) {
		this.comentarios.add(0,comentario);
	}
	
	public void deleteComentario(ComentarioPost comentario) {
		this.comentarios.remove(this.comentarios.indexOf(comentario));
	}
	
	
}

package com.example.demo.services;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.model.Agenda;
import com.example.demo.model.Profesional;
import com.example.demo.repository.AgendaRepo;
import com.example.demo.repository.ProfesionalRepo;
import com.example.demo.security.JWTUtil;

@Service
public class ProfesionalService {

	
	
	@Autowired AgendaRepo agendaRepo;
	@Autowired ProfesionalRepo profRepo;
	
    @Autowired private JWTUtil jwtUtil;
	@Autowired private PasswordEncoder passwordEncoder;
    @Autowired private AuthenticationManager authManager;
	
    
    public int correoOcupado(String correo) {
    	Profesional buscado = profRepo.findByEmail(correo).orElse(null);
    	return buscado==null ? 0 : 1;
	}
    
	public Profesional getProfesional(Long id) {
		Profesional prof=profRepo.findById(id).orElse(null);
		if(prof!=null) return prof;
		return null;
	}
	
	public List<Profesional> getProfesionales(){
		List<Profesional> todos= (List<Profesional>) profRepo.findAllNonAdmin();
		if(todos!=null && !todos.isEmpty()) {
			
			return todos;
		}
		else {
			initProfesionales();
			todos= profRepo.findAll();
			return todos;
		}
	}
	
	public List<Profesional> getProfesionalesSinVerificar(){
		List<Profesional>  todos= (List<Profesional>) profRepo.findAllNonVerified();
		
		return todos;
	}
	public List<Profesional> getProfesionalesVerificados(){
		List<Profesional> todos= (List<Profesional>) profRepo.findAllVerified();
		if(todos==null || todos.isEmpty()) {
			initProfesionales();
			todos= (List<Profesional>) profRepo.findAllNonVerified();
		}
		return todos;
	}
	
	
	
	public int newProfesional(Profesional prof) {
    	
    	if(prof==null || prof.getNombre()==null || prof.getApellidos()==null || prof.getContrasenia()==null ||
    			prof.getEmail()==null || prof.getTlfn()==null || prof.getEspecialidad()==null || prof.getDescripcion()==null) {
    		return -1;
    	}
    	else {
    		 String encodedPass = passwordEncoder.encode(prof.getContrasenia());
         
    		Profesional p = new Profesional(prof.getNombre(),prof.getApellidos(),encodedPass,
    				prof.getEmail(),prof.getTlfn(),prof.getEspecialidad(),prof.getDescripcion());
    		agendaRepo.save(p.getAgenda());
    		profRepo.save(p);
    		return 1;
    	}
	}

	public ResponseEntity<?> login(Profesional prof) {
		ResponseEntity<?> resp=null;
		if(prof==null || prof.getEmail()==null || prof.getContrasenia()==null) {
			resp=ResponseEntity.badRequest().body("Faltan Datos");
		}else{
			Profesional buscado= profRepo.findByEmail(prof.getEmail()).orElse(null);
			if(buscado==null) {	resp=ResponseEntity.badRequest().body("Email Incorrecto");}
			else {
				if(!passwordEncoder.matches(prof.getContrasenia(), buscado.getContrasenia())) {
					System.out.println(prof.getContrasenia()+ " "+buscado.getContrasenia());
					resp=ResponseEntity.badRequest().body("Contraseña Incorrecta");}	
				else {
					
					if(buscado.getVerificado()==false) {
					resp=ResponseEntity.badRequest().body("Esta cuenta no esta verificada todavia");
					}
					else {resp=generaToken(prof.getEmail(),prof.getContrasenia());}
					 }
				}
			  }
		return resp;
		
	}
	
	public ResponseEntity<?> generaToken(String email,String contra) {
		try {
          String token = jwtUtil.generateToken(email);          
          return ResponseEntity.ok(Collections.singletonMap("jwt-token", token));
      }catch (AuthenticationException authExc){
          return ResponseEntity.badRequest().body("Error al procesar login");
      }
	}
	
	
	
	
	public ResponseEntity<?> verificaProf(Long id){
		
		Profesional buscado = profRepo.findById(id).orElse(null);
		if(buscado==null) {return ResponseEntity.notFound().build();}
		else {
			//Si el que envia esto esta verificado
			if(buscado.getVerificado()==true) return ResponseEntity.badRequest().body("El profesional ya ha sido verificado");
			buscado.setVerificado(true);
			profRepo.save(buscado);
			return ResponseEntity.ok().build();
		}
	}
	
	public ResponseEntity<?> rechazaProf(Long id){
		
		Profesional buscado = profRepo.findById(id).orElse(null);
		if(buscado==null) {return ResponseEntity.notFound().build();}
		else {
			//Si el que envia esto esta verificado
			
			//Se borra el profesional
			if(buscado.getVerificado()==true) return ResponseEntity.badRequest().body("El profesional ya ha sido verificado");
			profRepo.delete(buscado);
			return ResponseEntity.noContent().build();}
	}
	
	public String getImg(String email) {
		Profesional p= profRepo.findByEmail(email).orElse(null);
		if(p==null || p.getImg()==null || p.getImg().isEmpty() || p.getImg().equals("")) {
			return null;
		}else {return p.getImg();}
	}
	
	public Profesional getDatos(String email) {
		return profRepo.findByEmail(email).orElse(null);
	}
	
	
	public ResponseEntity<?> putDatos(Profesional nuevo, Profesional guardado){
		if(nuevo.getNombre().isEmpty() || nuevo.getApellidos().isEmpty() || nuevo.getEmail().isEmpty()
				|| nuevo.getTlfn().isEmpty() || nuevo.getEspecialidad().isEmpty() || nuevo.getDescripcion().isEmpty()) {
			return ResponseEntity.badRequest().body("Faltan Datos");
		}
		if(guardado==null) {return ResponseEntity.notFound().build();}
		else {
			if(!guardado.getNombre().equals(nuevo.getNombre())) {guardado.setNombre(nuevo.getNombre());}
			if(!guardado.getApellidos().equals(nuevo.getApellidos())) {guardado.setApellidos(nuevo.getApellidos());}
			if(!guardado.getEmail().equals(nuevo.getEmail())) {guardado.setEmail(nuevo.getEmail());}
			if(!guardado.getTlfn().equals(nuevo.getTlfn())) {guardado.setTlfn(nuevo.getTlfn());}
			if(!guardado.getDescripcion().equals(nuevo.getDescripcion())) {guardado.setDescripcion(nuevo.getDescripcion());}
			if(!guardado.getEspecialidad().equals(nuevo.getEspecialidad())) {guardado.setEspecialidad(nuevo.getEspecialidad());}
			profRepo.save(guardado);
			return ResponseEntity.ok().build();
		}
	}
	
	
	
	public void initProfesionales() {
		Profesional admin = new Profesional();String encodedPass = passwordEncoder.encode("rad3#La00tR5%$$a2");
		admin.setEmail("administrador");admin.setContrasenia(encodedPass);
		Profesional marta = new Profesional("Marta", "Cuberos Mesa", "emai","tlfn");
		marta.setEmail("marta@correo.es");String contraMarta = passwordEncoder.encode("contrasenia123");
		marta.setContrasenia(contraMarta);
		Profesional ej1 = new Profesional("Ej1", "ejemplo1", "correo33@correo.com","tlfn");
		Profesional ej2 = new Profesional("Ej2", "ejemplo2", "emai","tlfn");
		agendaRepo.save(marta.getAgenda());agendaRepo.save(ej1.getAgenda());agendaRepo.save(ej2.getAgenda());
		agendaRepo.save(new Agenda());
		marta.setEspecialidad("Nutrición Materno-Infantil"); marta.setDescripcion("Además de interesarme la Nutrición infantil tengo experiencia en consultas tanto para personas\n"
				+ "                que quieren disminuir su peso y mejorar su relación con la comida, como para intolerantes a la fructosa, lactosa o gluten");
		marta.setImg("./assets/imagenes/Marta.jpeg");
		ej1.setEspecialidad("Ej1 Especialidad"); ej1.setDescripcion("Ej1 Descripcion");ej1.setImg("./assets/imagenes/usuario.png");
		ej2.setEspecialidad("Ej2 Especialidad"); ej2.setDescripcion("Ej2 Descripcion");ej2.setImg("./assets/imagenes/usuario.png");
		marta.setVerificado(true);admin.setVerificado(true);
		profRepo.save(marta);profRepo.save(ej1);profRepo.save(ej2);profRepo.save(admin);
	}
}

package com.example.demo.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.model.Agenda;
import com.example.demo.model.Profesional;
import com.example.demo.repository.AgendaRepo;
import com.example.demo.repository.ProfesionalRepo;
import com.example.demo.security.JWTUtil;

@Service
public class ProfesionalService {

	
	
	@Autowired AgendaRepo agendaRepo;
	@Autowired ProfesionalRepo profRepo;
	
	@Autowired BlogService blogService;
	
    @Autowired private JWTUtil jwtUtil;
	@Autowired private PasswordEncoder passwordEncoder;
	
    
    
    public ResponseEntity<?> setImagen(Long id,String email,MultipartFile imagen){
    	if(imagen.isEmpty() || imagen==null) {
    		return ResponseEntity.badRequest().body("Falta imagen");
    	}
		Profesional buscado = profRepo.findById(id).orElse(null);
		Profesional who = profRepo.findByEmail(email).orElse(null);
    	if(!who.getEmail().equals("administrador") && who.getId()!=buscado.getId()) {return ResponseEntity.badRequest().body("Falta permisos");}
    	else {
    		Path directorioImagenes = Paths.get("src//main//resources//static/images");
    		String rutaAbsoluta = directorioImagenes.toFile().getAbsolutePath();
    		ResponseEntity<?>resp;
    		try {
				byte[] bytesImg = imagen.getBytes();
				Path rutaCompleta = Paths.get(rutaAbsoluta+"//"+imagen.getOriginalFilename());
				Files.write(rutaCompleta, bytesImg);

		    	if(buscado!=null) {
		    		buscado.setImg(imagen.getOriginalFilename());
		    		profRepo.save(buscado);
		    		resp= ResponseEntity.ok(HttpStatus.CREATED);
		    	}
		    	else {
		    		resp= ResponseEntity.notFound().build();
		    	}
				
			} catch (Exception e) {
				resp=ResponseEntity.badRequest().body("Error al procesar la imagen");
				
			}
    	return resp;
    }
    	
    }
    
    
    public Resource getImgProf(Long id) {
		Profesional p= profRepo.findById(id).orElse(null);
		Resource res=null;
		if(p!=null) {		
			if(p.getImg()==null) {}
			else {
				Path directorioImagenes = Paths.get("src//main//resources//static/images");
	    		String rutaAbsoluta = directorioImagenes.toFile().getAbsolutePath();
				Path rutaCompleta = Paths.get(rutaAbsoluta+"//"+p.getImg());
				try {				
					Resource resource = new UrlResource(rutaCompleta.toUri());
					res= resource;
					} catch (IOException e) {
					res= null;
					}
				
				}
				
				return res;
		}
		return res;
	}
    
    public ResponseEntity<?> borraProfesional(Long id,String email){
    	if(email==null || !email.equals("administrador")) {return ResponseEntity.badRequest().body("Faltan permisos");}
    	Profesional buscado = profRepo.findById(id).orElse(null);
    	if(buscado!=null) {
    		blogService.salvaPosts(id);
    		profRepo.delete(buscado);
    		return ResponseEntity.noContent().build();
    	}else {
    		return ResponseEntity.notFound().build();
    	}
    }
    
    
    public int correoOcupado(String correo) {
    	Profesional buscado = profRepo.findByEmail(correo).orElse(null);
    	return buscado==null ? 0 : 1;
	}
    
	public Profesional getProfesional(Long id) {
		Profesional prof=profRepo.findById(id).orElse(null);
		if(prof!=null) return prof;
		return null;
	}
	
	public Profesional getProfesionalByEmail(String mail) {
		return profRepo.findByEmail(mail).orElse(null);
	
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
	
	
	
	public ResponseEntity<?> newProfesional(Profesional prof) {
    	
    	if(prof==null || prof.getNombre()==null || prof.getApellidos()==null || prof.getContrasenia()==null ||
    			prof.getEmail()==null || prof.getTlfn()==null || prof.getEspecialidad()==null || prof.getDescripcion()==null) {
    		return ResponseEntity.badRequest().body("Faltan datos");
    	}
    	else if(correoOcupado(prof.getEmail())==1) {
    		return ResponseEntity.badRequest().body("Correo Ocupado");
    	}
    	else {
    		 String encodedPass = passwordEncoder.encode(prof.getContrasenia());
         
    		Profesional p = new Profesional(prof.getNombre(),prof.getApellidos(),encodedPass,
    				prof.getEmail(),prof.getTlfn(),prof.getEspecialidad(),prof.getDescripcion());
    		agendaRepo.save(p.getAgenda());
    		profRepo.save(p);
    		return ResponseEntity.ok(HttpStatus.CREATED);
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
					resp=ResponseEntity.badRequest().body("Contraseña Incorrecta");}	
				else {
					
					if(buscado.getVerificado()==false) {
					resp=ResponseEntity.badRequest().body("Esta cuenta no esta verificada todavia");
					}
					else {
						resp=generaToken(prof.getEmail(),prof.getContrasenia());}
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
	
	
	
	
	public ResponseEntity<?> verificaProf(Long id,String email){
		if(email==null || !email.equals("administrador")) {return ResponseEntity.badRequest().body("Faltan permisos");}
		
		Profesional buscado = profRepo.findById(id).orElse(null);
		if(buscado==null) {return ResponseEntity.notFound().build();}
		else {
			//Si el que envia esto esta verificado
			if(buscado.getVerificado()) return ResponseEntity.badRequest().body("El profesional ya ha sido verificado");
			buscado.setVerificado(true);
			profRepo.save(buscado);
			return ResponseEntity.ok().build();
		}
	}
	public ResponseEntity<?> getDatos(String email) {
		
		if(email.isEmpty() || email.equals("anonymousUser")) {return ResponseEntity.badRequest().body("Falta token");}
		Profesional prof=profRepo.findByEmail(email).orElse(null);
		if(prof==null) {return ResponseEntity.notFound().build();}
		else {return ResponseEntity.ok(prof);}
	}
	
	public ResponseEntity<?> getProfIdByEmail(String email) {
		
		Profesional prof=profRepo.findByEmail(email).orElse(null);
		if(prof==null) {return ResponseEntity.notFound().build();}
		else {return ResponseEntity.ok(prof.getId());}
	}
	
	
	
	public ResponseEntity<?> putDatosProfesional(Profesional nuevo, Long idBuscado,String who){
		if(!who.equals("administrador")) {
			return ResponseEntity.badRequest().body("Faltan permisos");
		}
		Profesional guardado = profRepo.findById(idBuscado).orElse(null);
		
		if(nuevo.getNombre()==null || nuevo.getApellidos()==null || nuevo.getEmail()==null
				|| nuevo.getTlfn()==null || nuevo.getEspecialidad()==null || nuevo.getDescripcion()==null) {
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
		Profesional admin = new Profesional();
		String encodedPass = passwordEncoder.encode("rad3#La00tR5%$$a2");
		admin.setEmail("administrador");admin.setContrasenia(encodedPass);
		
		Profesional marta = new Profesional("Marta", "Cuberos Mesa", "emai","tlfn");
		marta.setEmail("marta@correo.es");String contraMarta = passwordEncoder.encode("contrasenia123");
		marta.setContrasenia(contraMarta);

		agendaRepo.save(marta.getAgenda());
		marta.setEspecialidad("Nutrición Materno-Infantil"); 
		marta.setDescripcion("Además de interesarme la Nutrición infantil tengo experiencia en consultas tanto para personas\n"
		+ " que quieren disminuir su peso y mejorar su relación con la comida, como para intolerantes a la fructosa, lactosa o gluten");

		marta.setVerificado(true); profRepo.save(marta);
		admin.setVerificado(true); profRepo.save(admin);
		
		
		
	}
}

package com.example.demo.controller;


import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Cita;
import com.example.demo.model.Comentario;
import com.example.demo.model.Dia;
import com.example.demo.model.Mes;
import com.example.demo.security.JWTUtil;
import com.example.demo.services.AgendaService;
import com.example.demo.services.ComentarioService;


@CrossOrigin(origins = "https://rafacm23.github.io")
@RestController
public class ApiController {
	
    @Autowired private JWTUtil jwtUtil;
    @Autowired private AuthenticationManager authManager;
    @Autowired private PasswordEncoder passwordEncoder;
	
    @Autowired private ComentarioService comentarioService;
    @Autowired private AgendaService agendaService;
    
    
    /**
     * Este metodo recibe un numero de un mes, y lo devuelve
     * @param numero
     * @return
     */
    @GetMapping("/mes/{numero}")
    public ResponseEntity<Mes> getMes(@PathVariable int numero){
    	//get enero del 2022 y todos los dias
    	Mes mes=agendaService.getMes(2022,numero);//cambiar el 2022 por anio actual
    	return ResponseEntity.ok(mes);
    }
    
    /**
     * Este metodo recibe una reserva y la envia al metodo nuevaReserva, si esta todo correcto se guarda
     * @param cita
     * @return ResponseEntity
     */
    @PostMapping("/reserva")
    public ResponseEntity<?> reservarCita(@RequestBody Cita cita){
    	agendaService.nuevaReserva(cita);
    	return ResponseEntity.ok().build();
    }
    
    
    
    
    /**
     * Este metodo recibe un comentario y si todos los campos son válidos se guarda
     * @param comentario
     * @return ResponseEntity
     */
    @PostMapping("/comentario")
    	public ResponseEntity<?> postComentarioServicio(@RequestBody(required=false) Comentario comentario){
    	
    	if( comentario==null || comentario.getAutor()==null || comentario.getAutor()=="" ||comentario.getContenido()==null
    			|| comentario.getContenido()=="" /*|| comentario.getFecha()==null*/) {
    		return ResponseEntity.badRequest().body("Faltan datos");
    	}
    	else {
    		if(comentarioService.postComentario(comentario)==1) {return ResponseEntity.ok(HttpStatus.CREATED);}
    		else {return ResponseEntity.badRequest().body("Ya existe el comentario");}
    		
    	}
    }
    
    
    
    
    
    
    
    //------------------------------------ Identificación ------------------------------------//
    

//	@PostMapping("/auth/register")
//    public ResponseEntity<?> register(@RequestBody(required=false) User user){
//		if(user==null||user.getName()==null||user.getNickname()==null||user.getEmail()==null|| user.getPassword()==null || user.getProvincia()==null) {
//			return ResponseEntity.badRequest().body("Faltan datos");
//		}
//    	User buscado =userRepo.findByEmail(user.getEmail()).orElse(null);
//    	User buscado2 = userRepo.findByNickname(user.getNickname()).orElse(null);
//		if(buscado==null && buscado2==null) {
//        String encodedPass = passwordEncoder.encode(user.getPassword());
//        user.setPassword(encodedPass);
//        user = userRepo.save(user);
//        String token = jwtUtil.generateToken(user.getEmail());
//        return 
//        		ResponseEntity.ok(Collections.singletonMap("jwt-token", token));
//		}
//		else {
//			return ResponseEntity.badRequest().body("Usuario en uso");
//		}
//    }
	
//
//    @PostMapping("/auth/login")
//    public ResponseEntity<?> login(@RequestBody(required=false) LoginCredentials body){
//    	
//    	if(body==null || body.getEmail()==null || body.getPassword()==null) {
//    		return ResponseEntity.badRequest().body("Faltan datos");
//    	}
//    	User buscado =userRepo.findByEmail(body.getEmail()).orElse(null);
//    	if(buscado==null) {
//    		
//    		return ResponseEntity.badRequest().body("Bad Mail");
//    	}
//    	//estoNoFunciona
//    	if(!passwordEncoder.matches(body.getPassword(), buscado.getPassword())) {
//    	
//    		return ResponseEntity.badRequest().body("Bad Password");
//    	}
//        try {
//            UsernamePasswordAuthenticationToken authInputToken =
//            new UsernamePasswordAuthenticationToken(body.getEmail(), body.getPassword());
//            
//            
//            authManager.authenticate(authInputToken);
//
//            String token = jwtUtil.generateToken(buscado.getEmail());
//
//            
//            return ResponseEntity.ok(Collections.singletonMap("jwt-token", token));
//        }catch (AuthenticationException authExc){
//            return ResponseEntity.badRequest().body("Error al procesar login");
//        }
//    }
    

	
}


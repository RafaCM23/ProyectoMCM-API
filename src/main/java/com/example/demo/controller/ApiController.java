package com.example.demo.controller;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.*;
import com.example.demo.repository.AnuncioRepo;
import com.example.demo.repository.UserRepo;
import com.example.demo.security.JWTUtil;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
public class ApiController {

	
	@Autowired private UserRepo userRepo;
    @Autowired private JWTUtil jwtUtil;
    @Autowired private AuthenticationManager authManager;
    @Autowired private PasswordEncoder passwordEncoder;
	
    
    //------------------------------------ API ------------------------------------//
    
    /**
     * Este metodo recibe un usuario por parametro, busca que no coincida ni nick ni email, y en ese
     * caso lo registra, guardando su contrasenia codificada y devuelve un token jwt. En caso contrario devuelve bad request 
     * si el nick o el correo esta en uso.
     * @param user Usuario
     * @return tokenJWT
     */
	@PostMapping("/auth/register")
    public ResponseEntity<?> register(@RequestBody(required=false) User user){
		if(user==null||user.getName()==null||user.getNickname()==null||user.getEmail()==null|| user.getPassword()==null || user.getProvincia()==null) {
			return ResponseEntity.badRequest().body("Faltan datos");
		}
    	User buscado =userRepo.findByEmail(user.getEmail()).orElse(null);
    	User buscado2 = userRepo.findByNickname(user.getNickname()).orElse(null);
		if(buscado==null && buscado2==null) {
        String encodedPass = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPass);
        user = userRepo.save(user);
        String token = jwtUtil.generateToken(user.getEmail());
        return 
        		ResponseEntity.ok(Collections.singletonMap("jwt-token", token));
		}
		else {
			return ResponseEntity.badRequest().body("Usuario en uso");
		}
    }
	
	/**
	 * Este metodo recibe las credenciales de login (email,password) y las coteja con la base de datos,
	 * si las credenciales son validas devuelve un tokenJWT, en caso contrario devuelve bad request segun si 
	 * ha fallado el email o la contrasenia. Cualquier otro error devuelve bad request error al procesar login
	 * @param body LoginCredentials
	 * @return tokenJWT
	 */
    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody(required=false) LoginCredentials body){
    	
    	if(body==null || body.getEmail()==null || body.getPassword()==null) {
    		return ResponseEntity.badRequest().body("Faltan datos");
    	}
    	User buscado =userRepo.findByEmail(body.getEmail()).orElse(null);
    	if(buscado==null) {
    		
    		return ResponseEntity.badRequest().body("Bad Mail");
    	}
    	//estoNoFunciona
    	if(!passwordEncoder.matches(body.getPassword(), buscado.getPassword())) {
    	
    		return ResponseEntity.badRequest().body("Bad Password");
    	}
        try {
            UsernamePasswordAuthenticationToken authInputToken =
            new UsernamePasswordAuthenticationToken(body.getEmail(), body.getPassword());
            
            
            authManager.authenticate(authInputToken);

            String token = jwtUtil.generateToken(buscado.getEmail());

            
            return ResponseEntity.ok(Collections.singletonMap("jwt-token", token));
        }catch (AuthenticationException authExc){
            return ResponseEntity.badRequest().body("Error al procesar login");
        }
    }
    


    //------------------------------------ Angular ------------------------------------//
    
    /**
     * Este metodo comprueba que el usuario esta registrado cotejando el token, si es asi devuelve su informacion
     * En caso encontrario devuelve not found
     * @return
     */
    @GetMapping("/misdatos")
    public ResponseEntity<User> cambiarDatos(){
    	
    	String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    	User usuario = userRepo.findByEmail(email).orElse(null);
    	
        if(usuario!=null) {return ResponseEntity.ok(usuario);}
        else {return ResponseEntity.notFound().build();}
    }
    
    
    
    /**
     * Este metodo modifica los datos del usuario. Recibe los nuevos datos por RequestBody y el usuario por token.
     * Si es el mismo usuario y los datos no son erroneos se modifican los datos. En caso contrario devuelve badRequest o not found.
     * @param usuario User
     * @return ResponseEntity
     */
    @PutMapping("/misdatos")
    public ResponseEntity<?> cambiarDatos(@RequestBody(required=false) User usuario){
    	String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
   
    	User usuarioToken = userRepo.findByEmail(email).orElse(null);
    	
    	if(usuario==null|| usuario.getId()==null ||usuario.getEmail()==null 
    	   ||usuario.getNickname()==null ||usuario.getName()==null) 
    	{
    		return ResponseEntity.badRequest().body("Faltan Datos");
    	}
    	
    	User usuarioDatos =userRepo.findById(usuario.getId()).orElse(null);
    	
    	if(usuarioToken==null || usuarioDatos==null || !usuarioToken.getId().equals(usuarioDatos.getId())) {
    		
    		return ResponseEntity.notFound().build();
    	}
    	else {
    		usuarioDatos.setEmail(usuario.getEmail());
    		usuarioDatos.setNickname(usuario.getNickname());
    		usuarioDatos.setProvincia(usuario.getProvincia());
    		usuarioDatos.setName(usuario.getName());
    		userRepo.save(usuarioDatos);
    		return ResponseEntity.ok().build();
    	}
    	
    }
    
    
    
    /**
     * Este metodo recibe unos datos de registro del cual se comprueba si el nick ya esta ocupado.
     * Si esta oc //si devuelve 200 esta ocupadoupado devuelve ok 200 y si no lo encuentra devuelve not found 404. SI no 
     * recibe el nick devuelve badRequest 
     * @param usuario
     * @return ResponseEntity
     */
    @PostMapping("/nickOcupado")
    public ResponseEntity<?> nickOcupado(@RequestBody(required=false) User usuario){
    	
    	if(usuario==null || usuario.getNickname()==null) {return ResponseEntity.badRequest().body("Faltan datos");}
    	
    	User buscado = userRepo.findByNickname(usuario.getNickname()).orElse(null);
    	System.out.println(usuario.getNickname());
    	if(buscado==null) {
    		return ResponseEntity.notFound().build();
    	}
    	else {
    		return ResponseEntity.ok().build();
    	}
    }
    
 
    /**
     * Este metodo recibe unos datos de registro del cual se comprueba si el correo ya esta ocupado.
     * Si esta ocupado devuelve ok 200 y si no lo encuentra devuelve not found 404. SI no 
     * recibe el correo devuelve badRequest 
     * @param usuario
     * @return ResponseEntity
     */
    @PostMapping("/correoOcupado")
    public ResponseEntity<?> correOcupado(@RequestBody(required=false) User usuario){
    	if(usuario==null || usuario.getEmail()==null) {
    		return ResponseEntity.badRequest().body("Faltan datos");
    	}
    	User buscado = userRepo.findByEmail(usuario.getEmail()).orElse(null);
    	if(buscado==null) {
    		return ResponseEntity.notFound().build();
    	}
    	else {
    		return ResponseEntity.ok().build();
    	}
    }
	
	
}


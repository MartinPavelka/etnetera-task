package com.etnetera.hr.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.etnetera.hr.data.JavaScriptFramework;
import com.etnetera.hr.repository.JavaScriptFrameworkRepository;
import com.etnetera.hr.rest.Errors;
import com.etnetera.hr.rest.ValidationError;

/**
 * Simple REST controller for accessing application logic.
 * 
 * @author Etnetera
 *
 */
@RestController
public class JavaScriptFrameworkController extends EtnRestController {

	private final JavaScriptFrameworkRepository repository;

	@Autowired
	public JavaScriptFrameworkController(JavaScriptFrameworkRepository repository) {
		this.repository = repository;
	}

	@GetMapping("/frameworks")
	public Iterable<JavaScriptFramework> frameworks() {
		return repository.findAll();
	}
	
	@PostMapping("/add")
	public ResponseEntity<?> add(@RequestBody JavaScriptFramework framework) {
		if (framework.getName() != null && framework.getName().length() <= 30) {
			JavaScriptFramework jsf = initFramework(framework);
			return new ResponseEntity<>(this.repository.save(jsf), HttpStatus.OK);
		}
		return handleAddError(framework);
	}
	
	
	
	@GetMapping("/frameworks/{id}")
	  Optional<JavaScriptFramework> one(@PathVariable Long id) {

	    return repository.findById(id);
	  }
	
	private JavaScriptFramework initFramework(JavaScriptFramework framework) {
		JavaScriptFramework jsf = new JavaScriptFramework(framework.getName());
		if (framework.getVersion() != null) jsf.setVersion(framework.getVersion());
		if (framework.getDeprecationDate() != null) jsf.setDeprecationDate(framework.getDeprecationDate());
		if (framework.getHypeLevel() != 0) jsf.setHypeLevel(framework.getHypeLevel());
//			deprecationDate
//			hypeLevel
		
		return framework;
	}
	

//	Handling errors here
	/*
	 * @param framework - POST request for creating new framework
	 * @return - Response entity with corresponding body
	 * 
	 * */
	private ResponseEntity<?> handleAddError(JavaScriptFramework framework) {
		Errors errors = new Errors();
		List<ValidationError> error = new ArrayList<>();
		
		if (framework.getName() == null) error.add(new ValidationError("name", "NotEmpty"));
		else error.add(new ValidationError("name", "Size"));
		errors.setErrors(error);
		return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
	}
	
}

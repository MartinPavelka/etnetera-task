package com.etnetera.hr.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

//	Find all
	@GetMapping("/frameworks")
	public Iterable<JavaScriptFramework> frameworks() {
		return repository.findAll();
	}

//	Add one
	@PostMapping("/add")
	public ResponseEntity<?> add(@RequestBody JavaScriptFramework framework) {
		if (framework.getName() != null && framework.getName().length() <= 30) {
			JavaScriptFramework jsf = initFramework(framework);
			return new ResponseEntity<>(this.repository.save(jsf), HttpStatus.OK);
		}
		return handleAddError(framework);
	}

//	In case values are available at creation, initialize object immediately
	private JavaScriptFramework initFramework(JavaScriptFramework framework) {
		JavaScriptFramework jsf = new JavaScriptFramework(framework.getName());
		if (framework.getVersion() != null) jsf.setVersion(framework.getVersion());
		if (framework.getDeprecationDate() != null) jsf.setDeprecationDate(framework.getDeprecationDate());
		if (framework.getHypeLevel() != 0) jsf.setHypeLevel(framework.getHypeLevel());

		return framework;
	}

//	FIND //
//	Find by id
	@GetMapping("/frameworks/{id}")
	Optional<JavaScriptFramework> one(@PathVariable Long id) {
		return repository.findById(id);
	}

//	Find by name
	@GetMapping("/frameworks/name/{name}")
	public List<JavaScriptFramework> findByName(@PathVariable String name) {
		List<JavaScriptFramework> frameworkList = new ArrayList<>();
		for (JavaScriptFramework framework : repository.findAll()) {
			if (framework.getName().equals(name + ".js") || framework.getName().equals(name))
				frameworkList.add(framework);
		}
		return frameworkList;
	}

// Find by hype
	@GetMapping("/frameworks/hype/{hypeLevel}")
	public List<JavaScriptFramework> findByHype(@PathVariable int hypeLevel) {
		List<JavaScriptFramework> frameworkList = new ArrayList<>();
		for (JavaScriptFramework framework : repository.findAll()) {
			if (framework.getHypeLevel() == hypeLevel)
				frameworkList.add(framework);
		}
		return frameworkList;
	}

//	DELETE //
// delete one
	@DeleteMapping("/delete/{id}")
	void deleteFramework(@PathVariable Long id) {
		repository.deleteById(id);
	}

//	delete by name
	@DeleteMapping("/delete/name/{name}")
	void deleteEmployee(@PathVariable String name) {
		for (JavaScriptFramework framework : repository.findAll()) {
			if (framework.getName().equals(name + ".js") || framework.getName().equals(name))
				repository.deleteById(framework.getId());
		}
	}
	
// EDIT //
//	edit by id
	@PutMapping("put/{id}")
	JavaScriptFramework editFramework(@RequestBody JavaScriptFramework newFramework, @PathVariable Long id) {
		return repository.findById(id)
				.map(framework -> {
					if (newFramework.getName() != null) framework.setName(newFramework.getName());
					if (newFramework.getVersion() != null) framework.setVersion(newFramework.getVersion());
					if (newFramework.getDeprecationDate() != null) framework.setDeprecationDate(newFramework.getDeprecationDate());
					if (newFramework.getHypeLevel() != 0) framework.setHypeLevel(newFramework.getHypeLevel());
					return repository.save(framework);
				}).orElseGet(() -> {
					newFramework.setId(id);
			        return repository.save(newFramework);
			      });
	}

//	Handling errors here
	/*
	 * @param framework - POST request for creating new framework
	 * 
	 * @return - Response entity with corresponding body
	 * 
	 */
	private ResponseEntity<?> handleAddError(JavaScriptFramework framework) {
		Errors errors = new Errors();
		List<ValidationError> error = new ArrayList<>();

		if (framework.getName() == null)
			error.add(new ValidationError("name", "NotEmpty"));
		else
			error.add(new ValidationError("name", "Size"));
		errors.setErrors(error);
		return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
	}

}

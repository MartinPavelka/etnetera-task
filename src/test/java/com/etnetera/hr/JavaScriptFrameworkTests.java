package com.etnetera.hr;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.etnetera.hr.data.JavaScriptFramework;
import com.etnetera.hr.repository.JavaScriptFrameworkRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Class used for Spring Boot/MVC based tests.
 * 
 * @author Etnetera
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class JavaScriptFrameworkTests {

	@Autowired
	private MockMvc mockMvc;

	private ObjectMapper mapper = new ObjectMapper();

	@Autowired
	private JavaScriptFrameworkRepository repository;

	private void prepareData() throws Exception {
		JavaScriptFramework react = new JavaScriptFramework("ReactJS");
		JavaScriptFramework vue = new JavaScriptFramework("Vue.js");

		repository.save(react);
		repository.save(vue);
	}
	
	public void similarDataSetup() {
		JavaScriptFramework framework1 = new JavaScriptFramework("karel.js");
		JavaScriptFramework framework2 = new JavaScriptFramework("karel.js");
		JavaScriptFramework framework3 = new JavaScriptFramework("snoud.js");
		JavaScriptFramework framework4 = new JavaScriptFramework("recreate.js");
		framework1.setVersion("1.3.2");
		framework2.setVersion("1.4");
		framework3.setVersion("2");
		framework1.setHypeLevel(1);
		framework2.setHypeLevel(2);
		framework3.setHypeLevel(4);
		framework4.setHypeLevel(2);
		
		repository.save(framework1);
		repository.save(framework2);
		repository.save(framework3);
		repository.save(framework4);
	}

	@Test
	public void frameworksTest() throws Exception {
		prepareData();

		mockMvc.perform(get("/frameworks")).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8)).andExpect(jsonPath("$", hasSize(2)))
				.andExpect(jsonPath("$[0].id", is(1))).andExpect(jsonPath("$[0].name", is("ReactJS")))
				.andExpect(jsonPath("$[1].id", is(2))).andExpect(jsonPath("$[1].name", is("Vue.js")));
	}

	@Test
	public void addFrameworkInvalid() throws JsonProcessingException, Exception {
		JavaScriptFramework framework = new JavaScriptFramework();
		mockMvc.perform(
				post("/add").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsBytes(framework)))
				.andExpect(status().isBadRequest()).andExpect(jsonPath("$.errors", hasSize(1)))
				.andExpect(jsonPath("$.errors[0].field", is("name")))
				.andExpect(jsonPath("$.errors[0].message", is("NotEmpty")));

		framework.setName("verylongnameofthejavascriptframeworkjavaisthebest");
		mockMvc.perform(
				post("/add").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsBytes(framework)))
				.andExpect(status().isBadRequest()).andExpect(jsonPath("$.errors", hasSize(1)))
				.andExpect(jsonPath("$.errors[0].field", is("name")))
				.andExpect(jsonPath("$.errors[0].message", is("Size")));
	}

	@Test
	public void addFrameworkValid() throws JsonProcessingException, Exception {
		JavaScriptFramework framework = new JavaScriptFramework("karel.js");
		mockMvc.perform(
				post("/add").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsBytes(framework)))
				.andExpect(status().isOk());
	}

	@Test
	public void deleteFrameworkIDValid() throws JsonProcessingException, Exception {
		JavaScriptFramework framework = new JavaScriptFramework("karel.js");
		mockMvc.perform(
				post("/add").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsBytes(framework)))
				.andExpect(status().isOk());
		mockMvc.perform(delete("/delete/1")).andExpect(status().isOk());
		mockMvc.perform(get("/frameworks")).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8)).andExpect(jsonPath("$", hasSize(0)));
	}
	


	@Test
	public void deleteFrameworksByNamevalid() throws JsonProcessingException, Exception {
		similarDataSetup();
		mockMvc.perform(delete("/delete/name/karel")).andExpect(status().isOk());
		mockMvc.perform(get("/frameworks")).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$", hasSize(2)));
	}
	
	@Test
	public void findFrameworksByHypeValid() throws JsonProcessingException, Exception {
		similarDataSetup();
		mockMvc.perform(get("/frameworks/hype/2")).andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
		.andExpect(jsonPath("$", hasSize(2)))
		.andExpect(jsonPath("$[0].id", is(2)))
		.andExpect(jsonPath("$[0].hypeLevel", is(2)))
		.andExpect(jsonPath("$[1].id", is(4)))
		.andExpect(jsonPath("$[1].hypeLevel", is(2)));
	}
	
	@Test
	public void findFrameworksByNameValid() throws JsonProcessingException, Exception {
		similarDataSetup();
		mockMvc.perform(get("/frameworks/name/karel")).andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
		.andExpect(jsonPath("$", hasSize(2)))
		.andExpect(jsonPath("$[0].name", is("karel.js")))
		.andExpect(jsonPath("$[1].name", is("karel.js")));
	}
	
	@Test
	public void findFrameworkByIdValid() throws JsonProcessingException, Exception {
		prepareData();
		mockMvc.perform(get("/frameworks/1")).andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
		.andExpect(jsonPath("$.name", is("ReactJS")))
		.andExpect(jsonPath("$.id", is(1)));
	}
	
	@Test
	public void editFrameworkValid() throws JsonProcessingException, Exception {
		similarDataSetup();
		mockMvc.perform(get("/frameworks/1")).andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$.name", is("karel.js")));
		
		JavaScriptFramework framework = new JavaScriptFramework("jenda.js");
		mockMvc.perform(put("/put/1").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsBytes(framework)))
				.andExpect(jsonPath("$.name", is("jenda.js")));
		
		mockMvc.perform(get("/frameworks/1")).andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
		.andExpect(jsonPath("$.name", is("jenda.js")));
	}

}

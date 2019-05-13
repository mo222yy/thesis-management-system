package project.controller;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


//import project.model.entities.Student;

import project.model.entities.Role;

import project.model.entities.User;
import project.model.repositories.StudentRepository;
import project.model.repositories.UserRepository;
import project.model.services.EncryptionService;

@RestController
class UserController {
	
	@Autowired
	private EncryptionService enrypt;

	private final UserRepository repository;
	private final StudentRepository studentRepository;
	
	
	UserController(UserRepository repository,StudentRepository studentRepository) {
		this.repository = repository;
		this.studentRepository = studentRepository;
	}

	@GetMapping("/users/{id} ")
	Resource<User> one(@PathVariable String id) {
		User user = repository.findFirstById(id);
		System.out.print(user.getId());
		return new Resource<>(user,
			    linkTo(methodOn(UserController.class).one(id)).withSelfRel(),
			    linkTo(methodOn(UserController.class).all()).withRel("employees"));
		
	}
	
	@GetMapping(value = "/users", produces = "application/json; charset=UTF-8")
	Resources<Resource<User>> all() {
		List<Resource<User>> users = repository.findAll().stream()
			    .map(user -> new Resource<>(user,
			    		linkTo(methodOn(UserController.class).one(user.getId())).withSelfRel(),
			    		linkTo(methodOn(UserController.class).all()).withRel("users")))
			    	    .collect(Collectors.toList());

		return new Resources<>(users,
				linkTo(methodOn(UserController.class).all()).withSelfRel());
	}

	@PutMapping("/users/{id}")
	User updateUser(@RequestBody User newUser, @PathVariable String id) {
		return repository.findById(id)
			.map(user -> {
				user.setName(newUser.getName());
				return repository.save(user);
			})
			.orElseGet(() -> {
				newUser.setId(id);
				return repository.save(newUser2());
			});
	}
//	@PostMapping("/createUser")
//	User newUser2() {
//		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(16);
//		String result = encoder.encode("myPassword");
//		
//		User newUser = new User("Jens", result, "Jens@hotmail.com", new Role[] { Role.STUDENT });
//		repository.save(newUser);
//		for(int i=0; i < newUser.getRoles().length; i++){
////			if(newUser.getRoles() == "Student") {
////				System.out.print(newUser.getId());
////				studentRepository.save(new Student(newUser.getId(), "None"));
////			}
//		}
//		return newUser;
//	}

	@PostMapping("/users")
	User newUser2() {
		return repository.save(new User("Test_Auth", enrypt.hash("password"), "Jtest@hotmail.com", new Role[] { Role.STUDENT } ));
	}
	
}
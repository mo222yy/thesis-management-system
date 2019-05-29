package project.controller;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import project.model.entities.Feedback;
import project.model.entities.FinalReport;
import project.model.entities.InitialReport;
import project.model.entities.ProjectDescription;
import project.model.entities.ProjectPlan;
import project.model.entities.Student;
import project.model.entities.Supervisor;
import project.model.entities.User;
import project.model.enums.PendingSupervisor;
import project.model.repositories.*;

@RestController
public class StudentController {
	private final UserRepository repository;
	private final SupervisorRepository supervisorRepository;
	private final ProjectPlanRepository projectPlanRepository;
	private final FeedbackRepository feedbackRepository;
	private final InitialReportRepository initialReportRepository;
	private final FinalReportRepository finalReportRepository;
	private final ProjectDescriptionRepository projectDescriptionRepository;
	private final StudentRepository studentRepository;
	
	StudentController(UserRepository repository,SupervisorRepository supervisorRepository, ProjectPlanRepository projectPlanRepository, FeedbackRepository feebackRepository,
			InitialReportRepository initialReportRepository, FinalReportRepository finalReportRepository, ProjectDescriptionRepository projectDescriptionRepository,
			StudentRepository studentRepository) {
		this.repository = repository;
		this.supervisorRepository = supervisorRepository;
		this.projectPlanRepository = projectPlanRepository;
		this.feedbackRepository = feebackRepository;
		this.initialReportRepository = initialReportRepository;
		this.finalReportRepository = finalReportRepository;
		this.projectDescriptionRepository = projectDescriptionRepository;
		this.studentRepository = studentRepository;
	}
	
//	@GetMapping(value = "/supervisors/{id}", produces = "application/json; charset=UTF-8")
//	Resource<Supervisor> one(@PathVariable String id) {
//		Supervisor supervisor = supervisorRepository.findFirstById(id);
//		return new Resource<>(supervisor,
//				linkTo(methodOn(StudentController.class).one(id)).withSelfRel(),
//				linkTo(methodOn(StudentController.class).all()).withRel("supervisors"));
//	}
	
	@GetMapping(value = "/student/getAvailableSupervisors", produces = "application/json; charset=UTF-8")
	Resources<Resource<Supervisor>> all() {
		List<Resource<Supervisor>> supervisors = supervisorRepository.findByAvailableForSupervisorTrue().stream()
			    .map(supervisor -> new Resource<>(supervisor,
			    		linkTo(methodOn(StudentController.class).all()).withRel("getAvailableSupervisors")))
			    	    .collect(Collectors.toList());
		return new Resources<>(supervisors,
				linkTo(methodOn(StudentController.class).all()).withSelfRel());
	}
	@PutMapping("/student/requestSupervisor")
	Supervisor updateSupervisor(@RequestParam String supervisorUserId) {
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String name = auth.getName();
		
		User user = repository.findFirstByEmailAdress(name);
		Student student = studentRepository.findFirstByuserId(user.getId());
		Supervisor supervisor = supervisorRepository.findFirstByuserId(supervisorUserId);
		
		if(PendingSupervisor.AWAITING.equals(student.getPendingSupervisor()) || PendingSupervisor.ACCEPTED.equals(student.getPendingSupervisor())) {
			return supervisor;
		} else {
			supervisor.getAwaitingResponse().add(user.getId());
			student.setPendingSupervisor(PendingSupervisor.AWAITING);
			student.setAssignedSupervisorId(supervisor.getUserId());

			
			studentRepository.save(student);
			return supervisorRepository.save(supervisor);
		}

	}
	
	@GetMapping(value = "/student/projectPlan", produces = "application/json; charset=UTF-8")
	Resource<ProjectPlan> one1() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String name = auth.getName();
		User user = repository.findFirstByEmailAdress(name);
		ProjectPlan projectplan = projectPlanRepository.findFirstByuserId(user.getId());
		return new Resource<>(projectplan,
				linkTo(methodOn(StudentController.class).one1()).withSelfRel());
	}
	@GetMapping(value = "/student/initialReport", produces = "application/json; charset=UTF-8")
	Resource<InitialReport> one3() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String name = auth.getName();
		User user = repository.findFirstByEmailAdress(name);
		InitialReport initialReport = initialReportRepository.findFirstByuserId(user.getId());
		return new Resource<>(initialReport,
				linkTo(methodOn(StudentController.class).one3()).withSelfRel());
	}
	
	@GetMapping(value = "/student/finalReport", produces = "application/json; charset=UTF-8")
	Resource<FinalReport> one4() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String name = auth.getName();
		User user = repository.findFirstByEmailAdress(name);
		FinalReport finalReport = finalReportRepository.findFirstByuserId(user.getId());
		return new Resource<>(finalReport,
				linkTo(methodOn(StudentController.class).one4()).withSelfRel());
	}
	@GetMapping(value = "/student/projectDescription", produces = "application/json; charset=UTF-8")
	Resource<ProjectDescription> one5() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String name = auth.getName();
		User user = repository.findFirstByEmailAdress(name);
		ProjectDescription projectDescription = projectDescriptionRepository.findFirstByuserId(user.getId());
		return new Resource<>(projectDescription,
				linkTo(methodOn(StudentController.class).one5()).withSelfRel());
	}
	
	@GetMapping(value = "/student/feedback/{id}", produces = "application/json; charset=UTF-8")
	Resource<Feedback> one2(@PathVariable String id) {
		Feedback feedback = feedbackRepository.findFirstById(id);
		return new Resource<>(feedback,
				linkTo(methodOn(StudentController.class).one2(id)).withSelfRel(),
				linkTo(methodOn(StudentController.class).all2(id)).withRel("feedback"));
	}
	@GetMapping(value = "/student/feedback", produces = "application/json; charset=UTF-8")
	Resources<Resource<Feedback>> all2(@RequestParam String documentId) {
		List<Resource<Feedback>> feedbacks = feedbackRepository.findBydocumentId(documentId).stream()
			    .map(feedback -> new Resource<>(feedback,
			    		linkTo(methodOn(StudentController.class).one2(feedback.getId())).withSelfRel(),
			    		linkTo(methodOn(StudentController.class).all2(documentId)).withRel("feedback")))
			    	    .collect(Collectors.toList());				
		return new Resources<>(feedbacks,
				linkTo(methodOn(StudentController.class).all2(documentId)).withSelfRel());
	}
	@GetMapping(value = "/student/studentInfo", produces = "application/json; charset=UTF-8")
	Resource<Student> one6() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String name = auth.getName();
		User user = repository.findFirstByEmailAdress(name);
		Student student = studentRepository.findFirstByuserId(user.getId());
		return new Resource<>(student,
				linkTo(methodOn(StudentController.class).one1()).withSelfRel());
	}

}
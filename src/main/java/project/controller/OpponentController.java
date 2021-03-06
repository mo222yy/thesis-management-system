package project.controller;




import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONObject;
import org.springframework.hateoas.Resource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import project.model.entities.Feedback;
import project.model.entities.InitialReport;
import project.model.entities.Opponent;
import project.model.entities.Reader;
import project.model.entities.Submission;
import project.model.entities.User;
import project.model.enums.Role;
import project.model.repositories.FeedbackRepository;
import project.model.repositories.InitialReportRepository;
import project.model.repositories.OpponentRepository;
import project.model.repositories.SubmissionRepository;
import project.model.repositories.UserRepository;

@RestController
public class OpponentController {
	private final UserRepository repository;
	private final FeedbackRepository feedbackRepository;
	private final InitialReportRepository initialReportRepository;
	private final OpponentRepository opponentRepository;
	private final SubmissionRepository submissionRepository;
	
	
	OpponentController(UserRepository repository, FeedbackRepository feedbackRepository, InitialReportRepository initialReportRepository, OpponentRepository opponentRepository,
			SubmissionRepository submissionRepository) {
		this.feedbackRepository = feedbackRepository;
		this.initialReportRepository = initialReportRepository;
		this.repository = repository;
		this.opponentRepository = opponentRepository;
		this.submissionRepository = submissionRepository;
	}
	
	@PostMapping("/opponent/feedback")
	Feedback newFeedback(@RequestParam String text) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String name = auth.getName();
		User user = repository.findFirstByEmailAdress(name);
		Opponent opponent = opponentRepository.findFirstByuserId(user.getId());
		
		Date date = new Date();
		
		Feedback feedback = new Feedback(user.getId(),opponent.getInitialReportId(), text, Role.OPPONENT, date);
		InitialReport report = initialReportRepository.findFirstById(feedback.getDocumentId());
		Boolean doesFeedBackExist = false;
		for(int i=0; i < report.getFeedBackIds().size(); i++) {
			Feedback oldFeedback = feedbackRepository.findFirstById(report.getFeedBackIds().get(i));
			if(oldFeedback.getRole().equals(Role.OPPONENT)) {
				doesFeedBackExist = true;
			}
		}
		if(doesFeedBackExist.equals(false)) {
			feedbackRepository.save(feedback);
			report.getFeedBackIds().add(feedback.getId());
			initialReportRepository.save(report);
		}
		return feedback;
	}
	@GetMapping(value = "/opponent/opponentInfo", produces = "application/json; charset=UTF-8")
	Resource<Opponent> one6() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String name = auth.getName();
		User user = repository.findFirstByEmailAdress(name);
		Opponent opponent = opponentRepository.findFirstByuserId(user.getId());
		return new Resource<>(opponent,
				linkTo(methodOn(OpponentController.class).one6()).withSelfRel());
	}
	
	@GetMapping(value = "/opponent/initailReportSubmission", produces = "application/json; charset=UTF-8")
	Resource<Submission> getReaderInfo() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String name = auth.getName();
		User user = repository.findFirstByEmailAdress(name);
		Opponent opponent = opponentRepository.findFirstByuserId(user.getId());
		
		InitialReport initialReport = initialReportRepository.findFirstById(opponent.getInitialReportId());
		Submission submission = submissionRepository.findFirstById(initialReport.getSubmissionId());
		return new Resource<>(submission,
				linkTo(methodOn(ReaderController.class).one6()).withSelfRel());
	}
}

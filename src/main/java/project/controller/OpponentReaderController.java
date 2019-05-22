package project.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import project.model.entities.Feedback;
import project.model.repositories.FeedbackRepository;

@RestController
public class OpponentReaderController {
	private final FeedbackRepository feedbackRepository;
	
	OpponentReaderController(FeedbackRepository feedbackRepository) {
		this.feedbackRepository = feedbackRepository;
	}
	
	@PostMapping("/feedback")
	Feedback newFeedback(@RequestBody Feedback feedback) {
		return feedbackRepository.save(feedback);
		
	}
}

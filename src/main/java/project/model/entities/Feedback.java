package project.model.entities;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import project.model.enums.Role;
@Data
@Document(collection = "feedback")
public class Feedback {
	@Id
	private String id;
	private String userId;
	private String documentId;
	private String text;
	private Role role;
	
	
	public Feedback(String userId, String documentId , String text, Role role) {
		this.userId = userId;
		this.documentId = documentId;
		this.text = text;
		this.role = role;
		
	}
}

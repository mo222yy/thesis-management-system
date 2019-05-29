package project.model.entities;

import java.util.ArrayList;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "supervisors")
public class Supervisor {
	@Id
	private String id;
	private String userId;
	private Boolean availableForSupervisor;
	private ArrayList<String> assignedStudents;
	private ArrayList<String> awaitingResponse;
	
	
	public Supervisor(String userId, Boolean availableForSupervisor, ArrayList<String> assignedStudents,ArrayList<String> awaitingResponse) {
		this.userId = userId;
		this.availableForSupervisor = availableForSupervisor;
		this.assignedStudents = assignedStudents;
		this.awaitingResponse = awaitingResponse;
		
	}

	public Boolean isAvailable()
	{
		return this.availableForSupervisor;
	}

	public void assignStudent(String userId)
	{
		if(awaitingResponse.remove(userId))
		{
			assignedStudents.add(userId);
		}
	}

	public void denyStudent(String userId)
	{
		awaitingResponse.remove(userId);
	}

	public Boolean isAssignedStudent(String userId)
	{
		return assignedStudents.contains(userId);

	}
}
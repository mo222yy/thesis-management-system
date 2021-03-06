package project.model.entities;

import java.util.ArrayList;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Document(collection = "supervisors")
public class Supervisor {
	@NotNull
	@Id
	private String id;
	@NotNull
	private String userId;
	@NotNull
	private Boolean availableForSupervisor;
	@NotNull
	private ArrayList<String> assignedStudents;
	@NotNull
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

package project.model.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import project.model.enums.Grade;

import javax.validation.constraints.NotNull;

@Data
@Document(collection = "projectPlans")
public class ProjectPlan {
	@NotNull
	@Id
	private String id;
	@NotNull
	private String userId;
	@NotNull
	private String submissionId;
	@NotNull
	private String feedBackId;
	@NotNull
	private Grade grade;
	@NotNull
	private String deadLine;
	@NotNull
	private Boolean approved;
	
	public ProjectPlan(String userId, String submissionId, String feedBackId, Grade grade, String deadLine, Boolean approved) {
		this.userId = userId;
		this.submissionId = submissionId;
		this.feedBackId = feedBackId;
		this.grade = grade;
		this.deadLine = deadLine;
		this.approved = approved;
	}

	public String getUserId()
	{
		return userId;
	}
	public void setApproved(Boolean state)
	{
		this.approved = state;
	}
}

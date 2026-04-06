package com.edu.university.modules.curriculum.dto.request;

import lombok.Data;
import java.math.BigDecimal;
import java.util.UUID;

@Data
public class TrainingProgramCourseRequestDTO {
    private UUID trainingProgramId;
    private UUID courseId;
    private String courseCode;
    private String courseName;
    private String semesterId;
    private String semesterCode;
    private String academicYear;
    private Boolean isRequired;
    private String groupCode;
    private BigDecimal credits;
    private UUID prerequisiteCourseId;
    private Boolean isPrerequisiteRequired;
    private String note;
    private Integer sortOrder;
    private String status;

    // Manual Setters
    public void setTrainingProgramId(UUID trainingProgramId) { this.trainingProgramId = trainingProgramId; }
    public void setCourseId(UUID courseId) { this.courseId = courseId; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    public void setSemesterId(String semesterId) { this.semesterId = semesterId; }
    public void setSemesterCode(String semesterCode) { this.semesterCode = semesterCode; }
    public void setAcademicYear(String academicYear) { this.academicYear = academicYear; }
    public void setIsRequired(Boolean isRequired) { this.isRequired = isRequired; }
    public void setGroupCode(String groupCode) { this.groupCode = groupCode; }
    public void setCredits(BigDecimal credits) { this.credits = credits; }
    public void setPrerequisiteCourseId(UUID prerequisiteCourseId) { this.prerequisiteCourseId = prerequisiteCourseId; }
    public void setIsPrerequisiteRequired(Boolean isPrerequisiteRequired) { this.isPrerequisiteRequired = isPrerequisiteRequired; }
    public void setNote(String note) { this.note = note; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    public void setStatus(String status) { this.status = status; }

    // Manual Getters
    public UUID getTrainingProgramId() { return trainingProgramId; }
    public UUID getCourseId() { return courseId; }
    public String getCourseCode() { return courseCode; }
    public String getCourseName() { return courseName; }
    public String getSemesterId() { return semesterId; }
    public String getSemesterCode() { return semesterCode; }
    public String getAcademicYear() { return academicYear; }
    public Boolean getIsRequired() { return isRequired; }
    public String getGroupCode() { return groupCode; }
    public BigDecimal getCredits() { return credits; }
    public UUID getPrerequisiteCourseId() { return prerequisiteCourseId; }
    public Boolean getIsPrerequisiteRequired() { return isPrerequisiteRequired; }
    public String getNote() { return note; }
    public Integer getSortOrder() { return sortOrder; }
    public String getStatus() { return status; }
}

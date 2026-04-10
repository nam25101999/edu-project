$tests = @(
    "AcademicYearControllerIT",
    "StudentClassControllerIT",
    "AssignmentControllerIT",
    "AttendanceControllerIT",
    "MaterialControllerIT",
    "SubmissionControllerIT",
    "GradeScaleControllerIT",
    "ExamTypeControllerIT",
    "ExamControllerIT",
    "EquivalentCourseControllerIT",
    "GraduationConditionControllerIT",
    "SurveyControllerIT"
)
 
foreach ($test in $tests) {
    Write-Host "Running $test..."
    mvn test -Dtest=$test
    if ($LASTEXITCODE -ne 0) {
        Write-Error "$test FAILED"
    } else {
        Write-Host "$test PASSED" -ForegroundColor Green
    }
}

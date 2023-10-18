package com.wy.ges;

// region imported libs
import com.fasterxml.jackson.databind.JsonNode;
import java.util.ArrayList;
// endregion

/**
 * This is the object class for current login session. It will store data from SQL database so that a lot of unnecessary
 * queries to the database can be avoided. This will minimize the overheads and improve the speed of the application.
 * This object class basically acts as the session data one would normally see in web page development.
 */
public class SessionData {
    private static SessionData instance = new SessionData(); // create a new instance of session data

    /**
     * This method is used to get the current session data
     * @return instance which is the current session data
     */
    public static SessionData getInstance() {
        return instance;
    }

    /**
     * This method is used to renew a session data. It is called when the user logs out
     */
    public static void newInstance() {
        instance = new SessionData();
    }

    // region private attributes
    private UserData userData; // application user login data

    private ArrayList<ClassroomData> classrooms; // list of classrooms

    private ArrayList<StudentData> students; // list of students

    private ArrayList<AssessmentData> assessments; // list of assessments

    private ArrayList<AssessmentResult> studentResults; //  list of students results

    // all Classroom student reports
    private ArrayList<StudentReport> currentClassroomReports;
    private ArrayList<StudentData> watchStudents;

    // endregion

    /**
     * This method is used to get the list of student results stored in current session data
     * @return list of student results
     */
    public ArrayList<AssessmentResult> getStudentResults() {
        return studentResults;
    }

    /**
     * This method is used to get the student's stats required for the report.
     * @param sid - id of a student given by the database
     * @return student's stats required for the report or null
     */
    public StudentReport getStudentReportById(int sid) {
        if (currentClassroomReports != null) {
            for (StudentReport report:
                 currentClassroomReports) {
                if (report.getStudentId() == sid) {
                    return report;
                }
            }
        }

        return null;
    }

    /**
     * This method is used to get the watchlist students stored in the session data
     * @return watchlist students
     */
    public ArrayList<StudentData> getWatchStudents() {
        return watchStudents;
    }

    /**
     * This method is used to add student results to the list of student results based on the data in the JsonNode
     * @param studentResults - JsonNode object mapped from JSON string
     */
    public void setStudentResults(JsonNode studentResults) {
        this.studentResults = new ArrayList<>(); // create a new empty list of student results
        for (int i = 0; i < studentResults.size(); i++) {
            // for every student results in the JsonNode object, add it to the list
            this.studentResults.add(new AssessmentResult(
                    studentResults.get(i).get("sid").asInt(),
                    "",
                    studentResults.get(i).get("aid").asInt(),
                    studentResults.get(i).get("cid").asInt(),
                    (float) studentResults.get(i).get("score").asDouble(),
                    (float) studentResults.get(i).get("percentage").asDouble()
            ));
        }
    }

    /**
     * This method is used to add assessments to the list of assessments based on the data in the JsonNode
     * @param assessments - JsonNode object mapped from JSON string
     */
    public void setAssessments(JsonNode assessments) {
        this.assessments = new ArrayList<>(); // create a new empty list of assessments
        for (int i = 0; i < assessments.size(); i++) {
            // for every assessment in the JsonNode object, add it to the list
            this.assessments.add(new AssessmentData(
                    assessments.get(i).get("id").asInt(),
                    assessments.get(i).get("name").asText(),
                    assessments.get(i).get("topic").asText(),
                    assessments.get(i).get("category").asText(),
                    assessments.get(i).get("maxScore").asInt(),
                    (float) assessments.get(i).get("highest").asDouble(),
                    (float) assessments.get(i).get("median").asDouble(),
                    (float) assessments.get(i).get("mean").asDouble(),
                    (float) assessments.get(i).get("lowest").asDouble(),
                    assessments.get(i).get("axScore").asInt(),
                    assessments.get(i).get("aScore").asInt(),
                    assessments.get(i).get("bScore").asInt(),
                    assessments.get(i).get("cScore").asInt(),
                    assessments.get(i).get("dScore").asInt(),
                    assessments.get(i).get("eScore").asInt(),
                    assessments.get(i).get("uScore").asInt()
            ));

        }
    }

    /**
     * This method is used to clear the elements in the students list
     */
    public void clearStudents() {
        students = null;
    }

    /**
     * This method is used to add students to the list of students based on the data in the JsonNode
     * @param students - JsonNode object mapped from JSON string
     */
    public void setStudents(JsonNode students) {
        this.students = new ArrayList<>(); // create a new empty list of students
        for (int i = 0; i < students.size(); i++) {
            // for each student in the JsonNode, add it to the list of students
            this.students.add(new StudentData(
                    students.get(i).get("id").asInt(),
                    students.get(i).get("name").asText(),
                    students.get(i).get("targetGrade").asText(),
                    students.get(i).get("aspirationalGrade").asText(),
                    students.get(i).get("currentGrade").asText(),
                    (float) students.get(i).get("median").asDouble(),
                    (float) students.get(i).get("mean").asDouble(),
                    (float) students.get(i).get("recentMean").asDouble(),
                    (float) students.get(i).get("lastScore").asDouble(),
                    students.get(i).get("tid").asInt(),
                    students.get(i).get("cid").asInt(),
                    students.get(i).get("badCount").asInt(),
                    students.get(i).get("goodCount").asInt()
            ));
        }
    }

    /**
     * This method is used to add watchlist students to the watchlist based on the data in the JsonNode
     * @param watchStudents - JsonNode object mapped from JSON string
     */
    public void setWatchStudents(JsonNode watchStudents) {
        this.watchStudents = new ArrayList<>(); // create a new empty watchlist
        for (int i = 0; i < watchStudents.size(); i++) {
            // for each watchlist student in the JsonNode, add it to the watchlist
            this.watchStudents.add(new StudentData(
                    watchStudents.get(i).get("name").asText(),
                    watchStudents.get(i).get("badCount").asInt(),
                    watchStudents.get(i).get("goodCount").asInt(),
                    watchStudents.get(i).get("className").asText()
            ));
        }
    }

    /**
     * This method is used to get the list of assessments stored in current session
     * @return list of assessments
     */
    public ArrayList<AssessmentData> getAssessments() {
        return assessments;
    }

    /**
     * This method is used to get the list of students stored in current session
     * @return list of students
     */
    public ArrayList<StudentData> getStudents() {
        return students;
    }

    /**
     * This method is used to get a student by their id
     * @param id - student id given by the database
     * @return student with that id
     */
    public StudentData getStudentById(int id) {
        if (students != null) {
            for (StudentData student:
                    students) {

                if (student.getId() == id) {
                    return student;
                }

            }
        }

        return null;

    }

    /**
     * This method is used to get the list of classrooms stored in current session
     * @return list of classrooms
     */
    public ArrayList<ClassroomData> getClassrooms() {
        return classrooms;
    }

    /**
     * This method is used to clear the list of classrooms
     */
    public void clearClassrooms() {
        classrooms = null;
    }

    /**
     * This method is used to add classrooms to the list of classrooms based on the data in the JsonNode
     * @param classrooms - JsonNode object mapped from JSON string
     */
    public void setClassrooms(JsonNode classrooms) {
        this.classrooms = new ArrayList<>(); // create a new empty list of classrooms
        for (int i = 0; i < classrooms.size(); i++) {
            // for each classroom in the JsonNode, add it to the list of classrooms
            this.classrooms.add(new ClassroomData(
                    classrooms.get(i).get("id").asInt(),
                    classrooms.get(i).get("name").asText(),
                    classrooms.get(i).get("note").asText(),
                    (float) classrooms.get(i).get("lastScore").asDouble(),
                    (float) classrooms.get(i).get("mean").asDouble(),
                    (float) classrooms.get(i).get("median").asDouble(),
                    (float) classrooms.get(i).get("recentMean").asDouble(),
                    classrooms.get(i).get("stotal").asInt(),
                    classrooms.get(i).get("tid").asInt()));


        }
    }

    /**
     * This method is used to get the user login data stored in current session
     * @return user login data
     */
    public UserData getUserData() {
        return userData;
    }

    /**
     * This method is used to save the user login data in the current session
     * @param userData - user login data
     */
    public void setUserData(UserData userData) {
        this.userData = userData;
    }

    /**
     * This method is used to get the current classroom reports from current session
     * @return current classroom reports
     */
    public ArrayList<StudentReport> getCurrentClassroomReports() {
        return currentClassroomReports;
    }

    /**
     * This method is used to save the current classroom reports in the current session
     * @param reports - the list of reports for the classroom
     */
    public void setCurrentClassroomReports(ArrayList<StudentReport> reports) {
        currentClassroomReports = reports;
    }
}

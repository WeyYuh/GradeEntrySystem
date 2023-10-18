<?php
// default JSON string
$json = '{
    "username": "",
    "password": "",
    "status": "invalid",
    "msg": ""
}';

$data = json_decode($json); // decode JSON to PHP object

/**
 * This method is used to save the error message in the object so that it can be sent back to Java using JSON
 * 
 * @param $d - PHP object with all the data
 * @param $str - error message string
 */
function errorReport($d, $str) {
    $d->msg = $str;
    $d->status = "invalid";
}

/**
 * This method is used to get the list of students in a classroom
 * 
 * @param $c - SQL connection
 * @param $d - PHP object containing all the data
 */
function getStudents($c, $d) {
    $d->students = array();

    // select all students in the classroom created by the user
    $sql = "SELECT * FROM students where tid = " . $d->id . " AND cid = " . $d->cid . " ORDER BY name ASC";
    $results = query($c, $sql, $d); // run the query

    while ($student = $results->fetch_assoc()) {
        array_push($d->students, $student); // for each student add it to the list
    }

}

if (strcasecmp($_SERVER['REQUEST_METHOD'], 'POST') == 0) {
    // Takes raw data from the request
    $json = file_get_contents('php://input');
    include_once "../inc/dbConnect.php";

    // Converts it into a PHP object
    $inputData = json_decode($json);
    $data = $inputData;

    // prepare a statement for username authentication
    $stmt = $conn->prepare("SELECT * FROM logindetails WHERE username = ?");

    // bind username to the statement to prevent SQL injection
    $stmt->bind_param('s', $data->username);

    if ($stmt->execute()) {
        $stmt->store_result(); // store the result in an internal buffer

        if ($stmt->num_rows() == 1) { // check if username exists

            // binds id, username, password, email to the prepared statement for result storage
            $stmt->bind_result($id, $username, $password, $email); 
            if ($stmt->fetch()) {

                if ($data->password == $password) {
                    // user inputted password is same as the one in database

                    $data->id = $id;
                    $data->status = "valid";
                    

                    $cmd = $data->cmd; // key term

                    // student details
                    $studentName = $data->studentName;
                    $targetGrade = $data->targetGrade;
                    $aspirationalGrade = $data->aspirationalGrade;
                    $currentGrade = $data->currentGrade;

                    switch ($cmd) {
                        case "getWatch":
                            // get watchlist students

                            $data->watchStudents = array();

                            // select all students with goodCount or badCount greater than or equal to 3
                            $sqlWatch = "SELECT students.name, students.badCount, students.goodCount, class.name as 
                            className FROM students INNER JOIN class ON students.cid = class.id 
                            where students.tid = " . $data->id . " AND (students.badCount >= 3 OR students.goodCount >= 3)
                            ORDER BY class.name ASC";

                            $resultsWatch = $conn->query($sqlWatch); // run the query

                            while ($watchStudent = $resultsWatch->fetch_assoc()) {
                                array_push($data->watchStudents, $watchStudent); // for each student add it to the list
                            }
                            break;
                        case "getList":
                            // get list of students
                            getStudents($conn, $data);
                            break;
                        case "add":
                            // add a student

                            // insert into students table with inputs
                            $sqlInsert = "INSERT INTO students (name, targetGrade, aspirationalGrade, currentGrade, tid, cid) 
                            VALUES ('" 
                                . str_replace("'", "''", $studentName) . "', '"
                                . str_replace("'", "''", $targetGrade) . "', '"
                                . str_replace("'", "''", $aspirationalGrade) . "', '"
                                . str_replace("'", "''", $currentGrade) . "', "
                                . $data->id . ", "
                                . $data->cid . ")";

                            $conn->query($sqlInsert); // run the query

                            // get list of students
                            getStudents($conn, $data);
                            break;
                        case "edit":
                            // edit an existing student

                            // update students with new information
                            $sqlUpdate = "UPDATE students SET 
                                name = '" . str_replace("'", "''", $studentName) . "',
                                targetGrade = '" . str_replace("'", "''", $targetGrade) . "',
                                aspirationalGrade = '" . str_replace("'", "''", $aspirationalGrade) . "',
                                currentGrade = '" . str_replace("'", "''", $currentGrade) . 
                                "' WHERE students.id = " . $data->studentId;
                            
                            $conn->query($sqlUpdate); // run the query
                            $data->update = $sqlUpdate;

                            // get list of students
                            getStudents($conn, $data);

                            break;
                        case "delete":
                            // delete a student

                            // delete the student in students table
                            $sqlDelete = "DELETE FROM students WHERE students.id = " . $data->studentId;
                            $conn->query($sqlDelete); // run query

                            // delete the student's score
                            $sqlDeleteResults = "DELETE FROM assessmentresults WHERE assessmentresults.sid = " . $data->studentId;
                            $conn->query($sqlDeleteResults); // run query

                            // get the list of students
                            getStudents($conn, $data);

                            break;

                        case "update":
                            // update student's scores

                            // update class stats
                            $sqlUpdate = "UPDATE class SET 
                            mean = " . $data->mean . ",
                            median = " . $data->median . ",
                            recentMean = " . $data->recentMean . ",
                            lastScore = " . $data->lastScore . "
                            WHERE class.id = " . $data->cid;

                            $conn->query($sqlUpdate); // run the query

                            foreach ($data->reports as $r) {

                                // for each report update student with the information
                                $sqlUpdateStudentRes = "UPDATE students SET
                                mean = " . $r->mean . ",
                                median = " . $r->median . ",
                                recentMean = " . $r->recentMean . ",
                                lastScore = " . $r->lastPercentage . ",
                                badCount = " . $r->badCount . ",
                                goodCount = " . $r->goodCount . ",
                                totalAssessment = " . $r->totalAssessment . "
                                WHERE students.id = " . $r->studentId;

                                $conn->query($sqlUpdateStudentRes); // run query
                                $data->msg = $sqlUpdateStudentRes;
                                
                            }

                            // get the list of students
                            getStudents($conn, $data);
                            
                            break;
                        default: 
                            // unknown command, report error
                            errorReport($data, "Unknown command");
                            break;

                    }

                } else {
                    // invalid password
                    errorReport($data, "Invalid username or password");
                }
            }
        } else {
            // invalid username
            errorReport($data, "Invalid username or password");
        }
    }

    close($conn); // close SQL connection

}



header("Content-Type: application/json");
echo json_encode($data); // return PHP object as JSON string
exit();


?>
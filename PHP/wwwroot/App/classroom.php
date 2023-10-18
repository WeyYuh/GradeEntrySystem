<?php
// default JSON string
$json = '{
    "username": "",
    "password": "",
    "status": "invalid",
    "msg": ""
}';

$data = json_decode($json); // decode JSON string into PHP object

/**
 * This method is used to save the error message in the object so that it can be sent back to Java using JSON
 * 
 * @param $d - PHP object with all the data
 * @param $str - error message string
 */
function errorReport($d, $str) {
    $d->msg = $str; // the error message
    $d->status = "invalid"; // set status to invalid
}

/**
 * This method is used to get the list of classrooms under the user
 * 
 * @param $c - SQL connection
 * @param $d - PHP object with all the data
 */
function getClassrooms($c, $d) {
    // update the number of students in the classroom using count
    $sqlUpdateCount = "UPDATE class SET stotal = (SELECT COUNT(id) FROM students WHERE students.cid = class.id)";
    $c->query($sqlUpdateCount); // run the query

    $d->classrooms = array(); // create an array for the classrooms

    // select all classrooms with the same user id
    $sql = "SELECT * FROM class WHERE tid = " . $d->id . " ORDER BY name ASC";
    $results = query($c, $sql, $d); // run the query

    while ($classroom = $results->fetch_assoc()) {
        array_push($d->classrooms, $classroom); // for every classroom add it to the array
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
                    $data->status = "valid"; // set the status to valid
                    

                    $cmd = $data->cmd; // term for request
                    $classroomName = $data->classroomName;
                    $classroomNote = $data->classroomNote;

                    switch ($cmd) {
                        case "getList":
                            // get the list of classrooms
                            getClassrooms($conn, $data);
                            break;
                        case "add":
                            // add a new classroom
                            $sqlInsert = "INSERT INTO class (name, note, tid) VALUES ('" 
                                . str_replace("'", "''", $classroomName) . "', '"
                                . str_replace("'", "''", $classroomNote) . "', "
                                . $data->id . ")";
                            $conn->query($sqlInsert); // run the query

                            getClassrooms($conn, $data); // get the list of classroom
                            break;
                        case "edit":
                            // edit an existing classroom
                            $sqlUpdate = "UPDATE class SET name = '"
                                 . str_replace("'", "''", $classroomName) . "', note = '"
                                 . str_replace("'", "''", $classroomNote) . "' WHERE class.id = " . $data->classroomId;
                            
                            $conn->query($sqlUpdate); // run the query
                            getClassrooms($conn, $data); // get the list of classrooms

                            break;
                        case "delete":
                            // delete classroom

                            // check if there are students in the classroom
                            $sqlClassroomStudents = "SELECT * FROM students WHERE students.cid = " . $data->classroomId;
                            
                            // run the query
                            $results = $conn->query($sqlClassroomStudents);

                            if ($results->num_rows > 0) {
                                // class is not empty, report error message
                                errorReport($data, "Class has to be emptied before deleting");
                                
                            } else {
                                // check if there are assessments for the classroom
                                $sqlClassroomAssessments = "SELECT * FROM assessments WHERE assessments.cid = ".$data->classroomId;
                                
                                $resultsAssessments = $conn->query($sqlClassroomAssessments); // run the query
                                
                                if ($resultsAssessments->num_rows > 0) {
                                    // there are assessments, report error message
                                    errorReport($data, "Please delete assessments before deleting this classroom");
                                } else {
                                    // classroom is empty, no assessments, delete classroom
                                    $sqlDelete = "DELETE FROM class WHERE class.id = " . $data->classroomId;
                                    $conn->query($sqlDelete); // run the query
                                }

                            }

                            // get the list of classrooms
                            getClassrooms($conn, $data);

                            break;
                        default: 
                            // unknown key term
                            errorReport($data, "Unknown command");
                            break;

                    }

                } else {

                    // incorrect password
                    errorReport($data, "Invalid username or password");
                }
            }
        } else {
            // incorrect username
            errorReport($data, "Invalid username or password");
        }
    }

    close($conn); // close the connection

}



header("Content-Type: application/json");
echo json_encode($data); // return PHP object as JSON string
exit();
?>
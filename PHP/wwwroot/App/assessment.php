<?php


// default JSON string
$json = '{
    "username": "",
    "password": "",
    "status": "invalid",
    "msg": ""
}';

$data = json_decode($json); // decode JSON into PHP object

/**
 * This method is used to save the error message in the object so that it can be sent back to Java using JSON
 * 
 * @param $d - PHP object with all the data
 * @param $str - error message string
 */
function errorReport($d, $str) {
    $d->msg = $str; // the error message
    $d->status = "invalid"; // set object's status to invalid
}

/**
 * This method is used to get all the classroom's assessments created by the user
 * 
 * @param $c - SQL connection
 * @param $d - PHP object with all the data
 */
function getAssessments($c, $d) {
    $d->assessments = array(); // create an array of assessments

    // only show assessments with matching teacher id and class id
    $sql = "SELECT * FROM assessments where tid = " . $d->id . " AND cid = " . $d->cid . " ORDER BY id ASC";
    $results = query($c, $sql, $d);

    while ($assessment = $results->fetch_assoc()) {
        array_push($d->assessments, $assessment); // add assessment to the array
    }

}

/**
 * This method is used to get all the classroom's assessments and results
 * 
 * @param $c - SQL connection
 * @param $d - PHP object with all the data
 */
function getClassroomResults($c, $d) {
    $d->assessments = array(); // create an array of assessments

    // only show assessments with matching class id
    $sqlAssessments = "SELECT * FROM assessments where cid = " . $d->cid . " ORDER BY id ASC";
    $assessments = query($c, $sqlAssessments, $d);
    $d->firstQuery = $sqlAssessments;

    while ($assessment = $assessments->fetch_assoc()) {
        array_push($d->assessments, $assessment); // add assessment to assessment array
    }
    
    $d->results = array(); // create an array of results
    $sqlResults = "SELECT * FROM assessmentresults WHERE cid = " . $d->cid;
    $results = query($c, $sqlResults, $d);
    $d->secondQuery = $sqlResults;

    while ($result = $results->fetch_assoc()) {
        array_push($d->results, $result); // add result to results array
    }
    
}

/**
 * This method is used to update results for the assessment
 * 
 * @param $c - SQL connection
 * @param $d - PHP object with all the data
 * @param $aid - id of the assessment
 */
function updateAssessmentResults($c, $d, $aid) {
    foreach($d->results as $result) {
        if ($aid > 0) {
            $result->aid = $aid; // set result's aid to the aid parsed in
        }

        // if duplicate update, else insert new result
        $sqlInsertUpdate = "INSERT INTO assessmentresults (sid, aid, cid, score, percentage)
        VALUES
        (" . $result->sid . ", ". $result->aid .", ". $d->cid .", ". $result->score .", ". $result->percentage .")
        ON DUPLICATE KEY UPDATE
        score = ". $result->score .",
        percentage = ". $result->percentage;

        $c->query($sqlInsertUpdate); // run the query

        $d->msg = $d->results; 
    }
}

/**
 * This method is used to get a list of the results for the assessment
 * 
 * @param $c - SQL connection
 * @param $d - PHP object containing all the data
 * @param $aid - id of the assessment
 */
function getResults($c, $d, $aid) {
    // select all results with matching assessment id
    $sql = "SELECT * FROM assessmentresults WHERE assessmentresults.aid = " . $aid;

    $queryResult = $c->query($sql); // run the query
    $d->results = array(); // create empty array for the object

    while ($result = $queryResult->fetch_assoc()) {
        array_push($d->results, $result); // for each result add it to the list
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
                    
                    // set the variables to the attributes in the PHP object

                    $cmd = $data->cmd; // term for processes
                    
                    // assessment information
                    $assessmentName = $data->assessmentName;
                    $topic = $data->topic;
                    $category = $data->category;
                    $maxScore = $data->maxScore;

                    // statistics
                    $highest = $data->highest;
                    $median = $data->median;
                    $mean = $data->mean;
                    $lowest = $data->lowest;

                    // score boundaries
                    $axScore = $data->axScore;
                    $aScore = $data->aScore;
                    $bScore = $data->bScore;
                    $cScore = $data->cScore;
                    $dScore = $data->dScore;
                    $eScore = $data->eScore;
                    $uScore = $data->uScore;

                    switch ($cmd) {
                        case "getList":
                            // return list of assessments
                            getAssessments($conn, $data);
                            break;
                        case "add":
                            // add a new assessment to the list
                            $sqlInsert = "INSERT INTO assessments 
                            (name, topic, category, maxScore, highest, median, mean, lowest, 
                            axScore, aScore, bScore, cScore, dScore, eScore, uScore, cid, tid) 
                            VALUES ('" 
                                . str_replace("'", "''", $assessmentName) . "', '"
                                . str_replace("'", "''", $topic) . "', '"
                                . str_replace("'", "''", $category) . "', "
                                . $maxScore . ", "
                                . $highest . ", "
                                . $median . ", "
                                . $mean . ", "
                                . $lowest . ", "
                                . $axScore . ", "
                                . $aScore . ", "
                                . $bScore . ", "
                                . $cScore . ", "
                                . $dScore . ", "
                                . $eScore . ", "
                                . $uScore . ", "
                                . $data->cid . ", "
                                . $data->id . ")";

                            $data->sqlQuery = $sqlInsert;
                            $conn->query($sqlInsert); // run the query
                        
                            $data->assessmentId = $conn->insert_id; // set the assessment id to the new id

                            updateAssessmentResults($conn, $data, $data->assessmentId); // update results for that assessment

                            getAssessments($conn, $data); // get the list of assessments
                            break;
                        case "edit":
                            // update existing assessment
                            $sqlUpdate = "UPDATE assessments SET 
                            name = '" . str_replace("'", "''", $assessmentName) . "',
                            topic = '" . str_replace("'", "''", $topic) . "',
                            category = '" . str_replace("'", "''", $category) . "',
                            maxScore = " . $maxScore . ",
                            highest = " . $highest . ",
                            median = " . $median . ",
                            mean = " . $mean . ",
                            lowest = " . $lowest . ",
                            axScore = " . $axScore . ",
                            aScore = " . $aScore . ",
                            bScore = " . $bScore . ",
                            cScore = " . $cScore . ",
                            dScore = " . $dScore . ",
                            eScore = " . $eScore . ",
                            uScore = " . $uScore . "
                             WHERE assessments.id = " . $data->assessmentId;
                            
                            $conn->query($sqlUpdate); // run the query
                            $data->sql = $sqlUpdate;

                            updateAssessmentResults($conn, $data, 0); // update existing assessment
                            getAssessments($conn, $data); // get the list of assessments

                            break;
                        case "delete":
                            // delete existing assessment
                            $sqlDelete = "DELETE FROM assessments WHERE assessments.id = " . $data->assessmentId;
                            $conn->query($sqlDelete); // run the query

                            // delete all results for that assessment
                            $sqlDeleteResults = "DELETE FROM assessmentresults WHERE assessmentresults.aid = " . $data->assessmentId;
                            $conn->query($sqlDeleteResults); // run the query
                            
                            getAssessments($conn, $data); // get the list of assessments

                            break;
                        case "getResults":
                            // get the results for the assessment
                            getResults($conn, $data, $data->assessmentId);
                            break;
                        case "getClassroomResult":
                            // get the classrooms results
                            getClassroomResults($conn, $data);

                            break;
                        
                        default: 
                            // command word unrecognised, report error
                            errorReport($data, "Unknown command");
                            break;

                    }

                } else {
                    // password incorrect
                    errorReport($data, "Invalid username or password");
                }
            }
        } else {
            // username doesn't exist
            errorReport($data, "Invalid username or password");
        }
    }

    close($conn); // close the SQL connection

}



header("Content-Type: application/json");
echo json_encode($data); // return PHP object as JSON string
exit();


?>
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
    $d->msg = $str;
    $d->status = "invalid";
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
        $stmt->store_result();  // store the result in an internal buffer

        if ($stmt->num_rows() == 1) { // check if username exists

            // binds id, username, password, email to the prepared statement for result storage
            $stmt->bind_result($id, $username, $password, $email);

            if ($stmt->fetch()) {
                if ($data->password == $password) {
                    // user inputted password is same as the one in database

                    $data->id = $id;
                    $data->email = $email;
                    $data->status = "valid";
                } else {
                    errorReport($data, "Invalid username or password");
                }
            }
        } else {
            errorReport($data, "Invalid username or password");
        }
    }

    

    close($conn); // close connection

}



header("Content-Type: application/json");
echo json_encode($data); // return PHP object as JSON string
exit();
?>
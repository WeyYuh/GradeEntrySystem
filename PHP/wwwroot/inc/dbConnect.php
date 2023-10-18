<?php
$servername = "110.4.46.195"; // SQL server IP
$dbUsrname = "wy123"; // SQL login username
$dbPassword = "wy123"; // SQL login password
$dbname = "wytest"; // SQL database name

$conn = new mysqli($servername, $dbUsrname, $dbPassword, $dbname); // create a new SQL connection

/**
 * This method is used to run SQL queries. It will check if the connection is established before running
 * the query
 * 
 * @param $conn - SQL connection
 * @param $sql - sql query to run
 * @param $data - PHP object containing all the data
 */
function query($conn, $sql, $data) {
    if (!$conn) {
        $data->msg = mysqli_connect_error();
        
    } else {
        $results = $conn->query($sql);
        return $results;
        
    }
}

/**
 * This method is used to the close the SQL connection
 * 
 * @param $conn - SQL connection
 */
function close($conn) {
    $conn->close;
}
?>
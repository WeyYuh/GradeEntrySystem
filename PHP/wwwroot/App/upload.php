<?php
// get the user's id
if (is_null( $_GET['id'])) {
    echo ("No id");
    exit;
} else {
    $id = $_GET['id'];
}

include_once "../inc/dbConnect.php";

// select logindetails with matching username and password
$sql = "SELECT * FROM logindetails WHERE username = '" . str_replace("'", "''", $_GET['u']) . "' AND password = '" . str_replace("'", "''", $_GET['p']) . "'";
$result = $conn->query($sql); // run the query

if ($result->num_rows == 1) {
    // username and password is correct

    $filename="./../img/staff/" . $id . ".jpg"; // name the file as [id].jpg
    $fileData=file_get_contents('php://input');
    $fhandle=fopen($filename, 'wb');

    // upload image to server
    fwrite($fhandle, $fileData);
    fclose($fhandle);
    echo("Done uploading");

    $conn->close(); // close SQL connection
    exit;
}
                    

                


$conn->close(); // close connection

echo "Fail"; // fail uploading
?>

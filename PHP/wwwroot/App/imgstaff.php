<?php

if (is_null( $_GET['id'])) $id=0;
else
    $id = $_GET['id'];

if (is_null($id)) $id=0;

// open the file in a binary mode
$name = './../img/staff/'.$id.'.jpg';

if (!file_exists($name))$name = './../img/staff/0.jpg';
$fp = fopen($name, 'rb');

// send the right headers
header("Content-Type: image/jpg");
header("Content-Length: " . filesize($name));

// dump the picture and stop the script
fpassthru($fp);
exit;

?>
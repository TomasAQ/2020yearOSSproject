<?php
include("./include/dbconn.php");
header("Content-Type: text/html; charset=UTF-8");
$email = $_POST["email"];
$pw = $_POST["pw"];


$sql = "SELECT * FROM userstable WHERE email LIKE '$email' AND pw LIKE '$pw' ";
$result = mysqli_query($conn,$sql);

$response = array();
$response["success"] = false;
if($row = mysqli_fetch_array($result)){
    $email = $row["email"];
    $nickname = $row["nickname"];
    $phonenum = $row["phonenum"];
    $response["success"] = true;
    $response["email"] = $email;
    $response["nickname"] = $nickname;
    $response["phonenum"] = $phonenum;
}

echo json_encode($response);


?>
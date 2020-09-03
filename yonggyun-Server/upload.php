<?php
header("Content-Type: text/html; charset=UTF-8");
$target_dir = "uploads/";
$target_file_name = $target_dir .basename($_FILES["file"]["name"]);
$response = array();


if (isset($_FILES["file"])) 
{
    if (move_uploaded_file($_FILES["file"]["tmp_name"], $target_file_name)){
        $success = true;
        $message = "성공";
    }else{
        $success = false;
        $message = "Error ";
    }
}
else 
{
 $success = false;
 $message = "File X";
}

$response["success"] = $success;
$response["message"] = $message;
echo json_encode($response);

?>
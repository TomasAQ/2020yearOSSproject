<?php
include("./include/dbconn.php");
header("Content-Type: text/html; charset=UTF-8");

if($_SERVER['REQUEST_METHOD'] == 'POST'){

    $result = array();
    $result['data'] = array();
    $sql = "SELECT idx, imageurl , name FROM petinformationtable";
    $responce = mysqli_query($conn,$sql);
    
    while($row = mysqli_fetch_array($responce)){
        $index['idx'] = $row[0];
        $index['imageurl'] = $row[1];
        $index['name'] = $row[2];
        
        array_push($result['data'] , $index);
    }
    $result["success"]="success";
    echo json_encode($result);
    mysqli_close($conn);
}

?>

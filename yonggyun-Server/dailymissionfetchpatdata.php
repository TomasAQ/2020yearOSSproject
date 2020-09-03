<?php
include("./include/dbconn.php");
header("Content-Type: text/html; charset=UTF-8");

if($_SERVER['REQUEST_METHOD'] == 'POST'){
    $result = array();
    $result['data'] = array();
    $sql = "SELECT missionimg , description , title , completcount , participantcount FROM dailymissiontable";
    $responce = mysqli_query($conn,$sql);
    while($row = mysqli_fetch_array($responce)){
        $index['missionimg'] = $row[0];
        $index['description'] = $row[1];
        $index['title'] = $row[2];
        $index['completcount'] = $row[3];
        $index['participantcount'] = $row[4];
        array_push($result['data'] , $index);
    }
    $result["success"]="success";
    echo json_encode($result);
    mysqli_close($conn);
}

?>

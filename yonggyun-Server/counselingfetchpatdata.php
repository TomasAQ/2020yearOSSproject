<?php
include("./include/dbconn.php");
header("Content-Type: text/html; charset=UTF-8");

if($_SERVER['REQUEST_METHOD'] == 'POST'){

    $result = array();
    $result['data'] = array();
    $sql = "SELECT cs.idx , pt.name , pt.imageurl , cs.petidx , cs.counselingclassification , cs.counselingcontent , cs.counselingimg FROM counselingdetails AS cs JOIN petinformationtable AS pt ON pt.idx = cs.petidx";

    $responce = mysqli_query($conn,$sql);
    
    while($row = mysqli_fetch_array($responce)){
        $index['cs.idx'] = $row[0];
        $index['pt.name'] = $row[1];
        $index['pt.imageurl'] = $row[2];
        $index['cs.petidx'] = $row[3];
        $index['cs.counselingclassification'] = $row[4];
        $index['cs.counselingcontent'] = $row[5];
        $index['cs.counselingimg'] = $row[6];
        array_push($result['data'] , $index);
    }
    $result["success"]="success";
    echo json_encode($result);
    mysqli_close($conn);
}

?>

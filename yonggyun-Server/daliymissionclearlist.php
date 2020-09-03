<?php

include("./include/dbconn.php");
header("Content-Type: text/html; charset=UTF-8");

$useremail = $_POST['useremail'];
$missiontitle = $_POST['missiontitle'];
$response =array();
$response['data'] = array();

        $sql = "SELECT nickname , completcount , completdate FROM userprogressmissiontable AS mis JOIN userstable AS us ON mis.useridx = us.idx WHERE missionidx=(SELECT idx FROM dailymissiontable WHERE title LIKE '$missiontitle' ) AND completdate = curdate() ";
        $result = mysqli_query($conn, $sql);

        if(mysqli_num_rows($result) > 0 ){
            while($row = mysqli_fetch_assoc($result)){
                $index['nickname'] = $row["nickname"];
                $index['completcount'] = $row["completcount"];
                $index['completdate'] = $row["completdate"];
                array_push($response['data'] , $index);
            }
        }else{
            echo "데이터 없음";
        }

    echo json_encode($response);
    mysqli_close($conn);
?>

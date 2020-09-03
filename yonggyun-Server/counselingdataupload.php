<?php
include("./include/dbconn.php");
header("Content-Type: text/html; charset=UTF-8");

$target_dir ='uploads/images';
$server_ip=gethostbyname(gethostname());
$upload_url='http://'.$server_ip.'/'.$target_dir;

        $image = $_POST['image'];
        $imageStore = rand()."_".time().".png";
        $target_dir =$target_dir."/".$imageStore;
        file_put_contents($target_dir,base64_decode($image));
        
        $petidx =$_POST['petidx'];
        $classification =$_POST['counselingclassification'];
        $content =$_POST['counselingcontent'];
        
        $statement = mysqli_prepare($conn, "INSERT INTO counselingdetails(petidx,counselingclassification,counselingcontent,counselingimg,date) VALUES (?,?,?,?,now())");
        mysqli_stmt_bind_param($statement, "isss", $petidx, $classification,$content,$imageStore);
        mysqli_stmt_execute($statement);
        

        $sql ="SELECT idx FROM counselingdetails WHERE counselingimg ='$imageStore' ";
        $result = mysqli_query($conn,$sql);

        if($row = mysqli_fetch_array($result)){
                $idx = $row["idx"];
        }

        echo $idx;
        mysqli_close($conn);
?>
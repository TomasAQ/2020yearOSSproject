<?php
include("./include/dbconn.php");
header("Content-Type: text/html; charset=UTF-8");

$target_dir ='uploads/petimages';
$server_ip=gethostbyname(gethostname());
$upload_url='http://'.$server_ip.'/'.$target_dir;

        $image = $_POST['image'];
        $imageStore = rand()."_".time().".png";
        $target_dir =$target_dir."/".$imageStore;
        file_put_contents($target_dir,base64_decode($image));

        $name =$_POST['name'];
        $age =$_POST['age'];
        $gender =$_POST['gender'];
        $species =$_POST['petspecies'];
        //예방접종
        $vaccination =$_POST['vaccination'];
        //중성화여부
        $neutralization =$_POST['neutralization'];


        $statement = mysqli_prepare($conn, "INSERT INTO petinformationtable(imageurl,name,age,gender,species,vaccination,neutralization) VALUES (?,?,?,?,?,?,?)");
        mysqli_stmt_bind_param($statement, "sssssss", $imageStore, $name, $age, $gender,$species,$vaccination,$neutralization);
        mysqli_stmt_execute($statement);
        
        echo "반려동물 정보 저장";
        mysqli_close($conn);
    
?>
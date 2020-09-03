<?php
include("./include/dbconn.php");

$upload_path='uploads/petimages';
$server_ip=gethostbyname(gethostname());
$upload_url='http://'.$server_ip.'/'.$upload_path;
$response =array();

if($_SERVER['REQUEST_METHOD'] == 'POST'){
    if(isset($_POST['name']) and isset($_POST['age']) and isset($_FILES['image']['name']) ){
        $name =$_POST['name'];
        $age =$_POST['age'];
        $fileinfo =pathinfo($_FILES['image']['name']);
        $extension = $fileinfo['extension'];
        $file_url=$upload_url.'IMG_'.$name.'.'.$extension;
        $file_path=$upload_path.'IMG_'.$name.'.'.$extension;
        
        try {
            move_uploaded_file($_FILES['image']['tmp_name'],$file_path);
            $sql ="INSERT INTO petinformationtable(name,age,imageurl) VALUES ('$name','$age','$file_url')";
            if(mysqli_query($conn,$sql)){
                $response['error']=false;
                $response['name']=$name;
                $response['age']=$age;
            }

        } catch (Exception $e) {
            $response['error']=true;
            $response['message']=$e->getMessage();
        }
        echo json_encode($response);
        mysqli_close($conn);
    }
}


?>
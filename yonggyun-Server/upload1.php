<?php
$target_dir = "upload/images";
$image = $_POST["image"];

if(file_exists($target_dir)){
    // upload/images 파일 만들기 
    mkdir($target_dir,0777,true);
}

$target_dir = $target_dir."/".rand()."_".time().".jpeg";
if(file_put_contents($target_dir,base64_decode($image))){

    echo json_encode([
        "Message"=>"fileUPload",
        "Status"=>"SUCCESS"
    ]);
}else{
    echo json_encode([
        "Message"=>"fileUPLoadFAIL",
        "Status"=>"ERROR"
    ]);
}

?>
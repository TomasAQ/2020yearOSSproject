<?php
include("./include/dbconn.php");
header("Content-Type: text/html; charset=UTF-8");

$requsetCode = $_POST["requsetCode"];

if($requsetCode == "1" ){
    //이메일 중복 검사
    $email = $_POST["email"];

     $statement = mysqli_prepare($conn, "SELECT email FROM userstable WHERE email LIKE ?");
     mysqli_stmt_bind_param($statement, "s", $email);
     mysqli_stmt_execute($statement);
     mysqli_stmt_store_result($statement);
     mysqli_stmt_bind_result($statement, $email);

     $response = array();
     $response["success"] = true;
     $response["email"] = $email;
     while(mysqli_stmt_fetch($statement)){
         $response["success"]=false;
         $response["email"]=$email;
     }
    
     
}else if($requsetCode == "2"){
    // 인증번호 전송
    include("./sendemail.php");

}else if($requsetCode =="3"){
    // 회원 가입
    $email = $_POST["email"];
    $pw = $_POST["pw"];
    $nickname = $_POST["nickname"];
    $phonenum = $_POST["phonenum"];

    $statement = mysqli_prepare($conn, "INSERT INTO userstable (email,pw,nickname,phonenum) VALUES (?,?,?,?)"); 
    mysqli_stmt_bind_param($statement, "ssss", $email, $pw, $nickname, $phonenum);
    mysqli_stmt_execute($statement);

    $response = array();
    $response["success"] = true;
    $response["email"] = $email;
    $response["pw"] = $pw;
    $response["nickname"] = $nickname;
    $response["phonenum"] = $phonenum;

}  
    echo json_encode($response);


?>
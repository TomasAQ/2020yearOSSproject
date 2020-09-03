<?php
include("./include/dbconn.php");
header("Content-Type: text/html; charset=UTF-8");

        $useremail = $_POST['useremail'];
        $missiontitle = $_POST['missiontitle'];
        $response =array();
        
        // 1. 유저 이메일을 통해서 유저 idx 값 가져오기
        $sql = "SELECT idx FROM userstable WHERE email LIKE '$useremail' ";
        $result = mysqli_query($conn, $sql);

        if(mysqli_num_rows($result) > 0 ){
            while($row = mysqli_fetch_assoc($result)){
                
                $useridx = $row["idx"];
                // 2. 미션 제목을 통해서 미션 idx 값 가져오기
                 $sql2 = "SELECT idx FROM dailymissiontable WHERE title LIKE '$missiontitle' ";
                 $result2 = mysqli_query($conn, $sql2);
                 if(mysqli_num_rows($result2) > 0 ){
                    while($row2 = mysqli_fetch_assoc($result2)){
                        $missionidx = $row2["idx"];
                        // echo "useridx".$useridx." , missionidx : ".$missionidx;
                        
                        // 3. 유저 idx 와 미션 idx 로 된 미션테이블 컬럼 생성여부 확인
                        $sql3 = "SELECT * FROM userprogressmissiontable WHERE useridx=$useridx AND missionidx =$missionidx ";
                        $result3 = mysqli_query($conn, $sql3);
                        if(mysqli_num_rows($result3) > 0 ){
                            // 컬럼 존재시에 update
                            $sqlupdate = "UPDATE userprogressmissiontable SET completcount = completcount+1 , completdate = curdate() WHERE useridx= $useridx AND missionidx=$missionidx AND completdate != curdate()";
                            mysqli_query($conn, $sqlupdate);
                        }else{
                            // 컬럼 존재하지 않기 때문에 insert 문으로 생성
                            $sqlinsert = "INSERT INTO userprogressmissiontable(useridx,missionidx,completcount,completdate)VALUES($useridx,$missionidx,1,curdate()) ";
                            mysqli_query($conn, $sqlinsert);
                        }
                        
                        $sql4 = "SELECT * FROM userprogressmissiontable WHERE useridx=$useridx AND missionidx =$missionidx ";
                        $result4 = mysqli_query($conn, $sql4);
                        if(mysqli_num_rows($result4) > 0 ){
                            while($row4 = mysqli_fetch_assoc($result4)){
                                $response['success']="true";
                                $response['idx']= $row4["idx"];
                                $response['completcount']= $row4["completcount"];
                                $response['completdate']= $row4["completdate"];
                            }
                        }

                    }
                 }else{
                    echo "두번째 쿼리 에러!!!";         
                 }

             }
        }else{
            echo "첫번째 쿼리 에러!!!";
        }

        echo json_encode($response);
        mysqli_close($conn);



?>
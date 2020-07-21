<!-- 
파일업로드 테스트 페이지 
AJAX을 활용한 화면 전환되지 않는 효과.
drag & drop으로 단일 파일을 업로드한다.
-->
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" 
  "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>

<style>
/* 화면 상에서 점선으로 표시됨. */
.fileDrop {
	width: 100%;
	height: 200px;
	border: 1px dotted blue;
}

small {
	margin-left: 3px;
	font-weight: bold;
	color: gray;
}
</style>
</head>
<body>

	<h3>Ajax File Upload</h3>
	<!-- 파일을 drop할 영역 -->
	<div class='fileDrop'></div>

	<div class='uploadedList'></div>

	<script src="//code.jquery.com/jquery-1.11.3.min.js"></script>
	<script>
		
		$(".fileDrop").on("dragenter dragover", function(event) {
			//이벤트를 막는다.
			//막지 않으면 새 창이 열린다.
			event.preventDefault();
		});
		
		//실제 drop 시 동작 정의.
		$(".fileDrop").on("drop", function(event){
			//이벤트를 막는다.
			//막지 않으면 새 창이 열린다.
			event.preventDefault();
			//event.originalEvent는 jQuery를 이용하는 경우 event가 순수한 DOM 이벤트가 아니기 때문에 event.originalEvent를 이용해서
			//순수한 원래의 DOM 이벤트를 가져온다.
			//event.dataTransfer는 이벤트와 같이 전달된 데이터를 의미하고, 그 안에 포함된 파일 데이터를 찾아내기 위해 dataTransfer.files를 이용한다.
			var files = event.originalEvent.dataTransfer.files;
			//file의 정보를 담고 있다. 브라우저에서 업로드되는 파일의 사이즈에 대한 제한이나 파일의 종류에 대한 제한을 할 수 있다.
			var file = files[0];

			//console.log(file);
			//FormData(HTML5에서 지원됨. 브라우저 제약이 있을 수 있다.IE10버전 이상에서만 사용가능.)를 이용하면 AJAX에서 파일 데이터를 전송할 수 있다.
			var formData = new FormData();
			
			formData.append("file", file);
			
			$.ajax({
				  url: '/uploadAjax',
				  data: formData,
				  dataType:'text',
				  processData: false,//필수
				  contentType: false,//필수
				  type: 'POST',
				  
				  //업로드한 파일의 썸네일 경로가 리턴된다.
				  success: function(data){
					  // data는 /2020/07/21/s_aa6bd1e8-7f04-43c5-9eca-096ccf0ab3f3_994BEF355CD0313D05.png.
					  var str ="";
					  //이미지 파일인지 검사한다.
					  //확장자를 따로 추출해서 검사할 수 있게 해야함.
					  if(checkImageType(data)){
						  str ="<div><a href=displayFile?fileName="+getImageLink(data)+">"
								  +"<img src='displayFile?fileName="+data+"'/>"
								  +"</a><small data-src="+data+">X</small></div>";
					  }else{
						  str = "<div><a href='displayFile?fileName="+data+"'>" 
								  + getOriginalName(data)+"</a>"
								  +"<small data-src="+data+">X</small></div></div>";
					  }
					  
					  $(".uploadedList").append(str);
				  }
				});	
		});

		// X 클릭 시
		$(".uploadedList").on("click", "small", function(event){
			
				 var that = $(this);
			
			   $.ajax({
				   url:"deleteFile",
				   type:"post",
				   //파일경로는 /2020/07/18/s_f0ac0864-0add-474c-9c85-ea7c47e1d1b7_994BEF355CD0313D05.png
				   data: {fileName:$(this).attr("data-src")}, 
				   dataType:"text",
				   success:function(result){
					   if(result == 'deleted'){
						   //화면 상에서 썸네일, 일반 파일이름 삭제
						   that.parent("div").remove();
					   }
				   }
			   });
		});
		
		
/* 		
$(".fileDrop").on("drop", function(event) {
	event.preventDefault();
	
	var files = event.originalEvent.dataTransfer.files;
	
	var file = files[0];

	//console.log(file);
	var formData = new FormData();
	
	formData.append("file", file);

	
	$.ajax({
		  url: '/uploadAjax',
		  data: formData,
		  dataType:'text',
		  processData: false,
		  contentType: false,
		  type: 'POST',
		  success: function(data){
			  
			  var str ="";
			  
			  console.log(data);
			  console.log(checkImageType(data));
			  
			  if(checkImageType(data)){
				  str ="<div><a href='displayFile?fileName="+getImageLink(data)+"'>"
						  +"<img src='displayFile?fileName="+data+"'/></a>"
						  +data +"</div>";
			  }else{
				  str = "<div><a href='displayFile?fileName="+data+"'>" 
						  + getOriginalName(data)+"</a></div>";
			  }
			  
			  $(".uploadedList").append(str);
		  }
		});			
});	 */

//브라우저에 출력되는 파일이름을 줄여준다.
//원래 파일이름만 추출하여 반환한다.
function getOriginalName(fileName){	
	//이미지 파일이라면
	if(checkImageType(fileName)){
		return;
	}
	
	var idx = fileName.indexOf("_") + 1 ;
	return fileName.substr(idx);
	
}

//업로드된 이미지 링크 클릭 시 썸네일이 아닌 원본 이미지를 띄우기 위해 파일 경로앞에 's_'를 제외한 경로를 추출한 뒤 반환한다.
function getImageLink(fileName){
	//이미지 파일이 아니라면 return;
	if(!checkImageType(fileName)){
		return;
	}
	// /년/월/일
	var front = fileName.substr(0,12);
	//'s_'제거한 파일이름.
	var end = fileName.substr(14);
	
	return front + end;
	
}




/* 		$(".fileDrop").on("drop", function(event) {
			event.preventDefault();
			
			var files = event.originalEvent.dataTransfer.files;
			
			var file = files[0];

			//console.log(file);
			var formData = new FormData();
			
			formData.append("file", file);
			
			$.ajax({
				  url: '/uploadAjax',
				  data: formData,
				  dataType:'text',
				  processData: false,
				  contentType: false,
				  type: 'POST',
				  success: function(data){
					 	
					  alert(data);
					 
				  }
				});
			
		}); */
		
	//전송받은 문자열(파일경로)가 이미지 파일인지 확인한다.
	function checkImageType(fileName){
		
		//i는 대소문자 구분이 없다.
		var pattern = /jpg|gif|png|jpeg/i;
		
		return fileName.match(pattern);
		
	}
		
		
	</script>

</body>
</html>
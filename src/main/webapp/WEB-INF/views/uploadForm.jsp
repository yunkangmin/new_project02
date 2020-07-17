<!-- 
파일업로드 테스트 페이지 
iframe을 활용한 화면 전환되지 않는 효과.
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
/* 화면상에 보이지 않게 설정 */
iframe {
	width: 0px;
	height: 0px;
	border: 0px
}
</style> 
</head>
<body>

	<!-- 
		파일업로드를 위해 enctype=multipart/form-data를 사용한다. multipart/form-data는 데이터를 여러 조작으로 나누어서 전송한다.
		각 부분(part)마다 경계가 되는 값을 이용해서 많은 양의 데이터를 POST방식으로 전송할 때 사용한다.
		form 태그에 target속성을 주고 iframe 태그을 이용하면 화면전환 효과를 없앨 수 있다.(일반적으로 form태그는 전송을 하고 나면 화면전환이 일어난다.)
		화면전환을 없애는 이유는 현재창에서 업로드된 결과를 바로 바로 확인하기 위함이다.
	-->
	 <form id='form1' action="uploadForm" method="post"
		enctype="multipart/form-data" target="zeroFrame">
		<input type='file' name='file'> <input type='submit'>
	</form>

	<iframe name="zeroFrame"></iframe>

	<script>
		function addFilePath(msg) {
			alert(msg);
			//파일업로드 초기화.
			document.getElementById("form1").reset();
		}
	</script> 


	 <!-- 	<form id='form1' action="uploadForm" method="post"
		enctype="multipart/form-data">
		<input type='file' name='file'> <input type='submit'>
	</form>
 -->
</body>
</html>


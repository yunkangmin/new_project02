<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page session="false"%>

<script>

var result = '${savedName}';
//부모창인 uploadForm 페이지의 addFilePath 함수 호출.
parent.addFilePath(result);

</script>


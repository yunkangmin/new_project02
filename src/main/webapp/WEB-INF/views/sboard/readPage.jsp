<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@include file="../include/header.jsp"%>
<!-- 
handlebars 템플릿을 사용하기 위한 jQuery 라이브러리 추가

handlebars를 사용하는 이유
1. 화면에 반복적으로 처리되는 템플릿 코들르 처리할 수 있다.
2. 서버에서는 댓글의 목록과 댓글의 리스트 데이터를 한 번에 전송할 수 있다.
 -->
<script src="https://cdnjs.cloudflare.com/ajax/libs/handlebars.js/3.0.1/handlebars.js"></script>
<!-- Main content -->
<section class="content">
	<div class="row">
		<!-- left column -->
		<div class="col-md-12">
			<!-- general form elements -->
			<div class="box box-primary">
				<div class="box-header">
					<h3 class="box-title">READ BOARD</h3>
				</div>
				<!-- /.box-header -->

				<form role="form" action="modifyPage" method="post">
					<!-- 기존 board/readPage.jsp에서 searchType, keyword가 추가됬다. -->
					<input type='hidden' name='bno' value="${boardVO.bno}"> <input
						type='hidden' name='page' value="${cri.page}"> <input
						type='hidden' name='perPageNum' value="${cri.perPageNum}">
					<input type='hidden' name='searchType' value="${cri.searchType}">
					<input type='hidden' name='keyword' value="${cri.keyword}">

				</form>

				<div class="box-body">
					<div class="form-group">
						<label for="exampleInputEmail1">Title</label> <input type="text"
							name='title' class="form-control" value="${boardVO.title}"
							readonly="readonly">
					</div>
					<div class="form-group">
						<label for="exampleInputPassword1">Content</label>
						<textarea class="form-control" name="content" rows="3"
							readonly="readonly">${boardVO.content}</textarea>
					</div>
					<div class="form-group">
						<label for="exampleInputEmail1">Writer</label> <input type="text"
							name="writer" class="form-control" value="${boardVO.writer}"
							readonly="readonly">
					</div>
				</div>
				<!-- /.box-body -->

			  <div class="box-footer">
			    <button type="submit" class="btn btn-warning" id="modifyBtn">Modify</button>
			    <button type="submit" class="btn btn-danger" id="removeBtn">REMOVE</button>
			    <button type="submit" class="btn btn-primary" id="goListBtn">GO LIST </button>
			  </div>



			</div>
			<!-- /.box -->
		</div>
		<!--/.col (left) -->

	</div>
	<!-- /.row -->


	<div class="row">
		<div class="col-md-12">

			<!-- 댓글 등록 HTML -->
			<div class="box box-success">
				<div class="box-header">
					<h3 class="box-title">ADD NEW REPLY</h3>
				</div>
				<div class="box-body">
					<label for="exampleInputEmail1">Writer</label> <input
						class="form-control" type="text" placeholder="USER ID"
						id="newReplyWriter"> <label for="exampleInputEmail1">Reply
						Text</label> <input class="form-control" type="text"
						placeholder="REPLY TEXT" id="newReplyText">

				</div>
				<!-- /.box-body -->
				<div class="box-footer">
					<button type="button" class="btn btn-primary" id="replyAddBtn">ADD
						REPLY</button>
				</div>
			</div>
			<!-- //댓글 등록 HTML -->

			<!-- 댓글 목록 보기 -->
			<!-- The time line -->
			<ul class="timeline">
			  <!-- timeline time label -->
			<li class="time-label" id="repliesDiv">
			  <span class="bg-green">
			    Replies List <small id='replycntSmall'> [ ${boardVO.replycnt} ] <%--댓글 개수 --%></small>
			    </span>
			  </li>
			</ul>
			<!-- //댓글 목록 보기 -->
			
			<!-- 댓글 하단 페이징 -->
			<div class='text-center'>
				<ul id="pagination" class="pagination pagination-sm no-margin ">

				</ul>
			</div>
			<!-- //댓글 하단 페이징 -->

		</div>
		<!-- /.col -->
	</div>
	<!-- /.row -->


          
<!-- Modal -->
<div id="modifyModal" class="modal modal-primary fade" role="dialog">
  <div class="modal-dialog">
    <!-- Modal content-->
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal">&times;</button>
        <h4 class="modal-title"></h4>
      </div>
      <div class="modal-body" data-rno>
        <p><input type="text" id="replytext" class="form-control"></p>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-info" id="replyModBtn">Modify</button>
        <button type="button" class="btn btn-danger" id="replyDelBtn">DELETE</button>
        <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
      </div>
    </div>
  </div>
</div>      
	
	
</section>
<!-- /.content -->

<!-- 
handlebars 템플릿 코드 
하단에 Modify 버튼을 누르면 a태그 속성에 data-target="#modifyModal"이 모달창을 호출함.
모달창에 id는 modifyModal임.
-->
<script id="template" type="text/x-handlebars-template">
{{#each .}}
<li class="replyLi" data-rno={{rno}}>
<i class="fa fa-comments bg-blue"></i>
 <div class="timeline-item" >
  <span class="time">
    <i class="fa fa-clock-o"></i>{{prettifyDate regdate}}
  </span>
  <h3 class="timeline-header"><strong>{{rno}}</strong> -{{replyer}}</h3>
  <div class="timeline-body">{{replytext}} </div>
    <div class="timeline-footer">
     <a class="btn btn-primary btn-xs" 
	    data-toggle="modal" data-target="#modifyModal">Modify</a>
    </div>
  </div>			
</li>
{{/each}}
</script>

<script>
	//날짜를 원하는 형식으로 만들어준다.
	//registerHelper로 원하는 기능을 만든다.
	Handlebars.registerHelper("prettifyDate", function(timeValue) {
		var dateObj = new Date(timeValue);
		var year = dateObj.getFullYear();
		var month = dateObj.getMonth() + 1;
		var date = dateObj.getDate();
		return year + "/" + month + "/" + date;
	});
	
	//댓글 출력
	var printData = function(replyArr, target, templateObject) {
        //댓글 템플릿을 가져온다.
		var template = Handlebars.compile(templateObject.html());
		//템플릿에 데이터를 세팅한다.
		var html = template(replyArr);
		//기존 댓글을 지운다.
		$(".replyLi").remove();
		//새로 세팅한다.
		target.after(html);

	}
	
	//게시글 번호
	var bno = ${boardVO.bno};
	
	var replyPage = 1;
	
	//특정 게시물에 대한 댓글 페이지 처리 함수
	//pageInfo -> /replies/bno(게시글 번호)/replyPage(댓글 페이지 번호)
	function getPage(pageInfo) {
		
		$.getJSON(pageInfo, function(data) {
			//댓글 출력 함수
			//댓글목록, 댓글이 출력될 영역, 댓글 템플릿이 파라미터로 넘어간다.
			printData(data.list, $("#repliesDiv"), $('#template'));
			//하단 페이징 출력 함수
			//pageMaker, 페이징이 출력될 영역이 파라미터로 넘어간다.
			printPaging(data.pageMaker, $(".pagination"));
			//모달창을 숨긴다.
			$("#modifyModal").modal('hide');
			//댓글 총 개수
			$("#replycntSmall").html("[ " + data.pageMaker.totalCount +" ]");

		});
	}
	
	//하단 페이징 출력 함수
	var printPaging = function(pageMaker, target) {

		var str = "";

		if (pageMaker.prev) {
			str += "<li><a href='" + (pageMaker.startPage - 1)
					+ "'> << </a></li>";
		}

		for (var i = pageMaker.startPage, len = pageMaker.endPage; i <= len; i++) {
			var strClass = pageMaker.cri.page == i ? 'class=active' : '';
			str += "<li "+strClass+"><a href='"+i+"'>" + i + "</a></li>";
		}

		if (pageMaker.next) {
			str += "<li><a href='" + (pageMaker.endPage + 1)
					+ "'> >> </a></li>";
		}

		target.html(str);
	};
	
	//댓글목록 버튼 클릭시
	$("#repliesDiv").on("click", function() {
		//댓글이 하나라도 보이면 종료
		if ($(".timeline li").size() > 1) {
			return;
		}
		//게시물 번호와 페이지 번호를 요청한다.
		getPage("/replies/" + bno + "/1");

	});
	
	//댓글 페이지 번호 클릭 시
	$(".pagination").on("click", "li a", function(event){
		
		event.preventDefault();
		
		replyPage = $(this).attr("href");
		
		getPage("/replies/"+bno+"/"+replyPage);
		
	});
	
	//댓글 등록시 
	$("#replyAddBtn").on("click",function(){
		 
		 //작성자
		 var replyerObj = $("#newReplyWriter");
		 //내용
		 var replytextObj = $("#newReplyText");
		 
		 var replyer = replyerObj.val();
		 var replytext = replytextObj.val();
		
		  
		  $.ajax({
				type:'post',
				url:'/replies/',
				headers: { 
				      "Content-Type": "application/json",
				      "X-HTTP-Method-Override": "POST" },
				dataType:'text',
				data: JSON.stringify({bno:bno, replyer:replyer, replytext:replytext}),
				success:function(result){
					console.log("result: " + result);
					if(result == 'SUCCESS'){
						alert("등록 되었습니다.");
						replyPage = 1;
						//댓글 목록을 다시 불러온다.
						getPage("/replies/"+bno+"/"+replyPage );
						replyerObj.val("");
						replytextObj.val("");
					}
			}});
	});

	//댓글 영역 클릭 시 댓글 번호와 댓글 내용이 모달창에 세팅됨.
	$(".timeline").on("click", ".replyLi", function(event){
		
		var reply = $(this);
		
		$("#replytext").val(reply.find('.timeline-body').text());
		$(".modal-title").html(reply.attr("data-rno"));
		
	});
	
	
	//모달창에서 수정 버튼 클릭 시
	$("#replyModBtn").on("click",function(){
		  //댓글 번호
		  var rno = $(".modal-title").html();
		  //댓글 내용
		  var replytext = $("#replytext").val();
		  
		  $.ajax({
				type:'put',
				url:'/replies/'+rno,
				headers: { 
				      "Content-Type": "application/json",
				      "X-HTTP-Method-Override": "PUT" },
				data:JSON.stringify({replytext:replytext}), 
				dataType:'text', 
				success:function(result){
					console.log("result: " + result);
					if(result == 'SUCCESS'){
						alert("수정 되었습니다.");
						getPage("/replies/"+bno+"/"+replyPage );
					}
			}});
	});
	
	//모달 창에서 댓글 삭제 클릭 시
	$("#replyDelBtn").on("click",function(){
		  //댓글 번호
		  var rno = $(".modal-title").html();
		  //댓글 내용
		  var replytext = $("#replytext").val();
		  
		  $.ajax({
				type:'delete',
				url:'/replies/'+rno,
				headers: { 
				      "Content-Type": "application/json",
				      "X-HTTP-Method-Override": "DELETE" },
				dataType:'text', 
				success:function(result){
					console.log("result: " + result);
					if(result == 'SUCCESS'){
						alert("삭제 되었습니다.");
						getPage("/replies/"+bno+"/"+replyPage );
					}
			}});
	});
	
</script>


<script>
$(document).ready(function(){
	
	var formObj = $("form[role='form']");
	
	console.log(formObj);
	
	$("#modifyBtn").on("click", function(){
		formObj.attr("action", "/sboard/modifyPage");
		formObj.attr("method", "get");		
		formObj.submit();
	});
	
	$("#removeBtn").on("click", function(){
		formObj.attr("action", "/sboard/removePage");
		formObj.submit();
	});
	
	$("#goListBtn ").on("click", function(){
		formObj.attr("method", "get");
		formObj.attr("action", "/sboard/list");
		formObj.submit();
	});
	
});
</script>






<%@include file="../include/footer.jsp"%>

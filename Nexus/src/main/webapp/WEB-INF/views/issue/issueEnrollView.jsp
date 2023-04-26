<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>

<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<script src="https://code.jquery.com/jquery-3.3.1.slim.min.js" integrity="sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo" crossorigin="anonymous"></script>
<script type="text/javascript" src="js/jquery/jquery-3.1.1.min.js"></script>
<script type="text/javascript" src="js/comboTreePlugin.js"></script>
<script type="text/javascript" src="js/icontains.js"></script>
<style>
</style>




<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-select/1.13.1/css/bootstrap-select.min.css">
<script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-select/1.13.1/js/bootstrap-select.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-select/1.13.1/js/i18n/defaults-*.min.js"></script>

<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/issue_select.css">
<link rel="stylesheet" href="https://uicdn.toast.com/editor/latest/toastui-editor.min.css">
<script src="https://uicdn.toast.com/editor/latest/toastui-editor-all.min.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/issue_mini.js"></script>




</head>

<body>

	<jsp:include page="../common/template.jsp" />






	<div class="container total-box" style="margin-left: 0px; margin-right: 0px;">

		<form action="createIssue.mi" method="post" id="issueEnrollForm">

			<input type="text" class="form-control issuetitle" id="isTitle" name="title" placeholder="Title" autofocus>
			<input type="hidden" name="issueNo" value="">

			<div class="why">
				<div class="editor-wrapper">
					<div id="editor"></div>

					<input type="hidden" name="body" value="">
					<!-- 본인 글일때만 보이게 분기처리 ### -->
					<div class="btn-box">
						<br>
						<button type="submit" class="btn btn-outline-primary" id="btn1">제출하기</button>


					</div>
				</div>
		</form>
	</div>




	<div class="editor-label">






		<div class="mb-3">
			<label for="defaultSelect" class="form-label">이슈 담당자</label>

			<select id="defaultSelect" class="form-select" name="assignees">
				<option>이슈 담당자</option>
				<c:forEach var="r" items="${RepoMembers }">
					<option value="${r.userName }">${r.userName}</option>
				</c:forEach>
			</select>
		</div>


		<div class="mb-3">
			<label for="defaultSelect" class="form-label">라벨</label>
			<select id="defaultSelect" class="form-select" name="labels">
				<option>라벨</option>
				<c:forEach var="l" items="${lList }">
					<option value="${l.name }">${l.name }</option>
				</c:forEach>
			</select>
		</div>






		<div class="mb-3">
			<label for="defaultSelect" class="form-label">프로젝트</label>
			<select id="defaultSelect" class="form-select" name="">
				<option>프로젝트</option>
				<option value="1" type="checkbox">One</option>
				<option value="2" type="checkbox">Two</option>
				<option value="3" type="checkbox">Three</option>
			</select>
		</div>


		<div class="mb-3">
			<label for="defaultSelect" class="form-label">마일스톤</label>
			<select id="defaultSelect" class="form-select" name="milestone">
				<option>마일스톤</option>
				<option value="1" type="checkbox">One</option>
				<option value="2" type="checkbox">Two</option>
				<option value="3" type="checkbox">Three</option>
			</select>
		</div>




		<div id="issueEditArea" class="menulist col-md-3">
			<!-- 	        이슈 담당자 menubox 시작 -->
			<div class="menubox card">
				<div class="menubox-header dropdown">
					<button class="btn dropdown-toggle" type="button" id="issue-assignee-btn" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
						<span>담당자</span> <i class="icon-settings menu-icon"></i>

					</button>
					<div class="assigneeboxes dropdown-menu" aria-labelledby="issue-assignee-btn">
						<ul>
						<!-- 
							<c:forEach var="r" items="${RepoMembers }">
								<li><input type="checkbox" value="${r.userName }">${r.userName}</li>
							</c:forEach>
 -->
						</ul>
					</div>
				</div>

			</div>




		</div>
		<!-- editor-label -->

	</div>

	<script>

            var editor = new toastui.Editor({
                el: document.querySelector('#editor'),
                height: '100%',
                initialEditType: 'markdown'

            });
            var markdownValue = editor.getMarkdown();
            console.log(markdownValue); // Output: "# Hello, World!"


      
		// body 에디터 input 히든 값 넣기
		$("#btn1").click(function(){
    var markdown = editor.getMarkdown();
    $("input[name='body']").val(markdown);
});

            
            
        </script>

</body>

</html>
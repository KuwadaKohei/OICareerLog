<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="ja">

<head>
<jsp:include page="/WEB-INF/views/common/head.jsp" />
<title>OICareerLog | 学科情報登録</title>
</head>

<body class="bg-light">
	<div
		class="container min-vh-100 d-flex flex-column justify-content-center py-5">
		<div class="mx-auto" style="max-width: 520px;">
			<div class="text-center mb-4">
				<h1 class="h4 fw-semibold mb-2">学科・学年を登録</h1>
				<p class="text-muted mb-0">学科と学年を選択して、OICareerLog の利用を開始しましょう。</p>
			</div>
			<div class="card shadow-sm border-0">
				<div class="card-body p-4">
					<c:if test="${not empty errorMessage}">
						<div class="alert alert-danger" role="alert">
							<c:out value="${errorMessage}" />
						</div>
					</c:if>
					<form method="post" action="${contextPath}/NewUser"
						class="d-grid gap-4">
						<input type="hidden" name="action" value="confirm" />
						<div>
							<label for="departmentId" class="form-label">学科</label> <select
								id="departmentId" name="departmentId" class="form-select"
								required>
								<option value="" disabled
									<c:if test="${empty selectedDepartmentId}">selected
                                            </c:if>>学科を選択してください</option>
								<c:forEach var="dept" items="${deptList}">
									<option value="${dept.departmentId}"
										<c:if
                                                test="${dept.departmentId == selectedDepartmentId}">selected</c:if>>
										${dept.departmentName}</option>
								</c:forEach>
							</select>
						</div>
						<div>
							<label for="grade" class="form-label">学年</label> <select
								id="grade" name="grade" class="form-select" required>
								<option value="" disabled
									<c:if test="${empty selectedGrade}">selected</c:if>>学年を選択してください</option>
								<c:forEach var="gradeOption" begin="1" end="3">
									<option value="${gradeOption}"
										<c:if test="${gradeOption == selectedGrade}">
                                                selected</c:if>>${gradeOption}年生</option>
								</c:forEach>
							</select>
						</div>
						<button type="submit" class="btn btn-primary btn-lg">
							確認画面に進む</button>
					</form>
				</div>
			</div>
		</div>
	</div>

	<script
		src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"
		integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz"
		crossorigin="anonymous"></script>
</body>

</html>
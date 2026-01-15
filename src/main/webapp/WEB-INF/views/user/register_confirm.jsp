<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<c:set var="loginUser" value="${sessionScope.loginUser}" />
<!DOCTYPE html>
<html lang="ja">

<head>
<jsp:include page="/WEB-INF/views/common/head.jsp" />
<title>OICareerLog | 学科情報確認</title>
</head>

<body class="bg-light">
	<div
		class="container min-vh-100 d-flex flex-column justify-content-center py-5">
		<div class="mx-auto" style="max-width: 520px;">
			<div class="text-center mb-4">
				<h1 class="h4 fw-semibold mb-2">入力内容の確認</h1>
				<p class="text-muted mb-0">以下の内容で登録します。問題がなければ「登録する」を押してください。</p>
			</div>
			<div class="card shadow-sm border-0">
				<div class="card-body p-4">
					<dl class="row mb-4">
						<dt class="col-4 text-muted">氏名</dt>
						<dd class="col-8 fw-semibold">${loginUser.name}</dd>
						<dt class="col-4 text-muted">メール</dt>
						<dd class="col-8 fw-semibold">${loginUser.email}</dd>
						<dt class="col-4 text-muted">学科</dt>
						<dd class="col-8 fw-semibold">${selectedDepartment.departmentName}</dd>
						<dt class="col-4 text-muted">学年</dt>
						<dd class="col-8 fw-semibold">${selectedGrade}年生</dd>
					</dl>
					<div class="d-flex flex-column flex-md-row gap-3">
						<c:url var="backUrl" value="/NewUser">
							<c:param name="departmentId" value="${selectedDepartmentId}" />
							<c:param name="grade" value="${selectedGrade}" />
						</c:url>
						<a class="btn btn-outline-secondary flex-fill" href="${backUrl}">
							入力画面に戻る </a>
						<form method="post" action="${contextPath}/NewUser"
							class="flex-fill d-grid gap-3">
							<input type="hidden" name="action" value="register" /> <input
								type="hidden" name="departmentId"
								value="${selectedDepartmentId}" /> <input type="hidden"
								name="grade" value="${selectedGrade}" />
							<button type="submit" class="btn btn-primary">登録する</button>
						</form>
					</div>
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
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<c:set var="loginUser" value="${sessionScope.loginUser}" />
<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<c:if test="${not empty loginUser}">
	<c:redirect url="/Home" />
</c:if>

<!DOCTYPE html>
<html lang="ja">

<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>OICareerLog | ログイン</title>
<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
	rel="stylesheet"
	integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH"
	crossorigin="anonymous">
<link
	href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css"
	rel="stylesheet">
</head>

<body class="bg-light">
	<div class="container min-vh-100 d-flex flex-column justify-content-center py-5">
		<div class="mx-auto text-center" style="max-width: 420px;">
			<div class="mb-4">
				<img src="${contextPath}/img/appLogo.png" alt="Recruit Memo Logo"
					class="img-fluid" style="max-height: 160px;">
			</div>
			<div class="card shadow-sm border-0">
				<div class="card-body py-5">
					<h1 class="h4 fw-semibold mb-4">OICareerLog</h1>
					<form method="get" action="${contextPath}/Login"
						class="d-grid gap-3">
						<input type="hidden" name="action" value="requestAuth" />
						<button type="submit" class="btn btn-primary btn-lg">
							ログイン
						</button>
					</form>
				</div>
			</div>
			<p class="text-muted mt-3 small">Google アカウントで認証後、OICareerLogをご利用いただけます。</p>
		</div>
	</div>

	<script
		src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"
		integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz"
		crossorigin="anonymous"></script>
</body>

</html>
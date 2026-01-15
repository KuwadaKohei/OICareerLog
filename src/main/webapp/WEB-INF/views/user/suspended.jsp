<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<c:if test="${empty logoutUrl}">
	<c:set var="logoutUrl" value="${contextPath}/Logout" />
</c:if>
<!DOCTYPE html>
<html lang="ja">

<head>
<jsp:include page="/WEB-INF/views/common/head.jsp" />
<title>OICareerLog | アカウント制限中</title>
</head>

<body class="bg-light">
	<main
		class="min-vh-100 d-flex align-items-center justify-content-center px-3">
		<div class="card shadow-sm border-0 w-100" style="max-width: 520px;">
			<div class="card-body text-center p-4 p-md-5">
				<div class="mb-3">
					<span class="badge text-bg-danger px-3 py-2">ACCESS BLOCKED</span>
				</div>
				<h1 class="h4 fw-bold mb-3">アカウントが制限されています</h1>
				<p class="text-muted mb-4">あなたのアカウントは制限されています。管理者にお問い合わせ下さい。</p>

				<c:if
					test="${not empty restrictedUserName || not empty restrictedUserEmail}">
					<div class="bg-light border rounded p-3 mb-4 text-start">
						<p class="mb-1 fw-semibold">
							<c:out value="${restrictedUserName}" default="ご利用者" />
						</p>
						<c:if test="${not empty restrictedUserEmail}">
							<p class="mb-0 text-muted small">
								<c:out value="${restrictedUserEmail}" />
							</p>
						</c:if>
					</div>
				</c:if>

				<div class="d-grid gap-2">
					<a href="${logoutUrl}" class="btn btn-primary btn-lg">ログアウトする</a> <a
						href="mailto:admin@example.com" class="btn btn-outline-secondary">管理者に連絡する</a>
				</div>
			</div>
		</div>
	</main>

	<script
		src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"
		integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz"
		crossorigin="anonymous">
		
	</script>
</body>

</html>
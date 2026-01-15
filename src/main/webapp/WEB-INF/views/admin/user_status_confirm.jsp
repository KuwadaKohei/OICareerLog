<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<c:set var="user" value="${requestScope.user}" />
<c:set var="targetActive" value="${requestScope.targetActive}" />
<!DOCTYPE html>
<html lang="ja">

<head>
<jsp:include page="/WEB-INF/views/common/head.jsp" />
<title>OICareerLog | ステータス確認</title>
<link rel="stylesheet" href="${contextPath}/css/style.css">
<style>
body {
	background: linear-gradient(135deg, #f8f9fa, #eef2ff);
	min-height: 100vh;
}
</style>
</head>

<body>
	<div class="container py-5">
		<div class="mx-auto" style="max-width: 760px;">
			<div class="text-center mb-4">
				<p class="text-primary fw-semibold mb-1">User Moderation</p>
				<h1 class="h4 fw-bold">
					<c:choose>
						<c:when test="${targetActive}">制限解除の確認</c:when>
						<c:otherwise>制限の確認</c:otherwise>
					</c:choose>
				</h1>
				<p class="text-muted mb-0">以下の内容を確認し、ステータス更新を実行してください。</p>
			</div>

			<c:if test="${user == null}">
				<div class="alert alert-warning">対象ユーザーが見つかりませんでした。検索画面に戻ってやり直してください。</div>
				<div class="text-center">
					<a href="${contextPath}/User/Suspend"
						class="btn btn-outline-secondary">検索に戻る</a>
				</div>
			</c:if>

			<c:if test="${user != null}">
				<div class="card border-0 shadow-sm mb-4">
					<div class="card-body">
						<h2 class="h6 text-muted text-uppercase">ユーザー情報</h2>
						<dl class="row mb-0">
							<dt class="col-sm-4 text-muted">ユーザーID</dt>
							<dd class="col-sm-8">
								#
								<c:out value="${user.userId}" />
							</dd>

							<dt class="col-sm-4 text-muted">氏名</dt>
							<dd class="col-sm-8">
								<c:out value="${user.name}" />
							</dd>

							<dt class="col-sm-4 text-muted">メール</dt>
							<dd class="col-sm-8">
								<c:out value="${user.email}" />
							</dd>

							<dt class="col-sm-4 text-muted">Google ID</dt>
							<dd class="col-sm-8">
								<c:out value="${user.googleAccountId}" />
							</dd>

							<dt class="col-sm-4 text-muted">ユーザー種別</dt>
							<dd class="col-sm-8">
								<c:out value="${user.userType}" />
							</dd>

							<dt class="col-sm-4 text-muted">学科 / 学年</dt>
							<dd class="col-sm-8">
								<c:out value="${user.departmentId}" />
								/
								<c:out value="${user.grade}" />
								年生
							</dd>

							<dt class="col-sm-4 text-muted">管理者</dt>
							<dd class="col-sm-8">
								<c:choose>
									<c:when test="${user.admin}">はい</c:when>
									<c:otherwise>いいえ</c:otherwise>
								</c:choose>
							</dd>
						</dl>
					</div>
				</div>

				<div class="card border-0 shadow-sm mb-4">
					<div class="card-body">
						<h2 class="h6 text-muted text-uppercase">ステータス</h2>
						<div class="row g-3">
							<div class="col-md-6">
								<div class="border rounded-3 p-3 h-100">
									<p class="text-muted small mb-1">現在</p>
									<span
										class="badge ${user.active ? 'bg-success-subtle text-success' : 'bg-danger-subtle text-danger'}">
										${user.active ? 'アクティブ' : '制限中'} </span>
								</div>
							</div>
							<div class="col-md-6">
								<div class="border rounded-3 p-3 h-100">
									<p class="text-muted small mb-1">更新後</p>
									<span
										class="badge ${targetActive ? 'bg-success-subtle text-success' : 'bg-danger-subtle text-danger'}">
										${targetActive ? 'アクティブ' : '制限中'} </span>
								</div>
							</div>
						</div>
					</div>
				</div>

				<form method="post" action="${contextPath}/User/Suspend"
					class="card border-0 shadow-sm">
					<div class="card-body p-4">
						<input type="hidden" name="action" value="updateStatus" /> <input
							type="hidden" name="userId" value="${user.userId}" /> <input
							type="hidden" name="targetActive" value="${targetActive}" />
						<div class="d-flex flex-column flex-md-row gap-3">
							<a href="${contextPath}/User/Suspend"
								class="btn btn-outline-secondary flex-fill">検索に戻る</a>
							<button type="submit"
								class="btn ${targetActive ? 'btn-success' : 'btn-danger'} flex-fill">
								<c:choose>
									<c:when test="${targetActive}">制限を解除する</c:when>
									<c:otherwise>このユーザーを制限する</c:otherwise>
								</c:choose>
							</button>
						</div>
					</div>
				</form>
			</c:if>
		</div>
	</div>

	<script
		src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"
		integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz"
		crossorigin="anonymous"></script>
</body>

</html>
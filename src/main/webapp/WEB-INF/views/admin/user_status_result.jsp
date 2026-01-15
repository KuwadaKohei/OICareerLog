<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<c:set var="user" value="${requestScope.user}" />
<c:set var="targetActive" value="${requestScope.targetActive}" />
<!DOCTYPE html>
<html lang="ja">

<head>
<jsp:include page="/WEB-INF/views/common/head.jsp" />
<title>OICareerLog | ステータス更新完了</title>
<style>
body {
	background: #f8fbff;
	min-height: 100vh;
}
</style>
</head>

<body>
	<div class="container py-5">
		<div class="mx-auto" style="max-width: 760px;">
			<div class="text-center mb-4">
				<div class="display-5 text-success mb-3">
					<i class="bi bi-check-circle-fill"></i>
				</div>
				<h1 class="h4 fw-bold">ステータスの更新が完了しました</h1>
				<p class="text-muted mb-0">最新の状態は以下のとおりです。</p>
			</div>

			<c:if test="${user == null}">
				<div class="alert alert-warning">対象ユーザーが見つかりませんでした。検索画面に戻って再度操作してください。</div>
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
						<h2 class="h6 text-muted text-uppercase">現在のステータス</h2>
						<p class="mb-0">
							<span
								class="badge ${user.active ? 'bg-success-subtle text-success' : 'bg-danger-subtle text-danger'} me-2">
								${user.active ? 'アクティブ' : '制限中'} </span> <span class="text-muted">更新後の状態を反映しています。</span>
						</p>
					</div>
				</div>

				<div class="card border-0 shadow-sm">
					<div class="card-body p-4 text-center">
						<a href="${contextPath}/User/Suspend" class="btn btn-primary px-5">検索に戻る</a>
					</div>
				</div>
			</c:if>
		</div>
	</div>

	<script
		src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"
		integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz"
		crossorigin="anonymous"></script>
</body>

</html>
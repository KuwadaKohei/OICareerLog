<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>
<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="ja">

<head>
<jsp:include page="/WEB-INF/views/common/head.jsp" />
<title>OICareerLog | ユーザー管理</title>
<link rel="stylesheet" href="${contextPath}/css/style.css">
</head>

<body class="bg-gradient-primary">
	<div class="offcanvas offcanvas-start text-bg-dark" tabindex="-1" id="sidebarOffcanvas"
		aria-labelledby="sidebarOffcanvasLabel">
		<span class="visually-hidden" id="sidebarOffcanvasLabel">サイドメニュー</span>
		<div class="offcanvas-body p-0">
			<jsp:include page="/WEB-INF/views/common/sidebar.jsp" />
		</div>
	</div>
	<jsp:include page="/WEB-INF/views/common/header.jsp">
		<jsp:param name="pageTitle" value="ユーザー検索" />
	</jsp:include>

	<div class="container-fluid">
		<div class="row g-0 min-vh-100">
			<div class="col-lg-3 col-xl-2 d-none d-lg-block p-0 bg-dark min-vh-100 border-end">
				<jsp:include page="/WEB-INF/views/common/sidebar.jsp" />
			</div>
			<main class="col-12 col-lg-9 col-xl-10 py-5 px-4 px-xl-5">
				<div class="text-center mb-5">
					<p class="text-primary fw-semibold mb-1">User Management</p>
					<h1 class="h3 fw-bold">ユーザー検索</h1>
					<p class="text-muted mb-0">氏名またはメールアドレスで検索し、ステータスの制限 / 解除へ進みます。</p>
				</div>

				<form method="get" action="${contextPath}/User/Suspend"
					class="search-hero">
					<div class="input-group input-group-lg shadow-sm">
						<input type="text" name="searchWord"
							class="form-control text-center py-3"
							placeholder="例: 田中 / ～@oic-ok.ac.jp"
							value="<c:out value='${searchWord}' />" aria-label="ユーザー検索">
						<button type="submit" class="btn btn-primary px-4">検索</button>
					</div>
				</form>

				<section class="mt-5">
					<c:choose>
						<c:when test="${not empty userList}">
							<div class="d-flex flex-column gap-3">
								<c:forEach var="user" items="${userList}">
									<c:set var="nextActive" value="${not user.active}" />
									<c:set var="ctaLabel"
										value="${user.active ? '制限画面へ進む' : '制限解除画面へ進む'}" />
									<a class="text-decoration-none text-body" role="link"
										href="${contextPath}/User/Suspend?action=confirm&userId=${user.userId}&targetActive=${nextActive}">
										<div class="user-card card border-0 shadow-sm">
											<div class="card-body p-4">
												<div
													class="d-flex flex-column flex-lg-row justify-content-between gap-3">
													<div>
														<p class="fw-semibold mb-1">
															<c:out value="${user.name}" />
															<span class="text-muted small ms-2">ID: <c:out
																	value="${user.userId}" />
															</span>
														</p>
														<p class="mb-1 text-muted">
															<c:out value="${user.email}" />
														</p>
														<p class="mb-1 text-muted small">
															Google ID:
															<c:out value="${user.googleAccountId}" />
														</p>
														<p class="mb-0 text-muted small">
															学科ID:
															<c:out value="${user.departmentId}" />
															/ 学年:
															<c:out value="${user.grade}" />
															/ タイプ:
															<c:out value="${user.userType}" />
														</p>
													</div>
													<div class="text-lg-end">
														<span
															class="badge ${user.active ? 'bg-success-subtle text-success' : 'bg-danger-subtle text-danger'} me-2">
															${user.active ? 'アクティブ' : '制限中'} </span>
														<c:if test="${user.admin}">
															<span class="badge bg-warning text-dark">管理者</span>
														</c:if>
														<div class="mt-3">
															<span class="text-primary fw-semibold">
																${ctaLabel} <i class="bi bi-arrow-right-short"></i>
															</span>
														</div>
													</div>
												</div>
											</div>
										</div>
									</a>
								</c:forEach>
							</div>
						</c:when>
						<c:when test="${not empty searchWord}">
							<div class="alert alert-light border text-center" role="alert">
								指定した条件に一致するユーザーは見つかりませんでした。</div>
						</c:when>
						<c:otherwise>
							<div class="text-center text-muted">
								キーワードを入力すると、ここにユーザーが表示されます。</div>
						</c:otherwise>
					</c:choose>
				</section>
			</main>
		</div>
	</div>

	<script
		src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"
		integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz"
		crossorigin="anonymous"></script>
</body>

</html>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html lang="ja">

<head>
<jsp:include page="/WEB-INF/views/common/head.jsp" />
<title>OICareerLog | 投稿一覧</title>
</head>

<body class="bg-body-tertiary">
	<div class="offcanvas offcanvas-start text-bg-dark" tabindex="-1" id="sidebarOffcanvas"
		aria-labelledby="sidebarOffcanvasLabel">
		<span class="visually-hidden" id="sidebarOffcanvasLabel">サイドメニュー</span>
		<div class="offcanvas-body p-0">
			<jsp:include page="/WEB-INF/views/common/sidebar.jsp" />
		</div>
	</div>
	<jsp:include page="/WEB-INF/views/common/header.jsp">
		<jsp:param name="pageTitle" value="投稿一覧" />
	</jsp:include>

	<div class="container-fluid">
		<div class="row g-0 min-vh-100">
			<div class="col-lg-3 col-xl-2 d-none d-lg-block p-0 bg-dark min-vh-100 border-end">
				<jsp:include page="/WEB-INF/views/common/sidebar.jsp" />
			</div>
			<main class="col-12 col-lg-9 col-xl-10 py-4 px-4">
				<section class="bg-white rounded-3 shadow-sm p-4">
					<div
						class="d-flex flex-wrap gap-3 justify-content-between align-items-center mb-3">
						<div>
							<h1 class="h4 mb-1 fw-bold">投稿一覧</h1>
							<p class="text-muted mb-0">最新の投稿を確認できます。</p>
						</div>
						<a class="btn btn-primary"
							href="${pageContext.request.contextPath}/ReportNew"> <i
							class="bi bi-file-earmark-plus me-1"></i>新規投稿
						</a>
					</div>

					<form method="get"
						action="${pageContext.request.contextPath}/ReportList"
						class="row g-2 mb-4">
						<div class="col-sm-9 col-md-10">
							<input type="text" name="searchWord" value="${param.searchWord}"
								class="form-control" placeholder="企業名やキーワードで検索">
						</div>
						<div class="col-sm-3 col-md-2 d-grid">
							<button type="submit" class="btn btn-outline-secondary">
								<i class="bi bi-search me-1"></i>検索
							</button>
						</div>
					</form>

					<c:choose>
						<c:when test="${postList == null || empty postList.reports}">
							<div class="alert alert-info mb-0">
								表示できる投稿がありません。新規投稿を作成するか、検索条件を変更してください。</div>
						</c:when>
						<c:otherwise>
							<div class="list-group list-group-flush">
								<c:forEach var="report" items="${postList.reports}">
									<a class="list-group-item list-group-item-action py-3"
										href="${pageContext.request.contextPath}/ReportView?postId=${report.postId}">
										<div
											class="d-flex flex-column flex-lg-row justify-content-between gap-3">
											<div class="pe-lg-3">
												<h2 class="h6 mb-1">${report.companyName}</h2>
												<p class="mb-1 text-muted small">
												<c:if test="${report.recruitmentNo > 0}">
													<span>求人票番号: ${report.recruitmentNo}</span>
													<span class="mx-2">|</span>
												</c:if>
													<span>${report.departmentCourseName}</span>
												</p>
											<p class="mb-0 text-muted small">最終更新: ${report.formattedUpdatedAt}</p>
											</div>
											<div class="text-muted small text-lg-end">
												<div class="fw-semibold text-body-secondary">試験日</div>
											<div>${report.formattedExamDate}</div>
											</div>
										</div>
									</a>
								</c:forEach>
							</div>
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
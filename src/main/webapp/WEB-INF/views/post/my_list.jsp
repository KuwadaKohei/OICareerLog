<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
	<%@ taglib prefix="c" uri="jakarta.tags.core" %>
		<!DOCTYPE html>
		<html lang="ja">

		<head>
			<jsp:include page="/WEB-INF/views/common/head.jsp" />
			<title>OICareerLog | 自分の投稿</title>
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
				<jsp:param name="pageTitle" value="自分の投稿" />
			</jsp:include>

			<div class="container-fluid">
				<div class="row g-0 min-vh-100">
					<div class="col-lg-3 col-xl-2 d-none d-lg-block p-0 bg-dark min-vh-100 border-end">
						<jsp:include page="/WEB-INF/views/common/sidebar.jsp" />
					</div>
					<main class="col-12 col-lg-9 col-xl-10 py-4 px-4">
						<section class="bg-white rounded-3 shadow-sm p-4">
							<div class="d-flex flex-wrap gap-3 justify-content-between align-items-center mb-3">
								<div>
								<h1 class="h4 mb-1 fw-bold">自分の投稿</h1>
									<p class="text-muted mb-0">作成した投稿を検索・確認できます。</p>
								</div>
								<a class="btn btn-primary" href="${pageContext.request.contextPath}/ReportNew"> <i
										class="bi bi-file-earmark-plus me-1"></i>新規投稿
								</a>
							</div>

							<form method="get" action="${pageContext.request.contextPath}/MyReports"
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
										表示できる投稿がありません。検索条件を変更するか、新しい投稿を作成してください。</div>
								</c:when>
								<c:otherwise>
									<div class="list-group list-group-flush">
										<c:forEach var="report" items="${postList.reports}">
											<div class="list-group-item py-3">
												<div class="d-flex justify-content-between flex-wrap gap-2">
													<div>
														<h2 class="h6 mb-1">${report.companyName}</h2>
														<p class="mb-1 text-muted small">
														<c:if test="${report.recruitmentNo > 0}">
															求人票番号: ${report.recruitmentNo} <span class="mx-1">|</span>
														</c:if>
															${report.departmentCourseName}
														</p>
													</div>
													<div class="text-muted small">
													試験日: ${report.formattedExamDate}<br /> 最終更新: ${report.formattedUpdatedAt}
													</div>
												</div>
												<div class="d-flex flex-wrap gap-2 mt-3">
													<a class="btn btn-outline-primary btn-sm"
														href="${pageContext.request.contextPath}/ReportView?postId=${report.postId}">
														<i class="bi bi-eye me-1"></i>詳細
													</a> <a class="btn btn-outline-secondary btn-sm"
														href="${pageContext.request.contextPath}/ReportEdit?postId=${report.postId}">
														<i class="bi bi-pencil me-1"></i>編集
													</a>
													<form method="post"
														action="${pageContext.request.contextPath}/DeleteReport"
														class="m-0">
														<input type="hidden" name="postId" value="${report.postId}" />
														<input type="hidden" name="action" value="confirm" />
														<button type="submit" class="btn btn-outline-danger btn-sm">
															<i class="bi bi-trash me-1"></i>削除
														</button>
													</form>
												</div>
											</div>
										</c:forEach>
									</div>
								</c:otherwise>
							</c:choose>
						</section>
					</main>
				</div>
			</div>

			<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"
				integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz"
				crossorigin="anonymous"></script>
		</body>

		</html>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>
<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<c:set var="loginUser" value="${sessionScope.loginUser}" />
<c:set var="post" value="${postDetail}" />
<!DOCTYPE html>
<html lang="ja">

<head>
<jsp:include page="/WEB-INF/views/common/head.jsp" />
<title>OICareerLog | 投稿詳細</title>
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
		<jsp:param name="pageTitle" value="投稿詳細" />
	</jsp:include>

	<div class="container-fluid">
		<div class="row g-0 min-vh-100">
			<div class="col-lg-3 col-xl-2 d-none d-lg-block p-0 bg-dark min-vh-100 border-end">
				<jsp:include page="/WEB-INF/views/common/sidebar.jsp" />
			</div>
			<main class="col-12 col-lg-9 col-xl-10 py-4 px-4 px-xl-5">
				<c:if test="${not empty pageMessage}">
					<div class="alert alert-warning" role="alert">
						<c:out value="${pageMessage}" />
					</div>
				</c:if>

				<c:choose>
					<c:when test="${post == null}">
						<div class="bg-white rounded-3 shadow-sm p-4">
							<p class="mb-4">表示できる投稿が見つかりませんでした。</p>
							<c:choose>
								<c:when test="${param.back == 'myPosts'}">
									<a href="${contextPath}/MyReports"
										class="btn btn-outline-secondary"> 戻る </a>
								</c:when>
								<c:otherwise>
									<button type="button" class="btn btn-outline-secondary"
										onclick="history.back()">戻る</button>
								</c:otherwise>
							</c:choose>
						</div>
					</c:when>
					<c:otherwise>
						<c:set var="canModify"
							value="${not empty loginUser and (loginUser.admin or loginUser.userId == post.posterUserId)}" />
						<section class="bg-white rounded-3 shadow-sm p-4">
							<div
								class="d-flex flex-wrap gap-3 justify-content-between align-items-center mb-4">
								<div>
									<h1 class="h4 mb-1 fw-bold">${post.companyName}</h1>
									<p class="text-muted mb-0">
										受験日:
									<c:out value="${post.formattedExamDate}" />
										/ 応募方法:
										<c:out value="${post.submissionMethodName}" />
									</p>
								</div>
								<div class="d-flex flex-wrap gap-2">
									<c:choose>
										<c:when test="${param.back == 'myPosts'}">
											<a class="btn btn-outline-secondary"
												href="${contextPath}/MyReports"> <i
												class="bi bi-arrow-left"></i> 戻る
											</a>
										</c:when>
										<c:otherwise>
											<button type="button" class="btn btn-outline-secondary"
												onclick="history.back()">
												<i class="bi bi-arrow-left"></i> 戻る
											</button>
										</c:otherwise>
									</c:choose>
									<c:if test="${canModify}">
										<a class="btn btn-outline-primary"
											href="${contextPath}/ReportEdit?postId=${post.postId}"> <i
											class="bi bi-pencil"></i> 編集
										</a>
										<form method="post" action="${contextPath}/DeleteReport"
											class="m-0">
											<input type="hidden" name="postId" value="${post.postId}" />
											<input type="hidden" name="action" value="confirm" />
											<button type="submit" class="btn btn-outline-danger">
												<i class="bi bi-trash"></i> 削除
											</button>
										</form>
									</c:if>
								</div>
							</div>

							<c:if test="${not post.anonymous or loginUser.admin}">
								<div class="border rounded-3 p-3 mb-4">
									<div
										class="d-flex flex-column flex-md-row justify-content-between gap-3">
										<div>
											<p class="text-muted small mb-1">投稿者</p>
											<p class="fs-5 fw-semibold mb-1">
												<c:out value="${post.posterName}" />
												<c:if test="${post.posterTeacher}">
													<span class="badge text-bg-info ms-2">教員</span>
												</c:if>
											</p>
											<p class="mb-0 text-muted">
												<c:out value="${post.departmentName}" />
												/
												<c:out value="${post.grade}" />
												年生
											</p>
										</div>
										<div class="text-md-end text-muted small">
											投稿ID:
											<c:out value="${post.postId}" />
										</div>
									</div>
								</div>
							</c:if>

							<div class="row g-4 mb-4">
								<div class="col-md-6">
									<div class="bg-body-tertiary rounded-3 p-3 h-100">
										<h2 class="h6 text-muted text-uppercase mb-3">基本情報</h2>
										<dl class="row mb-0">
											<dt class="col-4 text-muted">企業名</dt>
											<dd class="col-8 fw-semibold">
												<c:out value="${post.companyName}" />
											</dd>
											<dt class="col-4 text-muted">学科</dt>
											<dd class="col-8">
												<c:out value="${post.departmentName}" />
											</dd>
											<dt class="col-4 text-muted">受験日</dt>
											<dd class="col-8">
											<c:out value="${post.formattedExamDate}" />
											</dd>
											<dt class="col-4 text-muted">求人票</dt>
											<dd class="col-8">
												<c:choose>
													<c:when test="${post.recruitmentNo > 0}">
                                                                    #
                                                                    <c:out
															value="${post.recruitmentNo}" />
													</c:when>
													<c:otherwise>未入力</c:otherwise>
												</c:choose>
											</dd>
											<dt class="col-4 text-muted">会場</dt>
											<dd class="col-8">
												<c:choose>
													<c:when test="${not empty post.venueAddress}">
														<c:out value="${post.venueAddress}" />
													</c:when>
													<c:otherwise>未入力</c:otherwise>
												</c:choose>
											</dd>
										</dl>
									</div>
								</div>
								<div class="col-md-6">
									<div class="bg-body-tertiary rounded-3 p-3 h-100">
										<h2 class="h6 text-muted text-uppercase mb-3">選考情報</h2>
										<dl class="row mb-0">
											<dt class="col-5 text-muted">予定採用人数</dt>
											<dd class="col-7">
												<c:choose>
													<c:when test="${post.scheduledHires > 0}">
														<c:out value="${post.scheduledHires}" /> 人
                                                                </c:when>
													<c:otherwise>未設定</c:otherwise>
												</c:choose>
											</dd>
										</dl>
									</div>
								</div>
							</div>

							<div class="mb-4">
								<div
									class="d-flex justify-content-between align-items-center mb-3">
									<h2 class="h5 mb-0">試験項目</h2>
									<span class="badge text-bg-light">${fn:length(post.selectedExams)}
										件</span>
								</div>
								<c:choose>
									<c:when test="${empty post.selectedExams}">
										<div class="alert alert-light mb-0">登録された試験項目はありません。</div>
									</c:when>
									<c:otherwise>
										<div class="d-flex flex-column gap-3">
											<c:forEach var="exam" items="${post.selectedExams}"
												varStatus="status">
												<div class="border rounded-3 p-3">
													<div
														class="d-flex flex-wrap justify-content-between align-items-center gap-2">
														<div>
															<p class="mb-0 fw-semibold">
																<span class="text-muted small me-2"> <c:out
																		value="${exam.categoryName}" />
																</span>
																<c:out value="${exam.itemName}" />
															</p>
														</div>
														<span class="badge text-bg-secondary">項目
															${status.index + 1}</span>
													</div>
													<c:if test="${not empty exam.detailText}">
														<hr>
														<p class="mb-0" style="white-space: pre-line;">
															<c:out value="${exam.detailText}" />
														</p>
													</c:if>
												</div>
											</c:forEach>
										</div>
									</c:otherwise>
								</c:choose>
							</div>

							<div>
								<h2 class="h5 mb-2">後輩へのアドバイス</h2>
								<div class="bg-body-tertiary rounded-3 p-3"
									style="white-space: pre-line;">
									<c:choose>
										<c:when test="${not empty post.adviceText}">
											<c:out value="${post.adviceText}" />
										</c:when>
										<c:otherwise>アドバイスは登録されていません。</c:otherwise>
									</c:choose>
								</div>
							</div>
						</section>
					</c:otherwise>
				</c:choose>
			</main>
		</div>
	</div>

	<script
		src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"
		integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz"
		crossorigin="anonymous"></script>
</body>

</html>

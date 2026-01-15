<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<c:set var="loginUser" value="${sessionScope.loginUser}" />
<!DOCTYPE html>
<html lang="ja">

<head>
<jsp:include page="/WEB-INF/views/common/head.jsp" />
<title>OICareerLog | 更新内容の確認</title>
<link rel="stylesheet" href="${contextPath}/css/style.css">
<style>
body {
	background: linear-gradient(135deg, #f8f9fa, #eef2ff);
}
</style>
</head>

<body>
	<div class="container py-5">
		<div class="mx-auto" style="max-width: 960px;">
			<div class="mb-4 text-center">
				<p class="text-primary fw-semibold mb-1">Step 2</p>
				<h1 class="h3 fw-bold">更新内容の確認</h1>
				<p class="text-muted mb-0">入力内容を再確認し、問題なければ「この内容で更新する」を押してください。</p>
			</div>

			<div class="row g-4 mb-4">
				<div class="col-12 col-lg-4">
					<div class="card border-0 shadow-sm h-100">
						<div class="card-body">
							<h2 class="h6 text-muted text-uppercase">投稿者情報</h2>
							<p class="fs-5 fw-semibold mb-1">
								<c:out value="${loginUser.name}" />
							</p>
							<p class="mb-1 text-muted">
								<c:out value="${posterDepartmentName}" />
								/
								<c:out value="${loginUser.grade}" />
								年生
							</p>
							<p class="text-muted small mb-0">
								<c:out value="${loginUser.email}" />
							</p>
						</div>
					</div>
				</div>
				<div class="col-12 col-lg-8">
					<div class="card border-0 shadow-sm h-100">
						<div class="card-body">
							<h2 class="h6 text-muted text-uppercase">投稿内容</h2>
							<dl class="row mb-0">
								<dt class="col-sm-4 text-muted">企業・団体名</dt>
								<dd class="col-sm-8 fw-semibold">
									<c:out value="${postForm.companyName}" />
								</dd>

								<dt class="col-sm-4 text-muted">受験日</dt>
								<dd class="col-sm-8">
									<c:out value="${postForm.examDateStr}" />
								</dd>

								<dt class="col-sm-4 text-muted">応募方法</dt>
								<dd class="col-sm-8">
									<c:set var="method" value="${submissionMethodMap[postForm.methodId]}" /><c:choose>
										<c:when test="${not empty method}"><c:out value="${method.methodName}" /></c:when>
										<c:otherwise>未設定</c:otherwise>
									</c:choose>
								</dd>

								<dt class="col-sm-4 text-muted">試験会場</dt>
								<dd class="col-sm-8">
									<c:choose>
										<c:when test="${not empty postForm.venueAddress}">
											<c:out value="${postForm.venueAddress}" />
										</c:when>
										<c:otherwise>未入力</c:otherwise>
									</c:choose>
								</dd>

								<dt class="col-sm-4 text-muted">求人票番号</dt>
								<dd class="col-sm-8">
									<c:choose>
										<c:when test="${not empty postForm.recruitmentNoStr}">
                                                    #
                                                    <c:out
												value="${postForm.recruitmentNoStr}" />
										</c:when>
										<c:otherwise>未入力</c:otherwise>
									</c:choose>
								</dd>

								<dt class="col-sm-4 text-muted">予定採用人数</dt>
								<dd class="col-sm-8">
									<c:choose>
										<c:when test="${not empty postForm.scheduledHiresStr}">
											<c:out value="${postForm.scheduledHiresStr}" /> 人
                                                </c:when>
										<c:otherwise>未入力</c:otherwise>
									</c:choose>
								</dd>

								<dt class="col-sm-4 text-muted">匿名投稿</dt>
								<dd class="col-sm-8">
									<c:choose>
										<c:when test="${postForm.anonymous}">する</c:when>
										<c:otherwise>しない</c:otherwise>
									</c:choose>
								</dd>
							</dl>
						</div>
					</div>
				</div>
			</div>

			<div class="card border-0 shadow-sm mb-4">
				<div class="card-body">
					<h2 class="h5 fw-bold mb-3">試験項目</h2>
					<c:if test="${empty postForm.selectedExamIds}">
						<p class="text-muted mb-0">試験項目が選択されていません。</p>
					</c:if>
					<c:forEach var="examId" items="${postForm.selectedExamIds}"
						varStatus="loop">
						<c:set var="exam" value="${examContentMap[examId]}" />
						<div class="border rounded-4 p-3 mb-3">
							<div
								class="d-flex flex-column flex-md-row justify-content-between gap-2">
								<div>
									<p class="mb-0 fw-semibold">
										<c:choose>
											<c:when test="${not empty exam}">
												<span class="text-muted small me-2"> <c:out
														value="${exam.contentCategory}" />
												</span>
												<span> <c:out value="${exam.contentName}" />
												</span>
											</c:when>
											<c:otherwise>
                                                        未登録の試験項目 (ID:
                                                        <c:out
													value="${examId}" />)
                                                    </c:otherwise>
										</c:choose>
									</p>
								</div>
								<span class="badge bg-light text-dark align-self-start">
									項目 <c:out value="${loop.index + 1}" />
								</span>
							</div>
							<c:if test="${exam ne null and exam.needsDetail}">
								<hr class="text-muted" />
								<p class="text-muted small mb-1">詳細</p>
							<p class="mb-0 text-prewrap text-start">
									<c:choose>
										<c:when test="${not empty postForm.examDetails[examId]}">
											<c:out value="${postForm.examDetails[examId]}" />
										</c:when>
										<c:otherwise>未入力</c:otherwise>
									</c:choose>
								</p>
							</c:if>
						</div>
					</c:forEach>

					<div class="mt-4">
						<h2 class="h5 fw-bold mb-2">後輩へのアドバイス</h2>
						<div class="bg-light rounded-4 p-3">
						<p class="mb-0 text-prewrap text-start">
								<c:choose>
									<c:when test="${not empty postForm.adviceText}">
										<c:out value="${postForm.adviceText}" />
									</c:when>
									<c:otherwise>未入力</c:otherwise>
								</c:choose>
							</p>
						</div>
					</div>
				</div>
			</div>

			<form method="post" action="${contextPath}/ReportEdit"
				class="card border-0 shadow-sm">
				<div class="card-body p-4">
					<input type="hidden" name="postId" value="${postForm.postId}" /> <input
						type="hidden" name="companyName"
						value="<c:out value='${postForm.companyName}' />" /> <input
						type="hidden" name="examDateStr"
						value="<c:out value='${postForm.examDateStr}' />" /> <input
						type="hidden" name="venueAddress"
						value="<c:out value='${postForm.venueAddress}' />" /> <input
						type="hidden" name="methodId" value="${postForm.methodId}" /> <input
						type="hidden" name="recruitmentNoStr"
						value="<c:out value='${postForm.recruitmentNoStr}' />" /> <input
						type="hidden" name="scheduledHiresStr"
						value="<c:out value='${postForm.scheduledHiresStr}' />" />
					<textarea name="adviceText" class="d-none"><c:out
							value="${postForm.adviceText}" /></textarea>
					<c:if test="${postForm.anonymous}">
						<input type="hidden" name="isAnonymous" value="true" />
					</c:if>
					<c:forEach var="examId" items="${postForm.selectedExamIds}">
						<input type="hidden" name="selectedExamIds" value="${examId}" />
						<c:set var="detailValue" value="${postForm.examDetails[examId]}" />
						<textarea name="detailTexts" class="d-none"><c:out
								value="${detailValue}" /></textarea>
						<c:if test="${not empty detailValue}">
							<textarea name="detail_${examId}" class="d-none"><c:out
									value="${detailValue}" /></textarea>
						</c:if>
					</c:forEach>
					<div class="d-flex flex-column flex-md-row gap-3">
						<button type="submit" name="action" value="edit"
							class="btn btn-outline-secondary flex-fill">入力内容を修正する</button>
						<button type="submit" name="action" value="update"
							class="btn btn-primary flex-fill">この内容で更新する</button>
					</div>
				</div>
			</form>
		</div>
	</div>

	<script
		src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"
		integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz"
		crossorigin="anonymous"></script>
</body>

</html>
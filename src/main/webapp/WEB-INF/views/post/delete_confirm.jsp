<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<c:set var="post" value="${postDetail}" />
<!DOCTYPE html>
<html lang="ja">

<head>
<jsp:include page="/WEB-INF/views/common/head.jsp" />
<title>OICareerLog | 削除確認</title>
</head>

<body class="bg-light">
	<div class="container py-5">
		<div class="mx-auto" style="max-width: 840px;">
			<div class="text-center mb-4">
				<p class="text-danger fw-semibold mb-1">最終確認</p>
				<h1 class="h4 fw-bold">投稿を削除しますか？</h1>
				<p class="text-muted mb-0">この操作は元に戻せません。内容を確認の上で削除してください。</p>
			</div>

			<c:choose>
				<c:when test="${post == null}">
					<div class="card shadow-sm border-0">
						<div class="card-body p-4">
							<p class="mb-4">対象の投稿が見つかりませんでした。</p>
							<a class="btn btn-outline-secondary"
								href="${contextPath}/ReportList">一覧へ戻る</a>
						</div>
					</div>
				</c:when>
				<c:otherwise>
					<div class="card shadow-sm border-0 mb-4">
						<div class="card-body p-4">
							<h2 class="h5 fw-bold mb-3">投稿概要</h2>
							<dl class="row mb-0 gy-3">
								<dt class="col-md-3 text-muted">企業名</dt>
								<dd class="col-md-9 fw-semibold">
									<c:out value="${post.companyName}" />
								</dd>
								<dt class="col-md-3 text-muted">学科</dt>
								<dd class="col-md-9">
									<c:out value="${post.departmentName}" />
								</dd>
								<dt class="col-md-3 text-muted">受験日</dt>
								<dd class="col-md-9">
								<c:out value="${post.formattedExamDate}" />
								</dd>
								<dt class="col-md-3 text-muted">応募方法</dt>
								<dd class="col-md-9">
									<c:out value="${post.submissionMethodName}" />
								</dd>
								<dt class="col-md-3 text-muted">求人票番号</dt>
								<dd class="col-md-9">
									<c:choose>
										<c:when test="${post.recruitmentNo > 0}">
                                                    #
                                                    <c:out
												value="${post.recruitmentNo}" />
										</c:when>
										<c:otherwise>未入力</c:otherwise>
									</c:choose>
								</dd>
								<dt class="col-md-3 text-muted">試験会場</dt>
								<dd class="col-md-9">
									<c:choose>
										<c:when test="${not empty post.venueAddress}">
											<c:out value="${post.venueAddress}" />
										</c:when>
										<c:otherwise>未入力</c:otherwise>
									</c:choose>
								</dd>
								<dt class="col-md-3 text-muted">予定採用人数</dt>
								<dd class="col-md-9">
									<c:choose>
										<c:when test="${post.scheduledHires > 0}">
											<c:out value="${post.scheduledHires}" />
                                                    人
                                                </c:when>
										<c:otherwise>未設定</c:otherwise>
									</c:choose>
								</dd>
							</dl>

							<hr class="my-4">

							<h3 class="h6 text-muted mb-2">試験項目</h3>
							<c:choose>
								<c:when test="${empty post.selectedExams}">
									<p class="text-muted mb-3">登録された試験項目はありません。</p>
								</c:when>
								<c:otherwise>
									<div class="d-flex flex-column gap-2 mb-3">
										<c:forEach var="exam" items="${post.selectedExams}"
											varStatus="loop">
											<div class="border rounded-3 p-3">
												<div
													class="d-flex justify-content-between align-items-start gap-3">
													<div>
														<p class="mb-0 fw-semibold">
															<span class="text-muted small me-2"> <c:out
																	value="${exam.categoryName}" />
															</span>
															<c:out value="${exam.itemName}" />
														</p>
													</div>
													<span class="badge text-bg-light">項目 ${loop.index +
                                                                1}</span>
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

							<h3 class="h6 text-muted mb-2">後輩へのアドバイス</h3>
							<div class="bg-light rounded-3 p-3"
								style="white-space: pre-line;">
								<c:choose>
									<c:when test="${not empty post.adviceText}">
										<c:out value="${post.adviceText}" />
									</c:when>
									<c:otherwise>アドバイスは登録されていません。</c:otherwise>
								</c:choose>
							</div>
						</div>
					</div>

					<div class="d-flex flex-column flex-md-row gap-3">
						<button type="button" class="btn btn-outline-secondary flex-fill"
							onclick="history.back()">戻る</button>
						<form method="post" action="${contextPath}/DeleteReport"
							class="flex-fill">
							<input type="hidden" name="action" value="execute" /> <input
								type="hidden" name="postId" value="${post.postId}" />
							<button type="submit" class="btn btn-danger w-100">削除する
							</button>
						</form>
					</div>
				</c:otherwise>
			</c:choose>
		</div>
	</div>

	<script
		src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"
		integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz"
		crossorigin="anonymous"></script>
</body>

</html>
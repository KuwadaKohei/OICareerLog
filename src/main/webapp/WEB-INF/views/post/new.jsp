<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="ja">

<head>
<jsp:include page="/WEB-INF/views/common/head.jsp" />
<title>OICareerLog | 投稿作成</title>
<link rel="stylesheet" href="${contextPath}/css/style.css">
</head>

<body class="bg-gradient-primary">
	<!-- 固定ボタン -->
	<a href="${contextPath}/ReportList" class="btn btn-secondary fixed-back-button">
		<i class="bi bi-arrow-left"></i> 一覧へ戻る
	</a>

	<div class="container py-5">
		<div class="mx-auto" style="max-width: 960px;">
			<div class="mb-4 text-center">
				<p class="text-primary fw-semibold mb-1">Step 1</p>
				<h1 class="h3 fw-bold">投稿情報を入力する</h1>
				<p class="text-muted mb-0">試験内容やアドバイスを記録して、後輩の就活に役立てましょう。</p>
			</div>

			<c:if test="${not empty pageMessage}">
				<div class="alert alert-warning shadow-sm" role="alert">
					<c:out value="${pageMessage}" />
				</div>
			</c:if>

			<c:if test="${not empty errorMessages}">
				<div class="alert alert-danger shadow-sm" role="alert">
					<p class="fw-semibold mb-2">入力内容を確認してください。</p>
					<ul class="mb-0 ps-3">
						<c:forEach var="msg" items="${errorMessages}">
							<li><c:out value="${msg}" /></li>
						</c:forEach>
					</ul>
				</div>
			</c:if>

			<form method="post" action="${contextPath}/ReportNew" id="postForm">
				<input type="hidden" name="action" value="confirm">

				<div class="card shadow-sm border-0 mb-4">
					<div class="card-body p-4 p-md-5">
						<section class="mb-5">
							<h2 class="h5 fw-bold mb-3">基本情報</h2>
							<div class="row g-3">
								<div class="col-12">
									<div class="form-check form-switch">
										<input class="form-check-input" type="checkbox"
											id="isAnonymousSwitch" name="isAnonymous" value="true"
											<c:if test="${postForm.anonymous}">checked</c:if>>
											<label class="form-check-label" for="isAnonymousSwitch">氏名を非公開にする</label>
									</div>
								</div>
								<div class="col-12">
									<label for="companyName" class="form-label">企業名<span
										class="text-danger ms-1">*</span></label> <input type="text"
										class="form-control" id="companyName" name="companyName"
										maxlength="255"
										value="<c:out value='${postForm.companyName}' />"
										placeholder="例：株式会社〇〇" required>
								</div>
								<div class="col-md-6">
									<label for="examDateStr" class="form-label">受験日<span
										class="text-danger ms-1">*</span></label> <input type="date"
										class="form-control" id="examDateStr" name="examDateStr"
										value="<c:out value='${postForm.examDateStr}' />" required>
								</div>
								<div class="col-md-6">
									<label for="methodId" class="form-label">応募方法<span
										class="text-danger ms-1">*</span></label> <select
										class="form-select" id="methodId" name="methodId" required>
										<option value="" disabled
											<c:if test="${empty postForm.methodId or postForm.methodId == 0}">selected</c:if>>選択してください</option>
										<c:forEach var="method" items="${submissionMethods}">
											<option value="${method.methodId}"
												<c:if test="${method.methodId == postForm.methodId}">selected</c:if>>
												${method.methodName}</option>
										</c:forEach>
									</select>
								</div>
								<div class="col-12">
									<label for="venueAddress" class="form-label">試験会場<span
										class="text-danger ms-1">*</span></label> <input
										type="text" class="form-control" id="venueAddress"
										name="venueAddress" maxlength="200"
										value="<c:out value='${postForm.venueAddress}' />"
										placeholder="例：大阪市北区〇〇 〇〇ビル または オンライン Zoom" required>
								</div>
							</div>
						</section>

						<section class="mb-5">
							<h2 class="h5 fw-bold mb-3">詳細情報</h2>
							<div class="row g-3">
								<div class="col-md-6">
									<label for="recruitmentNoStr" class="form-label">求人票番号</label> <input
										type="text" class="form-control" id="recruitmentNoStr"
										name="recruitmentNoStr" inputmode="numeric" pattern="[0-9]*"
										value="<c:out value='${postForm.recruitmentNoStr}' />"
										placeholder="例：1234">
								</div>
								<div class="col-md-6">
									<label for="scheduledHiresStr" class="form-label">採用予定人数</label>
									<input type="text" class="form-control"
										id="scheduledHiresStr" name="scheduledHiresStr"
										inputmode="numeric" pattern="[0-9]*"
										value="<c:out value='${postForm.scheduledHiresStr}' />"
										placeholder="例：5">
								</div>
								<div class="col-12">
									<label for="adviceText" class="form-label">後輩へのアドバイス</label>
									<textarea class="form-control" id="adviceText" name="adviceText"
										rows="4" placeholder="印象に残った質問や当日の雰囲気などを共有しましょう。"><c:out
											value="${postForm.adviceText}" /></textarea>
								</div>
							</div>
						</section>

						<section>
							<div class="d-flex flex-column flex-md-row align-items-md-center justify-content-between gap-3 mb-3">
								<div>
									<h2 class="h5 fw-bold mb-1">試験項目</h2>
									<p class="text-muted mb-0">「+」ボタンで試験項目を追加できます。</p>
								</div>
								<button type="button" class="btn btn-primary" id="openExamModalBtn"
									<c:if test="${empty examContents}">disabled</c:if>>
									<i class="bi bi-plus-circle me-1"></i>試験項目を追加
								</button>
							</div>
							<c:if test="${empty examContents}">
								<div class="alert alert-warning" role="alert">
									試験項目マスタが未登録のため、管理者にお問い合わせください。
								</div>
							</c:if>
							<div id="selectedExamsContainer" class="d-flex flex-column gap-2">
								<c:choose>
									<c:when test="${not empty postForm.selectedExamIds}">
										<c:forEach var="examId" items="${postForm.selectedExamIds}" varStatus="status">
											<c:set var="exam" value="${examContentMap[examId]}" />
											<div class="card exam-item-card" data-content-id="${examId}">
												<div class="card-body">
													<div class="d-flex justify-content-between align-items-start">
														<div class="flex-grow-1">
															<h6 class="mb-1"><c:out value="${exam.contentCategory}" /> - <c:out value="${exam.contentName}" /></h6>
															<c:if test="${not empty postForm.examDetails[examId]}">
																<p class="text-muted small mb-0 text-prewrap"><c:out value="${postForm.examDetails[examId]}" /></p>
															</c:if>
														</div>
														<button type="button" class="btn btn-sm btn-outline-danger remove-exam-btn ms-2" data-content-id="${examId}">
															<i class="bi bi-trash"></i>
														</button>
													</div>
													<input type="hidden" name="selectedExamIds" value="${examId}" />
													<input type="hidden" name="detailTexts" value="<c:out value='${postForm.examDetails[examId]}' />" />
												</div>
											</div>
										</c:forEach>
									</c:when>
									<c:otherwise>
										<div class="text-muted text-center py-4" id="noExamMessage">
											試験項目が選択されていません。上の「+試験項目を追加」ボタンから追加してください。
										</div>
									</c:otherwise>
								</c:choose>
							</div>
						</section>
					</div>
					<div class="card-footer bg-white border-0 px-4 px-md-5 pb-4">
						<div class="d-flex flex-column flex-md-row gap-3">
							<a href="${contextPath}/ReportList"
								class="btn btn-secondary flex-fill">一覧へ戻る</a>
							<button type="submit" class="btn btn-primary flex-fill">確認画面へ進む</button>
						</div>
					</div>
				</div>
			</form>
		</div>
	</div>

	<!-- 試験項目追加モーダル -->
	<div class="modal fade" id="examModal" tabindex="-1" aria-labelledby="examModalLabel" aria-hidden="true">
		<div class="modal-dialog modal-dialog-centered">
			<div class="modal-content">
				<div class="modal-header">
					<h5 class="modal-title" id="examModalLabel">試験項目を追加</h5>
					<button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
				</div>
				<div class="modal-body">
					<div class="mb-3">
						<label for="examSelect" class="form-label">試験項目<span class="text-danger ms-1">*</span></label>
						<select class="form-select" id="examSelect">
							<option value="" selected disabled>選択してください</option>
							<c:forEach var="content" items="${examContents}">
								<option value="${content.contentId}"
									data-needs-detail="${content.needsDetail}"
									data-select-detail="${content.selectDetail}"
									data-example="<c:out value='${content.detailExample}' />">
									<c:out value="${content.contentCategory}" /> - <c:out value="${content.contentName}" />
								</option>
							</c:forEach>
						</select>
					</div>

					<!-- 選択肢表示エリア（select_detail=trueの場合） -->
					<div id="detailOptionsArea" class="mb-3" style="display: none;">
						<label class="form-label">詳細を選択（複数選択可）</label>
						<div id="detailOptionsList" class="d-flex flex-column gap-2">
							<!-- 動的に選択肢が追加されます -->
						</div>
					</div>

					<!-- フリーテキスト入力エリア -->
					<div id="detailTextArea" class="mb-3" style="display: none;">
						<label for="examDetailText" class="form-label">詳細情報</label>
						<small class="text-muted d-block mb-2" id="detailExample"></small>
						<textarea class="form-control" id="examDetailText" rows="3"
							placeholder="詳細を入力してください"></textarea>
					</div>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-secondary" data-bs-dismiss="modal">キャンセル</button>
					<button type="button" class="btn btn-primary" id="addExamBtn">追加</button>
				</div>
			</div>
		</div>
	</div>

	<!-- アプリケーション設定（JavaScript用） -->
	<div id="app-config" data-context-path="${contextPath}"></div>

	<!-- 試験詳細選択肢データ（JavaScript用） -->
	<div id="exam-detail-options-data" data-options='<c:out value="${examDetailOptionsJson}" escapeXml="false" />'></div>

	<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"
		integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz"
		crossorigin="anonymous"></script>
	<script src="${contextPath}/js/post-form.js"></script>
</body>

</html>

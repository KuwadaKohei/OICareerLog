<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
	<%@ taglib prefix="c" uri="jakarta.tags.core" %>
		<c:set var="resolvedBackUrl" value="${backUrl}" />
		<c:set var="resolvedBackLabel" value="${backLabel}" />
		<c:if test="${empty resolvedBackUrl}">
			<c:set var="resolvedBackUrl" value="${pageContext.request.contextPath}/ReportList" />
		</c:if>
		<c:if test="${empty resolvedBackLabel}">
			<c:set var="resolvedBackLabel" value="トップへ戻る" />
		</c:if>
		<!DOCTYPE html>
		<html lang="ja">

		<head>
			<meta charset="UTF-8">
			<meta name="viewport" content="width=device-width, initial-scale=1">
			<title>OICareerLog | エラーが発生しました</title>
			<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet"
				integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH"
				crossorigin="anonymous">
			<link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
		</head>

		<body class="bg-light min-vh-100 d-flex flex-column">
			<main class="flex-grow-1 d-flex align-items-center py-5">
				<div class="container">
					<div class="row justify-content-center">
						<div class="col-lg-7">
							<div class="card border-0 shadow-sm">
								<div class="card-body p-4 p-lg-5 text-center">
									<div class="display-4 text-danger mb-3"><i
											class="bi bi-exclamation-triangle-fill"></i></div>
									<h1 class="h4 fw-bold mb-3">エラーが発生しました</h1>
									<p class="text-muted mb-4">処理を続行できませんでした。以下の内容をご確認の上、必要であればやり直してください。</p>

									<div class="alert alert-light border text-start mb-4" role="alert">
										<p class="mb-2 fw-semibold text-danger">エラー内容</p>
										<p class="mb-0">
											<c:out value="${error}" />
										</p>
										<c:if test="${not empty errorCode}">
											<p class="mt-3 mb-0 text-muted small">エラーコード: <span class="fw-semibold">
													<c:out value="${errorCode}" />
												</span></p>
										</c:if>
									</div>

									<div class="card bg-body-tertiary border-0 mb-4">
										<div class="card-body">
											<p class="text-muted small mb-2">再試行のヒント</p>
											<ul class="list-unstyled mb-0 text-start">
												<li class="d-flex align-items-start gap-2 mb-1"><i
														class="bi bi-arrow-repeat text-secondary"></i><span>ブラウザをリロード・再起動してもう一度操作してください。</span>
												</li>
												<li class="d-flex align-items-start gap-2 mb-1"><i
														class="bi bi-clock-history text-secondary"></i><span>時間を置いてから再度アクセスしてください。</span>
												</li>
												<li class="d-flex align-items-start gap-2"><i
														class="bi bi-person-lines-fill text-secondary"></i><span>解決しない場合は管理者へお問い合わせください。</span>
												</li>
											</ul>
										</div>
									</div>

									<a href="${resolvedBackUrl}" class="btn btn-primary px-4">${resolvedBackLabel}</a>
								</div>
							</div>
						</div>
					</div>
				</div>
			</main>

			<footer class="text-center text-muted small py-3">
				&copy; Recruit Memo
			</footer>

			<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"
				integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz"
				crossorigin="anonymous"></script>
		</body>

		</html>
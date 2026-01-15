<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="ja">

<head>
<jsp:include page="/WEB-INF/views/common/head.jsp" />
<title>OICareerLog | 削除完了</title>
</head>

<body class="bg-light">
	<div
		class="container min-vh-100 d-flex align-items-center justify-content-center py-5">
		<div class="card shadow-sm border-0" style="max-width: 420px;">
			<div class="card-body text-center p-5">
				<p class="text-success fw-semibold mb-2">完了しました。</p>
				<p class="text-muted mb-4">投稿の削除が完了しました。</p>
				<a href="${contextPath}/MyReports" class="btn btn-primary">自分の投稿に戻る</a>
			</div>
		</div>
	</div>

	<script
		src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"
		integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz"
		crossorigin="anonymous"></script>
</body>

</html>
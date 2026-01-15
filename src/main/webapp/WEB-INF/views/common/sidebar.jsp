<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="loginUser" value="${sessionScope.loginUser}" />
<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<nav class="d-flex flex-column h-100 bg-dark text-white p-3 gap-2">
	<div class="d-flex align-items-center justify-content-between mb-2">
		<button class="btn btn-outline-light btn-sm d-lg-none" type="button" data-bs-dismiss="offcanvas"
			aria-label="メニューを閉じる">
			<i class="bi bi-x-lg"></i>
		</button>
	</div>
	<div class="list-group list-group-flush">
		<a
			class="list-group-item list-group-item-action d-flex align-items-center gap-2 bg-transparent text-white border-0"
			href="${contextPath}/ReportList"> <i class="bi bi-house-door-fill"></i>
			<span>トップ</span>
		</a> <a
			class="list-group-item list-group-item-action d-flex align-items-center gap-2 bg-transparent text-white border-0"
			href="${contextPath}/ReportNew"> <i class="bi bi-plus-circle-fill"></i>
			<span>新規投稿</span>
		</a> <a
			class="list-group-item list-group-item-action d-flex align-items-center gap-2 bg-transparent text-white border-0"
			href="${contextPath}/MyReports"> <i class="bi bi-journal-text"></i>
			<span>自分の投稿リスト</span>
		</a> <a
			class="list-group-item list-group-item-action d-flex align-items-center gap-2 bg-transparent text-white border-0"
			href="${contextPath}/Logout"> <i class="bi bi-box-arrow-right"></i>
			<span>ログアウト</span>
		</a>
	</div>
	<c:if test="${loginUser ne null and loginUser.admin}">
		<div class="mt-3 pt-3 border-top border-secondary">
			<span class="text-uppercase text-secondary small">Admin</span>
				<a class="list-group-item list-group-item-action d-flex align-items-center gap-2 bg-transparent text-white border-0 px-0"
				href="${contextPath}/User/Suspend"> <i class="bi bi-people-fill"></i>
				<span>ユーザー管理</span>
			</a>
		</div>
	</c:if>
</nav>
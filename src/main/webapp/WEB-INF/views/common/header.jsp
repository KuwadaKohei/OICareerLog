<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<header class="bg-white border-bottom shadow-sm">
	<div
		class="container-fluid py-3 d-flex justify-content-between align-items-center">
		<div class="d-flex align-items-center gap-2">
			<button class="btn btn-outline-secondary d-lg-none" type="button"
				data-bs-toggle="offcanvas" data-bs-target="#sidebarOffcanvas"
				aria-controls="sidebarOffcanvas" aria-label="Toggle sidebar">
				<i class="bi bi-list"></i>
			</button>
			<span class="fs-5 fw-semibold">OICareerLog</span>
		</div>
		<div class="text-muted small"><c:out value="${param.pageTitle}" /></div>
	</div>
</header>

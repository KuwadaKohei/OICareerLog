<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html lang="ja">
<head>
<jsp:include page="/WEB-INF/views/common/head.jsp" />
<title>OICareerLog | 教員情報登録</title>
</head>
<body class="bg-light">
	<div class="container min-vh-100 d-flex flex-column justify-content-center py-5">
		<div class="mx-auto" style="max-width: 500px; width: 100%;">
			<div class="card shadow-sm border-0">
				<div class="card-header bg-white border-bottom-0 pt-4 pb-0 text-center">
					<h1 class="h5 fw-bold text-primary">教員ユーザー登録</h1>
				</div>
				<div class="card-body p-4">
					<p class="text-muted text-center small mb-4">
						OICareerLogへようこそ。<br>
						ご利用にあたり、担当クラス情報を登録してください。
					</p>

					<c:if test="${not empty errorMessage}">
						<div class="alert alert-danger" role="alert">
							${errorMessage}
						</div>
					</c:if>

					<form action="${pageContext.request.contextPath}/NewTeacher" method="post">
						<div class="mb-4">
							<label class="form-label fw-semibold">区分</label>
							<div class="form-check card-radio mb-2">
								<input class="form-check-input" type="radio" name="teacherType"
									id="typeHomeroom" value="homeroom"
									${param.teacherType == 'homeroom' || empty param.teacherType ? 'checked' : ''}
									onchange="toggleDepartmentSelect()">
								<label class="form-check-label" for="typeHomeroom">
									担任教員（クラス情報の登録）
								</label>
							</div>
							<div class="form-check">
								<input class="form-check-input" type="radio" name="teacherType"
									id="typeOther" value="other"
									${param.teacherType == 'other' ? 'checked' : ''}
									onchange="toggleDepartmentSelect()">
								<label class="form-check-label" for="typeOther">
									その他教員・職員
								</label>
							</div>
						</div>

						<div id="departmentSection">
							<div class="mb-3">
								<label for="departmentId" class="form-label">担当学科</label>
								<select name="departmentId" id="departmentId" class="form-select">
									<option value="" selected>選択してください</option>
									<c:forEach var="dept" items="${deptList}">
										<!-- '教員・その他'は除外して表示する -->
										<c:if test="${dept.departmentName ne '教員・その他'}">
											<option value="${dept.departmentId}" 
												${dept.departmentId == selectedDepartmentId ? 'selected' : ''}>
												${dept.departmentName}
											</option>
										</c:if>
									</c:forEach>
								</select>
							</div>

							<div class="mb-4">
								<label for="grade" class="form-label">担当学年</label>
								<select name="grade" id="grade" class="form-select">
									<option value="">選択してください</option>
									<option value="1" ${selectedGrade == 1 ? 'selected' : ''}>1年生</option>
									<option value="2" ${selectedGrade == 2 ? 'selected' : ''}>2年生</option>
									<option value="3" ${selectedGrade == 3 ? 'selected' : ''}>3年生</option>
									<option value="4" ${selectedGrade == 4 ? 'selected' : ''}>4年生</option>
								</select>
							</div>
						</div>

					<input type="hidden" name="action" value="confirm">
					<div class="d-grid gap-2">
						<button type="submit" class="btn btn-primary py-2">
							確認画面に進む
							</button>
							<p class="text-center text-muted small mt-2">
								※登録内容は後から変更できません。
							</p>
						</div>
					</form>
				</div>
			</div>
		</div>
	</div>

	<script>
		function toggleDepartmentSelect() {
			const isHomeroom = document.getElementById('typeHomeroom').checked;
			const section = document.getElementById('departmentSection');
			const deptSelect = document.getElementById('departmentId');
			const gradeSelect = document.getElementById('grade');

			if (isHomeroom) {
				section.style.display = 'block';
				deptSelect.required = true;
				gradeSelect.required = true;
			} else {
				section.style.display = 'none';
				deptSelect.required = false;
				gradeSelect.required = false;
				// Reset values
				deptSelect.value = "";
				gradeSelect.value = "";
			}
		}
		// Initial call
		document.addEventListener('DOMContentLoaded', toggleDepartmentSelect);
	</script>
	
	<script
		src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"
		integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz"
		crossorigin="anonymous"></script>
</body>
</html>

package jp.ac.ok_oic.ikzm.careerlog.controller;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import jp.ac.ok_oic.ikzm.careerlog.constants.SessionKeys;
import jp.ac.ok_oic.ikzm.careerlog.entity.Department;
import jp.ac.ok_oic.ikzm.careerlog.entity.User;
import jp.ac.ok_oic.ikzm.careerlog.service.UserService;
import jp.ac.ok_oic.ikzm.careerlog.util.ErrorHandler;
import jp.ac.ok_oic.ikzm.careerlog.util.ModelConverter;

/**
 * 新規ユーザーの学科・学年情報を登録するためのサーブレット。
 *
 * doGet で入力画面を表示し、doPost で確認画面表示と登録処理を担当する。
 *
 */
@WebServlet("/NewUser")
public class NewUserServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String DEFAULT_BACK_PATH = "/NewUser";
	private static final String DEFAULT_BACK_LABEL = "学科情報入力に戻る";

	/**
	 * デフォルトコンストラクタ。
	 */
	public NewUserServlet() {
	}

	/**
	 * 学科一覧を取得し、新規ユーザー入力画面を表示する。
	 *
	 * @param request  リクエスト情報
	 * @param response レスポンス情報
	 * @throws ServletException フォワード失敗時
	 * @throws IOException      フォワード失敗時
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		UserService userService = new UserService();
		List<Department> deptList;
		try {
			// マスタを取得してプルダウンへ表示
			deptList = userService.getDepartmentList();
		} catch (Exception e) {
			ErrorHandler.handleError(request, response, e, DEFAULT_BACK_PATH, DEFAULT_BACK_LABEL);
			return;
		}

		request.setAttribute("deptList", deptList);
		request.setAttribute("selectedDepartmentId", ModelConverter.parseInteger(request.getParameter("departmentId")));
		request.setAttribute("selectedGrade", ModelConverter.parseInteger(request.getParameter("grade")));

		RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/user/register.jsp");
		dispatcher.forward(request, response);
	}

	/**
	 * 入力された学科・学年情報を登録し、完了後に投稿一覧へ遷移する。
	 *
	 * @param request  リクエスト情報
	 * @param response レスポンス情報
	 * @throws ServletException エラー画面へのフォワード時
	 * @throws IOException      リダイレクト時
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// 学科登録は認証済みユーザーが対象のため、セッションがなければ即エラー
		HttpSession session = request.getSession(false);
		if (session == null) {
			throw new IllegalStateException("Session not found. LoginFilter should guarantee authentication.");
		}
		User user = (User) session.getAttribute(SessionKeys.LOGIN_USER);
		if (user == null) {
			throw new IllegalStateException("loginUser not found. LoginFilter should guarantee authentication.");
		}

		String action = request.getParameter("action");
		if (action == null || action.isBlank()) {
			action = "confirm";
		}

		UserService userService = new UserService();
		List<Department> deptList;
		try {
			// 再描画時も常に最新の学科マスタを取得
			deptList = userService.getDepartmentList();
		} catch (Exception e) {
			ErrorHandler.handleError(request, response, e, DEFAULT_BACK_PATH, DEFAULT_BACK_LABEL);
			return;
		}

		ValidationResult validation = validateSelection(request, deptList);
		if (!validation.isValid()) {
			// 入力エラーがある場合はそのまま入力画面へ戻す
			request.setAttribute("deptList", deptList);
			request.setAttribute("errorMessage", validation.message());
			request.setAttribute("selectedDepartmentId", validation.departmentId());
			request.setAttribute("selectedGrade", validation.grade());
			RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/user/register.jsp");
			dispatcher.forward(request, response);
			return;
		}

		if ("confirm".equals(action)) {
			request.setAttribute("selectedDepartment", validation.department());
			request.setAttribute("selectedDepartmentId", validation.departmentId());
			request.setAttribute("selectedGrade", validation.grade());
			// 確認画面を表示
			RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/user/register_confirm.jsp");
			dispatcher.forward(request, response);
			return;
		}

		if ("register".equals(action)) {
			try {
				// DBへ登録実行
				boolean isSuccess = userService.registerUserDetail(user.getUserId(), validation.departmentId(),
					validation.grade());
				if (isSuccess) {
					user.setDepartmentId(validation.departmentId());
					user.setGrade(validation.grade());
					session.setAttribute(SessionKeys.LOGIN_USER, user);
					response.sendRedirect(request.getContextPath() + "/ReportList");
					return;
				}
			} catch (Exception e) {
				ErrorHandler.handleError(request, response, e, DEFAULT_BACK_PATH, DEFAULT_BACK_LABEL);
				return;
			}
			ErrorHandler.handleError(request, response, new Exception("DB-0065"), DEFAULT_BACK_PATH, DEFAULT_BACK_LABEL);
			return;
		}

		response.sendRedirect(request.getContextPath() + DEFAULT_BACK_PATH);
	}

	/**
	 * リクエストパラメータから学科と学年を検証し、結果を返す。
	 *
	 * @param request  リクエスト情報
	 * @param deptList 学科マスタ一覧
	 * @return 検証結果レコード
	 */
	private ValidationResult validateSelection(HttpServletRequest request, List<Department> deptList) {
		Integer departmentId = ModelConverter.parseInteger(request.getParameter("departmentId"));
		Integer grade = ModelConverter.parseInteger(request.getParameter("grade"));
		if (departmentId == null) {
			return ValidationResult.invalid("学科を選択してください。", null, grade, null);
		}
		if (grade == null) {
			return ValidationResult.invalid("学年を選択してください。", departmentId, null, null);
		}
		if (grade < 1 || grade > 3) {
			return ValidationResult.invalid("学年は1〜3から選択してください。", departmentId, grade, null);
		}
		Department department = findDepartment(deptList, departmentId);
		if (department == null) {
			return ValidationResult.invalid("選択した学科は存在しません。", departmentId, grade, null);
		}
		return ValidationResult.valid(departmentId, grade, department);
	}

	/**
	 * 学科リストから指定 ID の学科を検索する。
	 *
	 * @param departments  学科一覧
	 * @param departmentId 探索する学科ID
	 * @return 該当学科、存在しない場合は null
	 */
	private Department findDepartment(List<Department> departments, int departmentId) {
		for (Department department : departments) {
			if (department.getDepartmentId() == departmentId) {
				return department;
			}
		}
		return null;
	}

	/**
	 * 入力検証の結果をまとめるレコード。
	 */
	private record ValidationResult(boolean isValid, String message, Integer departmentId, Integer grade,
			Department department) {
		/**
		 * 成功結果を生成する。
		 */
		static ValidationResult valid(Integer departmentId, Integer grade, Department department) {
			return new ValidationResult(true, null, departmentId, grade, department);
		}

		/**
		 * 失敗結果を生成する。
		 */
		static ValidationResult invalid(String message, Integer departmentId, Integer grade, Department department) {
			return new ValidationResult(false, message, departmentId, grade, department);
		}
	}
}

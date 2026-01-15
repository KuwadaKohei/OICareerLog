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

/**
 * 教員ユーザーの初回登録情報を処理するサーブレット。
 *
 * 担任教員の場合は学科・学年を登録し、
 * その他教員の場合は「教員・その他」学科を自動的に割り当てる。
 */
@WebServlet("/NewTeacher")
public class NewTeacherServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String TEACHER_OTHER_DEPT_NAME = "教員・その他";

	/**
	 * 教員用登録画面を表示する。
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
			ErrorHandler.handleError(request, response, e, "/index.jsp", "トップへ戻る");
			return;
		}

		request.setAttribute("deptList", deptList);
		RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/user/teacher_register.jsp");
		dispatcher.forward(request, response);
	}

	/**
	 * 入力された教員情報を登録し、一覧画面へ遷移する。
	 *
	 * @param request  リクエスト情報
	 * @param response レスポンス情報
	 * @throws ServletException エラー画面へのフォワード時
	 * @throws IOException      エラー画面へのフォワード時
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession(false);
		if (session == null || session.getAttribute(SessionKeys.LOGIN_USER) == null) {
			response.sendRedirect(request.getContextPath() + "/index.jsp");
			return;
		}

		User user = (User) session.getAttribute(SessionKeys.LOGIN_USER);
		String teacherType = request.getParameter("teacherType");
		String action = request.getParameter("action");

		// 確認画面に遷移する処理
		if ("confirm".equals(action)) {
			try {
				UserService userService = new UserService();

				if ("homeroom".equals(teacherType)) {
					// 担任の場合：入力値をバリデーション
					String deptStr = request.getParameter("departmentId");
					String gradeStr = request.getParameter("grade");

					if (deptStr == null || deptStr.isBlank() || gradeStr == null || gradeStr.isBlank()) {
						request.setAttribute("errorMessage", "学科と学年を選択してください。");
						doGet(request, response);
						return;
					}

					int departmentId = Integer.parseInt(deptStr);
					int grade = Integer.parseInt(gradeStr);

					// 学科情報を取得
					List<Department> deptList = userService.getDepartmentList();
					Department selectedDept = null;
					for (Department d : deptList) {
						if (d.getDepartmentId() == departmentId) {
							selectedDept = d;
							break;
						}
					}

					if (selectedDept == null) {
						request.setAttribute("errorMessage", "学科情報の取得に失敗しました。");
						doGet(request, response);
						return;
					}

					request.setAttribute("selectedDepartment", selectedDept);
					request.setAttribute("selectedDepartmentId", departmentId);
					request.setAttribute("selectedGrade", grade);
				}

				request.setAttribute("teacherType", teacherType);
				RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/user/teacher_register_confirm.jsp");
				dispatcher.forward(request, response);
				return;

			} catch (Exception e) {
				ErrorHandler.handleError(request, response, e, "/index.jsp", "トップへ戻る");
				return;
			}
		}

		// 登録処理
		if (!"register".equals(action)) {
			doGet(request, response);
			return;
		}

		try {
			UserService userService = new UserService();
			int departmentId = 0;
			int grade = 0;

			if ("homeroom".equals(teacherType)) {
				// 担任の場合：入力値を使用
				String deptStr = request.getParameter("departmentId");
				String gradeStr = request.getParameter("grade");

				if (deptStr == null || deptStr.isBlank() || gradeStr == null || gradeStr.isBlank()) {
					request.setAttribute("errorMessage", "学科と学年を選択してください。");
					doGet(request, response);
					return;
				}
				departmentId = Integer.parseInt(deptStr);
				grade = Integer.parseInt(gradeStr);

			} else if ("other".equals(teacherType)) {
				// その他の場合：「教員・その他」
				List<Department> list = userService.getDepartmentList();
				Department otherDept = null;
				for (Department d : list) {
					if (TEACHER_OTHER_DEPT_NAME.equals(d.getDepartmentName())) {
						otherDept = d;
						break;
					}
				}
				if (otherDept == null) {
					throw new Exception("DB-0064");
				}

				departmentId = otherDept.getDepartmentId();
				grade = 0;
			} else {
				request.setAttribute("errorMessage", "教員区分を選択してください。");
				doGet(request, response);
				return;
			}

			// 登録実行
			boolean isSuccess = userService.registerUserDetail(user.getUserId(), departmentId, grade);
			if (isSuccess) {
				user.setDepartmentId(departmentId);
				user.setGrade(grade);
				session.setAttribute(SessionKeys.LOGIN_USER, user);
				response.sendRedirect(request.getContextPath() + "/ReportList");
			} else {
				throw new Exception("DB-0065");
			}

		} catch (Exception e) {
			ErrorHandler.handleError(request, response, e, "/index.jsp", "トップへ戻る");
		}
	}
}

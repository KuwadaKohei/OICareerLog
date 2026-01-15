package jp.ac.ok_oic.ikzm.careerlog.controller;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;

import jp.ac.ok_oic.ikzm.careerlog.entity.User;
import jp.ac.ok_oic.ikzm.careerlog.service.UserService;
import jp.ac.ok_oic.ikzm.careerlog.util.ErrorHandler;
import jp.ac.ok_oic.ikzm.careerlog.util.Logging;
import jp.ac.ok_oic.ikzm.careerlog.util.ModelConverter;
import jp.ac.ok_oic.ikzm.careerlog.util.SessionRegistry;

/**
 * 管理者によるユーザー検索およびBAN/解除処理を提供するサーブレット。
 */
@WebServlet("/User/Suspend")
public class UserSuspendServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String DEFAULT_BACK_PATH = "/User/Suspend";
	private static final String DEFAULT_BACK_LABEL = "ユーザー管理に戻る";
	private static final Logger LOGGER = Logging.getLogger(UserSuspendServlet.class);
	private static final Logger ACTION_LOGGER = Logging.actionLogger();

	/**
	 * ユーザー検索を実行し、ユーザー管理画面を表示する。
	 *
	 * @param request  リクエスト情報
	 * @param response レスポンス情報
	 * @throws ServletException フォワード失敗時
	 * @throws IOException      フォワード失敗時
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String action = request.getParameter("action");
		if ("confirm".equals(action)) {
			// BAN/解除の確認画面へ
			showConfirmScreen(request, response);
			return;
		}

		// 検索画面をデフォルト表示
		showSearchScreen(request, response);
	}

	/**
	 * BAN/解除の各種操作を受け付け、確認・実行画面へ遷移させる。
	 *
	 * @param request  リクエスト情報
	 * @param response レスポンス情報
	 * @throws ServletException フォワード失敗時
	 * @throws IOException      フォワード失敗時
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String action = request.getParameter("action");
		if ("updateStatus".equals(action)) {
			// ステータス更新要求のみ受け付ける
			processStatusUpdate(request, response);
			return;
		}

		response.sendRedirect(request.getContextPath() + DEFAULT_BACK_PATH);
	}

	/**
	 * 検索条件に応じてユーザー一覧を取得し、検索画面へフォワードする。
	 *
	 * @param request  リクエスト情報
	 * @param response レスポンス情報
	 * @throws ServletException フォワード失敗時
	 * @throws IOException      フォワード失敗時
	 */
	private void showSearchScreen(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String searchWord = request.getParameter("searchWord");
		List<User> userList = Collections.emptyList();
		if (searchWord != null && !searchWord.isBlank()) {
			UserService userService = new UserService();
			try {
				// 入力条件に応じて検索
				userList = userService.search(searchWord);
			} catch (Exception e) {
				Logging.logError(LOGGER, UserSuspendServlet.class,
						String.format("ユーザー検索に失敗しました。searchWord=%s", searchWord),
						e);
				ErrorHandler.handleError(request, response, e, DEFAULT_BACK_PATH, DEFAULT_BACK_LABEL);
				return;
			}
		}

		request.setAttribute("searchWord", searchWord);
		request.setAttribute("userList", userList);

		RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/admin/user_list.jsp");
		dispatcher.forward(request, response);
	}

	/**
	 * BAN/解除の対象情報を取得し、確認画面へフォワードする。
	 *
	 * @param request  リクエスト情報
	 * @param response レスポンス情報
	 * @throws ServletException フォワード失敗時
	 * @throws IOException      フォワード失敗時
	 */
	private void showConfirmScreen(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String userIdParam = request.getParameter("userId");
		int userId = ModelConverter.parseIntOrDefault(userIdParam, -1);
		if (userId <= 0) {
			// パラメータ異常時はトップへ戻す
			response.sendRedirect(request.getContextPath() + DEFAULT_BACK_PATH);
			return;
		}

		boolean targetActive = Boolean.parseBoolean(request.getParameter("targetActive"));
		UserService userService = new UserService();
		try {
			User user = userService.findById(userId);
			if (user == null) {
				response.sendRedirect(request.getContextPath() + DEFAULT_BACK_PATH);
				return;
			}
			if (request.getParameter("targetActive") == null) {
				targetActive = !user.isActive();
			}
			request.setAttribute("user", user);
			request.setAttribute("targetActive", targetActive);
			RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/admin/user_status_confirm.jsp");
			dispatcher.forward(request, response);
		} catch (Exception e) {
			Logging.logError(LOGGER, UserSuspendServlet.class,
					String.format("ユーザー情報の取得に失敗しました。userId=%d", userId),
					e);
			ErrorHandler.handleError(request, response, e, DEFAULT_BACK_PATH, DEFAULT_BACK_LABEL);
		}
	}

	/**
	 * ユーザーのステータス更新を実行し、結果画面へ遷移させる。
	 *
	 * @param request  リクエスト情報
	 * @param response レスポンス情報
	 * @throws ServletException フォワード失敗時
	 * @throws IOException      フォワード失敗時
	 */
	private void processStatusUpdate(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String userIdParam = request.getParameter("userId");
		boolean targetActive = Boolean.parseBoolean(request.getParameter("targetActive"));
		UserService userService = new UserService();
		try {
			boolean success = userService.updateActiveStatus(userIdParam, targetActive);
			if (!success) {
				// DB更新に失敗した場合は専用エラーを返す
				ErrorHandler.handleError(request, response, new Exception("DB-0061"), DEFAULT_BACK_PATH, DEFAULT_BACK_LABEL);
				return;
			}
			int userId = ModelConverter.parseIntOrDefault(userIdParam, -1);
			User refreshed = userService.findById(userId);
			if (refreshed == null) {
				// 想定外だがユーザーが見つからなければ一覧に戻す
				response.sendRedirect(request.getContextPath() + DEFAULT_BACK_PATH);
				return;
			}
			request.setAttribute("user", refreshed);
			request.setAttribute("targetActive", targetActive);
			if (!targetActive) {
				// BANした場合は関連セッションをすべて無効化
				SessionRegistry.invalidateSessions(refreshed.getUserId());
			}
			ACTION_LOGGER.info("ユーザーID={} の利用ステータスを{}へ更新しました。", refreshed.getUserId(), targetActive ? "アクティブ" : "制限");
			RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/admin/user_status_result.jsp");
			dispatcher.forward(request, response);
		} catch (Exception e) {
			Logging.logError(LOGGER, UserSuspendServlet.class,
					String.format("ユーザーステータス更新処理に失敗しました。userIdParam=%s targetActive=%s", userIdParam, targetActive),
					e);
			ErrorHandler.handleError(request, response, e, DEFAULT_BACK_PATH, DEFAULT_BACK_LABEL);
		}
	}

}

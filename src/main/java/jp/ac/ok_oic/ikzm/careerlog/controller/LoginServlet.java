package jp.ac.ok_oic.ikzm.careerlog.controller;

import java.io.IOException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.slf4j.Logger;

import jp.ac.ok_oic.ikzm.careerlog.constants.SessionKeys;
import jp.ac.ok_oic.ikzm.careerlog.entity.User;
import jp.ac.ok_oic.ikzm.careerlog.form.AuthResult;
import jp.ac.ok_oic.ikzm.careerlog.service.AuthService;
import jp.ac.ok_oic.ikzm.careerlog.util.ErrorHandler;
import jp.ac.ok_oic.ikzm.careerlog.util.Logging;
import jp.ac.ok_oic.ikzm.careerlog.util.SessionRegistry;

/**
 * Google OAuth 認証フローのエントリーポイントとなるサーブレット。
 *
 * 認可コード付きのコールバックを処理し、セッションへユーザー情報を保持する。
 * 認証状態に応じてログイン画面、新規ユーザー登録画面、トップページへ遷移させる。
 *
 */
@WebServlet("/Login")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String DEFAULT_BACK_PATH = "/index.jsp";
	private static final String DEFAULT_BACK_LABEL = "ログイン画面に戻る";
	private static final Logger LOGGER = Logging.getLogger(LoginServlet.class);
	private static final Logger ACTION_LOGGER = Logging.actionLogger();

	/**
	 * Google OAuth 認証リクエストおよびコールバックを処理する。
	 *
	 * @param request  リクエスト情報
	 * @param response レスポンス情報
	 * @throws ServletException フォワード時に発生
	 * @throws IOException      リダイレクト時に発生
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		try {
			// リクエストパラメータの取得
			String code = request.getParameter("code");
			String action = request.getParameter("action");
			String state = request.getParameter("state");

			AuthService authService = new AuthService();

			// 初回アクセス時はログイン画面を表示
			if (action == null && (code == null || code.isEmpty())) {
				RequestDispatcher dispatcher = request.getRequestDispatcher("/index.jsp");
				dispatcher.forward(request, response);
				return;
			}

			// ログインボタン押下時はGoogle同意画面へリダイレクト
			if ("requestAuth".equals(action)) {
				HttpSession session = request.getSession();

				// CSRF対策のためstateをセッションに保存
				String stateValue = authService.generateState();
				session.setAttribute(SessionKeys.OAUTH_STATE, stateValue);

				String authUrl = authService.buildGoogleAuthUrl(stateValue);
				response.sendRedirect(authUrl);
				return;
			}

			// 認可コードを受け取った場合のログイン処理
			if (code != null && !code.isEmpty()) {
				HttpSession session = request.getSession();
				String expectedState = (String) session.getAttribute(SessionKeys.OAUTH_STATE);

				// CSRF検証（stateパラメータの照合）
				if (expectedState == null || state == null || !expectedState.equals(state)) {
					SecurityException csrfException = new SecurityException("AUTH-1020");
					Logging.logError(LOGGER, LoginServlet.class, "CSRF検証に失敗しました。", csrfException);
					ErrorHandler.handleError(request, response, csrfException, DEFAULT_BACK_PATH, DEFAULT_BACK_LABEL);
					return;
				}

				// state再利用防止のため破棄
				session.removeAttribute(SessionKeys.OAUTH_STATE);

				// Google認証処理の実行
				AuthResult result = authService.authenticate(code);

				// 認証失敗時はログイン画面に戻す
				if (result == null) {
					request.setAttribute("pageMessage", "認証に失敗しました。再度ログインしてください。");
					RequestDispatcher dispatcher = request.getRequestDispatcher("/index.jsp");
					dispatcher.forward(request, response);
					return;
				}

				// セッションへのログイン情報登録
				User loginUser = result.getUser();
				SessionRegistry.unregister(session);
				session.setAttribute(SessionKeys.LOGIN_USER, loginUser);
				SessionRegistry.register(session, loginUser.getUserId());
				ACTION_LOGGER.info("ユーザーID={}（メール={}）がログインしました。", loginUser.getUserId(), loginUser.getEmail());

				// 遷移先の決定（新規ユーザーか既存ユーザーかで分岐）
				if (result.isNewUser() || result.needsDepartment()) {
					if ("teacher".equals(loginUser.getUserType())) {
						response.sendRedirect(request.getContextPath() + "/NewTeacher");
					} else {
						response.sendRedirect(request.getContextPath() + "/NewUser");
					}
				} else {
					response.sendRedirect(request.getContextPath() + "/ReportList");
				}
				return;
			}

			// フォールバック（通常は到達しない）
			RequestDispatcher dispatcher = request.getRequestDispatcher("/index.jsp");
			dispatcher.forward(request, response);

		} catch (Exception e) {
			Logging.logError(LOGGER, LoginServlet.class, "ログイン処理で予期せぬエラーが発生しました。", e);
			ErrorHandler.handleError(request, response, e, DEFAULT_BACK_PATH, DEFAULT_BACK_LABEL);
		}
	}
}

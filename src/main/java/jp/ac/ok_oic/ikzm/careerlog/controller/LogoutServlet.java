package jp.ac.ok_oic.ikzm.careerlog.controller;

import java.io.IOException;

import org.slf4j.Logger;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import jp.ac.ok_oic.ikzm.careerlog.constants.SessionKeys;
import jp.ac.ok_oic.ikzm.careerlog.entity.User;
import jp.ac.ok_oic.ikzm.careerlog.util.Logging;
import jp.ac.ok_oic.ikzm.careerlog.util.SessionRegistry;

/**
 * ログアウトリクエストを受け付け、セッション破棄後にログアウト画面へ遷移させるサーブレット。
 */
@WebServlet("/Logout")
public class LogoutServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger ACTION_LOGGER = Logging.actionLogger();

	/**
	 * ログアウト処理を実行し、ログアウト画面へフォワードする。
	 *
	 * @param request  リクエスト情報
	 * @param response レスポンス情報
	 * @throws ServletException フォワード失敗時
	 * @throws IOException      フォワード失敗時
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// セッションの取得と破棄
		HttpSession session = request.getSession(false);
		if (session != null) {
			User loginUser = (User) session.getAttribute(SessionKeys.LOGIN_USER);

			// 操作ログの記録
			if (loginUser != null) {
				ACTION_LOGGER.info("ユーザーID={}（メール={}）がログアウトしました。", loginUser.getUserId(), loginUser.getEmail());
			} else {
				ACTION_LOGGER.info("ログイン情報不明のセッションID={}でログアウト処理を実行しました。", session.getId());
			}

			// セッションの無効化
			SessionRegistry.unregister(session);
			session.invalidate();
		}

		// ログアウト画面へ遷移
		RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/user/logout.jsp");
		dispatcher.forward(request, response);
	}

}

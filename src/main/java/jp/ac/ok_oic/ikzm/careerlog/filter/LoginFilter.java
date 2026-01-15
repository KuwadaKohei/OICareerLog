package jp.ac.ok_oic.ikzm.careerlog.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import jp.ac.ok_oic.ikzm.careerlog.constants.ErrorCode;
import jp.ac.ok_oic.ikzm.careerlog.constants.SessionKeys;
import jp.ac.ok_oic.ikzm.careerlog.entity.User;


/**
 * ログイン認証を確認するフィルター。
 * 原則すべてリクエストを遮断し、特定の除外パスのみ許可する
 */
@WebFilter("/*")
public class LoginFilter implements Filter {

	// ホワイトリスト
	private static final List<String> EXCLUDE_PATHS = Arrays.asList(
			"/Login",
			"/login.jsp",
			"/css",
			"/js",
			"/img",
			"/index.jsp");

	/**
	 * ログイン状態を確認し、未ログイン時は共通エラーへフォワードする。
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;

		// リクエストパスの取得
		String requestPath = httpRequest.getServletPath();

		// 除外パスの判定
		if (isExcluded(requestPath)) {
			chain.doFilter(request, response);
			return;
		}

		// ログイン状態の確認
		HttpSession session = httpRequest.getSession();
		boolean isLoggedIn = false;

		if (session != null) {
			User user = (User) session.getAttribute(SessionKeys.LOGIN_USER);
			if (user != null) {
				isLoggedIn = true;
			}
		}

		// ログイン状態に応じたルーティング
		if (isLoggedIn) {
			chain.doFilter(request, response);
		} else {
			forwardToError(httpRequest, httpResponse);
		}
	}

	/**
	 * 未ログインエラー画面へフォワードする。
	 */
	private void forwardToError(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setAttribute("errorCode", "AUTH-1010");
		request.setAttribute("error", ErrorCode.getMessage("AUTH-1010"));
		request.setAttribute("backUrl", request.getContextPath() + "/Login");
		request.setAttribute("backLabel", "ログイン画面に戻る");
		RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/error.jsp");
		dispatcher.forward(request, response);
	}

	/**
	 * リクエストパスが除外対象かどうかを判定する。
	 *
	 * @param path サーブレットパス
	 * @return true なら除外対象
	 */
	private boolean isExcluded(String path) {
		for (String exclude : EXCLUDE_PATHS) {
			if (path.equals(exclude) || path.startsWith(exclude + "/")) {
				return true;
			}
		}
		return false;
	}
}
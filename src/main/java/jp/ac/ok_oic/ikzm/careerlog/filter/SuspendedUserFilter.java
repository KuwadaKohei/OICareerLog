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
import jakarta.servlet.http.HttpSession;

import org.slf4j.Logger;

import jp.ac.ok_oic.ikzm.careerlog.constants.SessionKeys;
import jp.ac.ok_oic.ikzm.careerlog.entity.User;
import jp.ac.ok_oic.ikzm.careerlog.util.Logging;

/**
 * ログイン済みユーザーの利用制限を確認し、停止中のアカウントを遮断するフィルター。
 */
@WebFilter("/*")
public class SuspendedUserFilter implements Filter {

	private static final List<String> EXCLUDE_PATHS = Arrays.asList(
			"/Login",
			"/login.jsp",
			"/Logout",
			"/css",
			"/js",
			"/images",
			"/index.jsp");

	private static final String RESTRICTED_VIEW = "/WEB-INF/views/user/suspended.jsp";
	private static final Logger LOGGER = Logging.getLogger(SuspendedUserFilter.class);
	private static final Logger ACTION_LOGGER = Logging.actionLogger();

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest httpRequest = (HttpServletRequest) request;

		// リクエストパスの取得と除外判定
		String requestPath = httpRequest.getServletPath();

		if (isExcluded(requestPath)) {
			chain.doFilter(request, response);
			return;
		}

		// ログインユーザーのアクティブ状態確認
		HttpSession session = httpRequest.getSession(false);

		if (session != null) {
			User loginUser = (User) session.getAttribute(SessionKeys.LOGIN_USER);

			// 利用停止ユーザーのアクセス遮断
			if (loginUser != null && !loginUser.isActive()) {
				String restrictedUserName = loginUser.getName();
				String restrictedUserEmail = loginUser.getEmail();

				// セッション破棄
				session.invalidate();

				// 制限画面へのフォワード準備
				request.setAttribute("restrictedUserName", restrictedUserName);
				request.setAttribute("restrictedUserEmail", restrictedUserEmail);
				request.setAttribute("logoutUrl", httpRequest.getContextPath() + "/Logout");
				request.setAttribute("forcedLogout", Boolean.TRUE);

				// ログ記録
				ACTION_LOGGER.info("利用停止ユーザーID={}（メール={}）を強制ログアウトしました。", loginUser.getUserId(), loginUser.getEmail());
				LOGGER.info("利用停止ユーザーのアクセスを遮断しました。path={}", requestPath);

				RequestDispatcher dispatcher = request.getRequestDispatcher(RESTRICTED_VIEW);
				dispatcher.forward(request, response);
				return;
			}
		}

		chain.doFilter(request, response);
	}

	private boolean isExcluded(String path) {
		for (String exclude : EXCLUDE_PATHS) {
			if (path.equals(exclude) || path.startsWith(exclude + "/")) {
				return true;
			}
		}
		return false;
	}
}

package jp.ac.ok_oic.ikzm.careerlog.filter;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import jp.ac.ok_oic.ikzm.careerlog.constants.SessionKeys;
import jp.ac.ok_oic.ikzm.careerlog.entity.User;
import jp.ac.ok_oic.ikzm.careerlog.util.ErrorHandler;

/**
 * 管理者権限を確認するフィルター。
 * 管理者でないユーザーからのアクセスを拒否する。
 */
@WebFilter({ "/User/Suspend" })
public class AdminFilter implements Filter {

	/**
	 * 管理者セッションを確認し、未管理者なら共通エラー画面へ遷移させる。
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		HttpSession session = httpRequest.getSession();

		// 管理者権限の確認
		boolean isAdmin = false;

		if (session != null) {
			User user = (User) session.getAttribute(SessionKeys.LOGIN_USER);
			if (user != null && user.isAdmin()) {
				isAdmin = true;
			}
		}

		// 権限に応じたルーティング
		if (isAdmin) {
			chain.doFilter(request, response);
		} else {
			forwardToError(httpRequest, httpResponse);
		}
	}

	/**
	 * 権限エラー画面へフォワードする。
	 */
	private void forwardToError(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		ErrorHandler.handleError(request, response, new Exception("AUTH-1011"), "/ReportList", "メインメニューへ戻る");
	}
}
package jp.ac.ok_oic.ikzm.careerlog.controller;

import java.io.IOException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import jp.ac.ok_oic.ikzm.careerlog.constants.SessionKeys;
import jp.ac.ok_oic.ikzm.careerlog.entity.User;
import jp.ac.ok_oic.ikzm.careerlog.service.PostService;
import jp.ac.ok_oic.ikzm.careerlog.util.ErrorHandler;
import jp.ac.ok_oic.ikzm.careerlog.viewmodel.SearchResultViewModel;

/**
 * ログインユーザー自身の投稿を検索し、自分の投稿一覧画面へフォワードするサーブレット。
 */
@WebServlet("/MyReports")
public class MyPostsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String DEFAULT_BACK_PATH = "/MyReports";
	private static final String DEFAULT_BACK_LABEL = "自分の投稿一覧へ戻る";

	/**
	 * 投稿一覧を取得し、自分の投稿画面へ表示する。
	 *
	 * @param request  リクエスト情報
	 * @param response レスポンス情報
	 * @throws ServletException フォワード失敗時
	 * @throws IOException      フォワード失敗時
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// リクエストパラメータの取得
		String searchWord = request.getParameter("searchWord");

		// ログインユーザー情報の取得
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute(SessionKeys.LOGIN_USER);

		// 投稿データの取得
		PostService postService = new PostService();

		try {
			SearchResultViewModel viewModel = postService.findAllByUser(user.getUserId(), searchWord);

			// 结果をリクエストに設定して画面へフォワード
			request.setAttribute("postList", viewModel);
			RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/post/my_list.jsp");
			dispatcher.forward(request, response);

		} catch (Exception e) {
			ErrorHandler.handleError(request, response, e, DEFAULT_BACK_PATH, DEFAULT_BACK_LABEL);
		}
	}
}

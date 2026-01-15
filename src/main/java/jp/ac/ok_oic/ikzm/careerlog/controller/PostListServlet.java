package jp.ac.ok_oic.ikzm.careerlog.controller;

import java.io.IOException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jp.ac.ok_oic.ikzm.careerlog.service.PostService;
import jp.ac.ok_oic.ikzm.careerlog.util.ErrorHandler;
import jp.ac.ok_oic.ikzm.careerlog.viewmodel.SearchResultViewModel;

/**
 * 投稿一覧を取得し、トップページを表示するサーブレット。
 */
@WebServlet({ "/ReportList", "/Home" })
public class PostListServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String DEFAULT_BACK_PATH = "/ReportList";
	private static final String DEFAULT_BACK_LABEL = "投稿一覧へ戻る";

	/**
	 * 投稿一覧または検索結果を取得し、トップページへフォワードする。
	 *
	 * @param request  リクエスト情報
	 * @param response レスポンス情報
	 * @throws ServletException フォワード失敗時
	 * @throws IOException      フォワード失敗時
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		try {
			// 検索パラメータの取得
			String searchWord = request.getParameter("searchWord");

			// 投稿データの取得
			PostService postService = new PostService();
			SearchResultViewModel viewModel;

			if (searchWord != null && !searchWord.isBlank()) {
				viewModel = postService.search(searchWord);
			} else {
				viewModel = postService.findAll();
			}

			// 结果をリクエストに設定して画面へフォワード
			request.setAttribute("postList", viewModel);
			RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/post/list.jsp");
			dispatcher.forward(request, response);

		} catch (Exception e) {
			ErrorHandler.handleError(request, response, e, DEFAULT_BACK_PATH, DEFAULT_BACK_LABEL);
		}
	}

}

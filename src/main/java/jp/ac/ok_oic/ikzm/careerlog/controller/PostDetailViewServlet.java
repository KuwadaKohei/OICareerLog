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
import jp.ac.ok_oic.ikzm.careerlog.viewmodel.PostViewModel;

/**
 * 投稿の詳細情報を取得し、詳細画面へフォワードするサーブレット。
 */
@WebServlet("/ReportView")
public class PostDetailViewServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String DEFAULT_BACK_PATH = "/ReportList";
	private static final String DEFAULT_BACK_LABEL = "投稿一覧へ戻る";

	/**
	 * 投稿IDから詳細情報を取得し、詳細画面を表示する。
	 *
	 * @param request  リクエスト情報
	 * @param response レスポンス情報
	 * @throws ServletException フォワード失敗時
	 * @throws IOException      リダイレクト失敗時
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// 投稿IDのパース
		String strId = request.getParameter("postId");
		int postId = 0;

		try {
			postId = Integer.parseInt(strId);
		} catch (NumberFormatException e) {
			response.sendRedirect(request.getContextPath() + "/ReportList");
			return;
		}

		// 投稿詳細の取得
		PostService postService = new PostService();

		try {
			PostViewModel viewModel = postService.getDetail(postId);

			if (viewModel == null) {
				request.setAttribute("pageMessage", "指定された投稿は見つかりませんでした。");
			}

			// 詳細画面へフォワード
			request.setAttribute("postDetail", viewModel);
			RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/post/detail.jsp");
			dispatcher.forward(request, response);

		} catch (Exception e) {
			ErrorHandler.handleError(request, response, e, DEFAULT_BACK_PATH, DEFAULT_BACK_LABEL);
		}
	}
}

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
import jp.ac.ok_oic.ikzm.careerlog.entity.Post;
import jp.ac.ok_oic.ikzm.careerlog.entity.User;
import jp.ac.ok_oic.ikzm.careerlog.service.PostManageService;
import jp.ac.ok_oic.ikzm.careerlog.service.PostService;
import jp.ac.ok_oic.ikzm.careerlog.util.ErrorHandler;
import jp.ac.ok_oic.ikzm.careerlog.util.Logging;
import jp.ac.ok_oic.ikzm.careerlog.viewmodel.PostViewModel;

/**
 * 投稿削除リクエストを管理し、権限制御を行いながら対象投稿を削除するサーブレット。
 */
@WebServlet("/DeleteReport")
public class PostDeleteServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String DEFAULT_BACK_PATH = "/ReportList";
	private static final String DEFAULT_BACK_LABEL = "投稿一覧へ戻る";
	private static final Logger LOGGER = Logging.getLogger(PostDeleteServlet.class);
	private static final Logger ACTION_LOGGER = Logging.actionLogger();

	/**
	 * 削除確認画面の表示および削除処理の本実行を行う。
	 *
	 * @param request  リクエスト情報
	 * @param response レスポンス情報
	 * @throws ServletException フォワード失敗時
	 * @throws IOException      リダイレクト失敗時
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		try {
			// リクエストパラメータの取得
			String action = request.getParameter("action");
			String postIdString = request.getParameter("postId");

			// 投稿IDのバリデーション
			if (postIdString == null || postIdString.isBlank()) {
				forwardError(request, response, "SYS-9000");
				return;
			}

			int postId;
			try {
				postId = Integer.parseInt(postIdString);
			} catch (NumberFormatException e) {
				Logging.logError(LOGGER, PostDeleteServlet.class,
						String.format("投稿削除リクエストのpostIdが不正です。postId=%s", postIdString), e);
				forwardError(request, response, "SYS-9000");
				return;
			}

			// ログインユーザーの取得
			HttpSession session = request.getSession();
			User user = (User) session.getAttribute(SessionKeys.LOGIN_USER);

			// 対象投稿の取得と存在確認
			PostService postService = new PostService();
			Post post = postService.findById(postId);

			if (post == null) {
				forwardError(request, response, "DB-0001");
				return;
			}

			// 権限チェック
			boolean authorized = post.getUserId() == user.getUserId() || user.isAdmin();

			if (!authorized) {
				session.invalidate();
				forwardError(request, response, "AUTH-1011");
				return;
			}

			PostManageService manageService = new PostManageService();

			// 確認画面の表示
			if ("confirm".equals(action)) {
				PostViewModel detailView = postService.getDetail(postId);

				if (detailView == null) {
					forwardError(request, response, "DB-0010");
					return;
				}

				request.setAttribute("postDetail", detailView);
				RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/post/delete_confirm.jsp");
				dispatcher.forward(request, response);
				return;
			}

			// 削除の実行
			if ("execute".equals(action)) {
				manageService.delete(postId, user.getUserId());
				ACTION_LOGGER.info("ユーザーID={} が投稿ID={} を削除しました。", user.getUserId(), postId);

				RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/post/deleted.jsp");
				dispatcher.forward(request, response);
				return;
			}

			forwardError(request, response, "SYS-9000");

		} catch (Exception e) {
			Logging.logError(LOGGER, PostDeleteServlet.class, "投稿削除処理で予期せぬエラーが発生しました。", e);
			ErrorHandler.handleError(request, response, e, DEFAULT_BACK_PATH, DEFAULT_BACK_LABEL);
		}
	}

	/**
	 * 異常系を検知した際に共通エラー画面へ誘導する。
	 *
	 * @param request  リクエスト情報
	 * @param response レスポンス情報
	 * @param code     表示したいエラーコード
	 * @throws ServletException フォワード失敗時
	 * @throws IOException      フォワード失敗時
	 */
	private void forwardError(HttpServletRequest request, HttpServletResponse response, String code)
			throws ServletException, IOException {
		Logging.logError(LOGGER, PostDeleteServlet.class,
				String.format("投稿削除リクエストが異常値のためエラー画面へ遷移します。code=%s", code),
				null);
		ErrorHandler.handleError(request, response, new Exception(code), DEFAULT_BACK_PATH, DEFAULT_BACK_LABEL);
	}

}

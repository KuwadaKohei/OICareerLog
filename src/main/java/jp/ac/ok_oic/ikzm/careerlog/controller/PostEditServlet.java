package jp.ac.ok_oic.ikzm.careerlog.controller;

import java.io.IOException;
import java.util.List;

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
import jp.ac.ok_oic.ikzm.careerlog.form.PostForm;
import jp.ac.ok_oic.ikzm.careerlog.service.PostManageService;
import jp.ac.ok_oic.ikzm.careerlog.service.PostService;
import jp.ac.ok_oic.ikzm.careerlog.util.ErrorHandler;
import jp.ac.ok_oic.ikzm.careerlog.util.Logging;
import jp.ac.ok_oic.ikzm.careerlog.util.ModelConverter;
import jp.ac.ok_oic.ikzm.careerlog.util.PostFormMasterDataHelper;

/**
 * 投稿編集フォームの表示と更新処理を担当するサーブレット。
 */
@WebServlet("/ReportEdit")
public class PostEditServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String DEFAULT_BACK_PATH = "/ReportList";
	private static final String DEFAULT_BACK_LABEL = "投稿一覧へ戻る";


	private static final Logger LOGGER = Logging.getLogger(PostEditServlet.class);
	private static final Logger ACTION_LOGGER = Logging.actionLogger();

	/**
	 * 指定された投稿を編集できるフォームを表示する。
	 *
	 * @param request  リクエスト情報
	 * @param response レスポンス情報
	 * @throws ServletException フォワード失敗時
	 * @throws IOException      リダイレクト失敗時
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession(false);
		User user = (User) session.getAttribute(SessionKeys.LOGIN_USER);

		int postId = ModelConverter.parseIntOrDefault(request.getParameter("postId"), -1);
		if (postId <= 0) {
			response.sendRedirect(request.getContextPath() + DEFAULT_BACK_PATH);
			return;
		}

		PostService postService = new PostService();
		PostForm form;
		try {
			// 権限チェック込みのフォーム取得
			form = postService.getFormForEdit(postId, user.getUserId());
		} catch (Exception e) {
			Logging.logError(LOGGER, PostEditServlet.class,
					String.format("投稿編集フォームの取得に失敗しました。postId=%d userId=%d", postId, user.getUserId()),
					e);
			ErrorHandler.handleError(request, response, e, DEFAULT_BACK_PATH, DEFAULT_BACK_LABEL);
			return;
		}

		if (form == null) {
			request.setAttribute("pageMessage", "指定された投稿が見つかりませんでした。");
			response.sendRedirect(request.getContextPath() + "/ReportList");
			return;
		}

		request.setAttribute("postForm", form);
		if (!ensureMasterData(request, response, postId)) {
			// マスタ取得に失敗した場合はエラー画面へ遷移済み
			return;
		}

		RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/post/edit.jsp");
		dispatcher.forward(request, response);
	}

	/**
	 * 編集フォームのバリデーション、確認画面表示、更新処理を実行する。
	 *
	 * @param request  リクエスト情報
	 * @param response レスポンス情報
	 * @throws ServletException フォワード失敗時
	 * @throws IOException      リダイレクト失敗時
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession(false);
		User user = (session != null) ? (User) session.getAttribute(SessionKeys.LOGIN_USER) : null;
		if (user == null) {
			response.sendRedirect(request.getContextPath() + "/Login");
			return;
		}

		String action = request.getParameter("action");
		PostForm form = ModelConverter.toPostForm(request);
		int postId = ModelConverter.parseInteger(request.getParameter("postId"));
		if (postId <= 0) {
			response.sendRedirect(request.getContextPath() + DEFAULT_BACK_PATH);
			return;
		}
		form.setPostId(postId);
		List<String> errorCodes = form.validate();

		if ("edit".equals(action)) {
			if (!ensureMasterData(request, response, postId)) {
				return;
			}
			// 入力画面を再表示
			request.setAttribute("postForm", form);
			RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/post/edit.jsp");
			dispatcher.forward(request, response);
			return;
		}

		if ("confirm".equals(action)) {
			if (!errorCodes.isEmpty()) {
				// 入力エラー時はJSP側でメッセージ表示
				request.setAttribute("errorCodes", errorCodes);
				request.setAttribute("errorMessages", ModelConverter.resolveErrorMessages(errorCodes));
				request.setAttribute("postForm", form);
				if (!ensureMasterData(request, response, postId)) {
					return;
				}
				RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/post/edit.jsp");
				dispatcher.forward(request, response);
				return;
			}
			if (!ensureMasterData(request, response, postId)) {
				return;
			}
			request.setAttribute("postForm", form);
			try {
			PostFormMasterDataHelper.loadPosterDepartmentName(request, user.getDepartmentId());
			} catch (Exception e) {
				Logging.logError(LOGGER, PostEditServlet.class,
						String.format("投稿編集の確認画面で学科名取得に失敗しました。postId=%d userId=%d", postId, user.getUserId()),
						e);
				String editPath = String.format("/ReportEdit?postId=%d", postId);
				ErrorHandler.handleError(request, response, e, editPath, "投稿編集へ戻る");
				return;
			}
			RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/post/edit_confirm.jsp");
			dispatcher.forward(request, response);
			return;
		}

		if ("update".equals(action)) {
			if (!errorCodes.isEmpty()) {
				request.setAttribute("errorCodes", errorCodes);
				request.setAttribute("errorMessages", ModelConverter.resolveErrorMessages(errorCodes));
				request.setAttribute("postForm", form);
				if (!ensureMasterData(request, response, postId)) {
					return;
				}
				RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/post/edit.jsp");
				dispatcher.forward(request, response);
				return;
			}
			// 更新実行
			PostManageService manageService = new PostManageService();
			boolean isSuccess;
			try {
				isSuccess = manageService.update(
						form,
						user.getUserId(),
						user.getDepartmentId(),
						user.getGrade());
			} catch (Exception e) {
				Logging.logError(LOGGER, PostEditServlet.class,
						String.format("投稿更新処理に失敗しました。postId=%d userId=%d", form.getPostId(), user.getUserId()),
						e);
				String editPath = String.format("/ReportEdit?postId=%d", form.getPostId());
				ErrorHandler.handleError(request, response, e, editPath, "投稿編集へ戻る");
				return;
			}

			if (isSuccess) {
				String redirectUrl = String.format("%s/ReportView?postId=%d&back=myPosts",
						request.getContextPath(),
						form.getPostId());
				ACTION_LOGGER.info("ユーザーID={} が投稿ID={} を更新しました。", user.getUserId(), form.getPostId());
				response.sendRedirect(redirectUrl);
			} else {
				request.setAttribute("pageMessage", "更新中にエラーが発生しました。");
				request.setAttribute("postForm", form);
				if (!ensureMasterData(request, response, postId)) {
					return;
				}
				RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/post/edit.jsp");
				dispatcher.forward(request, response);
			}
			return;
		}

		response.sendRedirect(request.getContextPath() + DEFAULT_BACK_PATH);
	}

	/**
	 * 画面表示に必要なマスタデータを取得する。
	 *
	 * @param request  リクエスト情報
	 * @param response レスポンス情報
	 * @param postId   処理対象の投稿ID（エラー時の戻り先計算に利用）
	 * @return マスタ取得に成功した場合は true
	 * @throws ServletException フォワード失敗時
	 * @throws IOException      フォワード失敗時
	 */
	private boolean ensureMasterData(HttpServletRequest request, HttpServletResponse response, int postId)
			throws ServletException, IOException {
		try {
			PostFormMasterDataHelper.loadMasterData(request);
			return true;
		} catch (Exception e) {
			Logging.logError(LOGGER, PostEditServlet.class,
					String.format("投稿編集フォーム表示に必要なマスタデータ取得に失敗しました。postId=%d", postId),
					e);
			String editPath = String.format("/ReportEdit?postId=%d", postId);
			ErrorHandler.handleError(request, response, e, editPath, "投稿編集へ戻る");
			return false;
		}
	}

}

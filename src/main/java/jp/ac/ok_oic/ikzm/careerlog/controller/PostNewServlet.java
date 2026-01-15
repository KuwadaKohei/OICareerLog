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
import jp.ac.ok_oic.ikzm.careerlog.util.ErrorHandler;
import jp.ac.ok_oic.ikzm.careerlog.util.Logging;
import jp.ac.ok_oic.ikzm.careerlog.util.ModelConverter;
import jp.ac.ok_oic.ikzm.careerlog.util.PostFormMasterDataHelper;

/**
 * 新規投稿フォームの表示と投稿登録処理を担当するサーブレット。
 *
 * @see jp.ac.ok_oic.ikzm.careerlog.service.PostManageService
 * @see jp.ac.ok_oic.ikzm.careerlog.util.PostFormMasterDataHelper
 */
@WebServlet("/ReportNew")
public class PostNewServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/** エラー発生時の戻り先パス */
	private static final String DEFAULT_BACK_PATH = "/ReportNew";
	/** エラー発生時の戻り先ボタンラベル */
	private static final String DEFAULT_BACK_LABEL = "投稿フォームに戻る";

	/** システムログ用ロガー */
	private static final Logger LOGGER = Logging.getLogger(PostNewServlet.class);
	/** 操作ログ用ロガー（ユーザー操作の記録） */
	private static final Logger ACTION_LOGGER = Logging.actionLogger();

	/**
	 * 空の投稿フォームを生成し、新規投稿画面を表示する。
	 *
	 * <p>投稿一覧画面からのリンクで呼び出されます。</p>
	 *
	 * @param request  リクエスト情報
	 * @param response レスポンス情報
	 * @throws ServletException フォワード失敗時
	 * @throws IOException      フォワード失敗時
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		PostForm form = new PostForm();
		// 空のフォームで投稿画面を表示
		forwardToForm(request, response, form);
	}

	/**
	 * 入力内容の検証、確認画面表示、投稿登録処理を実行する。
	 *
	 * <p>actionパラメータに応じて処理を分岐します。</p>
	 * @param request  リクエスト情報
	 * @param response レスポンス情報
	 * @throws ServletException フォワード失敗時
	 * @throws IOException      リダイレクト失敗時
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession();
		User user = (User) session.getAttribute(SessionKeys.LOGIN_USER);

		String action = request.getParameter("action");
		PostForm form = ModelConverter.toPostForm(request);
		List<String> errorCodes = form.validate();

		if ("confirm".equals(action)) {
			if (!errorCodes.isEmpty()) {
				// 入力エラーはそのままフォームへ戻す
				forwardToFormWithErrors(request, response, form, errorCodes);
				return;
			}

			request.setAttribute("postForm", form);
			if (!ensureMasterData(request, response)) {
				return;
			}
			try {
				// 投稿者の学科名を表示用に取得
			PostFormMasterDataHelper.loadPosterDepartmentName(request, user.getDepartmentId());
			} catch (Exception e) {
				Logging.logError(LOGGER, PostNewServlet.class,
						"投稿確認画面で学科名取得に失敗しました。", e);
				ErrorHandler.handleError(request, response, e, DEFAULT_BACK_PATH, DEFAULT_BACK_LABEL);
				return;
			}

			// 確認画面へフォワード
			RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/post/new_confirm.jsp");
			dispatcher.forward(request, response);
			return;
		}

		if ("edit".equals(action)) {
			forwardToForm(request, response, form);
			return;
		}

		if ("create".equals(action)) {
			if (!errorCodes.isEmpty()) {
				// バリデーションを通過していなければ再入力
				forwardToFormWithErrors(request, response, form, errorCodes);
				return;
			}

			PostManageService manageService = new PostManageService();
			boolean isSuccess;
			try {
				// 投稿作成を実行
				isSuccess = manageService.create(
						form,
						user.getUserId(),
						user.getDepartmentId(),
						user.getGrade());
			} catch (Exception e) {
				Logging.logError(LOGGER, PostNewServlet.class,
						String.format("投稿登録処理に失敗しました。userId=%d postId=%s", user.getUserId(), form.getPostId()),
						e);
				ErrorHandler.handleError(request, response, e, DEFAULT_BACK_PATH, DEFAULT_BACK_LABEL);
				return;
			}

			if (isSuccess) {
				ACTION_LOGGER.info("ユーザーID={} が新規投稿を作成しました。投稿ID={}", user.getUserId(), form.getPostId());
				// 正常終了時は一覧へリダイレクト
				response.sendRedirect(request.getContextPath() + "/ReportList");
				return;
			}

			request.setAttribute("pageMessage", "登録処理中にエラーが発生しました。");
			forwardToForm(request, response, form);
			return;
		}

		// action不正時は入力画面へ戻す
		doGet(request, response);
	}

	/**
	 * 入力画面に必要なデータを用意したうえでフォームJSPへフォワードする。
	 *
	 * @param request  リクエスト情報
	 * @param response レスポンス情報
	 * @param form     表示したいフォーム内容
	 * @throws ServletException フォワード失敗時
	 * @throws IOException      フォワード失敗時
	 */
	private void forwardToForm(HttpServletRequest request, HttpServletResponse response, PostForm form)
			throws ServletException, IOException {
		request.setAttribute("postForm", form);
		if (!ensureMasterData(request, response)) {
			return;
		}
		RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/post/new.jsp");
		dispatcher.forward(request, response);
	}

	/**
	 * バリデーションエラーを付与した状態で入力画面へ戻す。
	 *
	 * @param request    リクエスト情報
	 * @param response   レスポンス情報
	 * @param form       入力内容
	 * @param errorCodes 発生したエラーコード
	 * @throws ServletException フォワード失敗時
	 * @throws IOException      フォワード失敗時
	 */
	private void forwardToFormWithErrors(HttpServletRequest request, HttpServletResponse response, PostForm form,
			List<String> errorCodes) throws ServletException, IOException {
		request.setAttribute("errorCodes", errorCodes);
		request.setAttribute("errorMessages", ModelConverter.resolveErrorMessages(errorCodes));
		forwardToForm(request, response, form);
	}

	/**
	 * 画面表示に必要なマスタデータを読み込む。取得失敗時は共通エラーへ遷移する。
	 *
	 * @param request  リクエスト情報
	 * @param response レスポンス情報
	 * @return 正常に取得できた場合は true
	 * @throws ServletException フォワード失敗時
	 * @throws IOException      フォワード失敗時
	 */
	private boolean ensureMasterData(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			PostFormMasterDataHelper.loadMasterData(request);
			return true;
		} catch (Exception e) {
			Logging.logError(LOGGER, PostNewServlet.class, "投稿フォーム表示に必要なマスタデータ取得に失敗しました。", e);
			handleDataAccessError(request, response, e);
			return false;
		}
	}

	/**
	 * マスタ取得など事前処理で例外が出た際の共通処理。
	 *
	 * @param request  リクエスト情報
	 * @param response レスポンス情報
	 * @param e        発生した例外
	 * @throws ServletException フォワード失敗時
	 * @throws IOException      フォワード失敗時
	 */
	private void handleDataAccessError(HttpServletRequest request, HttpServletResponse response, Exception e)
			throws ServletException, IOException {
		Logging.logError(LOGGER, PostNewServlet.class, "投稿フォームの前処理でデータアクセスエラーが発生しました。", e);
		ErrorHandler.handleError(request, response, e, DEFAULT_BACK_PATH, DEFAULT_BACK_LABEL);
	}

}

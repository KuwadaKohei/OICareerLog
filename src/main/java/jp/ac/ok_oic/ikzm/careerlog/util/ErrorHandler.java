package jp.ac.ok_oic.ikzm.careerlog.util;

import java.io.IOException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jp.ac.ok_oic.ikzm.careerlog.constants.ErrorCode;

/**
 * エラー処理の共通ユーティリティクラス。
 *
 * このクラスは、サーブレットやフィルターで発生した例外を統一的に処理し、
 * ユーザーにわかりやすいエラー画面を表示するための機能を提供します。
 *
 * 主な責務：
 * 例外からエラーコードを抽出
 * エラーコードに対応するメッセージを設定
 * エラー画面（error.jsp）へのフォワード
 * 戻り先URL・ラベルの設定
 *
 * @see jp.ac.ok_oic.ikzm.careerlog.constants.ErrorCode
 */
public class ErrorHandler {

	/**
	 * プライベートコンストラクタ。ユーティリティクラスのためインスタンス化を禁止。
	 */
	private ErrorHandler() {
	}

	/**
	 * エラー画面へフォワードする。
	 *
	 * 例外のメッセージからエラーコードを抽出し、対応するユーザー向けメッセージと共に
	 * エラー画面（/WEB-INF/error.jsp）へフォワードします。
	 *
	 * リクエスト属性に以下の値を設定します：
	 * errorCode: エラーコード（例: "DB-0001"）
	 * error: ユーザー向けエラーメッセージ
	 * backUrl: 戻り先URL（既存設定がない場合のみ）
	 * backLabel: 戻り先ボタンのラベル（既存設定がない場合のみ）
	 *
	 * @param request          リクエスト情報
	 * @param response         レスポンス情報
	 * @param e                発生した例外（getMessage()でエラーコードを取得）
	 * @param defaultBackPath  デフォルトの戻り先パス（コンテキストパスからの相対パス、例: "/ReportList"）
	 * @param defaultBackLabel デフォルトの戻り先ラベル（例: "投稿一覧へ戻る"）
	 * @throws ServletException エラー画面へのフォワード時に発生
	 * @throws IOException      エラー画面へのフォワード時に発生
	 */
	public static void handleError(HttpServletRequest request, HttpServletResponse response,
			Exception e, String defaultBackPath, String defaultBackLabel)
			throws ServletException, IOException {

		// 例外メッセージからエラーコードを取得（未定義の場合はデフォルトコード）
		String code = ErrorCode.getCode(e.getMessage());
		request.setAttribute("errorCode", code);
		request.setAttribute("error", ErrorCode.getMessage(code));

		// 戻り先URLとラベルを設定（既に設定済みの場合は上書きしない）
		if (request.getAttribute("backUrl") == null) {
			request.setAttribute("backUrl", request.getContextPath() + defaultBackPath);
		}
		if (request.getAttribute("backLabel") == null) {
			request.setAttribute("backLabel", defaultBackLabel);
		}

		// エラー画面へフォワード
		RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/error.jsp");
		dispatcher.forward(request, response);
	}
}

package jp.ac.ok_oic.ikzm.careerlog.util;

import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jp.ac.ok_oic.ikzm.careerlog.entity.Department;
import jp.ac.ok_oic.ikzm.careerlog.entity.ExamContent;
import jp.ac.ok_oic.ikzm.careerlog.entity.ExamDetailOption;
import jp.ac.ok_oic.ikzm.careerlog.entity.SubmissionMethod;
import jp.ac.ok_oic.ikzm.careerlog.service.PostService;

/**
 * 投稿フォームで必要なマスタデータを管理するヘルパークラス。
 *
 * @see jp.ac.ok_oic.ikzm.careerlog.controller.PostNewServlet
 * @see jp.ac.ok_oic.ikzm.careerlog.controller.PostEditServlet
 */
public class PostFormMasterDataHelper {

	/**
	 * インスタンス化を禁止。
	 */
	private PostFormMasterDataHelper() {
	}

	/**
	 * 投稿フォームで利用するマスタ群をリクエストスコープへ展開する。
	 *
	 * 応募方法、試験内容、試験詳細オプションをデータベースから取得し、
	 * JSPで使用できるようリクエスト属性に設定。
	 * また、IDから名称を取得するためのMapも併せて設定する。
	 *
	 * @param request リクエスト情報
	 * @throws Exception PostServiceからの例外を透過（主にDB接続エラー）
	 */
	public static void loadMasterData(HttpServletRequest request) throws Exception {
		PostService postService = new PostService();

		// マスタデータを取得
		List<SubmissionMethod> methods = postService.getSubmissionMethods();
		List<ExamContent> examContents = postService.getExamContents();
		Map<Integer, List<ExamDetailOption>> detailOptionsMap = postService.getExamDetailOptionsMap();

		// フォーム選択肢用のリストを設定
		request.setAttribute("submissionMethods", methods);
		request.setAttribute("examContents", examContents);
		request.setAttribute("examDetailOptionsMap", detailOptionsMap);

		// JavaScript用にJSON形式のデータも設定
		request.setAttribute("examDetailOptionsJson", convertToJson(detailOptionsMap));

		// 確認画面で名称表示するためのマップを設定
		request.setAttribute("submissionMethodMap", ModelConverter.toSubmissionMethodMap(methods));
		request.setAttribute("examContentMap", ModelConverter.toExamContentMap(examContents));
	}

	/**
	 * 試験詳細オプションMapをJSON文字列に変換する。
	 *
	 * @param detailOptionsMap 試験詳細オプションのMap
	 * @return JSON文字列
	 */
	private static String convertToJson(Map<Integer, List<ExamDetailOption>> detailOptionsMap) {
		StringBuilder json = new StringBuilder("{");
		boolean firstEntry = true;

		for (Map.Entry<Integer, List<ExamDetailOption>> entry : detailOptionsMap.entrySet()) {
			if (!firstEntry) {
				json.append(",");
			}
			firstEntry = false;

			json.append("\"").append(entry.getKey()).append("\":[");

			boolean firstOption = true;
			for (ExamDetailOption option : entry.getValue()) {
				if (!firstOption) {
					json.append(",");
				}
				firstOption = false;

				json.append("{");
				json.append("\"optionId\":").append(option.getOptionId()).append(",");
				json.append("\"contentId\":").append(option.getContentId()).append(",");
				json.append("\"optionText\":\"").append(escapeJson(option.getOptionText())).append("\",");
				json.append("\"displayOrder\":").append(option.getDisplayOrder());
				json.append("}");
			}

			json.append("]");
		}

		json.append("}");
		return json.toString();
	}

	/**
	 * JSON文字列用にエスケープする。
	 *
	 * @param value エスケープする文字列
	 * @return エスケープ済み文字列
	 */
	private static String escapeJson(String value) {
		if (value == null) {
			return "";
		}
		return value
			.replace("\\", "\\\\")
			.replace("\"", "\\\"")
			.replace("\n", "\\n")
			.replace("\r", "\\r")
			.replace("\t", "\\t");
	}

	/**
	 * ユーザーの学科名をリクエストスコープに設定する。
	 *
	 * 確認画面で投稿者の学科名を表示するために使用します。
	 * 学科が見つからない場合は「未設定」を設定します。
	 *
	 * @param request リクエスト情報
	 * @param departmentId 学科ID（UserのdepartmentIdを渡す）
	 * @throws Exception PostServiceからの例外を透過（主にDB接続エラー）
	 */
	public static void loadPosterDepartmentName(HttpServletRequest request, int departmentId) throws Exception {
		PostService postService = new PostService();
		List<Department> departments = postService.getDepartments();
		String deptName = ModelConverter.resolveDepartmentName(departmentId, departments, "未設定");
		request.setAttribute("posterDepartmentName", deptName);
	}
}

package jp.ac.ok_oic.ikzm.careerlog.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

import jp.ac.ok_oic.ikzm.careerlog.constants.ErrorCode;
import jp.ac.ok_oic.ikzm.careerlog.entity.Post;
import jp.ac.ok_oic.ikzm.careerlog.entity.PostDetail;
import jp.ac.ok_oic.ikzm.careerlog.entity.PostExamSelection;
import jp.ac.ok_oic.ikzm.careerlog.form.PostForm;
import jp.ac.ok_oic.ikzm.careerlog.viewmodel.PostViewModel;
import jp.ac.ok_oic.ikzm.careerlog.viewmodel.SearchResultViewModel;

/**
 * アプリケーション全体で使用するデータ変換ユーティリティクラス。
 *
 * このクラスは以下の変換処理を提供する：
 *
 * 1. 表示系変換（DTO → ViewModel）
 * - toSearchResultViewModel - 投稿一覧用のビューモデル変換
 * - toPostViewModel - 投稿詳細用のビューモデル変換
 *
 * 2. 入力系変換（Form → DTO）
 * - toPost - フォームからPost DTOへの変換
 * - toPostDetail - フォームからPostDetail DTOへの変換
 *
 * 3. ユーティリティ変換
 * - parseInteger, parseIntOrDefault - 安全な数値パース
 * - resolveErrorMessages - エラーコードからメッセージへの変換
 * - toSubmissionMethodMap, toExamContentMap - List→Map変換
 * - resolveDepartmentName - 学科ID→学科名の解決
 *
 * @see jp.ac.ok_oic.ikzm.careerlog.form.PostForm
 * @see jp.ac.ok_oic.ikzm.careerlog.viewmodel.PostViewModel
 */
public class ModelConverter {

	/** 日付フォーマット（yyyy-MM-dd形式） */
	private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	/**
	 * 投稿DTOリストを簡易検索結果ビューモデルに変換する。
	 *
	 * @param posts 投稿DTOリスト
	 * @return ビューモデル
	 */
	public static SearchResultViewModel toSearchResultViewModel(List<Post> posts) {
		return toSearchResultViewModel(posts, Collections.emptyMap(), Collections.emptyMap());
	}

	/**
	 * 投稿DTOリストを検索結果ビューモデルに変換する。学科名と詳細情報を同時に埋め込む。
	 *
	 * @param posts               投稿DTOリスト
	 * @param departmentCourseMap departmentId → 学科名
	 * @param postDetailMap       postId → 投稿詳細
	 * @return ビューモデル
	 */
	public static SearchResultViewModel toSearchResultViewModel(
			List<Post> posts,
			Map<Integer, String> departmentCourseMap,
			Map<Integer, PostDetail> postDetailMap) {

		List<SearchResultViewModel.Summary> summaries = new ArrayList<>();

		if (posts != null) {
			Map<Integer, String> safeDepartmentMap = (departmentCourseMap != null)
					? departmentCourseMap
					: Collections.emptyMap();
			Map<Integer, PostDetail> safeDetailMap = (postDetailMap != null)
					? postDetailMap
					: Collections.emptyMap();

			for (Post post : posts) {
				String departmentCourseName = safeDepartmentMap.getOrDefault(post.getDepartmentId(), "未設定");
				PostDetail detail = safeDetailMap.get(post.getPostId());

				summaries.add(new SearchResultViewModel.Summary(
						post.getPostId(),
						post.getUserId(),
						post.getDepartmentId(),
						departmentCourseName,
						post.getRecruitmentNo(),
						post.getCompanyName(),
						post.getExamDate(),
						post.getCreateAt(),
						post.getUpdatedAt(),
						detail));
			}
		}
		return new SearchResultViewModel(summaries);
	}

	/**
	 * 投稿情報と関連マスタを集約し、詳細表示用ビューモデルを生成する。
	 */
	public static PostViewModel toPostViewModel(
			Post post,
			PostDetail detail,
			String departmentName,
			String methodName,
			String posterName,
			boolean posterTeacher,
			Map<Integer, String> examCategoryMap,
			Map<Integer, String> examNameMap) {

		if (examCategoryMap == null) {
			examCategoryMap = new HashMap<>();
		}

		List<PostViewModel.SelectedExamItem> examItems = new ArrayList<>();

		if (post.getExamSelection() != null) {
			for (PostExamSelection sel : post.getExamSelection()) {
				int contentId = sel.getContentId();
				String category = examCategoryMap.getOrDefault(contentId, "その他");
				String name = examNameMap.getOrDefault(contentId, "未定義項目");

				// カテゴリと項目名を合成して画面表示用リストを組み立てる
				examItems.add(new PostViewModel.SelectedExamItem(
						category,
						name,
						sel.getDetailText()));
			}
		}

		int recruitmentNo = post.getRecruitmentNo();

		int scheduledHires = (detail != null) ? detail.getScheduled_hires() : 0;
		String adviceText = (detail != null) ? detail.getAdviceText() : "";

		return new PostViewModel(
				post.getPostId(),
				post.getCompanyName(),
				post.getExamDate(),
				post.getVenueAddress(),
				departmentName,
				methodName,
				recruitmentNo,
				post.getGrade(),
				posterName,
				post.getUserId(),
				post.isAnonymous(),
				posterTeacher,
				scheduledHires,
				adviceText,
				examItems);
	}

	// 保存系 (Input:Form → DTO)

	/**
	 * フォーム入力を投稿DTOへ変換する。
	 */
	public static Post toPostDto(PostForm form, int userId, int departmentId, int grade) {
		Post post = new Post();

		post.setPostId(form.getPostId());
		post.setUserId(userId);
		post.setCompanyName(form.getCompanyName());
		post.setVenueAddress(form.getVenueAddress());
		post.setDepartmentId(departmentId);
		post.setMethodId(form.getMethodId());
		post.setRecruitmentNo(parseIntSafe(form.getRecruitmentNoStr()));
		post.setGrade(grade);
		post.setAnonymous(form.isAnonymous());
		post.setExamDate(parseDate(form.getExamDateStr()));

		return post;
	}

	/**
	 * フォーム入力を投稿詳細DTOへ変換する。
	 */
	public static PostDetail toPostDetailDto(PostForm form) {
		PostDetail detail = new PostDetail();

		detail.setPostId(form.getPostId());
		detail.setAdviceText(form.getAdviceText());
		detail.setScheduled_hires(parseIntSafe(form.getScheduledHiresStr()));

		return detail;
	}

	/**
	 * フォームの試験選択情報をDTOリストに変換する。
	 */
	public static List<PostExamSelection> toSelectionList(PostForm form) {
		List<PostExamSelection> list = new ArrayList<>();

		if (form.getSelectedExamIds() != null) {
			for (Integer contentId : form.getSelectedExamIds()) {
				String detailText = null;
				if (form.getExamDetails() != null) {
					detailText = form.getExamDetails().get(contentId);
				}
				// 第一引数であるpostIdは、投稿未登録時点では存在しないため0
				list.add(new PostExamSelection(0, contentId, detailText));
			}
		}
		return list;
	}

	// 編集初期表示系 (Load:DTO → Form)

	/**
	 * 投稿DTOと詳細DTOからフォーム初期値を構築する。
	 */
	public static PostForm toPostForm(Post post, PostDetail detail) {
		PostForm form = new PostForm();
		form.setPostId(post.getPostId());
		form.setCompanyName(post.getCompanyName());
		form.setVenueAddress(post.getVenueAddress());
		form.setMethodId(post.getMethodId());
		form.setRecruitmentNoStr(String.valueOf(post.getRecruitmentNo()));
		form.setAnonymous(post.isAnonymous());

		if (post.getExamDate() != null) {
			form.setExamDateStr(post.getExamDate().format(DATE_FMT));
		}

		if (detail != null) {
			form.setAdviceText(detail.getAdviceText());
			form.setScheduledHiresStr(String.valueOf(detail.getScheduled_hires()));
		}

		List<Integer> ids = new ArrayList<>();
		Map<Integer, String> details = new HashMap<>();

		if (post.getExamSelection() != null) {
			for (PostExamSelection sel : post.getExamSelection()) {
				ids.add(sel.getContentId());
				if (sel.getDetailText() != null) {
					details.put(sel.getContentId(), sel.getDetailText());
				}
			}
		}
		form.setSelectedExamIds(ids);
		form.setExamDetails(details);

		return form;
	}

	// リクエスト解析系 (Request → Form)

	// HTTPリクエストのパラメータを解析し、PostFormオブジェクトを作成する

	/**
	 * HTTPリクエストから投稿フォームを組み立てる。
	 */
	public static PostForm toPostForm(HttpServletRequest request) {
		PostForm form = new PostForm();

		// 基本的な文字列項目
		form.setCompanyName(request.getParameter("companyName"));
		form.setExamDateStr(request.getParameter("examDateStr"));
		form.setVenueAddress(request.getParameter("venueAddress"));
		form.setRecruitmentNoStr(request.getParameter("recruitmentNoStr"));
		form.setAdviceText(request.getParameter("adviceText"));

		// 数値型 (パースエラー対策込みの変換)
		form.setMethodId(parseIntSafe(request.getParameter("methodId")));

		// チェックボックス (boolean)
		form.setAnonymous("true".equals(request.getParameter("isAnonymous")));

		// 詳細情報
		form.setScheduledHiresStr(request.getParameter("scheduledHiresStr"));

		// 試験選択情報のマッピング

		// チェックされたIDリスト (selectedExamIds)
		String[] selectedIds = request.getParameterValues("selectedExamIds");
		List<Integer> idList = new ArrayList<>();
		if (selectedIds != null) {
			for (String s : selectedIds) {
				int id = parseIntSafe(s);
				if (id > 0)
					idList.add(id);
			}
		}
		form.setSelectedExamIds(idList);

		// 詳細記述マップ (examDetails)
		// name="detail_101" のようなパラメータを取得
		Map<Integer, String> detailMap = new HashMap<>();
		for (Integer id : idList) {
			String legacyDetail = request.getParameter("detail_" + id);
			if (legacyDetail != null && !legacyDetail.isBlank()) {
				detailMap.put(id, legacyDetail);
			}
		}

		String[] detailTexts = request.getParameterValues("detailTexts");
		if (detailTexts != null && detailTexts.length > 0) {
			int index = 0;
			for (Integer id : idList) {
				String candidate = (index < detailTexts.length) ? detailTexts[index] : null;
				// 旧スタイル(detail_XXX)で埋まっていなければ、detailTexts配列の内容を補完
				if (candidate != null && !candidate.isBlank() && id != null && id > 0 && !detailMap.containsKey(id)) {
					detailMap.put(id, candidate);
				}
				index++;
			}
		}

		form.setExamDetails(detailMap);

		return form;
	}

	// ヘルパーメソッド
	/**
	 * yyyy-MM-dd 形式の文字列をLocalDateに変換する。
	 *
	 * @param dateStr 日付文字列
	 * @return 変換結果。失敗時はnull
	 */
	private static LocalDate parseDate(String dateStr) {
		if (dateStr == null || dateStr.isEmpty())
			return null;
		try {
			return LocalDate.parse(dateStr, DATE_FMT);
		} catch (DateTimeParseException e) {
			return null;
		}
	}

	/**
	 * 数値文字列を安全に int へ変換する。
	 *
	 * @param numStr 数値文字列
	 * @return 変換結果。失敗時は0
	 */
	private static int parseIntSafe(String numStr) {
		if (numStr == null || numStr.isEmpty())
			return 0;
		try {
			return Integer.parseInt(numStr);
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	/**
	 * リクエストパラメータを安全にInteger型に変換する。
	 * 変換失敗時はnullを返す。
	 *
	 * @param value 変換対象の文字列
	 * @return 変換後の値、または変換失敗時はnull
	 */
	public static Integer parseInteger(String value) {
		if (value == null || value.trim().isEmpty()) {
			return null;
		}
		try {
			return Integer.parseInt(value.trim());
		} catch (NumberFormatException e) {
			return null;
		}
	}

	/**
	 * リクエストパラメータを安全にint型に変換する。
	 * 変換失敗時はデフォルト値を返す。
	 *
	 * @param value        変換対象の文字列
	 * @param defaultValue 変換失敗時のデフォルト値
	 * @return 変換後の値、または変換失敗時はdefaultValue
	 */
	public static int parseIntOrDefault(String value, int defaultValue) {
		if (value == null || value.trim().isEmpty()) {
			return defaultValue;
		}
		try {
			return Integer.parseInt(value.trim());
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	/**
	 * エラーコードのリストをエラーメッセージのリストに変換する。
	 *
	 * @param errorCodes エラーコードのリスト
	 * @return エラーメッセージのリスト
	 */
	public static List<String> resolveErrorMessages(List<String> errorCodes) {
		if (errorCodes == null || errorCodes.isEmpty()) {
			return Collections.emptyList();
		}
		List<String> messages = new ArrayList<>();
		for (String code : errorCodes) {
			messages.add(ErrorCode.getMessage(code));
		}
		return messages;
	}

	/**
	 * SubmissionMethodのリストをIDキーのマップに変換する。
	 *
	 * @param methods 応募方法のリスト
	 * @return methodIdをキーとしたマップ
	 */
	public static Map<Integer, jp.ac.ok_oic.ikzm.careerlog.entity.SubmissionMethod> toSubmissionMethodMap(
			List<jp.ac.ok_oic.ikzm.careerlog.entity.SubmissionMethod> methods) {
		Map<Integer, jp.ac.ok_oic.ikzm.careerlog.entity.SubmissionMethod> map = new HashMap<>();
		if (methods != null) {
			for (jp.ac.ok_oic.ikzm.careerlog.entity.SubmissionMethod method : methods) {
				map.put(method.getMethodId(), method);
			}
		}
		return map;
	}

	/**
	 * ExamContentのリストをIDキーのマップに変換する。
	 *
	 * @param examContents 試験内容のリスト
	 * @return contentIdをキーとしたマップ
	 */
	public static Map<Integer, jp.ac.ok_oic.ikzm.careerlog.entity.ExamContent> toExamContentMap(
			List<jp.ac.ok_oic.ikzm.careerlog.entity.ExamContent> examContents) {
		Map<Integer, jp.ac.ok_oic.ikzm.careerlog.entity.ExamContent> map = new HashMap<>();
		if (examContents != null) {
			for (jp.ac.ok_oic.ikzm.careerlog.entity.ExamContent examContent : examContents) {
				map.put(examContent.getContentId(), examContent);
			}
		}
		return map;
	}

	/**
	 * DepartmentオブジェクトからID→名称のマップを作成する。
	 *
	 * @param departments 学科のリスト
	 * @return departmentIdをキーとした学科名のマップ
	 */
	public static Map<Integer, String> toDepartmentNameMap(List<jp.ac.ok_oic.ikzm.careerlog.entity.Department> departments) {
		Map<Integer, String> map = new HashMap<>();
		if (departments != null) {
			for (jp.ac.ok_oic.ikzm.careerlog.entity.Department dept : departments) {
				map.put(dept.getDepartmentId(), dept.getDepartmentName());
			}
		}
		return map;
	}

	/**
	 * 学科IDから学科名を取得する。
	 * 見つからない場合はデフォルト値を返す。
	 *
	 * @param departmentId   学科ID
	 * @param departmentList 学科のリスト
	 * @param defaultName    見つからない場合のデフォルト値
	 * @return 学科名またはデフォルト値
	 */
	public static String resolveDepartmentName(int departmentId, List<jp.ac.ok_oic.ikzm.careerlog.entity.Department> departmentList,
			String defaultName) {
		if (departmentList == null) {
			return defaultName;
		}
		for (jp.ac.ok_oic.ikzm.careerlog.entity.Department dept : departmentList) {
			if (dept.getDepartmentId() == departmentId) {
				return dept.getDepartmentName();
			}
		}
		return defaultName;
	}
}
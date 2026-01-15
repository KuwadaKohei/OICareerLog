package jp.ac.ok_oic.ikzm.careerlog.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.ac.ok_oic.ikzm.careerlog.dao.DepartmentDAO;
import jp.ac.ok_oic.ikzm.careerlog.dao.ExamContentDAO;
import jp.ac.ok_oic.ikzm.careerlog.dao.ExamDetailOptionDAO;
import jp.ac.ok_oic.ikzm.careerlog.dao.PostDAO;
import jp.ac.ok_oic.ikzm.careerlog.dao.PostDetailDAO;
import jp.ac.ok_oic.ikzm.careerlog.dao.PostExamSelectionDAO;
import jp.ac.ok_oic.ikzm.careerlog.dao.SubmissionMethodDAO;
import jp.ac.ok_oic.ikzm.careerlog.dao.UserDAO;
import jp.ac.ok_oic.ikzm.careerlog.entity.Department;
import jp.ac.ok_oic.ikzm.careerlog.entity.ExamContent;
import jp.ac.ok_oic.ikzm.careerlog.entity.ExamDetailOption;
import jp.ac.ok_oic.ikzm.careerlog.entity.Post;
import jp.ac.ok_oic.ikzm.careerlog.entity.PostDetail;
import jp.ac.ok_oic.ikzm.careerlog.entity.SubmissionMethod;
import jp.ac.ok_oic.ikzm.careerlog.entity.User;
import jp.ac.ok_oic.ikzm.careerlog.form.PostForm;
import jp.ac.ok_oic.ikzm.careerlog.util.ModelConverter;
import jp.ac.ok_oic.ikzm.careerlog.viewmodel.PostViewModel;
import jp.ac.ok_oic.ikzm.careerlog.viewmodel.SearchResultViewModel;

/**
 * 投稿データに関するビジネスロジックを提供するサービスクラス。
 *
 * このクラスは、ServletからDAO層への橋渡し役として機能し、以下の責務を担います
 *
 * 主な責務
 *   データ取得: 投稿一覧、投稿詳細、検索結果の取得
 *   データ変換: DTO ↔ ViewModel の変換をModelConverterと連携
 *   マスタデータ提供: 応募方法、試験内容、学科などのマスタデータ取得
 *
 * @see jp.ac.ok_oic.ikzm.careerlog.service.PostManageService
 * @see jp.ac.ok_oic.ikzm.careerlog.util.ModelConverter
 */
public class PostService {

	/** 投稿テーブル操作用DAO */
	private final PostDAO postDao;
	/** 投稿詳細テーブル操作用DAO */
	private final PostDetailDAO postDetailDao;
	/** 試験選択テーブル操作用DAO */
	private final PostExamSelectionDAO postExamSelectionDao;
	/** ユーザーテーブル操作用DAO */
	private final UserDAO userDao;
	/** 学科マスタテーブル操作用DAO */
	private final DepartmentDAO departmentDao;
	/** 応募方法マスタテーブル操作用DAO */
	private final SubmissionMethodDAO submissionMethodDao;
	/** 試験内容マスタテーブル操作用DAO */
	private final ExamContentDAO examContentDao;
	/** 試験詳細オプションマスタテーブル操作用DAO */
	private final ExamDetailOptionDAO examDetailOptionDao;

	/**
	 * デフォルトコンストラクタ。
	 * 各DAOインスタンスを初期化する。
	 */
	public PostService() {
		this.postDao = new PostDAO();
		this.postDetailDao = new PostDetailDAO();
		this.postExamSelectionDao = new PostExamSelectionDAO();
		this.userDao = new UserDAO();
		this.departmentDao = new DepartmentDAO();
		this.submissionMethodDao = new SubmissionMethodDAO();
		this.examContentDao = new ExamContentDAO();
		this.examDetailOptionDao = new ExamDetailOptionDAO();
	}

	/**
	 * 投稿一覧を取得し{@link SearchResultViewModel}に変換する。
	 *
	 * <p>全投稿を取得し、画面表示用のビューモデルに変換します。
	 * 投稿には学科名と詳細情報も併せて設定されます。</p>
	 *
	 * @return 投稿一覧のビューモデル
	 * @throws Exception DAOからの例外を透過
	 */
	public SearchResultViewModel findAll() throws Exception {
		// DAOから全投稿を取得し、画面用のViewModelへ変換
		List<Post> posts = this.postDao.findAll();
		return buildSearchResultViewModel(posts);
	}

	/**
	 * 編集用に投稿フォームを取得する。
	 * 投稿が存在しない、もしくは他ユーザーの投稿の場合はnullを返す。
	 *
	 * @param postId 投稿ID
	 * @param userId 操作ユーザーID
	 * @return 編集用フォーム、または null
	 * @throws Exception DAOからの例外を透過
	 */
	public PostForm getFormForEdit(int postId, int userId) throws Exception {

		Post post = this.postDao.findById(postId);

		if (post == null || post.getUserId() != userId) {
			// データがない、または他人なら弾く
			return null;
		}

		// 関連データを収集する
		PostDetail detail = this.postDetailDao.findByPostId(postId);

		post.setExamSelection(this.postExamSelectionDao.findByPostId(postId));

		return ModelConverter.toPostForm(post, detail);
	}

	/**
	 * キーワードに応じた投稿検索を行う。
	 * プレフィックス #/@ に応じて検索条件を切り替える。
	 *
	 * @param searchWord 画面からの検索語
	 * @return 検索結果のビューモデル
	 * @throws Exception DAO 層の例外
	 */
	public SearchResultViewModel search(String searchWord) throws Exception {
		List<Post> posts = null;

		if (searchWord == null || searchWord.isBlank()) {
			// 全件取得
			return findAll();
		}

		// 検索文字列から空白文字を取り除く
		String term = searchWord.trim();

		try {
			if (term.startsWith("#")) {
				// #求人番号検索
				int termInt = Integer.parseInt(term.substring(1));
				posts = this.postDao.findAllByRecruitmentNo(termInt);

			} else if (term.startsWith("@")) {
				// 日付検索
				String dateStr = term.substring(1);
				LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
				posts = this.postDao.findAllByExamDate(date);

			} else {
				// 通常検索
				posts = this.postDao.findAllByKeyword(term);

			}
		} catch (NumberFormatException | java.time.format.DateTimeParseException e) {
			posts = List.of();
		}

		return buildSearchResultViewModel(posts);
	}

	/**
	 * 投稿詳細を取得し、表示用{@link PostViewModel}に変換する。
	 *
	 * @param postId 投稿ID
	 * @return 投稿詳細ビューモデル。存在しない場合は null
	 * @throws Exception DAO層の例外
	 */
	public PostViewModel getDetail(int postId) throws Exception {

		Post post = this.postDao.findById(postId);

		if (post == null) {
			return null;
		}

		// 詳細情報を取得
		PostDetail detail = this.postDetailDao.findByPostId(postId);

		// 投稿情報に基づく
		post.setExamSelection(this.postExamSelectionDao.findByPostId(postId));

		String posterName = "匿名";
		boolean posterTeacher = false;
		if (!post.isAnonymous()) {
			User user = this.userDao.findById(post.getUserId());
			if (user != null) {
				posterName = user.getName();
				posterTeacher = "teacher".equals(user.getUserType());
			}
		}

		// 学科名を取得
		Department dept = this.departmentDao.findById(post.getDepartmentId());
		String deptName = (dept != null) ? dept.getDepartmentName() : "不明";

		// 応募方法名を取得
		SubmissionMethod method = this.submissionMethodDao.findById(post.getMethodId());
		String methodName = (method != null) ? method.getMethodName() : "不明";

		Map<Integer, String> examCategoryMap = new HashMap<>();
		Map<Integer, String> examNameMap = new HashMap<>();

		List<ExamContent> allContents = this.examContentDao.findAll();
		if (allContents != null) {
			for (ExamContent c : allContents) {
				examCategoryMap.put(c.getContentId(), c.getContentCategory());
				examNameMap.put(c.getContentId(), c.getContentName());
			}
		}

		return ModelConverter.toPostViewModel(post, detail, deptName, methodName, posterName, posterTeacher, examCategoryMap,
				examNameMap);
	}

	/**
	 * ログインユーザー自身の投稿一覧を検索する。
	 *
	 * @param userId ログインユーザー ID
	 * @param searchWord 検索語（任意）
	 * @return 投稿一覧ビューモデル
	 * @throws Exception DAO 層の例外
	 */
	public SearchResultViewModel findAllByUser(int userId, String searchWord) throws Exception {

		List<Post> posts = null;

		if (searchWord == null || searchWord.isBlank()) {
			// 全件取得
			posts = this.postDao.findAllByUserId(userId);
		} else {
			// 検索取得

			// 検索文字列から空白文字を取り除く
			String term = searchWord.trim();

			try {
				if (term.startsWith("#")) {
					// 求人番号検索
					int recruitmentNo = Integer.parseInt(term.substring(1));
					posts = this.postDao.findAllByUserIdAndRecruitmentNo(userId, recruitmentNo);

				} else if (term.startsWith("@")) {
					// 試験日検索
					String prefix = term.substring(1);
					LocalDate date = LocalDate.parse(prefix, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
					posts = this.postDao.findAllByUserIdAndExamDate(userId, date);

				} else {
					// プレフィックスなしの検索を行う
					posts = this.postDao.findAllByUserIdAndKeyword(userId, term);

				}
			} catch (NumberFormatException | java.time.format.DateTimeParseException e) {
				posts = List.of();
			}
		}

		// Post DTOリストを ViewModel に変換して返す
		return buildSearchResultViewModel(posts);
	}

	/**
	 * 投稿 DTO のリストから{@link SearchResultViewModel}を構築する。
	 *
	 * @param posts 投稿 DTO リスト
	 * @return ビューモデル
	 * @throws Exception 必要な付帯情報取得時の例外
	 */
	private SearchResultViewModel buildSearchResultViewModel(List<Post> posts) throws Exception {
		if (posts == null || posts.isEmpty()) {
			return new SearchResultViewModel(Collections.<SearchResultViewModel.Summary>emptyList());
		}
		Map<Integer, String> departmentCourseMap = loadDepartmentCourseMap();
		Map<Integer, PostDetail> postDetailMap = loadPostDetailMap(posts);
		return ModelConverter.toSearchResultViewModel(posts, departmentCourseMap, postDetailMap);
	}

	/**
	 * 学科マスタを読み込み、departmentId → 学科名のマップを生成する。
	 *
	 * @return 学科名マップ
	 * @throws Exception DAOからの例外
	 */
	private Map<Integer, String> loadDepartmentCourseMap() throws Exception {
		Map<Integer, String> departmentMap = new HashMap<>();
		List<Department> departments = this.departmentDao.findAll();
		if (departments != null) {
			for (Department department : departments) {
				departmentMap.put(department.getDepartmentId(), department.getDepartmentName());
			}
		}
		return departmentMap;
	}

	/**
	 * 投稿 ID リストから投稿詳細情報を一括で取得する。
	 *
	 * @param posts 投稿リスト
	 * @return postId → PostDetailのマップ
	 * @throws Exception DAO層の例外
	 */
	private Map<Integer, PostDetail> loadPostDetailMap(List<Post> posts) throws Exception {
		if (posts == null || posts.isEmpty()) {
			return Collections.emptyMap();
		}
		List<Integer> postIds = new ArrayList<>();
		for (Post post : posts) {
			postIds.add(post.getPostId());
		}
		return this.postDetailDao.findByPostIds(postIds);
	}

	/**
	 * 申込み方法の全件リストを取得する。
	 *
	 * @return 申込み方法のリスト
	 * @throws Exception DAOからの例外
	 */
	public List<SubmissionMethod> getSubmissionMethods() throws Exception {
		return this.submissionMethodDao.findAll();
	}

	/**
	 * 試験内容の全件リストを取得する。
	 *
	 * @return 試験内容のリスト
	 * @throws Exception DAOからの例外
	 */
	public List<ExamContent> getExamContents() throws Exception {
		return this.examContentDao.findAll();
	}

	/**
	 * 試験詳細オプションをマップ形式で取得する。
	 *
	 * @return contentId → 詳細オプションリストのマップ
	 * @throws Exception DAOからの例外
	 */
	public Map<Integer, List<ExamDetailOption>> getExamDetailOptionsMap() throws Exception {
		return this.examDetailOptionDao.findAllAsMap();
	}

	/**
	 * 学科の全件リストを取得する。
	 *
	 * @return 学科のリスト
	 * @throws Exception DAOからの例外
	 */
	public List<Department> getDepartments() throws Exception {
		return this.departmentDao.findAll();
	}

	/**
	 * 投稿IDから投稿情報を取得する。
	 *
	 * @param postId 投稿ID
	 * @return 投稿情報、存在しない場合はnull
	 * @throws Exception DAOからの例外
	 */
	public Post findById(int postId) throws Exception {
		return this.postDao.findById(postId);
	}
}

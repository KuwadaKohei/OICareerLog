package jp.ac.ok_oic.ikzm.careerlog.viewmodel;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import jp.ac.ok_oic.ikzm.careerlog.entity.PostDetail;

/**
 * 検索結果一覧画面全体のビューモデル。
 * 複数の結果アイテムを保持する。
 */
public class SearchResultViewModel {

	// 検索結果のリスト
	private List<Summary> reports;

	/**
	 * デフォルトコンストラクタ。
	 */
	public SearchResultViewModel() {
	}

	/**
	 * 検索結果リストを受け取るコンストラクタ。
	 *
	 * @param reports 検索結果概要リスト
	 */
	public SearchResultViewModel(List<Summary> reports) {
		this.reports = reports;
	}

	/**
	 * 検索結果の一覧を返す。
	 *
	 * @return 検索結果概要リスト
	 */
	public List<Summary> getReports() {
		return reports;
	}

	// 1件分の概要情報を保持する内部クラス

	/**
	 * 検索結果1件分の概要情報を保持する値オブジェクト。
	 */
	public static class Summary {
		private int postId;
		private int userId;
		private int departmentId;
		private String departmentCourseName;
		private int recruitmentNo;
		private String companyName;
		private LocalDate examDate;
		private LocalDateTime createdAt;
		private LocalDateTime updatedAt;
		private PostDetail postDetail;

		/**
		 * デフォルトコンストラクタ。
		 */
		public Summary() {
		}

		/**
		 * 1件分の概要情報を構築するコンストラクタ。
		 *
		 * @param postId               投稿ID
		 * @param userId               ユーザーID
		 * @param departmentId         学科ID
		 * @param departmentCourseName 学科・コース名
		 * @param recruitmentNo        求人票番号
		 * @param companyName          企業名
		 * @param examDate             試験日
		 * @param createdAt            作成日時
		 * @param updatedAt            更新日時
		 * @param postDetail           投稿詳細情報
		 */
		public Summary(
				int postId,
				int userId,
				int departmentId,
				String departmentCourseName,
				int recruitmentNo,
				String companyName,
				LocalDate examDate,
				LocalDateTime createdAt,
				LocalDateTime updatedAt,
				PostDetail postDetail) {
			this.postId = postId;
			this.userId = userId;
			this.departmentId = departmentId;
			this.departmentCourseName = departmentCourseName;
			this.recruitmentNo = recruitmentNo;
			this.companyName = companyName;
			this.examDate = examDate;
			this.createdAt = createdAt;
			this.updatedAt = updatedAt;
			this.postDetail = postDetail;
		}

		/**
		 * 投稿IDを返す。
		 *
		 * @return 投稿ID
		 */
		public int getPostId() {
			return postId;
		}

		/**
		 * 投稿IDを設定する。
		 *
		 * @param postId 投稿ID
		 */
		public void setPostId(int postId) {
			this.postId = postId;
		}

		/**
		 * 投稿者ユーザーIDを返す。
		 *
		 * @return 投稿者ユーザーID
		 */
		public int getUserId() {
			return userId;
		}

		/**
		 * 投稿者ユーザーIDを設定する。
		 *
		 * @param userId 投稿者ユーザーID
		 */
		public void setUserId(int userId) {
			this.userId = userId;
		}

		/**
		 * 学科IDを返す。
		 *
		 * @return 学科ID
		 */
		public int getDepartmentId() {
			return departmentId;
		}

		/**
		 * 学科IDを設定する。
		 *
		 * @param departmentId 学科ID
		 */
		public void setDepartmentId(int departmentId) {
			this.departmentId = departmentId;
		}

		/**
		 * 学科・コース名を返す。
		 *
		 * @return 学科・コース名
		 */
		public String getDepartmentCourseName() {
			return departmentCourseName;
		}

		/**
		 * 学科・コース名を設定する。
		 *
		 * @param departmentCourseName 学科・コース名
		 */
		public void setDepartmentCourseName(String departmentCourseName) {
			this.departmentCourseName = departmentCourseName;
		}

		/**
		 * 求人票番号を返す。
		 *
		 * @return 求人票番号
		 */
		public int getRecruitmentNo() {
			return recruitmentNo;
		}

		/**
		 * 求人票番号を設定する。
		 *
		 * @param recruitmentNo 求人票番号
		 */
		public void setRecruitmentNo(int recruitmentNo) {
			this.recruitmentNo = recruitmentNo;
		}

		/**
		 * 受験企業名を返す。
		 *
		 * @return 受験企業名
		 */
		public String getCompanyName() {
			return companyName;
		}

		/**
		 * 受験企業名を設定する。
		 *
		 * @param companyName 受験企業名
		 */
		public void setCompanyName(String companyName) {
			this.companyName = companyName;
		}

		/**
		 * 試験日を返す。
		 *
		 * @return 試験日
		 */
		public LocalDate getExamDate() {
			return examDate;
		}

		/**
		 * 試験日を設定する。
		 *
		 * @param examDate 試験日
		 */
		public void setExamDate(LocalDate examDate) {
			this.examDate = examDate;
		}

		/**
		 * 作成日時を返す。
		 *
		 * @return 作成日時
		 */
		public LocalDateTime getCreatedAt() {
			return createdAt;
		}

		/**
		 * 作成日時を設定する。
		 *
		 * @param createdAt 作成日時
		 */
		public void setCreatedAt(LocalDateTime createdAt) {
			this.createdAt = createdAt;
		}

		/**
		 * 更新日時を返す。
		 *
		 * @return 更新日時
		 */
		public LocalDateTime getUpdatedAt() {
			return updatedAt;
		}

		/**
		 * 更新日時を設定する。
		 *
		 * @param updatedAt 更新日時
		 */
		public void setUpdatedAt(LocalDateTime updatedAt) {
			this.updatedAt = updatedAt;
		}

		/**
		 * 投稿詳細を返す。
		 *
		 * @return 投稿詳細
		 */
		public PostDetail getPostDetail() {
			return postDetail;
		}

		/**
		 * 投稿詳細を設定する。
		 *
		 * @param postDetail 投稿詳細
		 */
		public void setPostDetail(PostDetail postDetail) {
			this.postDetail = postDetail;
		}

		/**
		 * 試験日をyyyy年MM月dd日形式でフォーマットして返す。
		 *
		 * @return フォーマット済み試験日
		 */
		public String getFormattedExamDate() {
			if (examDate == null) {
				return "";
			}
			return examDate.format(DateTimeFormatter.ofPattern("yyyy年MM月dd日"));
		}

		/**
		 * 作成日時をyyyy年MM月dd日/HH時mm分形式でフォーマットして返す。
		 *
		 * @return フォーマット済み作成日時
		 */
		public String getFormattedCreatedAt() {
			if (createdAt == null) {
				return "";
			}
			return createdAt.format(DateTimeFormatter.ofPattern("yyyy年MM月dd日/HH時mm分"));
		}

		/**
		 * 更新日時をyyyy年MM月dd日/HH時mm分形式でフォーマットして返す。
		 *
		 * @return フォーマット済み更新日時
		 */
		public String getFormattedUpdatedAt() {
			if (updatedAt == null) {
				return "";
			}
			return updatedAt.format(DateTimeFormatter.ofPattern("yyyy年MM月dd日/HH時mm分"));
		}
	}
}
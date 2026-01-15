package jp.ac.ok_oic.ikzm.careerlog.viewmodel;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 就活報告書の詳細画面表示に使用するビューモデル。
 * Posts, Departments, SubmissionMethods, PostDetails,
 * PostExamSelectionsの情報を集約する。
 */
public class PostViewModel {

	// Postsコア情報と関連情報 (Posts, Departments, SubmissionMethods)
	private int postId;
	private String companyName;
	private LocalDate examDate;
	private String venueAddress;
	private String departmentName;
	private String submissionMethodName;
	private int recruitmentNo;
	private int grade;
	private String posterName;
	private int posterUserId;
	private boolean anonymous;
	private boolean posterTeacher;
	// PostDetails情報
	private int scheduledHires;
	private String adviceText;
	// 試験内容の選択結果 (ExamContent, PostExamSelections)
	private List<SelectedExamItem> selectedExams;

	/**
	 * デフォルトコンストラクタ。
	 */
	public PostViewModel() {
	}

	public PostViewModel(int postId, String companyName, LocalDate examDate, String venueAddress,
			String departmentName, String submissionMethodName, int recruitmentNo,
			int grade, String posterName, int posterUserId, boolean anonymous,
			boolean posterTeacher, int scheduledHires, String adviceText, List<SelectedExamItem> selectedExams) {

		this.postId = postId;
		this.companyName = companyName;
		this.examDate = examDate;
		this.venueAddress = venueAddress;
		this.departmentName = departmentName;
		this.submissionMethodName = submissionMethodName;
		this.recruitmentNo = recruitmentNo;
		this.grade = grade;
		this.posterName = posterName;
		this.posterUserId = posterUserId;
		this.anonymous = anonymous;
		this.posterTeacher = posterTeacher;
		this.scheduledHires = scheduledHires;
		this.adviceText = adviceText;
		this.selectedExams = selectedExams;
	}

	public int getPostId() {
		return postId;
	}

	public void setPostId(int postId) {
		this.postId = postId;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public LocalDate getExamDate() {
		return examDate;
	}

	public void setExamDate(LocalDate examDate) {
		this.examDate = examDate;
	}

	public String getVenueAddress() {
		return venueAddress;
	}

	public void setVenueAddress(String venueAddress) {
		this.venueAddress = venueAddress;
	}

	public String getDepartmentName() {
		return departmentName;
	}

	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}

	public String getSubmissionMethodName() {
		return submissionMethodName;
	}

	public void setSubmissionMethodName(String submissionMethodName) {
		this.submissionMethodName = submissionMethodName;
	}

	public int getRecruitmentNo() {
		return recruitmentNo;
	}

	public void setRecruitmentNo(int recruitmentNo) {
		this.recruitmentNo = recruitmentNo;
	}

	public int getGrade() {
		return grade;
	}

	public void setGrade(int grade) {
		this.grade = grade;
	}

	public String getPosterName() {
		return posterName;
	}

	public void setPosterName(String posterName) {
		this.posterName = posterName;
	}

	public int getPosterUserId() {
		return posterUserId;
	}

	public void setPosterUserId(int posterUserId) {
		this.posterUserId = posterUserId;
	}

	public boolean isAnonymous() {
		return anonymous;
	}

	public void setAnonymous(boolean anonymous) {
		this.anonymous = anonymous;
	}

	public boolean isPosterTeacher() {
		return posterTeacher;
	}

	public void setPosterTeacher(boolean posterTeacher) {
		this.posterTeacher = posterTeacher;
	}

	public int getScheduledHires() {
		return scheduledHires;
	}

	public void setScheduledHires(int scheduledHires) {
		this.scheduledHires = scheduledHires;
	}

	public String getAdviceText() {
		return adviceText;
	}

	public void setAdviceText(String adviceText) {
		this.adviceText = adviceText;
	}

	public List<SelectedExamItem> getSelectedExams() {
		return selectedExams;
	}

	public void setSelectedExams(List<SelectedExamItem> selectedExams) {
		this.selectedExams = selectedExams;
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

	public static class SelectedExamItem {
		private String categoryName;
		private String itemName;
		private String detailText;

		/**
		 * デフォルトコンストラクタ。
		 */
		public SelectedExamItem() {
		}

		// コンストラクタ
		public SelectedExamItem(String categoryName, String itemName, String detailText) {
			this.categoryName = categoryName;
			this.itemName = itemName;
			this.detailText = detailText;
		}

		// ゲッター・セッター
		public String getCategoryName() {
			return categoryName;
		}

		public void setCategoryName(String categoryName) {
			this.categoryName = categoryName;
		}

		public String getItemName() {
			return itemName;
		}

		public void setItemName(String itemName) {
			this.itemName = itemName;
		}

		public String getDetailText() {
			return detailText;
		}

		public void setDetailText(String detailText) {
			this.detailText = detailText;
		}
	}
}
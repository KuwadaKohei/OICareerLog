package jp.ac.ok_oic.ikzm.careerlog.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 削除済み投稿の履歴情報を保持する DTO。
 */
public class DeletedPost implements Serializable {
	/**
	 * 削除済み投稿ID（主キー、自動採番）
	 * NOT NULL
	 */
	private int deletedPostId;

	/**
	 * 元の投稿ID（削除前のpostsテーブルでのpost_id）
	 * NOT NULL
	 */
	private int originalPostId;

	/**
	 * 投稿者のユーザーID
	 * NOT NULL
	 */
	private int userId;

	/**
	 * 投稿者の学科ID
	 * NOT NULL
	 */
	private int departmentId;

	/**
	 * 応募方法ID
	 * NOT NULL
	 */
	private int methodId;

	/**
	 * 求人票番号
	 * 未入力時は0
	 */
	private int recruitmentNo;

	/**
	 * 受験企業名・事業所名
	 * NOT NULL
	 */
	private String companyName;

	/**
	 * 試験会場の住所
	 * NULL許容
	 */
	private String venueAddress;

	/**
	 * 試験日（受験日）
	 * NOT NULL
	 */
	private LocalDate examDate;

	/**
	 * 投稿者の学年
	 * NOT NULL
	 */
	private int grade;

	/**
	 * 匿名投稿フラグ
	 * true: 匿名表示, false: 氏名表示
	 */
	private boolean isAnonymous;

	/**
	 * 元投稿の作成日時
	 */
	private LocalDateTime createdAt;

	/**
	 * 元投稿の最終更新日時
	 */
	private LocalDateTime updatedAt;

	/**
	 * 削除実行日時
	 * レコードがdeleted_postsに挿入された日時
	 */
	private LocalDateTime deletedAt;

	/**
	 * 削除を実行したユーザーのID
	 * 投稿者本人による削除の場合はそのユーザーID、管理者削除の場合は管理者のID
	 * システム削除等の場合はNULL
	 */
	private Integer deletedBy;

	/**
	 * すべてのフィールドを指定するコンストラクタ。
	 */
	public DeletedPost(int deletedPostId, int originalPostId, int userId, int departmentId, int methodId,
			int recruitmentNo, String companyName, String venueAddress, LocalDate examDate, int grade,
			boolean isAnonymous, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt,
			Integer deletedBy, String postDetailsJson, String examSelectionsJson) {
		this.deletedPostId = deletedPostId;
		this.originalPostId = originalPostId;
		this.userId = userId;
		this.departmentId = departmentId;
		this.methodId = methodId;
		this.recruitmentNo = recruitmentNo;
		this.companyName = companyName;
		this.venueAddress = venueAddress;
		this.examDate = examDate;
		this.grade = grade;
		this.isAnonymous = isAnonymous;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
		this.deletedAt = deletedAt;
		this.deletedBy = deletedBy;
	}

	/**
	 * デフォルトコンストラクタ。
	 */
	public DeletedPost() {
	}

	public int getDeletedPostId() {
		return deletedPostId;
	}

	public void setDeletedPostId(int deletedPostId) {
		this.deletedPostId = deletedPostId;
	}

	public int getOriginalPostId() {
		return originalPostId;
	}

	public void setOriginalPostId(int originalPostId) {
		this.originalPostId = originalPostId;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(int departmentId) {
		this.departmentId = departmentId;
	}

	public int getMethodId() {
		return methodId;
	}

	public void setMethodId(int methodId) {
		this.methodId = methodId;
	}

	public int getRecruitmentNo() {
		return recruitmentNo;
	}

	public void setRecruitmentNo(int recruitmentNo) {
		this.recruitmentNo = recruitmentNo;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getVenueAddress() {
		return venueAddress;
	}

	public void setVenueAddress(String venueAddress) {
		this.venueAddress = venueAddress;
	}

	public LocalDate getExamDate() {
		return examDate;
	}

	public void setExamDate(LocalDate examDate) {
		this.examDate = examDate;
	}

	public int getGrade() {
		return grade;
	}

	public void setGrade(int grade) {
		this.grade = grade;
	}

	public boolean isAnonymous() {
		return isAnonymous;
	}

	public void setAnonymous(boolean isAnonymous) {
		this.isAnonymous = isAnonymous;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	public LocalDateTime getDeletedAt() {
		return deletedAt;
	}

	public void setDeletedAt(LocalDateTime deletedAt) {
		this.deletedAt = deletedAt;
	}

	public Integer getDeletedBy() {
		return deletedBy;
	}

	public void setDeletedBy(Integer deletedBy) {
		this.deletedBy = deletedBy;
	}
}

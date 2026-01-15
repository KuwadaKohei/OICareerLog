package jp.ac.ok_oic.ikzm.careerlog.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 投稿の基本情報を保持する DTO。
 */
public class Post implements Serializable{
	/**
	 * 投稿ID（主キー、自動採番）
	 * NOT NULL
	 */
	private int postId;

	/**
	 * 投稿者のユーザーID（usersテーブルへの外部キー）
	 * NOT NULL
	 */
	private int userId;

	/**
	 * 投稿者の学科ID（departmentsテーブルへの外部キー）
	 * 投稿時点での学科を記録
	 * NOT NULL
	 */
	private int departmentId;

	/**
	 * 応募方法ID（submission_methodsテーブルへの外部キー）
	 * NOT NULL
	 */
	private int methodId;

	/**
	 * 求人票番号
	 * 任意入力項目
	 * 未入力時は0
	 */
	private int recruitmentNo;

	/**
	 * 受験企業名・事業所名
	 * NOT NULL、最大100文字
	 */
	private String companyName;

	/**
	 * 試験会場の住所
	 * 任意入力項目、最大255文字
	 */
	private String venueAddress;

	/**
	 * 試験日（受験日）
	 * NOT NULL
	 */
	private LocalDate examDate;

	/**
	 * 投稿者の学年
	 * 投稿時点での学年を記録（1～3）
	 * NOT NULL
	 */
	private int grade;

	/**
	 * 匿名投稿フラグ
	 * true: 匿名表示, false: 氏名表示
	 */
	private boolean isAnonymous;

	/**
	 * レコード作成日時
	 * 投稿が新規登録された日時
	 */
	private LocalDateTime createAt;

	/**
	 * レコード更新日時
	 * 最後に編集された日時
	 */
	private LocalDateTime updatedAt;

	/**
	 * 選択された試験内容の一覧
	 * データベースから取得した試験選択情報を保持
	 */
	private List<PostExamSelection> examSelection;

	/**
	 * すべてのフィールドを指定するコンストラクタ。
	 */
	public Post(int postId, int userId, int departmentId, int methodId, int recruitmentNo, String companyName,
			String venueAddress, LocalDate examDate, int grade, boolean isAnonymous, LocalDateTime createAt,
			LocalDateTime updatedAt, List<PostExamSelection> examSelection) {
		super();
		this.postId = postId;
		this.userId = userId;
		this.departmentId = departmentId;
		this.methodId = methodId;
		this.recruitmentNo = recruitmentNo;
		this.companyName = companyName;
		this.venueAddress = venueAddress;
		this.examDate = examDate;
		this.grade = grade;
		this.isAnonymous = isAnonymous;
		this.createAt = createAt;
		this.updatedAt = updatedAt;
		this.examSelection = examSelection;
	}

	/**
	 * デフォルトコンストラクタ。
	 */
	public Post() {
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
	 * @return ユーザーID
	 */
	public int getUserId() {
		return userId;
	}

	/**
	 * 投稿者ユーザーIDを設定する。
	 *
	 * @param userId ユーザーID
	 */
	public void setUserId(int userId) {
		this.userId = userId;
	}

	/**
	 * 学科IDを返す。
	 */
	public int getDepartmentId() {
		return departmentId;
	}

	/**
	 * 学科IDを設定する。
	 */
	public void setDepartmentId(int departmentId) {
		this.departmentId = departmentId;
	}

	/**
	 * 応募方法IDを返す。
	 */
	public int getMethodId() {
		return methodId;
	}

	/**
	 * 応募方法IDを設定する。
	 */
	public void setMethodId(int methodId) {
		this.methodId = methodId;
	}

	/**
	 * 求人票番号を返す。
	 */
	public int getRecruitmentNo() {
		return recruitmentNo;
	}

	/**
	 * 求人票番号を設定する。
	 */
	public void setRecruitmentNo(int recruitmentNo) {
		this.recruitmentNo = recruitmentNo;
	}

	/**
	 * 受験企業名を返す。
	 */
	public String getCompanyName() {
		return companyName;
	}

	/**
	 * 受験企業名を設定する。
	 */
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	/**
	 * 会場住所を返す。
	 */
	public String getVenueAddress() {
		return venueAddress;
	}

	/**
	 * 会場住所を設定する。
	 */
	public void setVenueAddress(String venueAddress) {
		this.venueAddress = venueAddress;
	}

	/**
	 * 試験日を返す。
	 */
	public LocalDate getExamDate() {
		return examDate;
	}

	/**
	 * 試験日を設定する。
	 */
	public void setExamDate(LocalDate examDate) {
		this.examDate = examDate;
	}

	/**
	 * 学年を返す。
	 */
	public int getGrade() {
		return grade;
	}

	/**
	 * 学年を設定する。
	 */
	public void setGrade(int grade) {
		this.grade = grade;
	}

	/**
	 * 匿名投稿かを返す。
	 */
	public boolean isAnonymous() {
		return isAnonymous;
	}

	/**
	 * 匿名投稿フラグを設定する。
	 */
	public void setAnonymous(boolean isAnonymous) {
		this.isAnonymous = isAnonymous;
	}

	/**
	 * 作成日時を返す。
	 */
	public LocalDateTime getCreateAt() {
		return createAt;
	}

	/**
	 * 作成日時を設定する。
	 */
	public void setCreateAt(LocalDateTime createAt) {
		this.createAt = createAt;
	}

	/**
	 * 更新日時を返す。
	 */
	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	/**
	 * 更新日時を設定する。
	 */
	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	/**
	 * 選択した試験内容一覧を返す。
	 */
	public List<PostExamSelection> getExamSelection() {
		return examSelection;
	}

	/**
	 * 選択した試験内容一覧を設定する。
	 */
	public void setExamSelection(List<PostExamSelection> examSelection) {
		this.examSelection = examSelection;
	}

}

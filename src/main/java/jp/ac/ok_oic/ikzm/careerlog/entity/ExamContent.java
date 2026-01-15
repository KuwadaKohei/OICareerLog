package jp.ac.ok_oic.ikzm.careerlog.entity;

import java.io.Serializable;

/**
 * 試験内容マスタのDTO。カテゴリ・名称・詳細記述要否を保持する。
 */
public class ExamContent implements Serializable{
	/**
	 * 試験内容ID（主キー、自動採番）
	 * NOT NULL
	 */
	private int contentId;

	/**
	 * 試験内容のカテゴリ名
	 * 例: "筆記試験", "面接-個人"
	 * NOT NULL
	 */
	private String contentCategory;

	/**
	 * 試験内容の名称。
	 * 例: "専門試験", "適性検査"
	 * NOT NULL。
	 */
	private String contentName;

	/**
	 * 詳細入力要否フラグ。
	 * true: この試験内容選択時に詳細テキスト入力が必要。
	 */
	private boolean needsDetail;

	/**
	 * 詳細選択肢使用フラグ。
	 * true: exam_detail_optionsテーブルの選択肢を表示。
	 */
	private boolean selectDetail;

	/**
	 * 詳細入力の例文。
	 * フォームのプレースホルダー等に使用
	 * NULL許容
	 */
	private String detailExample;

	/**
	 * 全フィールド指定のコンストラクタ。
	 *
	 * @param contentId       コンテンツID
	 * @param contentCategory カテゴリ
	 * @param contentName     名称
	 * @param needsDetail     詳細入力要否
	 * @param selectDetail    詳細選択肢使用フラグ
	 * @param detailExample   詳細入力例
	 */
	public ExamContent(int contentId, String contentCategory, String contentName, boolean needsDetail,
			boolean selectDetail, String detailExample) {
		super();
		this.contentId = contentId;
		this.contentCategory = contentCategory;
		this.contentName = contentName;
		this.needsDetail = needsDetail;
		this.selectDetail = selectDetail;
		this.detailExample = detailExample;
	}

	/**
	 * detailExample を除くフィールドを指定するコンストラクタ。
	 *
	 * @param contentId       コンテンツID
	 * @param contentCategory カテゴリ
	 * @param contentName     名称
	 * @param needsDetail     詳細入力要否
	 * @param selectDetail    詳細選択肢使用フラグ
	 */
	public ExamContent(int contentId, String contentCategory, String contentName, boolean needsDetail,
			boolean selectDetail) {
		this(contentId, contentCategory, contentName, needsDetail, selectDetail, null);
	}

	/**
	 * デフォルトコンストラクタ。
	 */
	public ExamContent() {
	}

	/**
	 * コンテンツIDを返す。
	 *
	 * @return コンテンツID
	 */
	public int getContentId() {
		return contentId;
	}

	/**
	 * コンテンツIDを設定する。
	 *
	 * @param contentId コンテンツID
	 */
	public void setContentId(int contentId) {
		this.contentId = contentId;
	}

	/**
	 * カテゴリを返す。
	 *
	 * @return カテゴリ名
	 */
	public String getContentCategory() {
		return contentCategory;
	}

	/**
	 * カテゴリを設定する。
	 *
	 * @param contentCategory カテゴリ
	 */
	public void setContentCategory(String contentCategory) {
		this.contentCategory = contentCategory;
	}

	/**
	 * 名称を返す。
	 *
	 * @return コンテンツ名
	 */
	public String getContentName() {
		return contentName;
	}

	/**
	 * 名称を設定する。
	 *
	 * @param contentName コンテンツ名
	 */
	public void setContentName(String contentName) {
		this.contentName = contentName;
	}

	/**
	 * 詳細入力が必要かを返す。
	 *
	 * @return true: 必要
	 */
	public boolean isNeedsDetail() {
		return needsDetail;
	}

	/**
	 * 詳細入力要否を設定する。
	 *
	 * @param needsDetail true なら詳細入力が必要
	 */
	public void setNeedsDetail(boolean needsDetail) {
		this.needsDetail = needsDetail;
	}

	/**
	 * 詳細入力例を返す。
	 *
	 * @return 詳細入力例
	 */
	public String getDetailExample() {
		return detailExample;
	}

	/**
	 * 詳細入力例を設定する。
	 *
	 * @param detailExample 詳細入力例
	 */
	public void setDetailExample(String detailExample) {
		this.detailExample = detailExample;
	}

	/**
	 * 詳細選択肢使用フラグを返す。
	 *
	 * @return true: 選択肢を使用する
	 */
	public boolean isSelectDetail() {
		return selectDetail;
	}

	/**
	 * 詳細選択肢使用フラグを設定する。
	 *
	 * @param selectDetail true なら選択肢を使用
	 */
	public void setSelectDetail(boolean selectDetail) {
		this.selectDetail = selectDetail;
	}

}

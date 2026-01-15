package jp.ac.ok_oic.ikzm.careerlog.entity;

import java.io.Serializable;

/**
 * 試験詳細選択肢マスタのDTO。
 */
public class ExamDetailOption implements Serializable {
	/**
	 * 選択肢ID（主キー、自動採番）
	 * NOT NULL
	 */
	private int optionId;

	/**
	 * 試験内容ID（exam_contentsテーブルへの外部キー）
	 * どの試験内容に属する選択肢かを示す
	 * NOT NULL
	 */
	private int contentId;

	/**
	 * 選択肢の表示テキスト
	 * NOT NULL
	 */
	private String optionText;

	/**
	 * 表示順序
	 * 小さい値ほど先に表示
	 * NOT NULL
	 */
	private int displayOrder;

	/**
	 * 全フィールド指定のコンストラクタ。
	 *
	 * @param optionId     選択肢ID
	 * @param contentId    試験内容ID
	 * @param optionText   選択肢テキスト
	 * @param displayOrder 表示順序
	 */
	public ExamDetailOption(int optionId, int contentId, String optionText, int displayOrder) {
		this.optionId = optionId;
		this.contentId = contentId;
		this.optionText = optionText;
		this.displayOrder = displayOrder;
	}

	/**
	 * デフォルトコンストラクタ。
	 */
	public ExamDetailOption() {
	}

	/**
	 * 選択肢IDを返す。
	 *
	 * @return 選択肢ID
	 */
	public int getOptionId() {
		return optionId;
	}

	/**
	 * 選択肢IDを設定する。
	 *
	 * @param optionId 選択肢ID
	 */
	public void setOptionId(int optionId) {
		this.optionId = optionId;
	}

	/**
	 * 試験内容IDを返す。
	 *
	 * @return 試験内容ID
	 */
	public int getContentId() {
		return contentId;
	}

	/**
	 * 試験内容IDを設定する。
	 *
	 * @param contentId 試験内容ID
	 */
	public void setContentId(int contentId) {
		this.contentId = contentId;
	}

	/**
	 * 選択肢テキストを返す。
	 *
	 * @return 選択肢テキスト
	 */
	public String getOptionText() {
		return optionText;
	}

	/**
	 * 選択肢テキストを設定する。
	 *
	 * @param optionText 選択肢テキスト
	 */
	public void setOptionText(String optionText) {
		this.optionText = optionText;
	}

	/**
	 * 表示順序を返す。
	 *
	 * @return 表示順序
	 */
	public int getDisplayOrder() {
		return displayOrder;
	}

	/**
	 * 表示順序を設定する。
	 *
	 * @param displayOrder 表示順序
	 */
	public void setDisplayOrder(int displayOrder) {
		this.displayOrder = displayOrder;
	}
}

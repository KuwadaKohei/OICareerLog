package jp.ac.ok_oic.ikzm.careerlog.constants;

/**
 * バリデーションで使用する定数を定義するクラス。
 *
 * 各フォームクラスのバリデーションで使用する文字数制限などのマジックナンバーを集約。
 */
public final class ValidationConstants {

	/**
	 * インスタンス化を禁止
	 */
	private ValidationConstants() {
	}

	/**
	 * 会社名の最大文字数。
	 * DBカラム: posts.company_name VARCHAR(255)
	 */
	public static final int COMPANY_NAME_MAX_LENGTH = 100;

	/**
	 * 会場住所の最大文字数。
	 * DBカラム: posts.venue_address VARCHAR(255)
	 */
	public static final int VENUE_ADDRESS_MAX_LENGTH = 255;

	/**
	 * アドバイス文の最大文字数。
	 * DBカラム: post_details.advice_text TEXT
	 */
	public static final int ADVICE_TEXT_MAX_LENGTH = 2000;

	/**
	 * 試験詳細テキストの最大文字数。
	 * DBカラム: post_exam_selections.detail_text VARCHAR(500)
	 */
	public static final int EXAM_DETAIL_TEXT_MAX_LENGTH = 500;

	/**
	 * 学年の最小値。
	 */
	public static final int GRADE_MIN = 1;

	/**
	 * 学年の最大値。
	 */
	public static final int GRADE_MAX = 3;
}

package jp.ac.ok_oic.ikzm.careerlog.entity;

import java.io.Serializable;

/**
 * 投稿詳細情報（予定採用人数やアドバイス等）を保持する DTO。
 */
public class PostDetail implements Serializable{
	/**
	 * 投稿ID（postsテーブルへの外部キー、主キー）
	 * NOT NULL
	 */
	private int postId;

	/**
	 * 予定採用人数
	 * 企業が掲載している採用予定人数
	 * 未入力時は0
	 */
	private int scheduled_hires;

	/**
	 * アドバイス文
	 * 後輩へのアドバイスや所感を自由記述。最大2000文字
	 */
	private String adviceText;

	/**
	 * すべてのフィールドを指定するコンストラクタ。
	 */
	public PostDetail(int postId, int scheduled_hires, String adviceText) {
		super();
		this.postId = postId;
		this.scheduled_hires = scheduled_hires;
		this.adviceText = adviceText;
	}

	/**
	 * デフォルトコンストラクタ。
	 */
	public PostDetail() {
	}

	/**
	 * 投稿IDを返す。
	 */
	public int getPostId() {
		return postId;
	}

	/**
	 * 投稿IDを設定する。
	 */
	public void setPostId(int postId) {
		this.postId = postId;
	}

	/**
	 * 予定採用人数を返す。
	 */
	public int getScheduled_hires() {
		return scheduled_hires;
	}

	/**
	 * 予定採用人数を設定する。
	 */
	public void setScheduled_hires(int scheduled_hires) {
		this.scheduled_hires = scheduled_hires;
	}

	/**
	 * アドバイス文を返す。
	 */
	public String getAdviceText() {
		return adviceText;
	}

	/**
	 * アドバイス文を設定する。
	 */
	public void setAdviceText(String adviceText) {
		this.adviceText = adviceText;
	}

}

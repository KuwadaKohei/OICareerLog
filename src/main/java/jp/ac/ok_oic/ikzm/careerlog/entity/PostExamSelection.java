package jp.ac.ok_oic.ikzm.careerlog.entity;

import java.io.Serializable;

/**
 * 投稿に紐づく試験選択肢を表す DTO。
 */
public class PostExamSelection implements Serializable{
	/**
	 * 投稿ID（postsテーブルへの外部キー、複合主キーの一部）
	 * NOT NULL
	 */
	private int postId;

	/**
	 * 試験内容ID（exam_contentsテーブルへの外部キー、複合主キーの一部）
	 * NOT NULL
	 */
	private int contentId;

	/**
	 * 試験内容の詳細テキスト
	 * 専門試験の内容等、任意で入力された詳細情報
	 * 最大500文字
	 */
	private String detailText;

	/**
	 * 全フィールド指定のコンストラクタ。
	 */
	public PostExamSelection(int postId, int contentId, String detailText) {
		super();
		this.postId = postId;
		this.contentId = contentId;
		this.detailText = detailText;
	}

	public PostExamSelection() {
		super();
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
	 * 試験コンテンツIDを返す。
	 */
	public int getContentId() {
		return contentId;
	}

	/**
	 * 試験コンテンツIDを設定する。
	 */
	public void setContentId(int contentId) {
		this.contentId = contentId;
	}

	/**
	 * 詳細テキストを返す。
	 */
	public String getDetailText() {
		return detailText;
	}

	/**
	 * 詳細テキストを設定する。
	 */
	public void setDetailText(String detailText) {
		this.detailText = detailText;
	}

}

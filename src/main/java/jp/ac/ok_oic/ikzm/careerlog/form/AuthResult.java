package jp.ac.ok_oic.ikzm.careerlog.form;

import jp.ac.ok_oic.ikzm.careerlog.entity.User;

/**
 * 認証処理の結果（ユーザー情報、新規登録フラグ、学科登録要否）をまとめた値オブジェクト。
 */
public class AuthResult {
	private User user;
	private boolean newUser;
	private boolean needsDepartment;

	/**
	 * デフォルトコンストラクタ。
	 */
	public AuthResult() {
	}

	/**
	 * すべての値を指定するコンストラクタ。
	 */
	public AuthResult(User user, boolean newUser, boolean needsDepartment) {
		this.user = user;
		this.newUser = newUser;
		this.needsDepartment = needsDepartment;
	}

	/**
	 * 認証済みユーザーを取得する。
	 */
	public User getUser() {
		return user;
	}

	/**
	 * 認証済みユーザーを設定する。
	 *
	 * @param user ユーザー
	 */
	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * 取得したユーザーが新規作成されたかどうか。
	 */
	public boolean isNewUser() {
		return newUser;
	}

	/**
	 * 新規ユーザーフラグを設定する。
	 *
	 * @param newUser 新規ユーザーフラグ
	 */
	public void setNewUser(boolean newUser) {
		this.newUser = newUser;
	}

	/**
	 * 学科登録が必要かどうかの判定結果。
	 */
	public boolean needsDepartment() {
		return needsDepartment;
	}

	/**
	 * 学科登録要否フラグを設定する。
	 *
	 * @param needsDepartment 学科登録要否フラグ
	 */
	public void setNeedsDepartment(boolean needsDepartment) {
		this.needsDepartment = needsDepartment;
	}
}

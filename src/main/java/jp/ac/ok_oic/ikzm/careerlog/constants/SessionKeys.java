package jp.ac.ok_oic.ikzm.careerlog.constants;

/**
 * セッションスコープに格納する属性のキー名を管理する定数クラス。
 *
 * セッション属性の取得時は session.getAttribute() が Object型を返すため、
 * 各定数のJavadocに記載された型情報を参照してキャストを行うこと。
 *
 * 使用例：
 * // 格納
 * session.setAttribute(SessionKeys.LOGIN_USER, user);
 *
 * // 取得（Javadocで型を確認してキャスト）
 * User user = (User) session.getAttribute(SessionKeys.LOGIN_USER);
 *
 * @see jakarta.servlet.http.HttpSession
 */
public final class SessionKeys {

	/**
	 * インスタンス化を禁止。
	 */
	private SessionKeys() {
	}

	/**
	 * ログイン済みユーザー情報のセッションキー。
	 *
	 * 格納される型: jp.ac.ok_oic.ikzm.careerlog.entity.User
	 * 格納タイミング: Google OAuth認証成功後、LoginServletで格納される。
	 * 値の意味: 現在ログイン中のユーザー情報（ID、名前、学科、権限等）を保持。
	 * このキーが存在しない場合、ユーザーは未ログイン状態とみなす。
	 * 更新タイミング: 学科登録時（NewUserServlet, NewTeacherServlet）に
	 * departmentIdとgradeが更新された状態で再格納される。
	 */
	public static final String LOGIN_USER = "loginUser";

	/**
	 * OAuth認証時のCSRF対策用stateパラメータのセッションキー。
	 *
	 * 格納される型: java.lang.String
	 * 格納タイミング: LoginServletでGoogle認証画面へリダイレクトする直前に
	 * ランダム生成された文字列を格納。
	 * 値の意味: OAuth 2.0のstateパラメータ値。認証コールバック時に
	 * リクエストのstateと照合し、一致しない場合はCSRF攻撃の可能性ありとして認証を拒否する。
	 * 削除タイミング: 認証フロー完了後、明示的に削除されないが、
	 * セッション破棄時に自動的に消失する。
	 */
	public static final String OAUTH_STATE = "oauth_State";

}

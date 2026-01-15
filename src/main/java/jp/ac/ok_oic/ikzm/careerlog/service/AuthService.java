package jp.ac.ok_oic.ikzm.careerlog.service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Collections;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;

import jp.ac.ok_oic.ikzm.careerlog.dao.UserDAO;
import jp.ac.ok_oic.ikzm.careerlog.entity.User;
import jp.ac.ok_oic.ikzm.careerlog.form.AuthResult;

/**
 * Google OAuthを利用した認証フローをまとめたサービスクラス。
 * 認可URLの生成、コールバック処理、ユーザー種別判定などを提供する。
 */
public class AuthService {

	// クライアントID等の取得
	private static final String CLIENT_ID = System.getenv("IKZM_OICAREER_OAUTH_CLIENT_ID");
	private static final String CLIENT_SECRET = System.getenv("IKZM_OICAREER_OAUTH_CLIENT_SECRET");
	private static final String REDIRECT_URI = System.getenv("IKZM_OICAREER_OAUTH_REDIRECT_URI");

	// 認証サービスで使用するインスタンス
	private static final NetHttpTransport HTTP = new NetHttpTransport();
	private static final GsonFactory JSON = GsonFactory.getDefaultInstance();

	private final UserDAO userDao;

	/**
	 * デフォルトコンストラクタ。各DAOを初期化する。
	 */
	public AuthService() {
		this.userDao = new UserDAO();
	}

	/**
	 * Google 認可サーバーへリダイレクトするためのURLを生成する。
	 *
	 * @param state CSRF 対策用のステート文字列
	 * @return 認可エンドポイントの完全 URL
	 */
	public String buildGoogleAuthUrl(String state) {
		return new GoogleAuthorizationCodeRequestUrl(
				CLIENT_ID,
				REDIRECT_URI,
				java.util.Arrays.asList("openid", "email", "profile"))
						.setAccessType("online")
						.setState(state)
						.build();
	}

	/**
	 * 認可コードを受け取り、アクセストークン・ID トークンを検証したうえで
	 * {@link AuthResult} を構築する。
	 *
	 * @param authCode Googleから返却された認可コード
	 * @return 認証結果を格納した {@link AuthResult}
	 * @throws Exception 検証失敗や通信障害が発生した場合
	 */
	public AuthResult authenticate(String authCode) throws Exception {

		try {
			// 認可コードをトークンに交換
			GoogleTokenResponse tokenResponse = new GoogleAuthorizationCodeTokenRequest(
					HTTP,
					JSON,
					"https://oauth2.googleapis.com/token",
					CLIENT_ID,
					CLIENT_SECRET,
					authCode,
					REDIRECT_URI).execute();

			String idTokenString = tokenResponse.getIdToken();

			// IDトークンの検証
			GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(HTTP, JSON)
					.setAudience(Collections.singletonList(CLIENT_ID))
					.setIssuer("https://accounts.google.com")
					.build();

			GoogleIdToken idToken = verifier.verify(idTokenString);

			if (idToken == null) {
				throw new Exception("AUTH-1001");
			}

			// ユーザー情報の抽出
			Payload payload = idToken.getPayload();
			String accountId = payload.getSubject();
			String email = payload.getEmail();
			String name = (String) payload.get("name");

			// 既存ユーザーの検索
			User user = this.userDao.findByGoogleAccountId(accountId);
			String userType = whichUserType(email);
			boolean newUser = false;

			// 新規ユーザーの登録
			if (user == null) {
				User newUserEntity = new User();
				newUserEntity.setGoogleAccountId(accountId);
				newUserEntity.setEmail(email);
				newUserEntity.setUserType(userType);
				newUserEntity.setName(name);
				newUserEntity.setDepartmentId(0);
				newUserEntity.setGrade(0);
				newUserEntity.setActive(true);
				newUserEntity.setAdmin(false);

				int newId = this.userDao.insert(newUserEntity);
				user = new User(newId, accountId, email, userType, name, 0, 0, true, false);
				newUser = true;
			}

			// 学科登録状況の確認
			boolean needsDept = !this.userDao.hasDepartment(user.getUserId());

			return new AuthResult(user, newUser, needsDept);

		} catch (IOException | GeneralSecurityException e) {
			throw new Exception("AUTH-1000");
		}
	}

	/**
	 * メールアドレスからユーザー種別を推定する。
	 *
	 * @param email 判定対象メールアドレス
	 * @return student/teacher のいずれか
	 */
	private String whichUserType(String email) {
		// 8桁の数字+@oic-ok.ac.jpを判定します。特に正規表現の案内はしません。変更の際はググって。
		if (email.matches("^\\d{8}@oic-ok\\.ac\\.jp$")) {
			return "student";
		} else {
			return "teacher";
		}
	}

	/**
	 * CSRF 対策用のステート文字列を生成する。
	 *
	 * @return URLセーフなBase64 文字列
	 */
	public String generateState() {

		SecureRandom random = new SecureRandom();
		int stateByteLength = 16;

		// 実際の文字列生成
		byte[] bytes = new byte[stateByteLength];
		random.nextBytes(bytes);

		// Httpリクエストで使用するため、URLとして使えるようにエンコード
		return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);

	}
}

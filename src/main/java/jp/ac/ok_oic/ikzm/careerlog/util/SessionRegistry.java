package jp.ac.ok_oic.ikzm.careerlog.util;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.servlet.http.HttpSession;

/**
 * ユーザーIDとHttpSessionの関連を追跡し、特定ユーザーの強制ログアウトを実現するためのレジストリ。
 */
public final class SessionRegistry {

	private SessionRegistry() {
	}

	private static final String SESSION_USER_ID_ATTRIBUTE = "__session_registry_user_id";
	private static final Map<Integer, Set<String>> USER_SESSION_IDS = new ConcurrentHashMap<>();
	private static final Map<String, HttpSession> ACTIVE_SESSIONS = new ConcurrentHashMap<>();

	/**
	 * 新規ログインセッションを登録する。
	 *
	 * @param session 対象セッション
	 * @param userId  セッション所有者のユーザーID
	 */
	public static void register(HttpSession session, int userId) {
		if (session == null) {
			return;
		}
		// サーバー再起動後も参照しやすいようユーザーIDをセッション属性に持たせる
		session.setAttribute(SESSION_USER_ID_ATTRIBUTE, userId);
		ACTIVE_SESSIONS.put(session.getId(), session);
		if (!USER_SESSION_IDS.containsKey(userId)) {
			USER_SESSION_IDS.put(userId, ConcurrentHashMap.newKeySet());
		}
		USER_SESSION_IDS.get(userId).add(session.getId());
	}

	/**
	 * セッションの関連付けを解除する。
	 *
	 * @param session 終了したセッション
	 */
	public static void unregister(HttpSession session) {
		if (session == null) {
			return;
		}
		ACTIVE_SESSIONS.remove(session.getId());
		Object userIdAttr = session.getAttribute(SESSION_USER_ID_ATTRIBUTE);
		if (userIdAttr instanceof Integer userId) {
			Set<String> sessionIds = USER_SESSION_IDS.get(userId);
			if (sessionIds != null) {
				sessionIds.remove(session.getId());
				if (sessionIds.isEmpty()) {
					// 最後のセッションが消えたタイミングでマップもクリーンアップ
					USER_SESSION_IDS.remove(userId, sessionIds);
				}
			}
		}
	}

	/**
	 * 指定ユーザーに紐づく全セッションを無効化する。
	 *
	 * @param userId 強制ログアウトさせたいユーザーID
	 */
	public static void invalidateSessions(int userId) {
		Set<String> sessionIds = USER_SESSION_IDS.remove(userId);
		if (sessionIds == null) {
			return;
		}
		for (String sessionId : sessionIds) {
			HttpSession session = ACTIVE_SESSIONS.remove(sessionId);
			if (session != null) {
				try {
					// BAN時などに利用。既に無効化済みでIllegalStateになっても握りつぶす
					session.invalidate();
				} catch (IllegalStateException ignored) {
					// 既に無効化済み
				}
			}
		}
	}
}

package jp.ac.ok_oic.ikzm.careerlog.listener;

import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;

import jp.ac.ok_oic.ikzm.careerlog.util.SessionRegistry;

/**
 * セッション破棄イベントをフックして SessionRegistry をクリーンに保つリスナー。
 */
@WebListener
public class SessionCleanupListener implements HttpSessionListener {

	@Override
	public void sessionDestroyed(HttpSessionEvent se) {
		SessionRegistry.unregister(se.getSession());
	}
}

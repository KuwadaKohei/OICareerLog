package jp.ac.ok_oic.ikzm.careerlog.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SLF4J ロガーの生成を一元化するユーティリティ。
 */
public final class Logging {

	private static final Logger ACTION_LOGGER = LoggerFactory.getLogger("USER_ACTION");

	private Logging() {
	}

	private static final DateTimeFormatter ERROR_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	/**
	 * クラス単位で標準ロガーを取得する。
	 *
	 * @param type 出力元クラス
	 * @return ロガー
	 */
	public static Logger getLogger(Class<?> type) {
		return LoggerFactory.getLogger(type);
	}

	/**
	 * ユーザー操作を記録する専用ロガーを取得する。
	 *
	 * @return 操作ログ用ロガー
	 */
	public static Logger actionLogger() {
		return ACTION_LOGGER;
	}

	/**
	 * 指定フォーマットでエラーログを出力する。
	 *
	 * @param logger    出力対象ロガー
	 * @param clazz     例外が発生したクラス
	 * @param message   追加メッセージ
	 * @param throwable 例外
	 */
	public static void logError(Logger logger, Class<?> clazz, String message, Throwable throwable) {
		String timestamp = LocalDateTime.now().format(ERROR_TIME_FORMATTER);
		String baseMessage = String.format("ERROR : %s/%s %s", timestamp, clazz.getName(), message);
		logger.error(baseMessage, throwable);
	}
}

package jp.ac.ok_oic.ikzm.careerlog.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import io.github.cdimascio.dotenv.Dotenv;

/**
 * データベース接続を提供するユーティリティ DAO。
 * 
 * @author KuwadaKohei
 */
public class DBConnectionManager {

	/**
	 * 環境変数または.envファイルの設定を用いてMySQL/MariaDBへの接続を確立する。
	 *
	 * @return オープン済み JDBC コネクション
	 * @throws SQLException ドライバ読み込み失敗や接続失敗時
	 */
	public Connection getConnection() throws SQLException {
		try {
			Class.forName("org.mariadb.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			throw new SQLException("MariaDB JDBCドライバーの読み込みに失敗しました。", e);
		}

		// .envファイルを読み込む（存在しない場合は無視）
		Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

		// .env から取得し、なければ環境変数から取得する
		String url = dotenv.get("IKZM_OICAREER_DB_URL", System.getenv("IKZM_OICAREER_DB_URL"));
		String user = dotenv.get("IKZM_OICAREER_DB_USER", System.getenv("IKZM_OICAREER_DB_USER"));
		String password = dotenv.get("IKZM_OICAREER_DB_PASSWORD", System.getenv("IKZM_OICAREER_DB_PASSWORD"));

		return DriverManager.getConnection(url, user, password);
	}
}

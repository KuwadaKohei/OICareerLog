package jp.ac.ok_oic.ikzm.careerlog.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import jp.ac.ok_oic.ikzm.careerlog.entity.User;

/**
 * ユーザーテーブル(users)を操作するDAO。
 */
public class UserDAO {

	private final DBConnectionManager connectionManager = new DBConnectionManager();

	/**
	 * メールアドレスをLIKE検索し、部分一致するユーザーを取得する。
	 * 
	 * @param keyword 検索語
	 * @return ユーザーのリスト
	 */
	public ArrayList<User> findListByEmail(String keyword) throws Exception {
		ArrayList<User> list = new ArrayList<User>();
		String sql = "SELECT user_id, google_account_id, email, user_type, name, department_id, grade, is_active, is_admin FROM users WHERE email LIKE ? ORDER BY email";

		try (Connection conn = connectionManager.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, likePattern(keyword));
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				list.add(mapUser(rs));
			}
		} catch (SQLException e) {
			throw new Exception("DB-0060");
		}

		return list;
	}

	/**
	 * ユーザー名をLIKE検索し、部分一致するユーザーを取得する。
	 * 
	 * @param keyword 検索語
	 * @return ユーザーのリスト
	 */
	public ArrayList<User> findListByUsername(String keyword) throws Exception {
		ArrayList<User> list = new ArrayList<User>();
		String sql = "SELECT user_id, google_account_id, email, user_type, name, department_id, grade, is_active, is_admin FROM users WHERE name LIKE ? ORDER BY name";

		try (Connection conn = connectionManager.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, likePattern(keyword));
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				list.add(mapUser(rs));
			}
		} catch (SQLException e) {
			throw new Exception("DB-0060");
		}

		return list;
	}

	/**
	 * アクティブフラグを更新する。
	 * 
	 * @param userId   ユーザーID
	 * @param isActive 有効にするか
	 * @return 更新成功時true
	 */
	public boolean updateActiveStatus(int userId, boolean isActive) throws Exception {
		boolean updated = false;
		String sql = "UPDATE users SET is_active = ? WHERE user_id = ?";

		try (Connection conn = connectionManager.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setBoolean(1, isActive);
			stmt.setInt(2, userId);
			updated = stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			throw new Exception("DB-0061");
		}

		return updated;
	}

	/**
	 * ユーザーIDで1件取得する。
	 * 
	 * @param userId ユーザーID
	 * @return DTO（見つからない場合はnull）
	 */
	public User findById(int userId) throws Exception {
		User user = null;
		String sql = "SELECT user_id, google_account_id, email, user_type, name, department_id, grade, is_active, is_admin FROM users WHERE user_id = ?";

		try (Connection conn = connectionManager.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, userId);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				user = mapUser(rs);
			}
		} catch (SQLException e) {
			throw new Exception("DB-0062");
		}

		return user;
	}

	/**
	 * GoogleアカウントIDでユーザーを取得する。
	 * 
	 * @param googleAccountId Google側のサブID
	 * @return DTO（見つからなければnull）
	 */
	public User findByGoogleAccountId(String googleAccountId) throws Exception {
		User user = null;
		String sql = "SELECT user_id, google_account_id, email, user_type, name, department_id, grade, is_active, is_admin FROM users WHERE google_account_id = ?";

		try (Connection conn = connectionManager.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, googleAccountId);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				user = mapUser(rs);
			}
		} catch (SQLException e) {
			throw new Exception("DB-0062");
		}

		return user;
	}

	/**
	 * Userオブジェクトを新規登録する。
	 * 
	 * @param user 登録するユーザー情報
	 * @return 採番されたユーザーID（失敗時0）
	 */
	public int insert(User user) throws Exception {
		int generatedId = 0;
		String sql = "INSERT INTO users (google_account_id, email, user_type, name, department_id, grade, is_active, is_admin) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

		try (Connection conn = connectionManager.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			stmt.setString(1, user.getGoogleAccountId());
			stmt.setString(2, user.getEmail());
			stmt.setString(3, user.getUserType());
			stmt.setString(4, user.getName());
			stmt.setInt(5, user.getDepartmentId());
			stmt.setInt(6, user.getGrade());
			stmt.setBoolean(7, user.isActive());
			stmt.setBoolean(8, user.isAdmin());

			int affected = stmt.executeUpdate();
			if (affected > 0) {
				ResultSet rs = stmt.getGeneratedKeys();
				if (rs.next()) {
					generatedId = rs.getInt(1);
				}
			}
		} catch (SQLException e) {
			throw new Exception("DB-0063");
		}

		return generatedId;
	}

	/**
	 * 学科情報が登録済みかを判定する。
	 * 
	 * @param userId ユーザーID
	 * @return department_idが1以上ならtrue
	 */
	public boolean hasDepartment(int userId) throws Exception {
		boolean exists = false;
		String sql = "SELECT department_id FROM users WHERE user_id = ?";

		try (Connection conn = connectionManager.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, userId);
			// department_id が 0 より大きければ登録済みとみなす
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				exists = rs.getInt("department_id") > 0;
			}
		} catch (SQLException e) {
			throw new Exception("DB-0064");
		}

		return exists;
	}

	/**
	 * 部署・学年情報を更新する。
	 * 
	 * @param userId       ユーザーID
	 * @param departmentId 学科ID
	 * @param grade        学年
	 * @return 更新成功時true
	 */
	public boolean updateDepartmentInfo(int userId, int departmentId, int grade) throws Exception {
		boolean updated = false;
		String sql = "UPDATE users SET department_id = ?, grade = ? WHERE user_id = ?";

		try (Connection conn = connectionManager.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, departmentId);
			stmt.setInt(2, grade);
			stmt.setInt(3, userId);
			updated = stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			throw new Exception("DB-0065");
		}

		return updated;
	}

	/**
	 * ResultSetの1行をUserDTOに変換する。
	 */
	private User mapUser(ResultSet rs) throws SQLException {
		User user = new User();
		user.setUserId(rs.getInt("user_id"));
		user.setGoogleAccountId(rs.getString("google_account_id"));
		user.setEmail(rs.getString("email"));
		user.setUserType(rs.getString("user_type"));
		user.setName(rs.getString("name"));
		user.setDepartmentId(rs.getInt("department_id"));
		user.setGrade(rs.getInt("grade"));
		user.setActive(rs.getBoolean("is_active"));
		user.setAdmin(rs.getBoolean("is_admin"));
		return user;
	}

	/**
	 * LIKE検索用のパターン文字列を作成する。
	 * 
	 * <p>セキュリティ対策として、ワイルドカード文字（%, _）を
	 * エスケープしてからパターンを構築します。</p>
	 */
	private String likePattern(String keyword) {
		if (keyword == null || keyword.isBlank()) {
			return "%";
		}
		// ワイルドカード文字をエスケープ
		String escaped = keyword
				.replace("\\", "\\\\")
				.replace("%", "\\%")
				.replace("_", "\\_");
		return "%" + escaped + "%";
	}
}

package jp.ac.ok_oic.ikzm.careerlog.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;

import jp.ac.ok_oic.ikzm.careerlog.entity.Post;

/**
 * 削除済み投稿履歴テーブル(deleted_posts)を操作するDAOクラス。
 */
public class DeletedPostDAO {

	private final DBConnectionManager connectionManager = new DBConnectionManager();

	/**
	 * Post DTOから削除履歴を作成してINSERTする。
	 *
	 * @param post      削除される投稿
	 * @param deletedBy 削除実行者ID
	 * @return 挿入された削除履歴ID
	 * @throws Exception DB接続エラーまたはSQL実行エラー
	 */
	public int insertFromPost(Post post, Integer deletedBy) throws Exception {
		try (Connection conn = connectionManager.getConnection()) {
			return insertFromPost(post, deletedBy, conn);
		} catch (SQLException e) {
			throw new Exception("DB-0090");
		}
	}

	/**
	 * Post DTOから削除履歴を作成してINSERTする（既存コネクション使用）。
	 *
	 * @param post      削除される投稿
	 * @param deletedBy 削除実行者ID（nullも可）
	 * @param conn      既存のコネクション
	 * @return 挿入された削除履歴ID
	 * @throws Exception DB接続エラーまたはSQL実行エラー
	 */
	public int insertFromPost(Post post, Integer deletedBy, Connection conn) throws Exception {
		int generatedId = 0;
		String sql = "INSERT INTO deleted_posts "
				+ "(original_post_id, user_id, department_id, method_id, recruitment_no, "
				+ "company_name, venue_address, exam_date, grade, is_anonymous, "
				+ "created_at, updated_at, deleted_at, deleted_by) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			stmt.setInt(1, post.getPostId());
			stmt.setInt(2, post.getUserId());
			stmt.setInt(3, post.getDepartmentId());
			stmt.setInt(4, post.getMethodId());
			setNullableInt(stmt, 5, post.getRecruitmentNo());
			stmt.setString(6, post.getCompanyName());
			stmt.setString(7, post.getVenueAddress());
			setDateParameter(stmt, 8, post.getExamDate());
			stmt.setInt(9, post.getGrade());
			stmt.setBoolean(10, post.isAnonymous());
			stmt.setTimestamp(11, Timestamp.valueOf(post.getCreateAt()));
			stmt.setTimestamp(12, Timestamp.valueOf(post.getUpdatedAt()));
			stmt.setTimestamp(13, Timestamp.valueOf(LocalDateTime.now()));
			if (deletedBy != null) {
				stmt.setInt(14, deletedBy);
			} else {
				stmt.setNull(14, Types.INTEGER);
			}

			int affected = stmt.executeUpdate();
			if (affected > 0) {
				ResultSet rs = stmt.getGeneratedKeys();
				if (rs.next()) {
					generatedId = rs.getInt(1);
				}
			}
		} catch (SQLException e) {
			throw new Exception("DB-0090");
		}

		return generatedId;
	}

	/**
	 * LocalDateをSQL DATEへ変換して設定する。
	 *
	 * @param stmt  PreparedStatement
	 * @param index プレースホルダ位置
	 * @param date  変換元日付
	 * @throws SQLException 変換時の例外
	 */
	private void setDateParameter(PreparedStatement stmt, int index, java.time.LocalDate date) throws SQLException {
		if (date != null) {
			stmt.setDate(index, Date.valueOf(date));
		} else {
			stmt.setNull(index, Types.DATE);
		}
	}

	/**
	 * 0以下をNULL扱いにしつつ整数値を設定する。
	 *
	 * @param stmt  PreparedStatement
	 * @param index プレースホルダ位置
	 * @param value 設定する値
	 * @throws SQLException 設定時の例外
	 */
	private void setNullableInt(PreparedStatement stmt, int index, int value) throws SQLException {
		if (value > 0) {
			stmt.setInt(index, value);
		} else {
			stmt.setNull(index, Types.INTEGER);
		}
	}
}

package jp.ac.ok_oic.ikzm.careerlog.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jp.ac.ok_oic.ikzm.careerlog.entity.PostExamSelection;

/**
 * 投稿と試験内容の関連(post_exam_selections)を扱うDAO。
 */
public class PostExamSelectionDAO {

	private final DBConnectionManager connectionManager = new DBConnectionManager();

	/**
	 * 投稿IDに紐づく試験選択を全件取得する。
	 * 
	 * @param postId 投稿ID
	 * @return DTOリスト
	 */
	public List<PostExamSelection> findByPostId(int postId) throws Exception {
		List<PostExamSelection> list = new ArrayList<PostExamSelection>();
		String sql = "SELECT post_id, content_id, detail_text FROM post_exam_selections WHERE post_id = ? ORDER BY content_id";

		try (Connection conn = connectionManager.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, postId);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				list.add(mapSelection(rs));
			}
		} catch (SQLException e) {
			throw new Exception("DB-0020");
		}

		return list;
	}

	/**
	 * 指定投稿の関連レコードを削除する。
	 * 
	 * @param postId 投稿ID
	 * @return 削除件数が1件以上ならtrue
	 */
	public boolean deleteByPostId(int postId) throws Exception {
		try (Connection conn = connectionManager.getConnection()) {
			return deleteByPostId(postId, conn);
		} catch (SQLException e) {
			throw new Exception("DB-0022");
		}
	}

	/**
	 * 既存コネクションを利用して関連レコードを削除する。
	 *
	 * @param postId 投稿ID
	 * @param conn   共有トランザクション用コネクション
	 * @return 実行成功時true
	 * @throws SQLException SQL例外
	 */
	public boolean deleteByPostId(int postId, Connection conn) throws Exception {
		String sql = "DELETE FROM post_exam_selections WHERE post_id = ?";

		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, postId);
			stmt.executeUpdate();
		} catch (SQLException e) {
			throw new Exception("DB-0022");
		}

		return true;
	}

	/**
	 * 試験選択を一括登録する。
	 * 
	 * @param selections 登録対象リスト
	 * @return 全件成功した場合true（空リストはtrue）
	 */
	public boolean insertList(List<PostExamSelection> selections) throws Exception {
		if (selections == null || selections.isEmpty()) {
			return true;
		}

		try (Connection conn = connectionManager.getConnection()) {
			return insertList(selections, conn);
		} catch (SQLException e) {
			throw new Exception("DB-0021");
		}
	}

	/**
	 * 既存コネクションを利用して試験選択を一括登録する。
	 *
	 * @param selections 登録対象リスト
	 * @param conn       共有トランザクション用コネクション
	 * @return 全件成功した場合true
	 * @throws SQLException SQL例外
	 */
	public boolean insertList(List<PostExamSelection> selections, Connection conn) throws Exception {
		if (selections == null || selections.isEmpty()) {
			return true;
		}

		boolean success = false;
		String sql = "INSERT INTO post_exam_selections (post_id, content_id, detail_text) VALUES (?, ?, ?)";

		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			for (PostExamSelection selection : selections) {
				stmt.setInt(1, selection.getPostId());
				stmt.setInt(2, selection.getContentId());
				stmt.setString(3, selection.getDetailText());
				stmt.addBatch();
			}
			int[] results = stmt.executeBatch();
			success = results.length == selections.size();
		} catch (SQLException e) {
			throw new Exception("DB-0021");
		}

		return success;
	}

	/**
	 * ResultSetをPostExamSelection DTOへ変換する。
	 */
	private PostExamSelection mapSelection(ResultSet rs) throws SQLException {
		PostExamSelection selection = new PostExamSelection(
				rs.getInt("post_id"),
				rs.getInt("content_id"),
				rs.getString("detail_text"));
		return selection;
	}
}

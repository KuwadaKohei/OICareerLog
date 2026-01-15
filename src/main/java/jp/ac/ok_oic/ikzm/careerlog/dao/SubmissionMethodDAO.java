package jp.ac.ok_oic.ikzm.careerlog.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jp.ac.ok_oic.ikzm.careerlog.entity.SubmissionMethod;

/**
 * 応募方法マスタ(submission_methods)を扱うDAO。
 */
public class SubmissionMethodDAO {

	private final DBConnectionManager connectionManager = new DBConnectionManager();

	/**
	 * すべての応募方法を取得する。
	 * 
	 * @return 応募方法リスト
	 */
	public List<SubmissionMethod> findAll() throws Exception {
		List<SubmissionMethod> list = new ArrayList<SubmissionMethod>();
		String sql = "SELECT method_id, method_name FROM submission_methods ORDER BY method_id";

		try (Connection conn = connectionManager.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				list.add(mapMethod(rs));
			}
		} catch (SQLException e) {
			throw new Exception("DB-0050");
		}

		return list;
	}

	/**
	 * 主キーで応募方法を取得する。
	 * 
	 * @param methodId 応募方法ID
	 * @return DTO（なければnull）
	 */
	public SubmissionMethod findById(int methodId) throws Exception {
		SubmissionMethod method = null;
		String sql = "SELECT method_id, method_name FROM submission_methods WHERE method_id = ?";

		try (Connection conn = connectionManager.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, methodId);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				method = mapMethod(rs);
			}
		} catch (SQLException e) {
			throw new Exception("DB-0050");
		}

		return method;
	}

	/**
	 * ResultSetをSubmissionMethod DTOへ変換する。
	 */
	private SubmissionMethod mapMethod(ResultSet rs) throws SQLException {
		SubmissionMethod method = new SubmissionMethod();
		method.setMethodId(rs.getInt("method_id"));
		method.setMethodName(rs.getString("method_name"));
		return method;
	}
}

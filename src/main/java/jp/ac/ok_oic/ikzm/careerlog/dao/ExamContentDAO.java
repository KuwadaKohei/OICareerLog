package jp.ac.ok_oic.ikzm.careerlog.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jp.ac.ok_oic.ikzm.careerlog.entity.ExamContent;

/**
 * 試験内容マスタ(exam_content)を扱うDAO。
 */
public class ExamContentDAO {

	private final DBConnectionManager connectionManager = new DBConnectionManager();

	/**
	 * 全試験内容をID昇順で取得する。
	 *
	 * @return DTOリスト
	 */
	public List<ExamContent> findAll() throws Exception {
		List<ExamContent> list = new ArrayList<ExamContent>();
		String sql = "SELECT content_id, category, name, needs_detail, select_detail, detail_example FROM exam_contents ORDER BY content_id";

		try (Connection conn = connectionManager.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				list.add(mapExamContent(rs));
			}
		} catch (SQLException e) {
			throw new Exception("DB-0040");
		}

		return list;
	}

	/**
	 * ResultSetをExamContent DTOへ変換する。
	 *
	 * @param rs クエリ結果
	 * @return DTO
	 * @throws SQLException 変換例外
	 */
	private ExamContent mapExamContent(ResultSet rs) throws SQLException {
		ExamContent content = new ExamContent();
		content.setContentId(rs.getInt("content_id"));
		content.setContentCategory(rs.getString("category"));
		content.setContentName(rs.getString("name"));
		content.setNeedsDetail(rs.getBoolean("needs_detail"));
		content.setSelectDetail(rs.getBoolean("select_detail"));
		content.setDetailExample(rs.getString("detail_example"));
		return content;
	}
}

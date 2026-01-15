package jp.ac.ok_oic.ikzm.careerlog.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.ac.ok_oic.ikzm.careerlog.entity.ExamDetailOption;

/**
 * 試験詳細選択肢マスタのDAO。
 */
public class ExamDetailOptionDAO {

	private final DBConnectionManager connectionManager = new DBConnectionManager();

	/**
	 * 全ての試験詳細選択肢を取得する。
	 *
	 * @return 試験詳細選択肢のリスト
	 * @throws Exception DB接続エラーまたはSQL実行エラー
	 */
	public List<ExamDetailOption> findAll() throws Exception {
		List<ExamDetailOption> list = new ArrayList<>();
		String sql = "SELECT option_id, content_id, option_text, display_order "
				+ "FROM exam_detail_options "
				+ "ORDER BY content_id, display_order";

		try (Connection conn = connectionManager.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {
				ExamDetailOption option = new ExamDetailOption(
						rs.getInt("option_id"),
						rs.getInt("content_id"),
						rs.getString("option_text"),
						rs.getInt("display_order"));
				list.add(option);
			}
		} catch (SQLException e) {
			throw new Exception("DB-0066", e);
		}
		return list;
	}

	/**
	 * 試験内容IDをキーにした選択肢のMapを取得する。
	 *
	 * @return Map<contentId, List<ExamDetailOption>>
	 * @throws Exception DB接続エラーまたはSQL実行エラー
	 */
	public Map<Integer, List<ExamDetailOption>> findAllAsMap() throws Exception {
		Map<Integer, List<ExamDetailOption>> map = new HashMap<>();
		List<ExamDetailOption> allOptions = findAll();

		for (ExamDetailOption option : allOptions) {
			int contentId = option.getContentId();
			if (!map.containsKey(contentId)) {
				map.put(contentId, new ArrayList<>());
			}
			map.get(contentId).add(option);
		}

		return map;
	}
}

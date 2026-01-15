package jp.ac.ok_oic.ikzm.careerlog.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jp.ac.ok_oic.ikzm.careerlog.entity.Department;

/**
 * 学科マスタ(departments)を操作するDAO。
 */
public class DepartmentDAO {

	private final DBConnectionManager connectionManager = new DBConnectionManager();

	/**
	 * 全学科を取得する。
	 * 
	 * @return 学科リスト（0件時は空）
	 */
	public List<Department> findAll() throws Exception {
		List<Department> list = new ArrayList<Department>();
		String sql = "SELECT department_id, department_name FROM departments ORDER BY department_id";

		try (Connection conn = connectionManager.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			// 学科Idの昇順で表示したいので ORDER BY 付き
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				list.add(mapDepartment(rs));
			}
		} catch (SQLException e) {
			throw new Exception("DB-0030");
		}

		return list;
	}

	/**
	 * 主キーで学科を1件取得する。
	 * 
	 * @param departmentId 学科ID
	 * @return DTO（見つからない場合はnull）
	 */
	public Department findById(int departmentId) throws Exception {
		Department department = null;
		String sql = "SELECT department_id, department_name FROM departments WHERE department_id = ?";

		try (Connection conn = connectionManager.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, departmentId);
			// 1件のみ期待しているので if で十分
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				department = mapDepartment(rs);
			}
		} catch (SQLException e) {
			throw new Exception("DB-0030");
		}

		return department;
	}

	/**
	 * ResultSetをDepartment DTOへ変換する。
	 * 
	 * @param rs クエリ結果
	 * @return DTO
	 * @throws SQLException 変換エラー
	 */
	private Department mapDepartment(ResultSet rs) throws SQLException {
		Department department = new Department();
		department.setDepartmentId(rs.getInt("department_id"));
		department.setDepartmentName(rs.getString("department_name"));
		return department;
	}
}

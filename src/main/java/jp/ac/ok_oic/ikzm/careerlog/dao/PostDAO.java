package jp.ac.ok_oic.ikzm.careerlog.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jp.ac.ok_oic.ikzm.careerlog.entity.Post;

/**
 * 投稿テーブル(posts)を操作するDAOクラス。
 * 各メソッドは必要に応じて新規にDBコネクションを取得し、
 * データアクセスを提供する。削除された投稿はdeleted_postsテーブルに保管される。
 */
public class PostDAO {

	private final DBConnectionManager connectionManager = new DBConnectionManager();

	// 再利用するSELECT句を生成する。
	private final String SELECT_CLAUSE = "SELECT post_id, user_id, department_id, method_id, "
			+ "recruitment_no, company_name, venue_address, exam_date, grade, is_anonymous, "
			+ "created_at, updated_at FROM posts ";
	/**
	 * 投稿を更新日時の降順で全件取得する。
	 *
	 * @return 検索結果のDTOリスト（0件の場合は空リスト）
	 */
	public List<Post> findAll() throws Exception {
		List<Post> posts = new ArrayList<Post>();
		String sql = SELECT_CLAUSE + "ORDER BY updated_at DESC";

		try (Connection conn = connectionManager.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {
			// 論理削除済みを除外しつつ更新日時の降順で取得
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				posts.add(mapPost(rs));
			}
		} catch (SQLException e) {
			throw new Exception("DB-0001");
		}

		return posts;
	}

	/**
	 * 投稿IDをキーに投稿を1件取得する。
	 *
	 * @param postId 投稿ID
	 * @return DTO（見つからない場合はnull
	 */
	public Post findById(int postId) throws Exception {
		Post post = null;
		String sql = SELECT_CLAUSE + "WHERE post_id = ?";

		try (Connection conn = connectionManager.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setInt(1, postId);
			// 主キー検索なので1件のみ確認
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				post = mapPost(rs);
			}
		} catch (SQLException e) {
			throw new Exception("DB-0001");
		}

		return post;
	}

	/**
	 * 指定ユーザーが作成した投稿を全件取得する。
	 *
	 * @param userId ユーザーID
	 * @return 投稿リスト
	 */
	public List<Post> findAllByUserId(int userId) throws Exception {
		List<Post> posts = new ArrayList<Post>();
		String sql = SELECT_CLAUSE
				+ "WHERE user_id = ? ORDER BY updated_at DESC";

		try (Connection conn = connectionManager.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setInt(1, userId);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				posts.add(mapPost(rs));
			}
		} catch (SQLException e) {
			throw new Exception("DB-0001");
		}

		return posts;
	}

	/**
	 * 求人票番号を条件に投稿を検索する。
	 *
	 * @param recruitmentNo 求人票番号
	 * @return 投稿リスト
	 */
	public List<Post> findAllByRecruitmentNo(int recruitmentNo) throws Exception {
		List<Post> posts = new ArrayList<Post>();
		String sql = SELECT_CLAUSE
				+ "WHERE recruitment_no = ? ORDER BY updated_at DESC";

		try (Connection conn = connectionManager.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setInt(1, recruitmentNo);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				posts.add(mapPost(rs));
			}
		} catch (SQLException e) {
			throw new Exception("DB-0001");
		}

		return posts;
	}

	/**
	 * ユーザーIDと求人票番号を条件に投稿を検索する。
	 *
	 * @param userId        ユーザーID
	 * @param recruitmentNo 求人票番号
	 * @return 投稿リスト
	 */
	public List<Post> findAllByUserIdAndRecruitmentNo(int userId, int recruitmentNo) throws Exception {
		List<Post> posts = new ArrayList<Post>();
		String sql = SELECT_CLAUSE
				+ "WHERE user_id = ? AND recruitment_no = ? ORDER BY updated_at DESC";

		try (Connection conn = connectionManager.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setInt(1, userId);
			stmt.setInt(2, recruitmentNo);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				posts.add(mapPost(rs));
			}
		} catch (SQLException e) {
			throw new Exception("DB-0001");
		}

		return posts;
	}

	/**
	 * 受験日を条件に投稿を検索する。
	 *
	 * @param examDate 受験日
	 * @return 投稿リスト
	 */
	public List<Post> findAllByExamDate(LocalDate examDate) throws Exception {
		List<Post> posts = new ArrayList<Post>();
		String sql = SELECT_CLAUSE
				+ "WHERE exam_date = ? ORDER BY updated_at DESC";

		try (Connection conn = connectionManager.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {

			if (examDate != null) {
				stmt.setDate(1, Date.valueOf(examDate));
			} else {
				stmt.setNull(1, Types.DATE);
			}
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				posts.add(mapPost(rs));
			}
		} catch (SQLException e) {
			throw new Exception("DB-0001");
		}

		return posts;
	}

	/**
	 * ユーザーIDと受験日を条件に投稿を検索する。
	 *
	 * @param userId   ユーザーID
	 * @param examDate 受験日
	 * @return 投稿リスト
	 */
	public List<Post> findAllByUserIdAndExamDate(int userId, LocalDate examDate) throws Exception {
		List<Post> posts = new ArrayList<Post>();
		String sql = SELECT_CLAUSE
				+ "WHERE user_id = ? AND exam_date = ? ORDER BY updated_at DESC";

		try (Connection conn = connectionManager.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setInt(1, userId);
			if (examDate != null) {
				stmt.setDate(2, Date.valueOf(examDate));
			} else {
				stmt.setNull(2, Types.DATE);
			}
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				posts.add(mapPost(rs));
			}
		} catch (SQLException e) {
			throw new Exception("DB-0001");
		}

		return posts;
	}

	/**
	 * 会社名または会場住所のLIKE検索を行う。
	 *
	 * @param keyword キーワード
	 * @return 投稿リスト
	 */
	public List<Post> findAllByKeyword(String keyword) throws Exception {
		List<Post> posts = new ArrayList<Post>();
		String sql = SELECT_CLAUSE
				+ "WHERE (company_name LIKE ? OR venue_address LIKE ?) ORDER BY updated_at DESC";

		try (Connection conn = connectionManager.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {

			// 検索キーワードは両端に%をつけて部分一致検索
			String pattern = buildLikePattern(keyword);
			stmt.setString(1, pattern);
			stmt.setString(2, pattern);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				posts.add(mapPost(rs));
			}
		} catch (SQLException e) {
			throw new Exception("DB-0001");
		}

		return posts;
	}

	/**
	 * ユーザーIDを含むキーワード検索を行う。
	 *
	 * @param userId  ユーザーID
	 * @param keyword キーワード
	 * @return 投稿リスト
	 */
	public List<Post> findAllByUserIdAndKeyword(int userId, String keyword) throws Exception {
		List<Post> posts = new ArrayList<Post>();
		String sql = SELECT_CLAUSE
				+ "WHERE user_id = ? "
				+ "AND (company_name LIKE ? OR venue_address LIKE ?) ORDER BY updated_at DESC";

		try (Connection conn = connectionManager.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {

			String pattern = buildLikePattern(keyword);
			stmt.setInt(1, userId);
			stmt.setString(2, pattern);
			stmt.setString(3, pattern);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				posts.add(mapPost(rs));
			}
		} catch (SQLException e) {
			throw new Exception("DB-0001");
		}

		return posts;
	}

	/**
	 * 投稿を新規登録し、採番されたIDを返却する。
	 *
	 * トランザクションを共有したい場合は、外部でコネクションを管理しPreparedStatementを組み立てる実装へ差し替えることを想定している。
	 *
	 * @param post 登録対象DTO
	 * @return 採番されたID（失敗時は0）
	 */
	public int insert(Post post) throws Exception {
		try (Connection conn = connectionManager.getConnection()) {
			return insert(post, conn);
		} catch (SQLException e) {
			throw new Exception("DB-0002");
		}
	}

	/**
	 * 既存コネクションを利用して投稿を登録する。
	 *
	 * @param post 登録対象DTO
	 * @param conn 共有トランザクション用コネクション
	 * @return 採番されたID
	 * @throws SQLException SQL例外
	 */
	public int insert(Post post, Connection conn) throws Exception {
		int generatedId = 0;
		String sql = "INSERT INTO posts (user_id, department_id, method_id, recruitment_no, "
				+ "company_name, venue_address, exam_date, grade, is_anonymous, created_at, "
				+ "updated_at) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

			bindInsertParameters(stmt, post);
			int affected = stmt.executeUpdate();
			if (affected > 0) {
				ResultSet rs = stmt.getGeneratedKeys();
				if (rs.next()) {
					generatedId = rs.getInt(1);
				}
			}
		} catch (SQLException e) {
			throw new Exception("DB-0002");
		}

		return generatedId;
	}

	/**
	 * 投稿を更新する。
	 *
	 * @param post 更新内容を含むDTO
	 * @return 更新件数が1件以上ならtrue
	 */
	public boolean update(Post post) throws Exception {
		try (Connection conn = connectionManager.getConnection()) {
			return update(post, conn);
		} catch (SQLException e) {
			throw new Exception("DB-0003");
		}
	}

	/**
	 * 既存コネクションを利用して投稿を更新する。
	 *
	 * @param post 更新内容を含むDTO
	 * @param conn 共有トランザクション用コネクション
	 * @return 更新件数が1件以上ならtrue
	 * @throws SQLException SQL例外
	 */
	public boolean update(Post post, Connection conn) throws Exception {
		boolean updated = false;
		String sql = "UPDATE posts SET department_id = ?, method_id = ?, recruitment_no = ?, "
				+ "company_name = ?, venue_address = ?, exam_date = ?, grade = ?, is_anonymous = ?,"
				+ " updated_at = ? WHERE post_id = ? AND user_id = ?";

		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, post.getDepartmentId());
			stmt.setInt(2, post.getMethodId());
			setNullableInt(stmt, 3, post.getRecruitmentNo());
			stmt.setString(4, post.getCompanyName());
			stmt.setString(5, post.getVenueAddress());
			setDateParameter(stmt, 6, post.getExamDate());
			stmt.setInt(7, post.getGrade());
			stmt.setBoolean(8, post.isAnonymous());
			stmt.setTimestamp(9, toTimestamp(post.getUpdatedAt()));
			stmt.setInt(10, post.getPostId());
			stmt.setInt(11, post.getUserId());

			updated = stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			throw new Exception("DB-0003");
		}

		return updated;
	}

	/**
	 * 投稿を物理削除する。
	 *
	 * @param postId 投稿ID
	 * @param conn 既存のコネクション
	 * @return 削除に成功した場合true
	 * @throws Exception SQL実行エラー
	 */
	public boolean delete(int postId, Connection conn) throws Exception {
		String sql = "DELETE FROM posts WHERE post_id = ?";

		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, postId);
			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			throw new Exception("DB-0004");
		}
	}

	/**
	 * ResultSetの1行をPost DTOへマッピングする。
	 *
	 * @param rs クエリ結果
	 * @return DTO
	 * @throws SQLException 変換時の例外
	 */
	private Post mapPost(ResultSet rs) throws SQLException {
		Post post = new Post();
		post.setPostId(rs.getInt("post_id"));
		post.setUserId(rs.getInt("user_id"));
		post.setDepartmentId(rs.getInt("department_id"));
		post.setMethodId(rs.getInt("method_id"));
		post.setRecruitmentNo(rs.getInt("recruitment_no"));
		post.setCompanyName(rs.getString("company_name"));
		post.setVenueAddress(rs.getString("venue_address"));

		Date examDate = rs.getDate("exam_date");
		if (examDate != null) {
			post.setExamDate(examDate.toLocalDate());
		}

		post.setGrade(rs.getInt("grade"));
		post.setAnonymous(rs.getBoolean("is_anonymous"));

		Timestamp createdAt = rs.getTimestamp("created_at");
		if (createdAt != null) {
			post.setCreateAt(createdAt.toLocalDateTime());
		}

		Timestamp updatedAt = rs.getTimestamp("updated_at");
		if (updatedAt != null) {
			post.setUpdatedAt(updatedAt.toLocalDateTime());
		}

		return post;
	}

	/**
	 * INSERT文に必要なパラメータをPreparedStatementへ設定する。
	 *
	 * @param stmt PreparedStatement
	 * @param post DTO
	 * @throws SQLException パラメータ設定時の例外
	 */
	private void bindInsertParameters(PreparedStatement stmt, Post post) throws SQLException {
		LocalDateTime createdAt = post.getCreateAt();
		LocalDateTime updatedAt = post.getUpdatedAt();
		if (createdAt == null) {
			createdAt = LocalDateTime.now();
		}
		if (updatedAt == null) {
			updatedAt = createdAt;
		}

		stmt.setInt(1, post.getUserId());
		stmt.setInt(2, post.getDepartmentId());
		stmt.setInt(3, post.getMethodId());
		setNullableInt(stmt, 4, post.getRecruitmentNo());
		stmt.setString(5, post.getCompanyName());
		stmt.setString(6, post.getVenueAddress());
		setDateParameter(stmt, 7, post.getExamDate());
		stmt.setInt(8, post.getGrade());
		stmt.setBoolean(9, post.isAnonymous());
		stmt.setTimestamp(10, Timestamp.valueOf(createdAt));
		stmt.setTimestamp(11, Timestamp.valueOf(updatedAt));
	}

	/**
	 * LocalDateをSQL DATEへ変換して設定する。
	 *
	 * @param stmt  PreparedStatement
	 * @param index プレースホルダ位置
	 * @param date  変換元日付
	 * @throws SQLException 変換時の例外
	 */
	private void setDateParameter(PreparedStatement stmt, int index, LocalDate date) throws SQLException {
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

	/**
	 * LIKE演算子で使用する検索パターンを生成する。
	 *
	 * <p>セキュリティ対策として、ワイルドカード文字（%, _）を
	 * エスケープしてからパターンを構築します。</p>
	 *
	 * @param keyword 生のキーワード
	 * @return "%keyword%" 形式の文字列（nullの場合はワイルドカードのみ）
	 */
	private String buildLikePattern(String keyword) {
		if (keyword == null) {
			return "%";
		}
		// ワイルドカード文字をエスケープ
		String escaped = keyword
				.replace("\\", "\\\\")
				.replace("%", "\\%")
				.replace("_", "\\_");
		return "%" + escaped + "%";
	}

	/**
	 * LocalDateTimeをTimestampへ変換する。nullの場合は現在時刻を使用する。
	 *
	 * @param value 変換元日時
	 * @return Timestamp
	 */
	private Timestamp toTimestamp(LocalDateTime value) {
		LocalDateTime target = value;
		if (target == null) {
			target = LocalDateTime.now();
		}
		return Timestamp.valueOf(target);
	}
}

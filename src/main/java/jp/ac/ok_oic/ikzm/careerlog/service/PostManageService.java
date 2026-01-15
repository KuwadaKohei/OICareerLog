package jp.ac.ok_oic.ikzm.careerlog.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import jp.ac.ok_oic.ikzm.careerlog.dao.DBConnectionManager;
import jp.ac.ok_oic.ikzm.careerlog.dao.DeletedPostDAO;
import jp.ac.ok_oic.ikzm.careerlog.dao.PostDAO;
import jp.ac.ok_oic.ikzm.careerlog.dao.PostDetailDAO;
import jp.ac.ok_oic.ikzm.careerlog.dao.PostExamSelectionDAO;
import jp.ac.ok_oic.ikzm.careerlog.entity.Post;
import jp.ac.ok_oic.ikzm.careerlog.entity.PostDetail;
import jp.ac.ok_oic.ikzm.careerlog.entity.PostExamSelection;
import jp.ac.ok_oic.ikzm.careerlog.form.PostForm;
import jp.ac.ok_oic.ikzm.careerlog.util.ModelConverter;

/**
 * 投稿の登録・更新・削除といったトランザクションを司るサービスクラス。
 * DAO呼び出しとフォーム変換をまとめ、整合性を担保する。
 */
public class PostManageService {

	private final DBConnectionManager connectionManager;
	private final PostDAO postDao;
	private final PostDetailDAO postDetailDao;
	private final PostExamSelectionDAO postExamSelectionDao;
	private final DeletedPostDAO deletedPostDao;

	/**
	 * デフォルトコンストラクタ。各DAOを初期化する。
	 */
	public PostManageService() {
		this.connectionManager = new DBConnectionManager();
		this.postDao = new PostDAO();
		this.postDetailDao = new PostDetailDAO();
		this.postExamSelectionDao = new PostExamSelectionDAO();
		this.deletedPostDao = new DeletedPostDAO();
	}

	/**
	 * 新規投稿を登録する。関連テーブルへの登録も含めて一つのトランザクションで処理する。
	 *
	 * @param form         入力フォーム
	 * @param userId       投稿者ユーザー ID
	 * @param departmentId 投稿者所属学科 ID
	 * @param grade        投稿者学年
	 * @return 登録成功時  true
	 * @throws Exception DAO操作またはトランザクション失敗
	 */
	public boolean create(PostForm form, int userId, int departmentId, int grade) throws Exception {

		Connection conn = null;
		boolean hasError = false;

		try {
			// 接続取得＆トランザクション開始
			conn = this.connectionManager.getConnection();
			conn.setAutoCommit(false);

			// フォーム型をDTOに変換
			Post post = ModelConverter.toPostDto(form, userId, departmentId, grade);

			// 作成日時の取得
			post.setCreateAt(LocalDateTime.now());
			post.setUpdatedAt(LocalDateTime.now());

			// postテーブルへINSERT
			int newPostId = this.postDao.insert(post, conn);
			if (newPostId == 0) {
				throw new Exception("DB-0002");
			}

			PostDetail detail = ModelConverter.toPostDetailDto(form);
			detail.setPostId(newPostId);

			if (!this.postDetailDao.insert(detail, conn)) {
				throw new Exception("DB-0011");
			}

			List<PostExamSelection> selections = ModelConverter.toSelectionList(form);

			if (selections != null && !selections.isEmpty()) {
				for (PostExamSelection sel : selections) {
					sel.setPostId(newPostId);
				}

				if (!this.postExamSelectionDao.insertList(selections, conn)) {
					throw new Exception("DB-0021");
				}
			}
			// コミット
			conn.commit();
			return true;
		} catch (Exception e) {
			hasError = true;
			// 失敗時はロールバックし、エラーコードを投げる
			if (conn != null) {
				try {
					conn.rollback();
				} catch (SQLException rollbackEx) {
					throw new Exception("APP-2000");
				}
			}
			throw new Exception("APP-2000");
		} finally {
			if (conn != null) {
				try {
					conn.setAutoCommit(true);
					conn.close();
				} catch (SQLException closeEx) {
					if (!hasError) {
						throw new Exception("APP-2002");
					}
				}
			}
		}

	}

	/**
	 * 既存投稿を更新する。詳細や試験選択も含めて全体を更新する。
	 *
	 * @param form         入力フォーム
	 * @param userId       投稿者ユーザー ID
	 * @param departmentId 投稿者所属学科 ID
	 * @param grade        投稿者学年
	 * @return 更新成功時 true（投稿が他人のものなどの場合は false）
	 * @throws Exception DAO 操作またはトランザクション失敗
	 */
	public boolean update(PostForm form, int userId, int departmentId, int grade) throws Exception {

		int postId = form.getPostId();

		// 該当する投稿が存在するか確認
		Post existing = this.postDao.findById(postId);
		if (existing == null || existing.getUserId() != userId) {
			return false;
		}

		Connection conn = null;
		boolean hasError = false;
		try {
			// 接続取得＆トランザクション開始
			conn = this.connectionManager.getConnection();
			conn.setAutoCommit(false);

			// Postテーブルの更新
			Post post = ModelConverter.toPostDto(form, userId, departmentId, grade);
			post.setUpdatedAt(LocalDateTime.now());
			if (!this.postDao.update(post, conn)) {
				throw new Exception("DB-0003");
			}

			// Detailテーブルの更新
			PostDetail detail = ModelConverter.toPostDetailDto(form);
			detail.setPostId(postId);
			if (!this.postDetailDao.update(detail, conn)) {
				throw new Exception("DB-0012");
			}

			// ExamSelectionのレコードは一度削除して追加する
			this.postExamSelectionDao.deleteByPostId(postId, conn);

			List<PostExamSelection> selections = ModelConverter.toSelectionList(form);
			if (selections != null && !selections.isEmpty()) {
				for (PostExamSelection sel : selections) {
					sel.setPostId(postId);
				}
				if (!this.postExamSelectionDao.insertList(selections, conn)) {
					throw new Exception("DB-0021");
				}
			}

			conn.commit();
			return true;
		} catch (Exception e) {
			hasError = true;
			// 更新中の例外もロールバックしてアプリ用のコードを返す
			if (conn != null) {
				try {
					conn.rollback();
				} catch (SQLException rollbackEx) {
					throw new Exception("APP-2001");
				}
			}
			throw new Exception("APP-2001");
		} finally {
			if (conn != null) {
				try {
					conn.setAutoCommit(true);
					conn.close();
				} catch (SQLException closeEx) {
					if (!hasError) {
						throw new Exception("APP-2002");
					}
				}
			}
		}
	}

	/**
	 * 投稿を削除する。削除前にdeleted_postsテーブルへ履歴を保存し、その後物理削除を行う。
	 *
	 * @param postId 投稿ID
	 * @param deletedBy 削除実行者のユーザーID（nullの場合は記録なし）
	 * @return 削除に成功した場合true
	 * @throws Exception トランザクション失敗または削除失敗
	 */
	public boolean delete(int postId, Integer deletedBy) throws Exception {
		Connection conn = null;
		boolean hasError = false;
		boolean deleted = false;

		try {
			// 接続取得＆トランザクション開始
			conn = this.connectionManager.getConnection();
			conn.setAutoCommit(false);

			// 削除前に投稿データを取得
			Post post = this.postDao.findById(postId);
			if (post == null) {
				throw new Exception("APP-2003"); // 投稿が存在しない
			}

			// 削除履歴を保存
			this.deletedPostDao.insertFromPost(post, deletedBy, conn);

			// 投稿を物理削除
			deleted = this.postDao.delete(postId, conn);
			if (!deleted) {
				throw new Exception("DB-0004");
			}

			conn.commit();
		} catch (SQLException e) {
			hasError = true;
			if (conn != null) {
				try {
					conn.rollback();
				} catch (SQLException rollbackEx) {
					throw new Exception("APP-2002");
				}
			}
			throw new Exception("APP-2001");
		} catch (Exception e) {
			hasError = true;
			if (conn != null) {
				try {
					conn.rollback();
				} catch (SQLException rollbackEx) {
					throw new Exception("APP-2002");
				}
			}
			throw e;
		} finally {
			if (conn != null) {
				try {
					conn.setAutoCommit(true);
					conn.close();
				} catch (SQLException closeEx) {
					if (!hasError) {
						throw new Exception("APP-2002");
					}
				}
			}
		}

		return deleted;
	}

	/**
	 * 投稿を削除する（削除実行者なし）。
	 *
	 * @param postId 投稿ID
	 * @return 削除に成功した場合true
	 * @throws Exception トランザクション失敗または削除失敗
	 */
	public boolean delete(int postId) throws Exception {
		return delete(postId, null);
	}

}

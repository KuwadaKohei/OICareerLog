package jp.ac.ok_oic.ikzm.careerlog.service;

import java.util.ArrayList;
import java.util.List;

import jp.ac.ok_oic.ikzm.careerlog.dao.DepartmentDAO;
import jp.ac.ok_oic.ikzm.careerlog.dao.UserDAO;
import jp.ac.ok_oic.ikzm.careerlog.entity.Department;
import jp.ac.ok_oic.ikzm.careerlog.entity.User;

/**
 * ユーザー管理関連のビジネスロジックをまとめたサービスクラス。
 * 検索・状態変更・学科登録といった操作を提供する。
 */
public class UserService {

	private final UserDAO userDao;
	private final DepartmentDAO departmentDao;

	/**
	 * デフォルトコンストラクタ。各DAOを初期化する。
	 */
	public UserService() {
		this.userDao = new UserDAO();
		this.departmentDao = new DepartmentDAO();
	}

	/**
	 * 検索文字列をもとにユーザー一覧を取得する。
	 *
	 * @param searchWord ユーザー名またはメールアドレスの一部
	 * @return 該当ユーザーのリスト
	 * @throws Exception DAO 層の例外
	 */
	public ArrayList<User> search(String searchWord) throws Exception {

		ArrayList<User> userList = new ArrayList<User>();

		// スラッシュ始まりならメール検索、それ以外は名前検索
		if (searchWord.startsWith("/")) {
			userList = this.userDao.findListByEmail(searchWord);
		} else {
			userList = this.userDao.findListByUsername(searchWord);
		}

		return userList;

	}

	/**
	 * 任意のアクティブ状態へ更新する。
	 *
	 * @param userIdString 対象ユーザー ID（文字列）
	 * @param isActive     更新後のアクティブ状態
	 * @return 更新成功時 true
	 * @throws Exception DAO 層の例外
	 */
	public boolean updateActiveStatus(String userIdString, boolean isActive) throws Exception {
		int userId = parseUserId(userIdString);
		if (userId <= 0) {
			return false;
		}

		// DAO側ではtrue/falseで更新結果を返す
		return this.userDao.updateActiveStatus(userId, isActive);
	}

	/**
	 * ユーザーIDから詳細情報を取得する。
	 *
	 * @param userId ユーザーID
	 * @return ユーザー情報（存在しない場合はnull）
	 * @throws Exception DAO 層の例外
	 */
	public User findById(int userId) throws Exception {
		if (userId <= 0) {
			return null;
		}
		return this.userDao.findById(userId);
	}

	/**
	 * 文字列から安全にユーザー ID をパースする。
	 *
	 * @param userIdString ユーザー ID 文字列
	 * @return 正常な整数 ID。失敗時は -1
	 */
	private int parseUserId(String userIdString) {
		if (userIdString == null || userIdString.isBlank()) {
			return -1;
		}
		try {
			return Integer.parseInt(userIdString);
		} catch (NumberFormatException e) {
			return -1;
		}
	}

	/**
	 * 学科マスタ一覧を取得する。
	 *
	 * @return 学科リスト（null の場合は空リスト）
	 * @throws Exception DAO 層の例外
	 */
	public List<Department> getDepartmentList() throws Exception {
		// DAOが未実装でも動くように、nullチェックや空リスト返却を考慮
		List<Department> list = this.departmentDao.findAll();
		if (list == null) {
			return new ArrayList<>();
		}
		return list;
	}

	/**
	 * 学科と学年情報をユーザーに紐づける。
	 *
	 * @param userId       対象ユーザー ID
	 * @param departmentId 学科 ID
	 * @param grade        学年
	 * @return 更新成功時 true（バリデーション NG の場合は false）
	 * @throws Exception DAO 層の例外
	 */
	public boolean registerUserDetail(int userId, int departmentId, int grade) throws Exception {
		if (departmentId <= 0 || grade <= 0) {
			return false;
		}

		return this.userDao.updateDepartmentInfo(userId, departmentId, grade);
	}
}

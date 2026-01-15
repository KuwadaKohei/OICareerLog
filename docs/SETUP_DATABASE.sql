-- 就活メモシステム データベースセットアップスクリプト

-- 作成日: 2026-01-08
-- 作成者: OIC 24入学 情報システム学科 桒田 晃平
-- バージョン: 2.0.0

/*
 注意: ユーザー名とパスワードは適宜作成・変更してください
 Linux環境のMySQLでは、大文字と小文字を区別します。
*/

-- 文字コード設定
SET NAMES utf8mb4;

-- データベースの再作成
DROP DATABASE IF EXISTS IKZM_OICareerLog;
CREATE DATABASE IKZM_OICareerLog CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE IKZM_OICareerLog;

-- テーブル定義

-- 学科マスタテーブル
CREATE TABLE departments (
	department_id INT AUTO_INCREMENT PRIMARY KEY COMMENT '学科ID',
	department_name VARCHAR(255) NOT NULL COMMENT '学科・コース名'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学科マスタ';

-- 応募方法マスタテーブル
CREATE TABLE submission_methods (
	method_id INT AUTO_INCREMENT PRIMARY KEY COMMENT '応募方法ID',
	method_name VARCHAR(50) NOT NULL COMMENT '応募方法名'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='応募方法マスタ';

-- 試験内容マスタテーブル
CREATE TABLE exam_contents (
	content_id INT AUTO_INCREMENT PRIMARY KEY COMMENT 'コンテンツID',
	category VARCHAR(50) NOT NULL COMMENT 'カテゴリ(筆記/面接/その他)',
	name VARCHAR(100) NOT NULL COMMENT '項目名',
	needs_detail BOOLEAN NOT NULL DEFAULT FALSE COMMENT '詳細記述が必要か',
	select_detail BOOLEAN NOT NULL DEFAULT FALSE COMMENT '詳細選択肢使用フラグ',
	detail_example VARCHAR(255) NULL COMMENT '記述例'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='試験内容マスタ';

-- 試験詳細選択肢マスタテーブル
CREATE TABLE exam_detail_options (
	option_id INT AUTO_INCREMENT PRIMARY KEY COMMENT '選択肢ID',
	content_id INT NOT NULL COMMENT '試験内容ID (exam_contents.content_id)',
	option_text VARCHAR(200) NOT NULL COMMENT '選択肢テキスト',
	display_order INT NOT NULL DEFAULT 0 COMMENT '表示順序',
	
	FOREIGN KEY (content_id) REFERENCES exam_contents(content_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='試験詳細の選択肢マスタ';

-- ユーザーテーブル
CREATE TABLE users (
	user_id INT AUTO_INCREMENT PRIMARY KEY COMMENT 'ユーザーID',
	google_account_id VARCHAR(255) NOT NULL UNIQUE COMMENT 'Googleアカウント固有ID',
	email VARCHAR(255) NOT NULL UNIQUE COMMENT 'メールアドレス',
	user_type VARCHAR(20) NOT NULL DEFAULT 'student' COMMENT 'ユーザー種別(student/teacher)',
	name VARCHAR(100) NOT NULL COMMENT '氏名',
	department_id INT NOT NULL DEFAULT 0 COMMENT '所属学科ID (departments.department_id)',
	grade INT NOT NULL DEFAULT 0 COMMENT '学年',
	is_active BOOLEAN NOT NULL DEFAULT TRUE COMMENT '有効フラグ',
	is_admin BOOLEAN NOT NULL DEFAULT FALSE COMMENT '管理者フラグ'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='ユーザー情報';

-- 投稿テーブル (posts)
CREATE TABLE posts (
	post_id INT AUTO_INCREMENT PRIMARY KEY COMMENT '投稿ID',
	user_id INT NOT NULL COMMENT '投稿者ID',
	department_id INT NOT NULL COMMENT '投稿時の所属学科ID',
	method_id INT NOT NULL COMMENT '応募方法ID',
	recruitment_no INT COMMENT '求人票番号',
	company_name VARCHAR(255) NOT NULL COMMENT '受験先事業所名',
	venue_address VARCHAR(255) COMMENT '試験会場住所',
	exam_date DATE NOT NULL COMMENT '受験日',
	grade INT NOT NULL COMMENT '投稿時の学年',
	is_anonymous BOOLEAN NOT NULL DEFAULT FALSE COMMENT '匿名フラグ',
	created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '作成日時',
	updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新日時',
	
	FOREIGN KEY (user_id) REFERENCES users(user_id),
	FOREIGN KEY (department_id) REFERENCES departments(department_id),
	FOREIGN KEY (method_id) REFERENCES submission_methods(method_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='就活報告書コア情報';

-- 投稿詳細テーブル (post_details)
CREATE TABLE post_details ( 
	post_id INT PRIMARY KEY COMMENT '投稿ID (posts.post_id)',
	scheduled_hires INT COMMENT '採用予定人数',
	advice_text TEXT COMMENT '受験のアドバイス',
	
	FOREIGN KEY (post_id) REFERENCES posts(post_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='就活報告書詳細';

-- 投稿試験選択中間テーブル (post_exam_selections)
CREATE TABLE post_exam_selections (
	post_id INT NOT NULL COMMENT '投稿ID',
	content_id INT NOT NULL COMMENT '試験内容ID',
	detail_text TEXT COMMENT '詳細記述 (テーマや人数など)',
	
	PRIMARY KEY (post_id, content_id),
	
	FOREIGN KEY (post_id) REFERENCES posts(post_id) ON DELETE CASCADE,
	FOREIGN KEY (content_id) REFERENCES exam_contents(content_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='投稿と試験内容の中間テーブル';

-- 削除済み投稿履歴テーブル (deleted_posts)
CREATE TABLE deleted_posts (
	deleted_post_id INT AUTO_INCREMENT PRIMARY KEY COMMENT '削除履歴ID',
	original_post_id INT NOT NULL COMMENT '元の投稿ID',
	user_id INT NOT NULL COMMENT '投稿者ID',
	department_id INT NOT NULL COMMENT '投稿時の所属学科ID',
	method_id INT NOT NULL COMMENT '応募方法ID',
	recruitment_no INT COMMENT '求人票番号',
	company_name VARCHAR(255) NOT NULL COMMENT '受験先事業所名',
	venue_address VARCHAR(255) COMMENT '試験会場住所',
	exam_date DATE NOT NULL COMMENT '受験日',
	grade INT NOT NULL COMMENT '投稿時の学年',
	is_anonymous BOOLEAN NOT NULL COMMENT '匿名フラグ',
	created_at DATETIME NOT NULL COMMENT '元の作成日時',
	updated_at DATETIME NOT NULL COMMENT '元の更新日時',
	deleted_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '削除日時',
	deleted_by INT COMMENT '削除実行者ID',
	post_details_json JSON COMMENT '投稿詳細のJSONデータ',
	exam_selections_json JSON COMMENT '試験内容選択のJSONデータ',
	
	INDEX idx_original_post_id (original_post_id),
	INDEX idx_deleted_at (deleted_at),
	INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='削除済み投稿の履歴';

-- マスタデータの挿入

-- 学科マスタ初期データ
INSERT INTO departments (department_name) VALUES
('情報システム学科'),
('情報スペシャリスト学科'),
('ゲームVRクリエイター学科-ゲームクリエイターコース'),
('ゲームVRクリエイター学科-VRクリエイターコース'),
('CGデザイン学科-3DCGデザインコース'),
('ネット・動画クリエイター学科'),
('デジタルビジネス学科'),
('ホテル・ブライダル学科'),
('医療福祉事務学科'),
('医療情報マネジメント学科'),
('経営アシスト学科'),
('公務員総合学科'),
('国際ITビジネス学科'),
('教員・その他');

-- 応募方法マスタ初期データ
INSERT INTO submission_methods (method_name) VALUES
('学校斡旋'),
('自己開拓'),
('縁故'),
('その他');

-- 試験内容マスタ初期データ
INSERT INTO exam_contents (category, name, needs_detail, select_detail, detail_example) VALUES
	('面接','個人',TRUE, TRUE, '服装、評価基準やフィードバック有無、合否連絡のタイミング'),
	('面接','グループ',TRUE, TRUE, '服装、評価基準やフィードバック有無、合否連絡のタイミング'),
	('適性検査','SPI',TRUE, TRUE, 'その他出題範囲、制限時間'),
	('適性検査','CAB',TRUE, TRUE, 'その他出題範囲、制限時間'),
	('適性検査','GAB',TRUE, TRUE, 'その他出題範囲、制限時間'),
	('適性検査','玉手箱',TRUE, TRUE, 'その他出題範囲、制限時間'),
	('適性検査','その他',TRUE, TRUE, '出題範囲、制限時間'),
	('筆記','一般常識',TRUE, FALSE, '出題範囲、制限時間'),
	('筆記','専門試験',TRUE, FALSE, '出題範囲、制限時間'),
	('その他','グループディスカッション',TRUE, FALSE, 'テーマ、参加人数'),
	('その他','作文',TRUE, FALSE, 'テーマ、所要時間など'),
	('その他','プレゼンテーション',TRUE, FALSE, 'テーマ、所要時間など'),
	('その他','実技試験',TRUE, FALSE, '内容、所要時間など');

-- 試験詳細選択肢マスタ初期データ (個人面接用)
INSERT INTO exam_detail_options (content_id, option_text, display_order) VALUES
	(1, '面接官: 1人', 1),
	(1, '面接官: 2人', 2),
	(1, '面接官: 3人', 3),
	(1, '面接官: 4人以上', 4),
	(1, '時間: ~15分', 5),
	(1, '時間: 30分程度', 6),
	(1, '時間: 45分程度', 7),
	(1, '時間: 60分程度', 8),
	(1, '時間: 90分以上', 9),
    (1, '質問例: 志望動機・将来の目標', 10),
	(1, '質問例: 自己PR・性格（長所・短所）', 11),
	(1, '質問例: 学業・資格・授業内容', 12),
	(1, '質問例: 学校生活（部活・行事・出欠席）', 13),
	(1, '質問例: 趣味・特技・アルバイト', 14),
	(1, '質問例: 会社の印象・職種への理解', 15),
	(1, '質問例: 他社受験状況・他社との比較', 16),
	(1, '質問例: 勤務条件（勤務地・転勤・通勤）', 17),
	(1, '質問例: 休暇・休日・福利厚生について', 18),
	(1, '質問例: 個人に関すること（家族・健康等）', 19);

-- 試験詳細選択肢マスタ初期データ (グループ面接用)
INSERT INTO exam_detail_options (content_id, option_text, display_order) VALUES
	(2, '面接官: 1人', 1),
	(2, '面接官: 2人', 2),
	(2, '面接官: 3人', 3),
	(2, '面接官: 4人以上', 4),
	(2, '時間: ~15分', 5),
	(2, '時間: 30分程度', 6),
	(2, '時間: 45分程度', 7),
	(2, '時間: 60分程度', 8),
	(2, '時間: 90分以上', 9),
    (2, '質問例: 志望動機・将来の目標', 10),
	(2, '質問例: 自己PR・性格（長所・短所）', 11),
	(2, '質問例: 学業・資格・授業内容', 12),
	(2, '質問例: 学校生活（部活・行事・出欠席）', 13),
	(2, '質問例: 趣味・特技・アルバイト', 14),
	(2, '質問例: 会社の印象・職種への理解', 15),
	(2, '質問例: 他社受験状況・他社との比較', 16),
	(2, '質問例: 勤務条件（勤務地・転勤・通勤）', 17),
	(2, '質問例: 休暇・休日・福利厚生について', 18),
	(2, '質問例: 個人に関すること（家族・健康等）', 19);

-- 試験詳細選択肢マスタ初期データ (SPI用)
INSERT INTO exam_detail_options (content_id, option_text, display_order) VALUES
    (3,'出題範囲：言語',1),
    (3,'出題範囲：非言語',2),
    (3,'出題範囲：英語',3),
    (3,'回答形式：WEBテスト',4),
    (3,'回答形式：ペーパーテスト',5),
    (3,'制限時間あり',6),
    (3,'制限時間なし',7);

-- 試験詳細選択肢マスタ初期データ (CAB用)
INSERT INTO exam_detail_options (content_id, option_text, display_order) VALUES
    (4,'出題範囲：言語',1),
    (4,'出題範囲：非言語',2),
    (4,'回答形式：WEBテスト',3),
    (4,'回答形式：ペーパーテスト',4),
    (4,'制限時間あり',5),
    (4,'制限時間なし',6);

-- 試験詳細選択肢マスタ初期データ (GAB用)
INSERT INTO exam_detail_options (content_id, option_text, display_order) VALUES
    (5,'出題範囲：言語',1),
    (5,'出題範囲：非言語',2),
    (5,'回答形式：WEBテスト',3),
    (5,'回答形式：ペーパーテスト',4),
    (5,'制限時間あり',5),
    (5,'制限時間なし',6);

-- 試験詳細選択肢マスタ初期データ (玉手箱用)
INSERT INTO exam_detail_options (content_id, option_text, display_order) VALUES
    (6,'出題範囲：言語',1),
    (6,'出題範囲：非言語',2),
    (6,'回答形式：WEBテスト',3),
    (6,'回答形式：ペーパーテスト',4),
    (6,'制限時間あり',5),
    (6,'制限時間なし',6);

-- 試験詳細選択肢マスタ初期データ (その他適性検査用)
INSERT INTO exam_detail_options (content_id, option_text, display_order) VALUES
    (7,'回答形式：WEBテスト',1),
    (7,'回答形式：ペーパーテスト',2),
    (7,'制限時間あり',3),
    (7,'制限時間なし',4);

SELECT '===== IKZM_OICareerLog データベースのセットアップが完了しました =====' AS 'STATUS';
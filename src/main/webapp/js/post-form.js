/**
 * 投稿フォーム用JavaScript (new.jsp / edit.jsp 共通)
 * 試験項目モーダル制御・追加ロジック
 */

(function() {
	'use strict';

	// 設定情報をdata属性から取得
	const appConfig = document.getElementById('app-config');
	const contextPath = appConfig ? appConfig.dataset.contextPath : '';

	// 試験詳細選択肢データをdata属性から取得（JSON）
	const examDataElement = document.getElementById('exam-detail-options-data');
	let examDetailOptionsData = {};
	if (examDataElement && examDataElement.dataset.options) {
		try {
			examDetailOptionsData = JSON.parse(examDataElement.dataset.options);
		} catch (e) {
			console.error('Failed to parse exam detail options data:', e);
		}
	}

	// DOM要素の取得
	const examModalElement = document.getElementById('examModal');
	const examSelect = document.getElementById('examSelect');
	const detailOptionsArea = document.getElementById('detailOptionsArea');
	const detailOptionsList = document.getElementById('detailOptionsList');
	const detailTextArea = document.getElementById('detailTextArea');
	const examDetailText = document.getElementById('examDetailText');
	const detailExample = document.getElementById('detailExample');
	const addExamBtn = document.getElementById('addExamBtn');
	const selectedExamsContainer = document.getElementById('selectedExamsContainer');
	const openExamModalBtn = document.getElementById('openExamModalBtn');
	let noExamMessage = document.getElementById('noExamMessage');

	// 要素が存在しない場合は終了（このページでは使用しない）
	if (!examModalElement || !examSelect) {
		return;
	}

	// Bootstrapモーダルの初期化
	const examModal = new bootstrap.Modal(examModalElement);

	// 選択された詳細オプション
	let selectedDetailOptions = [];

	/**
	 * モーダルを開く
	 */
	if (openExamModalBtn) {
		openExamModalBtn.addEventListener('click', function() {
			resetModal();
			examModal.show();
		});
	}

	/**
	 * モーダルのリセット
	 */
	function resetModal() {
		examSelect.value = '';
		detailOptionsArea.style.display = 'none';
		detailTextArea.style.display = 'none';
		detailOptionsList.innerHTML = '';
		examDetailText.value = '';
		detailExample.textContent = '';
		selectedDetailOptions = [];
	}

	/**
	 * 試験項目選択時の処理
	 */
	examSelect.addEventListener('change', function(e) {
		const selectedOption = e.target.selectedOptions[0];
		const contentId = parseInt(e.target.value);
		const needsDetail = selectedOption.dataset.needsDetail === 'true';
		const selectDetail = selectedOption.dataset.selectDetail === 'true';
		const example = selectedOption.dataset.example;

		// 詳細エリアをリセット
		detailOptionsArea.style.display = 'none';
		detailTextArea.style.display = 'none';
		detailOptionsList.innerHTML = '';
		examDetailText.value = '';
		selectedDetailOptions = [];

		if (needsDetail) {
			if (selectDetail) {
				// 選択肢を表示
				displayDetailOptions(contentId);
				detailOptionsArea.style.display = 'block';
			}
			// フリーテキストは常に表示（パターンC: プルダウンまたは自由記述）
			detailTextArea.style.display = 'block';
			detailExample.textContent = example || '';
		}
	});

	/**
	 * 詳細選択肢を表示
	 * @param {number} contentId - 試験項目ID
	 */
	function displayDetailOptions(contentId) {
		const options = examDetailOptionsData[contentId] || [];

		options.forEach(function(option) {
			const div = document.createElement('div');
			div.className = 'border rounded p-2 detail-option-item';
			div.dataset.optionId = option.optionId;
			div.dataset.optionText = option.optionText;
			div.textContent = option.optionText;

			div.addEventListener('click', function() {
				div.classList.toggle('selected');
				toggleDetailOption(option.optionText);
			});

			detailOptionsList.appendChild(div);
		});
	}

	/**
	 * 詳細選択肢のトグル
	 * @param {string} optionText - 選択肢テキスト
	 */
	function toggleDetailOption(optionText) {
		const index = selectedDetailOptions.indexOf(optionText);
		if (index > -1) {
			selectedDetailOptions.splice(index, 1);
		} else {
			selectedDetailOptions.push(optionText);
		}
	}

	/**
	 * 試験項目を追加
	 */
	addExamBtn.addEventListener('click', function() {
		const selectedOption = examSelect.selectedOptions[0];
		if (!selectedOption || !examSelect.value) {
			alert('試験項目を選択してください。');
			return;
		}

		const contentId = parseInt(examSelect.value);
		const category = selectedOption.textContent.trim();

		// 既に選択されているかチェック
		const existingItems = document.querySelectorAll('.exam-item-card');
		for (let i = 0; i < existingItems.length; i++) {
			if (parseInt(existingItems[i].dataset.contentId) === contentId) {
				alert('この試験項目は既に追加されています。');
				return;
			}
		}

		// 詳細テキストを構築
		let detailText = '';
		if (selectedDetailOptions.length > 0) {
			detailText = selectedDetailOptions.join('\n');
		}
		const freeText = examDetailText.value.trim();
		if (freeText) {
			if (detailText) {
				detailText += '\n' + freeText;
			} else {
				detailText = freeText;
			}
		}

		// 試験項目カードを追加
		addExamItemCard(contentId, category, detailText);

		// モーダルを閉じる
		examModal.hide();
	});

	/**
	 * 試験項目カードを追加
	 * @param {number} contentId - 試験項目ID
	 * @param {string} category - カテゴリ名
	 * @param {string} detailText - 詳細テキスト
	 */
	function addExamItemCard(contentId, category, detailText) {
		// noExamMessageを削除
		noExamMessage = document.getElementById('noExamMessage');
		if (noExamMessage) {
			noExamMessage.remove();
		}

		const card = document.createElement('div');
		card.className = 'card exam-item-card';
		card.dataset.contentId = contentId;

		const cardBody = document.createElement('div');
		cardBody.className = 'card-body';

		const flexDiv = document.createElement('div');
		flexDiv.className = 'd-flex justify-content-between align-items-start';

		const contentDiv = document.createElement('div');
		contentDiv.className = 'flex-grow-1';

		const title = document.createElement('h6');
		title.className = 'mb-1';
		title.textContent = category;
		contentDiv.appendChild(title);

		if (detailText) {
			const detail = document.createElement('p');
			detail.className = 'text-muted small mb-0 text-prewrap';
			detail.textContent = detailText;
			contentDiv.appendChild(detail);
		}

		const removeBtn = document.createElement('button');
		removeBtn.type = 'button';
		removeBtn.className = 'btn btn-sm btn-outline-danger remove-exam-btn ms-2';
		removeBtn.dataset.contentId = contentId;
		removeBtn.innerHTML = '<i class="bi bi-trash"></i>';
		removeBtn.addEventListener('click', function() {
			card.remove();
			checkIfEmpty();
		});

		flexDiv.appendChild(contentDiv);
		flexDiv.appendChild(removeBtn);
		cardBody.appendChild(flexDiv);

		// Hidden inputs
		const hiddenId = document.createElement('input');
		hiddenId.type = 'hidden';
		hiddenId.name = 'selectedExamIds';
		hiddenId.value = contentId;
		cardBody.appendChild(hiddenId);

		const hiddenDetail = document.createElement('input');
		hiddenDetail.type = 'hidden';
		hiddenDetail.name = 'detailTexts';
		hiddenDetail.value = detailText;
		cardBody.appendChild(hiddenDetail);

		card.appendChild(cardBody);
		selectedExamsContainer.appendChild(card);
	}

	/**
	 * 既存の削除ボタンにイベントリスナーを追加
	 */
	document.querySelectorAll('.remove-exam-btn').forEach(function(btn) {
		btn.addEventListener('click', function(e) {
			const card = e.target.closest('.exam-item-card');
			if (card) {
				card.remove();
				checkIfEmpty();
			}
		});
	});

	/**
	 * 試験項目が空かどうかをチェック
	 */
	function checkIfEmpty() {
		const items = document.querySelectorAll('.exam-item-card');
		noExamMessage = document.getElementById('noExamMessage');
		if (items.length === 0 && !noExamMessage) {
			const msg = document.createElement('div');
			msg.className = 'text-muted text-center py-4';
			msg.id = 'noExamMessage';
			msg.textContent = '試験項目が選択されていません。上の「+試験項目を追加」ボタンから追加してください。';
			selectedExamsContainer.appendChild(msg);
		}
	}

	// グローバルにエクスポート
	window.PostForm = {
		resetModal: resetModal,
		checkIfEmpty: checkIfEmpty
	};

})();

/*
    画面幅がlg未満の場合にサイドバーを拡張する
*/
// ハンバーガーメニュー
const toggle = document.querySelector('.navbar-toggler');
// メニュー
const menu = document.querySelector('#sidebarMenu');
// サイドバー
const sidebar = document.querySelector('.sidebar-content');
// メインコンテンツ
const mainContainer = document.querySelector('.main-container');

// ハンバーガーメニューをクリックしたときにサイドバーを拡大する
toggle.addEventListener('click', () => {
    if (!sidebar.classList.contains('sidebar-open')) {
        // サイドバーがz-indexにより、浮上するため、補填用の要素を生成する
        const div = document.createElement('div');
        div.classList.add('col-1');
        mainContainer.before(div);

        // 拡大
        sidebar.classList.add('sidebar-open');
    }
});

// ハンバーガーメニューをクリックしたときにサイドバーを収縮する
menu.addEventListener('hidden.bs.collapse', () => {
    if (!sidebar.classList.contains('sidebar-open')) return;
    // サイドバーのz-indexが消えるため、補填用の要素を削除する
    const space = mainContainer.previousElementSibling;
    if (space) {
        space.remove();
    }

    // 収縮
    sidebar.classList.remove('sidebar-open');
});

// サイドバー以外をクリックしたときにサイドバーを収縮する
mainContainer.addEventListener('click', () => {
    if (!sidebar.classList.contains('sidebar-open')) return;
    const collapse = bootstrap.Collapse.getOrCreateInstance(menu);
    collapse.hide();
});

/*
    ホーム画面の初期表示
*/
window.addEventListener('load', async () => {
    const response = await fetch('/weight/weightHome');
    switch (response.status) {
        // 表示成功
        case 200:
            const html = await response.text();
            document.querySelector('#main-content').innerHTML = html;
            break;
        // 表示失敗（セッションエラー）
        case 401:
            window.location.href = '/';
            return;
    }
    // 初期化処理
    initDataSet();
});

/*
    画面遷移の非同期処理
*/
document.querySelectorAll('[data-page]').forEach((link) => {
    link.addEventListener('click', async (event) => {
        // 同期処理を停止
        event.preventDefault();
        // 非同期処理
        const response = await fetch(link.href);
        switch (response.status) {
            // 表示成功
            case 200:
                const html = await response.text();
                document.querySelector('#main-content').innerHTML = html;
                break;
            // 表示失敗（セッションエラー）
            case 401:
                window.location.href = '/';
                return;
        }
        // 初期化処理
        initDataSet();
    });
});

/*
    画面遷移の初期化処理
*/
function initDataSet() {
    // 今月
    const today = new Date();
    const startDt = new Date(today.getFullYear(), today.getMonth(), 1);
    const endDt = new Date(today.getFullYear(), today.getMonth() + 1, 1);
    const startDtStr = formatDate(startDt);
    const endDtStr = formatDate(endDt);

    const page = document.querySelector('#main-content > div');
    switch (page.id) {
        case 'weightHomePage':
            initWeightLineChart(startDtStr, endDtStr);
            break;
        case 'weightCalendarPage':
            initWeightCalendar();
            initWeightModal();
            break;
        case 'weightGoalCalendarPage':
            initWeightGoalCalendar();
            initWeightGoalModal();
            break;
    }
}

/*
    Date型から日付へ変換する
*/
function formatDate(date) {
    return date.getFullYear() + '-' + String(date.getMonth() + 1).padStart(2, '0') + '-' + String(date.getDate()).padStart(2, '0');
}

/*
     体重推移用折れ線グラフ初期表示
 */
async function initWeightLineChart(startDt, endDt) {
    // 結果を取得する
    const weightResponse = await fetch('/weight/weightLineChart/get?startDt=' + startDt + '&endDt=' + endDt);
    const weights = await weightResponse.json();
    const idealWeightResponse = await fetch('/weight/idealWeightLineChart/get?startDt=' + startDt + '&endDt=' + endDt);
    const idealWeights = await idealWeightResponse.json();

    // 日付
    const labels = weights.map((weight) => {
        return weight.weightDt.substring(8, 10);
    });
    // 体重
    const weightData = weights.map((weight) => weight.weight);
    const idealWeightData = idealWeights.map((idealWeight) => idealWeight.idealWeight);
    // タイトル
    const chartTitle = startDt.split('-')[0] + '年' + startDt.split('-')[1] + '月';

    const canvas = document.getElementById('weightLineChart');
    if (!canvas) return;

    const ctx = canvas.getContext('2d');
    new Chart(ctx, {
        type: 'line',
        data: {
            labels: labels,
            datasets: [
                {
                    label: '体重',
                    data: weightData,
                    spanGaps: true,
                    borderWidth: 1,
                },
                {
                    label: '理想体重',
                    data: idealWeightData,
                    spanGaps: false,
                    borderWidth: 1,
                },
            ],
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,

            plugins: {
                title: {
                    display: true,
                    text: chartTitle,
                },
                tooltip: {
                    callbacks: {
                        title: function (context) {
                            return context[0].label + '日';
                        },
                        label: function (context) {
                            return context.parsed.y + ' kg';
                        },
                    },
                },
            },

            scales: {
                x: {
                    title: {
                        display: true,
                        text: '日付（日）',
                    },
                },
                y: {
                    title: {
                        display: true,
                        text: '体重（kg）',
                    },
                    ticks: {
                        callback: function (value) {
                            return Number(value).toFixed(1);
                        },
                    },
                    grace: '10%',
                },
            },

            elements: {
                line: {
                    tension: 0,
                },
            },
        },
    });
}

/*
    体重入力用カレンダー初期表示
*/
async function initWeightCalendar() {
    const calendarEl = document.getElementById('weightCalendar');
    if (!calendarEl) return;

    // 初期表示の期間データを取得する
    const today = new Date();
    const dates = getCalendarDates('dayGridMonth', today);
    // 初期表示の体重データを取得する
    await getCalendarWeights('dayGridMonth', dates[0], dates[1]);

    const calendar = new FullCalendar.Calendar(calendarEl, {
        // カレンダーの条件
        initialView: 'dayGridMonth',
        locale: 'ja',
        height: '100%',

        // ボタンのカスタマイズ
        buttons: {
            myPrev: {
                text: '‹',
                click: async function () {
                    const date = calendar.getDate();
                    let dates;
                    switch (calendar.view.type) {
                        case 'dayGridMonth':
                            // 先月
                            date.setMonth(date.getMonth() - 1);
                            // 先月の期間データを取得する
                            dates = getCalendarDates('dayGridMonth', date);
                            // 先月の体重データを取得する
                            await getCalendarWeights('dayGridMonth', dates[0], dates[1]);
                            break;
                        case 'dayGridWeek':
                            // 先週
                            date.setDate(date.getDate() - 7);
                            // 先週の期間データを取得する
                            dates = getCalendarDates('dayGridWeek', date);
                            // 先週の体重データを取得する
                            await getCalendarWeights('dayGridWeek', dates[0], dates[1]);
                            break;
                    }
                    // 先月・先週へ切り替える
                    calendar.prev();
                },
            },
            myNext: {
                text: '›',
                click: async function () {
                    const date = calendar.getDate();
                    let dates;
                    switch (calendar.view.type) {
                        case 'dayGridMonth':
                            // 来月
                            date.setMonth(date.getMonth() + 1);
                            // 来月の期間データを取得する
                            dates = getCalendarDates('dayGridMonth', date);
                            // 来月の体重データを取得する
                            await getCalendarWeights('dayGridMonth', dates[0], dates[1]);
                            break;
                        case 'dayGridWeek':
                            // 来週
                            date.setDate(date.getDate() + 7);
                            // 来週の期間データを取得する
                            dates = getCalendarDates('dayGridWeek', date);
                            // 来週の体重データを取得する
                            await getCalendarWeights('dayGridWeek', dates[0], dates[1]);
                            break;
                    }
                    // 来月・来週へ切り替える
                    calendar.next();
                },
            },
            myDayGridMonth: {
                text: '月',
                click: async function () {
                    // 基準月
                    const date = calendar.getDate();
                    // 基準月の期間データを取得する
                    const dates = getCalendarDates('dayGridMonth', date);
                    // 基準月の体重データを取得する
                    await getCalendarWeights('dayGridMonth', dates[0], dates[1]);
                    // 基準月へ切り替える
                    calendar.changeView('dayGridMonth', date);
                },
            },
            myDayGridWeek: {
                text: '週',
                click: async function () {
                    // 基準日
                    const date = calendar.getDate();
                    // 基準週の期間データを取得する
                    const dates = getCalendarDates('dayGridWeek', date);
                    // 基準週の体重データを取得する
                    await getCalendarWeights('dayGridWeek', dates[0], dates[1]);
                    // 基準週へ切り替える
                    calendar.changeView('dayGridWeek', date);
                },
            },
            myMultiMonthYear: {
                text: '年',
                click: function () {
                    // 年間カレンダーは体重データを表示しない
                    weights = [];
                    // 基準年へ切り替える
                    calendar.changeView('multiMonthYear', calendar.getDate());
                },
            },
        },

        // カレンダーのヘッダー
        headerToolbar: {
            left: 'myPrev,today,myNext',
            center: 'title',
            right: 'myDayGridWeek,myDayGridMonth,myMultiMonthYear',
        },

        // カレンダーのセルに体重データを表示する
        dayCellDidMount: function (info) {
            // 表示期間の体重データを照合する
            const date = formatDate(info.date);
            const dayCellWeight = weights.find(function (item) {
                return item.weightDt === date;
            });
            if (dayCellWeight == null) return;

            const flexGrowEl = Array.from(info.el.children).find(function (element) {
                return getComputedStyle(element).flexGrow === '1';
            });

            const infoEl = document.createElement('div');
            infoEl.id = 'calendar-info-' + date;

            // カレンダーのセルに体重データを追加する
            const weightEl = document.createElement('div');
            weightEl.id = 'calendar-weight-' + date;
            weightEl.textContent = dayCellWeight.weight == null ? '' : dayCellWeight.weight + 'kg';
            infoEl.appendChild(weightEl);

            if (calendar.view.type === 'dayGridWeek') {
                // カレンダーのセルに体重データを追加する
                const fatEl = document.createElement('div');
                fatEl.id = 'calendar-fat-' + date;
                fatEl.textContent = dayCellWeight.fat == null ? '' : dayCellWeight.fat + '%';
                infoEl.appendChild(fatEl);

                const memoEl = document.createElement('div');
                memoEl.id = 'calendar-memo-' + date;
                memoEl.textContent = dayCellWeight.memo == null ? '' : dayCellWeight.memo;
                infoEl.appendChild(memoEl);
            }

            flexGrowEl.appendChild(infoEl);
        },

        datesSet: async function (info) {
            // 年間カレンダーから月間カレンダーへの切り替え
            if (calendar.view.type === 'multiMonthYear') {
                initChangeYearToMonth(calendar);
            }
        },

        // カレンダーの日付を選択したときにモーダルを表示する
        dateClick: async function (info) {
            // 選択日付の体重データを取得する
            const response = await fetch('/weight/weightModal/get?weightDt=' + info.dateStr);
            switch (response.status) {
                // 取得成功（入力済みの体重データを表示する）
                case 200:
                    const weight = await response.json();
                    document.querySelectorAll('#weightForm [name]').forEach((element) => {
                        element.value = weight?.[element.name] ?? '';
                    });
                    break;
                // 取得失敗（セッションエラー）
                case 401:
                    window.location.href = '/';
                    return;
            }

            // モーダルの日付に選択日付を設定する
            const dateInput = document.getElementById('weightDt');
            if (dateInput) dateInput.value = info.dateStr;

            // モーダルを表示する
            const modal = new bootstrap.Modal(document.getElementById('weightModal'));
            modal.show();
        },
    });
    calendar.render();
}

/*
    体重入力用カレンダー期間データ取得
*/
function getCalendarDates(viewType, date) {
    let startDt;
    let endDt;

    if (viewType === 'dayGridMonth') {
        const year = date.getFullYear();
        const month = date.getMonth();

        // 月間カレンダーの開始日を作成する
        const firstDt = new Date(year, month, 1);
        startDt = new Date(firstDt);
        startDt.setDate(1 - firstDt.getDay());

        // 月間カレンダーの終了日を作成する（7列 * 6行）
        endDt = new Date(startDt);
        endDt.setDate(startDt.getDate() + 42);
    } else if (viewType === 'dayGridWeek') {
        // 週間カレンダーの開始日を作成する
        startDt = new Date(date);
        startDt.setDate(date.getDate() - date.getDay());

        // 週間カレンダーの終了日を作成する
        endDt = new Date(startDt);
        endDt.setDate(startDt.getDate() + 7);
    } else if (viewType === 'multiMonthYear') {
        // 年間カレンダーの開始日を作成する
        startDt = new Date(date.getFullYear(), 0, 1);

        // 年間カレンダーの終了日を作成する
        endDt = new Date(date.getFullYear() + 1, 0, 1);
    }

    // Date型から日付へ変換する
    const startDtStr = formatDate(startDt);
    const endDtStr = formatDate(endDt);

    return [startDtStr, endDtStr];
}

/*
    体重入力用カレンダー体重データ取得
*/
let weights = [];
async function getCalendarWeights(viewType, startDtStr, endDtStr) {
    // 表示期間の体重データを取得する
    const response = await fetch('/weight/weightCalendar/get?viewType=' + viewType + '&startDt=' + startDtStr + '&endDt=' + endDtStr);
    switch (response.status) {
        // 取得成功（表示期間の体重データを取得する）
        case 200:
            weights = await response.json();
            break;

        // 取得失敗（セッションエラー）
        case 401:
            window.location.href = '/';
            return;
    }
}

/*
    年間カレンダーから月間カレンダーへ切り替える
*/
function initChangeYearToMonth(calendar) {
    document.querySelectorAll('[id^="fc:1:month-"]').forEach((element) => {
        element.addEventListener('click', async () => {
            // 選択月
            const dateStr = element.id.replace('fc:1:month-', '');
            const date = new Date(dateStr + '-01');
            // 選択月の期間データを取得する
            const dates = getCalendarDates('dayGridMonth', date);
            // 選択月の体重データを取得する
            await getCalendarWeights('dayGridMonth', dates[0], dates[1]);
            // 選択月へ切り替える
            calendar.changeView('dayGridMonth', date);
        });
    });
}

/*
    体重入力用モーダル初期表示
    ①体重と体脂肪率の入力欄からフォーカスが外れたときに、小数点第二位以下を切り捨てる
    ②体重データを非同期処理で登録する
    ③モーダルが消えたときに、メッセージを非表示にする
*/
function initWeightModal() {
    // ①体重と体脂肪率の入力欄からフォーカスが外れたときに、小数点第二位以下を切り捨てる
    document.querySelectorAll('#weight, #fat').forEach((input) => {
        input.addEventListener('blur', () => {
            const value = parseFloat(input.value);
            if (!isNaN(value)) {
                input.value = Math.floor(value * 10) / 10;
            }
        });
    });

    // ②体重データを非同期処理で登録する
    const form = document.getElementById('weightForm');
    if (form) {
        form.addEventListener('submit', async (event) => {
            // 同期処理を停止
            event.preventDefault();

            // 非同期処理
            const response = await fetch(form.action, {
                method: form.method,
                body: new FormData(form),
            });

            // 事前に前回のCSSの付与をリセットする
            const statusContent = document.getElementById('weightStatus');
            statusContent.classList.remove('alert-success', 'alert-danger');

            // 結果を取得する
            switch (response.status) {
                // 登録成功（登録成功メッセージを表示し、カレンダーのセルを更新する）
                case 200:
                    // カレンダーのセルを更新する
                    const weight = await response.json();
                    const weightEl = document.getElementById('calendar-weight-' + weight.weightDt);
                    weightEl.textContent = weight.weight == null ? '' : weight.weight + 'kg';

                    const index = weights.findIndex(function (item) {
                        return item.weightDt === weight.weightDt;
                    });

                    if (index > -1) {
                        weights[index] = weight;
                    }

                    // 登録成功メッセージを表示する
                    statusContent.textContent = '登録しました';
                    statusContent.classList.add('alert-success');
                    break;
                // 登録失敗（バリデーションエラー）
                case 400:
                    const errors = await response.json();
                    const fieldEl = Array.from(document.querySelectorAll('#weightForm [name]')).map((element) => element.name);
                    const messages = [];
                    fieldEl.forEach((field) => {
                        if (errors[field]) {
                            messages.push(...errors[field]);
                        }
                    });
                    statusContent.textContent = messages.join('\n');
                    statusContent.classList.add('alert-danger');
                    break;
                // 登録失敗（セッションエラー）
                case 401:
                    window.location.href = '/';
                    return;
            }

            document.getElementById('weightMessage').classList.remove('d-none');
        });
    }

    // ③モーダルが消えたときに、メッセージを非表示にする
    const modalEl = document.getElementById('weightModal');

    modalEl.addEventListener('hidden.bs.modal', () => {
        document.getElementById('weightMessage').classList.add('d-none');
    });
}

/*
    目標体重入力用カレンダー共通変数
*/
// 無期限日付
const INDEFINITE_DATE = '2999-12-31';

/*
    目標体重入力用カレンダー初期表示
*/
let calendar;
let eventId;
async function initWeightGoalCalendar() {
    const calendarEl = document.getElementById('weightGoalCalendar');
    if (!calendarEl) return;

    // 初期表示の期間を指定する
    let viewType = 'dayGridMonth';

    calendar = new FullCalendar.Calendar(calendarEl, {
        // カレンダーの条件
        initialView: 'dayGridMonth',
        locale: 'ja',
        height: '100%',

        // ボタンのカスタマイズ
        buttons: {
            myPrev: {
                text: '‹',
                click: async function () {
                    // 表示の期間を指定する
                    viewType = calendar.view.type;
                    // 先月・先週へ切り替える
                    calendar.prev();
                },
            },
            myNext: {
                text: '›',
                click: async function () {
                    // 表示の期間を指定する
                    viewType = calendar.view.type;
                    // 来月・来週へ切り替える
                    calendar.next();
                },
            },
            myDayGridMonth: {
                text: '月',
                click: async function () {
                    // 表示の期間を指定する
                    viewType = calendar.view.type;
                    // 基準月へ切り替える
                    calendar.changeView('dayGridMonth', calendar.getDate());
                },
            },
            myDayGridWeek: {
                text: '週',
                click: async function () {
                    // 表示の期間を指定する
                    viewType = calendar.view.type;
                    // 基準週へ切り替える
                    calendar.changeView('dayGridWeek', calendar.getDate());
                },
            },
            myMultiMonthYear: {
                text: '年',
                click: function () {
                    // 表示の期間を指定する
                    viewType = 'multiMonthYear';
                    // 基準年へ切り替える
                    calendar.changeView('multiMonthYear', calendar.getDate());
                },
            },
        },

        // カレンダーのヘッダー
        headerToolbar: {
            left: 'myPrev,today,myNext',
            center: 'title',
            right: 'myDayGridWeek,myDayGridMonth,myMultiMonthYear',
        },

        // カレンダーのセルに目標体重データを表示する
        events: async function (info, callback) {
            const date = new Date(info.start);
            // 月間カレンダーの場合は、基準月に調整する
            if (viewType == 'dayGridMonth') {
                date.setDate(date.getDate() + 7);
            }
            // 表示期間の目標体重データを取得する
            const events = await weightGoalEvent(viewType, date);
            callback(events);
        },

        datesSet: async function (info) {
            // 年間カレンダーから月間カレンダーへの切り替え
            if (calendar.view.type === 'multiMonthYear') {
                initChangeYearToMonthByGoal(calendar);
            }
        },

        // カレンダーの日付を選択したときにモーダルを表示する
        dateClick: async function (info) {
            // 選択日付
            const clickDt = info.dateStr;
            // 選択日付の目標体重データを取得する
            const response = await fetch('/weight/weightGoalModal/get?clickDt=' + clickDt);
            let weightGoal = null;
            switch (response.status) {
                // 取得成功（入力済みの目標体重データを表示する）
                case 200:
                    weightGoal = await response.json();
                    document.querySelectorAll('#weightGoalForm [name]').forEach((element) => {
                        // 入力済みの目標体重データを設定する
                        if (element.name === 'edDt' && weightGoal?.edDt === INDEFINITE_DATE) {
                            // 終了日が無期限の場合は、日付を空にする
                            element.value = '';
                        } else {
                            element.value = weightGoal?.[element.name] ?? '';
                        }
                    });
                    break;
                // 取得失敗（セッションエラー）
                case 401:
                    window.location.href = '/';
                    return;
            }

            //終了日無期限処理
            const indefiniteInput = document.getElementById('indefinite');
            indefiniteInput.checked = weightGoal?.edDt == INDEFINITE_DATE;
            const edDtInput = document.getElementById('edDt');
            edDtInput.disabled = indefiniteInput.checked;

            if (weightGoal?.weightGoal == null) {
                // 入力済みの目標体重データが無いときは、初期値を設定する
                const today = new Date();
                const todayStr = formatDate(today);
                // モーダルの開始日
                if (clickDt >= todayStr) {
                    // 選択日付が本日以降の場合は、本日を設定する
                    document.getElementById('stDt').value = todayStr;
                }
                // モーダルの終了日
                if (weightGoal.edDt != INDEFINITE_DATE) {
                    // 選択日付を設定する
                    document.getElementById('edDt').value = clickDt;
                }
            } else {
                // 入力済みの目標体重データが有るときは、削除ボタンを非表示にする
                document.getElementById('weightDelete').classList.remove('d-none');
            }

            // 目標体重データが入力済みの場合は、イベントのidを設定する
            if (weightGoal?.weightGoal != null) {
                const eventEdDt = new Date(weightGoal.edDt);
                eventEdDt.setDate(eventEdDt.getDate() + 1);
                eventId = formatDate(eventEdDt);
            } else {
                eventId = '';
            }
            document.getElementById('eventId').value = eventId;

            // モーダルを表示する
            const modal = new bootstrap.Modal(document.getElementById('weightGoalModal'));
            modal.show();
        },
    });
    calendar.render();
}

/*
    目標体重入力用カレンダー目標体重データ取得
*/
async function weightGoalEvent(viewType, date) {
    // 目標体重入力用カレンダー期間データ取得
    const dates = getCalendarDates(viewType, date);

    // 表示期間の目標体重データを取得する
    const response = await fetch('/weight/weightGoalCalendar/get?viewType=' + viewType + '&startDt=' + dates[0] + '&endDt=' + dates[1]);
    switch (response.status) {
        // 取得成功（表示期間の目標体重データを取得する）
        case 200:
            const weightGoals = await response.json();
            return weightGoals.map((weightGoal) => {
                const eventEdDt = new Date(weightGoal.edDt);
                eventEdDt.setDate(eventEdDt.getDate() + 1);

                return {
                    id: formatDate(eventEdDt),
                    title: weightGoal.weightGoal + 'kg',
                    start: weightGoal.stDt,
                    end: formatDate(eventEdDt),
                };
            });
        // 取得失敗（セッションエラー）
        case 401:
            window.location.href = '/';
            return;
    }
}

/*
    年間カレンダーから月間カレンダーへ切り替える
*/
function initChangeYearToMonthByGoal(calendar) {
    document.querySelectorAll('[id^="fc:1:month-"]').forEach((element) => {
        element.addEventListener('click', async () => {
            // 選択月
            const dateStr = element.id.replace('fc:1:month-', '');
            const date = new Date(dateStr + '-01');
            // 選択月へ切り替える
            calendar.changeView('dayGridMonth', date);
        });
    });
}

/*
    目標体重入力用モーダル初期表示
    ①目標体重の入力欄からフォーカスが外れたときに、小数点第二位以下を切り捨てる
    ②無期限の有無で終了日を活性・非活性にする
    ③目標体重データを非同期処理で登録する
    ④目標体重データを非同期処理で削除する
    ⑤モーダルが消えたときに、メッセージを非表示にする
*/
function initWeightGoalModal() {
    // ①目標体重の入力欄からフォーカスが外れたときに、小数点第二位以下を切り捨てる
    document.querySelector('#weightGoal').addEventListener('blur', function () {
        const value = parseFloat(this.value);
        if (!isNaN(value)) {
            this.value = Math.floor(value * 10) / 10;
        }
    });

    // ②無期限の有無で終了日を活性・非活性にする
    document.getElementById('indefinite').addEventListener('change', function () {
        document.getElementById('edDt').disabled = this.checked;
    });

    // ③目標体重データを非同期処理で登録する
    const form = document.getElementById('weightGoalForm');
    if (form) {
        form.addEventListener('submit', async (event) => {
            // 同期処理を停止
            event.preventDefault();

            const formData = new FormData(form);
            // 無期限の有無で終了日を設定する
            if (document.getElementById('indefinite').checked) formData.set('edDt', INDEFINITE_DATE);

            // 非同期処理
            const response = await fetch(form.action, {
                method: form.method,
                body: formData,
            });

            // 事前に前回のCSSの付与をリセットする
            const statusContent = document.getElementById('weightStatus');
            statusContent.classList.remove('alert-success', 'alert-danger');

            // 結果を取得する
            switch (response.status) {
                // 登録成功（カレンダーのセルを更新し、登録成功メッセージを表示する）
                case 200:
                    // カレンダーのセルを更新する
                    const weightGoal = await response.json();
                    calendar.refetchEvents();
                    eventId = weightGoal.edDt;
                    document.getElementById('eventId').value = eventId;

                    // 登録成功メッセージを表示する
                    statusContent.textContent = '登録しました';
                    statusContent.classList.add('alert-success');

                    // 削除ボタンを表示する
                    document.getElementById('weightDelete').classList.remove('d-none');
                    break;
                // 登録失敗（バリデーションエラー）
                case 400:
                    const errors = await response.json();
                    const messages = [];
                    // フィールド単位のエラーメッセージを出力する
                    const fieldEl = Array.from(document.querySelectorAll('#weightGoalForm [name]')).map((element) => element.name);
                    fieldEl.forEach((field) => {
                        if (errors[field]) {
                            messages.push(...errors[field]);
                        }
                    });
                    // 個別のエラーメッセージを出力する
                    if (errors.global) messages.push(...errors.global);

                    statusContent.textContent = messages.join('\n');
                    statusContent.classList.add('alert-danger');
                    break;
                // 登録失敗（セッションエラー）
                case 401:
                    window.location.href = '/';
                    return;
            }

            document.getElementById('weightMessage').classList.remove('d-none');
        });
    }

    // ④目標体重データを非同期処理で削除する
    document.getElementById('weightDelete').addEventListener('click', async () => {
        if (!confirm('本当に削除しても良いですか？')) return;

        // 非同期処理
        const response = await fetch('/weight/weightGoalModal/delete?eventId=' + eventId);

        // 事前に前回のCSSの付与をリセットする
        const statusContent = document.getElementById('weightStatus');
        statusContent.classList.remove('alert-success', 'alert-danger');

        // 結果を取得する
        switch (response.status) {
            // 削除成功（カレンダーのセルを更新し、削除成功メッセージを表示する）
            case 200:
                // カレンダーのセルを更新する
                calendar.refetchEvents();
                eventId = '';
                document.getElementById('eventId').value = eventId;

                // フォームコントロールをリセットする（目標体重のみ）
                document.getElementById('weightGoal').value = '';

                // 削除成功メッセージを表示する
                statusContent.textContent = '削除しました';
                statusContent.classList.add('alert-success');

                // 削除ボタンを非表示する
                document.getElementById('weightDelete').classList.add('d-none');
                break;
            // 削除失敗（未登録エラー）
            case 400:
                statusContent.textContent = '削除に失敗しました';
                statusContent.classList.add('alert-danger');
                break;
            // 削除失敗（セッションエラー）
            case 401:
                window.location.href = '/';
                return;
        }

        document.getElementById('weightMessage').classList.remove('d-none');
    });

    // ⑤モーダルが消えたときに、オブジェクトを非表示にする
    const modalEl = document.getElementById('weightGoalModal');
    modalEl.addEventListener('hidden.bs.modal', () => {
        document.getElementById('weightMessage').classList.add('d-none');
        document.getElementById('weightDelete').classList.add('d-none');
    });
}

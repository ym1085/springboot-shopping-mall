const URL_VERIFY_REQUEST = '/api/v1/email/send'; // 인증 코드 전송
const URL_VERIFY_EMAIL = '/api/v1/email/verify'; // 이메일 인증
const URL_MEMBER_EXISTS_ACCOUNT = '/api/v1/member/exists'; // 아이디 중복
const URL_MEMBER_JOIN = '/api/v1/member/join'; // 회원 가입

const HTTP_METHODS = {
    GET: 'GET',
    POST: 'POST',
    PUT: 'PUT',
    DELETE: 'DELETE',
    PATCH: 'PATCH',
};

const HEADER_KEY = {
    CONTENT_TYPE: 'Content-Type',
    CACHE_CONTROL: 'Cache-Control',
    ACCEPT: 'Accept',
    COOKIE: 'Cookie',
};

const CONTENT_TYPE = {
    JSON: 'application/json',
    FORM: 'application/x-www-form-urlencoded',
    MULTIPART: 'multipart/form-data',
    TEXT: 'text/plain',
};

function loadLoginForm() {
    closeCurrentModal();
    loadModal('/member/loginForm', 'loginModal');
}

function loadJoinForm() {
    closeCurrentModal();
    loadModal('/member/joinForm', 'joinModal');
}

function closeCurrentModal() {
    // 현재 열려있는 모달이 있으면 닫고 DOM에서 완전히 제거
    const currentModal = $('.modal.show'); // 현재 열려있는 모달을 찾기
    if (currentModal.length > 0) {
        currentModal.modal('hide'); // 모달을 숨기고
        currentModal.on('hidden.bs.modal', function () {
            currentModal.remove(); // DOM에서 완전히 제거
        });
    }
}

function loadModal(url, id) {
    $('#mainModalContainer').empty();
    $('#mainModalContainer').load(url + ' #' + id, function () {
        $('#' + id).modal('show');
    });
}

function movePageWithPageNo(pageNo) {
    const searchForm = document.getElementById('searchForm');
    const queryString = {
        pageNo: pageNo ? pageNo : 1,
        recordSizePerPage: 10,
        pageSize: 10,
        searchType: searchForm.searchType.value,
        searchKeyword: searchForm.searchKeyword.value,
    };
    location.href = location.pathname + '?' + new URLSearchParams(queryString).toString();
}

function focus(element) {
    element.focus();
}

/**
 * 페이지 리로드 함수
 */
function reload() {
    location.reload();
}

/**
 * 이벤트 성공 및 실패 후 화면 이동
 * @param url
 * @param query
 */
function redirectURL(url, query) {
    if (typeof url !== 'string' || isEmpty(url)) {
        showMessage('URL이 존재하지 않습니다.');
        return;
    }

    if (isEmpty(query)) {
        location.href = url;
        return;
    }

    if (isNotEmpty(query)) {
        location.href = url + '?' + query;
        return;
    }

    throw new Error('Invalid query and url location, please try again');
}

// 페이지를 새로 고침하거나 닫을 때 실행되는 코드
window.addEventListener('beforeunload', function () {
    localStorage.removeItem('isClickEnabled');
});

// let isClickEnabled = false; // Todo: 전역 변수 문제 될수도 있을듯... 확인 필요
function checkDoubleClick() {
    let isClickEnabled = localStorage.getItem('isClickEnabled');
    if (isClickEnabled) {
        return true;
    } else {
        localStorage.setItem('isClickEnabled', 'true');
        return false;
    }
}

/**
 * 경고 문구 얼럿 출력
 * @param message
 */
function showMessage(message) {
    alert(message);
}

/**
 * 문자열 공백 여부 체크
 * @param str
 */
function isEmpty(str) {
    return (
        str === '' || str === undefined || str === 'undefined' || str === null || str === 'null'
        // str.trim() === ''
    );
}

/**
 * object 공백 체크
 * @param obj {key1: value1, key2: value2}
 * @returns {boolean}
 */
function isEmptyObject(obj) {
    return Object.keys(obj).length === 0 && obj.constructor === Object;
}

/**
 * object가 비어있지 않은지 체크
 * @param obj {key1: value1, key2: value2}
 * @returns {boolean}
 */
function isNotEmptyObject(obj) {
    // Object.keys(obj).length !== 0: 객체의 속성(key)들이 존재하는지 여부를 확인합니다.
    // obj.constructor === Object: 객체가 일반적인 객체(Object)인지 확인합니다.
    // 둘 중 하나라도 만족하면 객체가 비어있지 않다고 판단합니다.
    return Object.keys(obj).length !== 0 && obj.constructor === Object;
}

/**
 * 문자열 공백 여부 없는 경우 체크
 * @param str
 * @returns {boolean}
 */
function isNotEmpty(str) {
    return !isEmpty(str);
}

/**
 * 입력 문자가 숫자인지 검증
 * @param value
 * @returns {boolean}
 */
function isNotNumericRegExp(value) {
    const regEx = /^\d+$/;
    return !regEx.test(value);
}

/* =================================
------------------------------------
	EndGam - Gaming Magazine Template
	Version: 1.0
 ------------------------------------
 ====================================*/


'use strict';


$(window).on('load', function () {
	/*------------------
		Preloder
	--------------------*/
	$(".loader").fadeOut();
	$("#preloder").delay(400).fadeOut("slow");

});

(function ($) {
	/*------------------
		Navigation
	--------------------*/
	$('.primary-menu').slicknav({
		appendTo: '.header-warp',
		closedSymbol: '<i class="fa fa-angle-down"></i>',
		openedSymbol: '<i class="fa fa-angle-up"></i>'
	});


	/*------------------
		Background Set
	--------------------*/
	$('.set-bg').each(function () {
		var bg = $(this).data('setbg');
		$(this).css('background-image', 'url(' + bg + ')');
	});


	/*------------------
		Hero Slider
	--------------------*/
	$('.hero-slider').owlCarousel({
		loop: true,
		nav: true,
		dots: true,
		navText: ['', '<img src="img/icons/solid-right-arrow.png">'],
		mouseDrag: false,
		animateOut: 'fadeOut',
		animateIn: 'fadeIn',
		items: 1,
		//autoplay: true,
		autoplayTimeout: 10000,
	});

	var dot = $('.hero-slider .owl-dot');
	dot.each(function () {
		var index = $(this).index() + 1;
		if (index < 10) {
			$(this).html('0').append(index + '.');
		} else {
			$(this).html(index + '.');
		}
	});


	/*------------------
		Video Popup
	--------------------*/
	$('.video-popup').magnificPopup({
		type: 'iframe'
	});

	$('#stickySidebar').stickySidebar({
		topSpacing: 60,
		bottomSpacing: 60
	});


})(jQuery);

/*------------------
    리그오브레전드 전적검색
--------------------*/
document.addEventListener("DOMContentLoaded", function () {
	const searchInputLOL = document.getElementById("searchHomeLOL");
	const recentSearchesContainerLOL = document.getElementById("recentSearchesLOL");
	const customPlaceholderLOL = document.querySelector(".custom-placeholder-lol");

	searchInputLOL.addEventListener("focus", function () {
		recentSearchesContainerLOL.style.display = "block";
	});

	searchInputLOL.addEventListener("blur", function () {
		setTimeout(function () {
			recentSearchesContainerLOL.style.display = "none";
		}, 200);
	});

	searchInputLOL.addEventListener("input", function () {
		if (searchInputLOL.value === "") {
			customPlaceholderLOL.style.opacity = 1;
		} else {
			customPlaceholderLOL.style.opacity = 0;
		}
	});
});
/*------------------
    오버워치 전적검색
--------------------*/

document.addEventListener("DOMContentLoaded", function () {
	const searchInputLOL = document.getElementById("searchHomeLOL");
	const recentSearchesContainerLOL = document.getElementById("recentSearchesLOL");
	const customPlaceholderLOL = document.querySelector(".custom-placeholder-lol");

	searchInputLOL.addEventListener("focus", function () {
		recentSearchesContainerLOL.style.display = "block";
	});

	searchInputLOL.addEventListener("blur", function () {
		setTimeout(function () {
			recentSearchesContainerLOL.style.display = "none";
		}, 200);
	});

	searchInputLOL.addEventListener("input", function () {
		if (searchInputLOL.value === "") {
			customPlaceholderLOL.style.opacity = 1;
		} else {
			customPlaceholderLOL.style.opacity = 0;
		}
	});
});

/*------------------
    사이퍼즈 전적검색
--------------------*/

document.addEventListener("DOMContentLoaded", function () {
	const searchInputCyphers = document.getElementById("searchHomeCyphers");
	const recentSearchesContainerCyphers = document.getElementById("recentSearchesCyphers");
	const customPlaceholderCyphers = document.querySelector(".custom-placeholder-cyphers");

	const recentSearchesCyphers = [
		"PlayerA#Cyphers1",
		"PlayerB#Cyphers1",
		"PlayerC#Cyphers1"
	];

	searchInputCyphers.addEventListener("focus", function () {
		recentSearchesContainerCyphers.style.display = "block";
		const ul = recentSearchesContainerCyphers.querySelector("ul");
		ul.innerHTML = "";
		recentSearchesCyphers.forEach(search => {
			const li = document.createElement("li");
			li.textContent = search;
			ul.appendChild(li);
		});
	});

	searchInputCyphers.addEventListener("blur", function () {
		setTimeout(function () {
			recentSearchesContainerCyphers.style.display = "none";
		}, 200);
	});

	searchInputCyphers.addEventListener("input", function () {
		if (searchInputCyphers.value === "") {
			customPlaceholderCyphers.style.opacity = 1;
		} else {
			customPlaceholderCyphers.style.opacity = 0;
		}
	});
});

document.addEventListener('DOMContentLoaded', function () {
	const tierIcon = document.getElementById('tier-icon');
	const tier = "Diamond"; // 티어 정보를 여기서 설정합니다.

	// 티어별 이미지 경로 설정
	const tierImages = {
		"Iron": "img/Emblem_Iron.png",
		"Bronze": "img/Emblem_Bronze.png",
		"Silver": "img/Emblem_Silver.png",
		"Gold": "img/Emblem_Gold.png",
		"Platinum": "img/Emblem_Platinum.png",
		"Diamond": "img/Emblem_Diamond.png",
		"Master": "img/Emblem_Master.png",
		"Grandmaster": "img/Emblem_Grandmaster.png",
		"Challenger": "img/Emblem_Challenger.png",
		"Unranked": "img/Emblem_Unranked.png"
	};

	// 티어에 맞는 이미지로 변경
	if (tierImages[tier]) {
		tierIcon.src = tierImages[tier];
	}
});

//회원가입

document.addEventListener('DOMContentLoaded', function () {
	var btnJoin = document.querySelector('#btnJoin');
	var pw1 = document.querySelector('#pswd1');
	var pw2 = document.querySelector('#pswd2');
	var pwMsg = document.querySelector('#alertTxt');
	var pwMsgArea = document.querySelector('.int_pass');
	var error = document.querySelectorAll('.error_next_box');
	var userName = document.querySelector('#name');
	var email = document.querySelector('#email');
	var idInput = document.querySelector('#nickname');

	btnJoin.addEventListener('click', function (event) {
		if (!validateAllInputs()) {
			alert("모든 필드를 올바르게 채워주세요.");
			event.preventDefault(); // 폼 제출 중지
		}
	});

	pw1.addEventListener("input", checkPw);
	pw2.addEventListener("input", comparePw);
	userName.addEventListener("input", checkName);
	email.addEventListener("input", isEmailCorrect);
	idInput.addEventListener("input", checkId);

	function validateAllInputs() {
		return checkId() && checkPw() && comparePw() && checkName() && isEmailCorrect();
	}

	function checkPw() {
		var pwPattern = /[a-zA-Z0-9~!@#$%^&*()_+|<>?:{}]{8,16}/;
		if (pw1.value === "") {
			error[1].innerHTML = "필수 정보입니다.";
			pwMsg.style.display = "block";
			pwMsgArea.style.paddingRight = "40px";
			error[1].style.display = "block";
			return false;
		} else if (!pwPattern.test(pw1.value)) {
			error[1].innerHTML = "8~16자 영문 대 소문자, 숫자, 특수문자를 사용하세요.";
			pwMsg.innerHTML = "사용불가";
			pwMsgArea.style.paddingRight = "93px";
			error[1].style.display = "block";
			pwMsg.style.color = "red";
			pwMsg.style.display = "block";
			return false;
		} else {
			error[1].style.display = "none";
			pwMsg.innerHTML = "안전";
			pwMsgArea.style.paddingRight = "93px";
			pwMsg.style.color = "#03c75a";
			pwMsg.style.display = "block";
			return true;
		}
	}

	function comparePw() {
		if (pw2.value === "") {
			error[2].innerHTML = "필수 정보입니다.";
			error[2].style.display = "block";
			return false;
		} else if (pw2.value !== pw1.value) {
			error[2].innerHTML = "비밀번호가 일치하지 않습니다.";
			error[2].style.display = "block";
			return false;
		} else {
			error[2].style.display = "none";
			return true;
		}
	}

	function checkName() {
		var namePattern = /[a-zA-Z가-힣]/;
		if (userName.value === "") {
			error[3].innerHTML = "필수 정보입니다.";
			error[3].style.display = "block";
			return false;
		} else if (!namePattern.test(userName.value) || userName.value.indexOf(" ") > -1) {
			error[3].innerHTML = "한글과 영문 대 소문자를 사용하세요. (특수기호, 공백 사용 불가)";
			error[3].style.display = "block";
			return false;
		} else {
			error[3].style.display = "none";
			return true;
		}
	}

	function isEmailCorrect() {
		var emailPattern = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$/;
		if (email.value === "") {
			error[0].innerHTML = "이메일 주소를 입력해주세요.";
			error[0].style.display = "block";
			return false;
		} else if (!emailPattern.test(email.value)) {
			error[0].innerHTML = "유효한 이메일 주소를 입력해주세요.";
			error[0].style.display = "block";
			return false;
		} else {
			error[0].style.display = "none";
			return true;
		}
	}

	function checkId() {
		var idPattern = /^[a-zA-Z0-9가-힣-_]{2,8}$/;
		var errorIndex = 7; // 닉네임의 오류 메시지 위치를 나타내는 인덱스
		var errorMessage = error[errorIndex]; // 해당 인덱스의 오류 메시지 요소를 선택
		if (idInput.value === "") {
			errorMessage.innerHTML = "필수 정보입니다.";
			errorMessage.style.display = "block";
			return false;
		} else if (!idPattern.test(idInput.value)) {
			errorMessage.innerHTML = "2~8자의 영문 소문자, 숫자만 사용 가능합니다.";
			errorMessage.style.display = "block";
			return false;
		} else {
			errorMessage.innerHTML = "멋진 아이디네요!";
			errorMessage.style.color = "#08A600";
			errorMessage.style.display = "block";
			return true;
		}
	}

	var form = document.querySelector("form");
	form.addEventListener("submit", function (event) {
		event.preventDefault(); // 기본 폼 제출 방지

		if (!checkId()) {
			alert("닉네임을 입력해주세요.");
			return;
		}

		var memberEmail = email.value;
		$.ajax({
			url: '/check-email', // 이메일 중복 검사를 처리하는 URL
			type: 'POST',
			data: {memberEmail: memberEmail},
			dataType: 'json'
		})
			.done(function (data) {
				console.log(data);  // 서버 응답 로깅
				if (data.exists) {
					alert("이미 존재하는 이메일입니다."); // 중복 알림
				} else {
					// 중복이 없을 경우 폼을 서버에 제출하고, 성공적으로 제출된 후 페이지를 새로고침
					form.submit();
					window.setTimeout(function () {
						location.reload(true); // 폼 제출 후 페이지를 새로고침
					}, 1000); // 폼 제출 후 충분한 시간을 주어 서버가 처리할 수 있도록 합니다
				}
			})
			.fail(function (xhr, status, error) {
				alert("오류가 발생했습니다. 다시 시도해주세요."); // 오류 알림
			});
	});
});

document.getElementById('login-btn').addEventListener('click', function (event) {
	event.preventDefault();
	document.getElementById('login-modal').classList.add('active');
	document.getElementById('modal-overlay').classList.add('active');
});

document.getElementById('close-login-modal').addEventListener('click', function () {
	document.getElementById('login-modal').classList.remove('active');
	document.getElementById('modal-overlay').classList.remove('active');
});

document.getElementById('modal-overlay').addEventListener('click', function () {
	document.getElementById('login-modal').classList.remove('active');
	document.getElementById('modal-overlay').classList.remove('active');
});

//회원가입

document.addEventListener('DOMContentLoaded', function () {
	var btnJoin = document.querySelector('#btnJoin');
	var pw1 = document.querySelector('#pswd1');
	var pw2 = document.querySelector('#pswd2');
	var pwMsg = document.querySelector('#alertTxt');
	var pwMsgArea = document.querySelector('.int_pass');
	var error = document.querySelectorAll('.error_next_box');
	var userName = document.querySelector('#name');
	var email = document.querySelector('#email');
	var idInput = document.querySelector('#nickname');
	var form = document.querySelector("form");

	btnJoin.addEventListener('click', function (event) {
		if (!validateAllInputs()) {
			alert("모든 필드를 올바르게 채워주세요.");
			event.preventDefault(); // 폼 제출 중지
		}
	});

	pw1.addEventListener("input", checkPw);
	pw2.addEventListener("input", comparePw);
	userName.addEventListener("input", checkName);
	email.addEventListener("input", isEmailCorrect);
	idInput.addEventListener("input", checkId);

	function validateAllInputs() {
		return checkId() && checkPw() && comparePw() && checkName() && isEmailCorrect();
	}

	function checkPw() {
		var pwPattern = /[a-zA-Z0-9~!@#$%^&*()_+|<>?:{}]{8,16}/;
		if (pw1.value === "") {
			error[1].innerHTML = "필수 정보입니다.";
			pwMsg.style.display = "block";
			pwMsgArea.style.paddingRight = "40px";
			error[1].style.display = "block";
			return false;
		} else if (!pwPattern.test(pw1.value)) {
			error[1].innerHTML = "8~16자 영문 대 소문자, 숫자, 특수문자를 사용하세요.";
			pwMsg.innerHTML = "사용불가";
			pwMsgArea.style.paddingRight = "93px";
			error[1].style.display = "block";
			pwMsg.style.color = "red";
			pwMsg.style.display = "block";
			return false;
		} else {
			error[1].style.display = "none";
			pwMsg.innerHTML = "안전";
			pwMsgArea.style.paddingRight = "93px";
			pwMsg.style.color = "#03c75a";
			pwMsg.style.display = "block";
			return true;
		}
	}

	function comparePw() {
		if (pw2.value === "") {
			error[2].innerHTML = "필수 정보입니다.";
			error[2].style.display = "block";
			return false;
		} else if (pw2.value !== pw1.value) {
			error[2].innerHTML = "비밀번호가 일치하지 않습니다.";
			error[2].style.display = "block";
			return false;
		} else {
			error[2].style.display = "none";
			return true;
		}
	}

	function checkName() {
		var namePattern = /[a-zA-Z가-힣]/;
		if (userName.value === "") {
			error[3].innerHTML = "필수 정보입니다.";
			error[3].style.display = "block";
			return false;
		} else if (!namePattern.test(userName.value) || userName.value.indexOf(" ") > -1) {
			error[3].innerHTML = "한글과 영문 대 소문자를 사용하세요. (특수기호, 공백 사용 불가)";
			error[3].style.display = "block";
			return false;
		} else {
			error[3].style.display = "none";
			return true;
		}
	}

	function isEmailCorrect() {
		var emailPattern = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$/;
		if (email.value === "") {
			error[0].innerHTML = "이메일 주소를 입력해주세요.";
			error[0].style.display = "block";
			return false;
		} else if (!emailPattern.test(email.value)) {
			error[0].innerHTML = "유효한 이메일 주소를 입력해주세요.";
			error[0].style.display = "block";
			return false;
		} else {
			error[0].style.display = "none";
			return true;
		}
	}

	function checkId() {
		var idPattern = /^[a-zA-Z0-9가-힣-_]{2,8}$/;
		var errorIndex = 4; // 닉네임의 오류 메시지 위치를 나타내는 인덱스
		var errorMessage = error[errorIndex]; // 해당 인덱스의 오류 메시지 요소를 선택
		if (idInput.value === "") {
			errorMessage.innerHTML = "필수 정보입니다.";
			errorMessage.style.display = "block";
			return false;
		} else if (!idPattern.test(idInput.value)) {
			errorMessage.innerHTML = "2~8자의 영문 소문자, 숫자만 사용 가능합니다.";
			errorMessage.style.display = "block";
			return false;
		} else {
			errorMessage.style.display = "none";
			return true;
		}
	}

	form.addEventListener("submit", function (event) {
		event.preventDefault(); // 기본 폼 제출 방지

		if (!validateAllInputs()) {
			alert("모든 필드를 올바르게 채워주세요.");
			return;
		}

	});
});







document.getElementById('login-btn').addEventListener('click', function (event) {
	event.preventDefault();
	document.getElementById('login-modal').classList.add('active');
	document.getElementById('modal-overlay').classList.add('active');
});

document.getElementById('close-login-modal').addEventListener('click', function () {
	document.getElementById('login-modal').classList.remove('active');
	document.getElementById('modal-overlay').classList.remove('active');
});

document.getElementById('modal-overlay').addEventListener('click', function () {
	document.getElementById('login-modal').classList.remove('active');
	document.getElementById('modal-overlay').classList.remove('active');
});

//vote
document.addEventListener('DOMContentLoaded', (event) => {
	const checkboxes = document.querySelectorAll('input[type="checkbox"]');
	let checkedCount = 0;

	checkboxes.forEach(checkbox => {
		checkbox.addEventListener('change', () => {
			if (checkbox.checked) {
				checkedCount++;
				if (checkedCount > 5) {
					checkbox.checked = false;
					checkedCount--;
					alert('5개까지만 선택 가능합니다.');
				}
			} else {
				checkedCount--;
			}
		});
	});
});

document.addEventListener("DOMContentLoaded", function() {
	const loginBtn = document.getElementById("login-btn");
	const closeLoginModalBtn = document.getElementById("close-login-modal");
	const forgotPasswordBtn = document.getElementById("forgot-password-btn");
	const closeForgotPasswordModalBtn = document.getElementById("close-forgot-password-modal");

	const loginModal = document.getElementById("login-modal");
	const forgotPasswordModal = document.getElementById("forgot-password-modal");
	const modalOverlay = document.getElementById("modal-overlay");

	if (loginBtn) {
		loginBtn.addEventListener("click", function() {
			loginModal.classList.add("active");
			modalOverlay.classList.add("active");
		});
	}

	if (closeLoginModalBtn) {
		closeLoginModalBtn.addEventListener("click", function() {
			loginModal.classList.remove("active");
			modalOverlay.classList.remove("active");
		});
	}

	if (forgotPasswordBtn) {
		forgotPasswordBtn.addEventListener("click", function() {
			loginModal.classList.remove("active");
			forgotPasswordModal.classList.add("active");
			modalOverlay.classList.add("active");
		});
	}

	if (closeForgotPasswordModalBtn) {
		closeForgotPasswordModalBtn.addEventListener("click", function() {
			forgotPasswordModal.classList.remove("active");
			modalOverlay.classList.remove("active");
		});
	}

	if (modalOverlay) {
		modalOverlay.addEventListener("click", function() {
			loginModal.classList.remove("active");
			forgotPasswordModal.classList.remove("active");
			modalOverlay.classList.remove("active");
		});
	}

	const emailCheckBtn = document.getElementById('emailCheckBtn');
	const injeungCheckBtn = document.getElementById('injeungCheckBtn');
	const findPwdBtn = document.getElementById('findPwdBtn');

	let check_email = false;

	emailCheckBtn.addEventListener("click", function(){
		const mbEmail = document.getElementById( 	'mbEmail').value;
		const emailCheckWarn = document.getElementById('emailCheckWarn');

		$.ajax({
			url: "/checkNameEmail.dw", // 해당 URL을 이메일 확인 URL로 변경하세요.
			data: {"mbEmail": mbEmail},
			success: (data) => {
				if(data === 'true') {
					$.ajax({
						url: "/mailInjeung.dw",
						data: {"mbEmail": mbEmail},
						success: (data) => {
							emailCheckWarn.innerText = '인증번호가 발송되었습니다.';
							emailCheckWarn.style.color = "black";
							document.getElementById('injeungbunho').value = data;
						},
						error: (error) => {
							emailCheckWarn.innerText = '인증번호 발송에 실패하였습니다.';
							emailCheckWarn.style.color = "red";
						}
					});
				} else {
					emailCheckWarn.innerText = "이메일 주소를 다시 확인해주세요.";
					emailCheckWarn.style.color = "red";
				}
			},
			error: (error) => {
				console.log(error);
			}
		});
	});
});
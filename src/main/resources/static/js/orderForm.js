(function(){
    const menuSelect = document.getElementById('menuSelect');
    const resetBtn = document.getElementById('resetBtn');
    const menuCards = document.querySelectorAll('.menu-card');

    const voiceBtn = document.getElementById('voiceBtn');
    const voiceInput = document.getElementById('voiceText');
    const voiceForm = document.getElementById('voiceForm');
    const voiceMessage = document.getElementById('voiceMessage');

    let recognition = null;
    let recognizing = false;
    let interimTranscript = "";

    // 아이템 input들을 객체로 캐싱
    const items = {
        wine: document.getElementById('wine'),
        steak: document.getElementById('steak'),
        coffee_cup: document.getElementById('coffee_cup'),
        coffee_pot: document.getElementById('coffee_pot'),
        salad: document.getElementById('salad'),
        eggscramble: document.getElementById('eggscramble'),
        bacon: document.getElementById('bacon'),
        bread: document.getElementById('bread'),
        baguette: document.getElementById('baguette'),
        champagne: document.getElementById('champagne')
    };

    // 모든 아이템 수량 0으로 초기화하는 함수
    function resetItems() {
        Object.values(items).forEach(input => input.value = 0);
    }

    // 메뉴 선택 시 스타일/아이템 초기화 및 기본값 반영 로직
    function updateMenuSelection(selectedValue) {
        // 1. 모든 아이템 수량 초기화
        resetItems();

        // 2. 스타일 옵션과 값 초기화
        document.querySelectorAll('input[name="dinnerStyle"]').forEach(radio => radio.disabled = false); // 모든 스타일 활성화
        document.querySelector('#styleSimple').checked = true; // 'Simple'을 기본으로 선택

        // 3. 메뉴별 기본 아이템 값 설정
        switch(selectedValue){
            case 'VALENTINE':
                items.wine.value = 1;
                items.steak.value = 1;
                break;
            case 'FRENCH':
                items.coffee_cup.value = 1;
                items.wine.value = 1;
                items.salad.value = 1;
                items.steak.value = 1;
                break;
            case 'ENGLISH':
                items.eggscramble.value = 1;
                items.bacon.value = 1;
                items.bread.value = 1;
                items.steak.value = 1;
                break;
            case 'CHAMPAGNE':
                items.champagne.value = 1;
                items.baguette.value = 4;
                items.coffee_pot.value = 1;
                items.wine.value = 1;
                items.steak.value = 1;

                // CHAMPAGNE 메뉴의 특수 로직: SIMPLE 스타일 비활성화
                document.querySelector('#styleSimple').disabled = true;
                document.querySelector('#styleGrand').checked = true;
                break;
        }
    }

    function selectMenu(menuValue) {
        if (!menuValue) return; // 메뉴 값이 없으면 아무것도 하지 않음

        const upperMenuValue = menuValue.toUpperCase();

        // 1. 시각적 활성화/비활성화
        menuCards.forEach(card => {
            if (card.dataset.menuValue === upperMenuValue) {
                card.classList.add('selected');
            } else {
                card.classList.remove('selected');
            }
        });

        // 2. 숨겨진 <select> 값 업데이트 (폼 제출을 위해 필수)
        menuSelect.value = upperMenuValue;

        // 3. 아이템/스타일 초기화 및 기본값 설정 로직 실행
        updateMenuSelection(upperMenuValue);
    }

    // --- 메뉴 카드 클릭 이벤트 핸들러 ---
    menuCards.forEach(card => {
        card.addEventListener('click', () => {
            const menuValue = card.getAttribute('data-menu-value');
            selectMenu(menuValue); // 새로 만든 함수 호출
        });
    });
    // reset 시 옵션과 아이템 재설정
    resetBtn.addEventListener('click', () => {
        resetItems();
        document.querySelectorAll('input[name="dinnerStyle"]').forEach(radio => {
            radio.disabled = false;
            radio.checked = false;
        });
        menuSelect.value = '';

        // 초기화 시 카드 선택 표시 제거
        menuCards.forEach(c => c.classList.remove('selected'));
    });

    // 음성 인식 버튼
    voiceBtn.addEventListener('click', () => {
        const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;
        if (!SpeechRecognition) { alert("브라우저가 음성인식을 지원하지 않습니다."); return; }
        if (recognizing) { recognition.stop(); recognizing = false; return; }

        recognition = new SpeechRecognition();
        recognition.lang = "ko-KR";
        recognition.interimResults = true;
        recognizing = true;
        interimTranscript = "";
        const originalText = "음성 인식";
        voiceBtn.textContent = "말하는 중... (클릭 시 종료)";
        recognition.start();

        recognition.onresult = (event) => {
            let finalTranscript = "";
            for (let i = event.resultIndex; i < event.results.length; ++i) {
                if (event.results[i].isFinal) finalTranscript += event.results[i][0].transcript;
                else interimTranscript = event.results[i][0].transcript;
            }
            voiceInput.value = finalTranscript || interimTranscript;
        };

        recognition.onend = () => {
            recognizing = false;
            voiceBtn.textContent = originalText;
            if (interimTranscript && !voiceInput.value) voiceInput.value = interimTranscript;
            voiceMessage.textContent = "인식 종료. 필요 시 텍스트를 수정 후 적용 버튼을 누르세요.";
        };
    });

    // 적용 버튼 (AI → JSON → Form 반영)
    voiceForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const text = voiceInput.value.trim();
        if (!text) { voiceMessage.textContent = "텍스트가 비어 있습니다."; return; }
        voiceMessage.textContent = "AI 파싱 중...";

        // ... voiceForm submit 이벤트 리스너 내부 try 블록
       try {
           const response = await fetch("/customer/ai-parse-order", {
               method: "POST",
               headers: {"Content-Type": "application/json"},
               body: JSON.stringify({ text })
           });
           const data = await response.json();
           if (data.error) { voiceMessage.textContent = "Error: " + data.error; return; }

           // JSON => menu, style 반영
           if (data.menu) {
                   selectMenu(data.menu);
           }
           if (data.style) {
               const styleRadio = document.querySelector(`input[name="dinnerStyle"][value="${data.style.toUpperCase()}"]`);
               if (styleRadio) styleRadio.checked = true;
           }

           // JSON => item 반영
           // selectmenu가 이미 아이템 초기화 수행하므로 resetItems() 제거
           if (data.items && typeof data.items === "object") {
               Object.keys(data.items).forEach(key => {
                   if (items[key]) { // 우리 item 목록에 있는 키일 경우에만
                       let val = parseInt(data.items[key]);
                       items[key].value = isNaN(val) || val < 0 ? 0 : val;
                   }
               });
           }

           // JSON => deliveryAddress, cardNumber 반영
           document.getElementById("deliveryAddress").value = data.deliveryAddress || "";
           document.getElementById("cardNumber").value = data.cardNumber || "";

           voiceMessage.textContent = "주문 폼이 자동으로 채워졌습니다.";
           if (data.comment) { // comment가 있으면 추가 표시
               voiceMessage.innerHTML += "<br>Comment: " + (data.comment ?? "");
           }

       } catch (err) {
           voiceMessage.textContent = "오류: " + err.message;
       }
    });
})();
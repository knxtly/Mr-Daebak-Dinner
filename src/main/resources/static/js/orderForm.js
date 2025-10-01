(function(){
    const menuSelect = document.getElementById('menuSelect');
    const styleSelect = document.getElementById('styleSelect');
    const resetBtn = document.getElementById('resetBtn');

    const voiceBtn = document.getElementById('voiceBtn');
    const voiceInput = document.getElementById('voiceText');
    const voiceForm = document.getElementById('voiceForm');
    const voiceMessage = document.getElementById('voiceMessage');

    let recognition = null;
    let recognizing = false;
    let interimTranscript = "";

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

    function resetItems() {
        Object.values(items).forEach(input => input.value = 0);
    }

    menuSelect.addEventListener('change', () => {
        resetItems();
        switch(menuSelect.value){
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
                Array.from(styleSelect.options).forEach(opt => {
                    if(opt.value === 'SIMPLE'){
                        opt.disabled = true;
                        if(styleSelect.value === 'SIMPLE') styleSelect.value = 'GRAND';
                    } else {
                        opt.disabled = false;
                    }
                });
                return;
        }
        Array.from(styleSelect.options).forEach(opt => opt.disabled = false);
        styleSelect.value = 'SIMPLE';
    });

    resetBtn.addEventListener('click', () => {
        resetItems();
        Array.from(styleSelect.options).forEach(opt => opt.disabled = false);
        styleSelect.value = '';
        menuSelect.value = '';
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
        const originalText = voiceBtn.textContent;
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

        try {
            const response = await fetch("/customer/ai-parse-order", {
                method: "POST",
                headers: {"Content-Type": "application/json"},
                body: JSON.stringify({ text })
            });
            const data = await response.json();
            if (data.error) { voiceMessage.textContent = "Error: " + data.error; return; }

            // Structured Output 안전 반영
            if (data.menu) menuSelect.value = data.menu;
            if (data.style) styleSelect.value = data.style;

            resetItems();
            if (data.items && typeof data.items === "object") {
                Object.keys(items).forEach(key => {
                    let val = parseInt(data.items[key]);
                    items[key].value = isNaN(val) || val < 0 ? 0 : val;
                });
            }

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

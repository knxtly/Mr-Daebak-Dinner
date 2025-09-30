(function(){
    const menuSelect = document.getElementById('menuSelect');
    const styleSelect = document.getElementById('styleSelect');
    const resetBtn = document.getElementById('resetBtn');
    const menuCards = document.querySelectorAll('.menu-card'); // class = menu-card인 모든 요소 저장

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

    // 모든 아이템 수량 0으로 초기화
    function resetItems() {
        Object.values(items).forEach(input => input.value = 0);
    }

    // 메뉴 선택 시 스타일/아이템 초기화 및 기본값 반영 로직
    function updateMenuSelection(selectedValue) {
        // 1. 모든 아이템 수량 초기화 (가장 중요한 요구사항)
        resetItems();

        // 2. 스타일 옵션과 값 초기화 (가장 중요한 요구사항)
        Array.from(styleSelect.options).forEach(opt => opt.disabled = false); // 모든 스타일 활성화
        styleSelect.value = ''; // 스타일 선택 값 초기화

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
                Array.from(styleSelect.options).forEach(opt => {
                    if(opt.value === 'SIMPLE'){
                        opt.disabled = true;
                    }
                });
                break;
        }
    }

    // --- 메뉴 카드 클릭 이벤트 핸들러 ---
    menuCards.forEach(card => {
        card.addEventListener('click', () => {
            const menuValue = card.getAttribute('data-menu-value');

            // 1. 시각적 활성화/비활성화
            menuCards.forEach(c => c.classList.remove('selected'));
            card.classList.add('selected');

            // 2. 숨겨진 <select> 값 업데이트 (폼 제출을 위해 필수)
            menuSelect.value = menuValue;

            // 3. 아이템/스타일 초기화 및 기본값 설정 로직 실행
            updateMenuSelection(menuValue);
        });
    });

    // reset 시 옵션과 아이템 재설정
    resetBtn.addEventListener('click', () => {
        resetItems();
        Array.from(styleSelect.options).forEach(opt => opt.disabled = false);
        styleSelect.value = '';
        menuSelect.value = '';

        // 초기화 시 카드 선택 표시 제거
        menuCards.forEach(c => c.classList.remove('selected'));
    });
})();
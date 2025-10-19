// 갤러리 썸네일 클릭 시 메인 이미지 교체
(function(){
  const main = document.getElementById('mainImage');
  if (!main) return;

  document.querySelectorAll('.thumbs .thumb').forEach((thumb) => {
    thumb.addEventListener('click', () => {
      document.querySelectorAll('.thumbs .thumb').forEach(t => t.classList.remove('active'));
      thumb.classList.add('active');

      const direct = thumb.getAttribute('data-src');
      const bg = thumb.style.backgroundImage;
      const url = direct || (bg ? bg.slice(5, -2) : null);
      if (url) {
        main.style.backgroundImage = `url('${url}')`;
      }
    });
  });
})();

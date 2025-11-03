(function(){
    console.log('here???')
    try {
        var stored = localStorage.getItem('jw-theme');
        var prefersDark = window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches;
        var theme = (stored === 'light' || stored === 'dark') ? stored : (prefersDark ? 'dark' : 'light');
        document.documentElement.setAttribute('data-theme', theme);
    } catch (error) {
        document.documentElement.setAttribute('data-theme', 'light');
    }

    const $mypageBtn = $('.my-page');
    console.log('$mypageBtn',$mypageBtn)
    console.log('$mypageBtn',$mypageBtn)
    $mypageBtn.style.display = 'none'
})()


(function($){
    $(function(){
        const pageSize = 10;
        const $rows = $('table.table tbody tr');
        const total = $rows.length;
        const totalPages = Math.max(1, Math.ceil(total / pageSize));

        function renderPagination(current){
            const $p = $('#pagination').empty();
            if(totalPages <= 1) return;

            const $prev = $('<li class="page-item"><a href="#" class="page-link">Previous</a></li>');
            if (current === 1) {
                $prev.addClass('disabled');
            }
            $prev.on('click', function(e) {
                e.preventDefault();
                if (current > 1) {
                    renderPage(current - 1);
                }
            });
            $p.append($prev);

            for (let i = 1; i <= totalPages; i++) {
                const $li = $(`<li class="page-item ${i===current? 'active' : ''}"><a href="#" class="page-link">${i}</a></li>`);
                (function(page) {
                    $li.on('click', function(e) {
                        e.preventDefault();
                        renderPage(page);
                    });
                })(i);
                $p.append($li);
            }

            const $next = $('<li class="page-item"><a href="#" class="page-link">Next</a></li>');
            if (current === totalPages) {
                $next.addClass('disabled');
            }
            $next.on('click', function(e) {
                e.preventDefault();
                if (current < totalPages) {
                    renderPage(current + 1);
                }
            });
            $p.append($next);
        }

        function renderPage(page){
            const start = (page - 1) * pageSize;
            const end = start + pageSize;
            $rows.hide().slice(start, end).show();
            let showingStart;
            if (total === 0) {
                showingStart = 0;
            } else {
                showingStart = start + 1;
            }
            const showingEnd = Math.min(end, total);
            $('#showingText').text(`Showing ${showingStart} to ${showingEnd} of ${total} entries`);
            renderPagination(page);
        }

        // initialize
        renderPage(1);
    });

    $(document).ready(function () {
        $('[data-toggle="tooltip"]').tooltip();
    });
})(jQuery);

/* Avatar file validation (moved from newemployee template) */
(function(){
    const MAX_BYTES = 2 * 1024 * 1024; // 2MB
    const fileInput = document.getElementById('avatar');
    if (fileInput) {
        fileInput.addEventListener('change', function(e){
            const f = e.target.files[0];
            if (!f) return;
            if (!f.type.startsWith('image/')) {
                alert('Please select an image file (JPG/PNG/GIF).');
                e.target.value = '';
                return;
            }
            if (f.size > MAX_BYTES) {
                alert('Image too large. Maximum allowed size is 2MB.');
                e.target.value = '';
            }
        });
    }
})();

const token = localStorage.getItem('token');

async function fetchFiles() {
    const response = await fetch('/api/files/my-files', {
        headers: { 'Authorization': `Bearer ${token}` }
    });
    const files = await response.json();
    renderFiles(files);
}

function renderFiles(files) {
    const tbody = document.getElementById('fileList');
    tbody.innerHTML = files.map(file => `
        <tr>
            <td><i class="fa-regular fa-file-lines file-icon"></i> ${file.fileName}</td>
            <td>tôi</td>
            <td>${new Date(file.uploadDate).toLocaleDateString('vi-VN')}</td>
            <td>${file.fileSizeReadable}</td>
            <td>
                <button class="action-btn" onclick="downloadFile(${file.id}, '${file.fileName}')"><i class="fa-solid fa-download"></i></button>
                <button class="action-btn" onclick="deleteFile(${file.id})"><i class="fa-solid fa-trash-can"></i></button>
            </td>
        </tr>
    `).join('');
}

document.addEventListener('DOMContentLoaded', () => {
    const trigger = document.getElementById('profileTrigger');
    const menu = document.getElementById('profileMenu');
    const logoutBtn = document.getElementById('logoutBtn');

    const username = localStorage.getItem('username') || 'Nam';
    const email = localStorage.getItem('email') || `${username.toLowerCase()}@gmail.com`;

    if(trigger) trigger.innerText = username.charAt(0).toUpperCase();

    const largeAvatar = document.querySelector('.large-avatar');
    if(largeAvatar) largeAvatar.innerText = username.charAt(0).toUpperCase();

    const displayEmail = document.getElementById('displayEmail');
    if(displayEmail) displayEmail.innerText = email;

    const displayFullName = document.getElementById('displayFullName');
    if(displayFullName) displayFullName.innerText = `Hi ${username}!`;

    trigger.addEventListener('click', (e) => {
        e.stopPropagation();
        menu.classList.toggle('show');
        console.log("Dropdown state:", menu.classList.contains('show'));
    });

    document.addEventListener('click', (e) => {
        if (!menu.contains(e.target) && e.target !== trigger) {
            menu.classList.remove('show');
        }
    });

    document.addEventListener('keydown', (e) => {
        if (e.key === 'Escape') menu.classList.remove('show');
    });

    if(logoutBtn) {
        logoutBtn.addEventListener('click', () => {
            localStorage.clear();
            window.location.href = '/login.html';
        });
    }

    if (typeof fetchFiles === 'function') {
        fetchFiles();
    }
});

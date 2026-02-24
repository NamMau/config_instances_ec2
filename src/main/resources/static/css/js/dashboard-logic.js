const token = localStorage.getItem('token');
// Hàm tải file lên Server
async function uploadFile(input) {
    const files = input.files;
    if (files.length === 0) return;

    const formData = new FormData();
    for (let i = 0; i < files.length; i++) {
        formData.append('files', files[i]);
    }

    try {
        const response = await fetch('/api/files/upload', {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('token')}`
            },
            body: formData
        });

        if (response.ok) {
            alert('Tải lên thành công!');
            fetchFiles(); // Cập nhật lại danh sách ngay lập tức
        } else {
            const error = await response.json();
            alert('Lỗi: ' + (error.message || 'Không thể tải lên'));
        }
    } catch (err) {
        console.error('Upload error:', err);
    } finally {
        input.value = ''; // Reset input để có thể chọn lại cùng 1 file
    }
}

// Hàm chọn Icon dựa trên định dạng file
function getFileIcon(fileName) {
    const ext = fileName.split('.').pop().toLowerCase();
    switch (ext) {
        case 'pdf': return '<i class="fa-solid fa-file-pdf" style="color: #ea4335;"></i>';
        case 'jpg':
        case 'jpeg':
        case 'png':
        case 'gif': return '<i class="fa-solid fa-file-image" style="color: #f4b400;"></i>';
        case 'doc':
        case 'docx': return '<i class="fa-solid fa-file-word" style="color: #4285f4;"></i>';
        case 'xls':
        case 'xlsx': return '<i class="fa-solid fa-file-excel" style="color: #0f9d58;"></i>';
        case 'zip':
        case 'rar': return '<i class="fa-solid fa-file-zipper" style="color: #5f6368;"></i>';
        default: return '<i class="fa-solid fa-file-lines" style="color: var(--box-blue);"></i>';
    }
}

function renderFiles(files) {
    const tbody = document.getElementById('fileList');
    if (!tbody) return;

    if (files.length === 0) {
        tbody.innerHTML = `<tr><td colspan="5" style="text-align:center; padding: 50px; color: #999;">Thư mục trống</td></tr>`;
        return;
    }

    tbody.innerHTML = files.map(file => `
        <tr>
            <td>
                <div style="display: flex; align-items: center; gap: 10px;">
                    ${getFileIcon(file.fileName)}
                    <span class="file-name" style="cursor:pointer;" onclick="previewFile(${file.id})">${file.fileName}</span>
                </div>
            </td>
            <td>tôi</td>
            <td>${new Date(file.uploadDate).toLocaleDateString('vi-VN')}</td>
            <td>${file.fileSizeReadable || '0 KB'}</td>
            <td>
                <button class="action-btn" onclick="downloadFile(${file.id}, '${file.fileName}')"><i class="fa-solid fa-download"></i></button>
                <button class="action-btn" onclick="deleteFile(${file.id})"><i class="fa-solid fa-trash-can"></i></button>
            </td>
        </tr>
    `).join('');
}
async function fetchFiles() {
    const response = await fetch('/api/files/my-files', {
        headers: { 'Authorization': `Bearer ${token}` }
    });
    const files = await response.json();
    renderFiles(files);
}

function renderFiles(files) {
    const tbody = document.getElementById('fileList');
    if (!tbody) return;

    tbody.innerHTML = files.map(file => `
        <tr>
            <td>
                <div style="display: flex; align-items: center; gap: 10px;">
                    ${getFileIcon(file.fileName)}
                    <span class="file-name" style="cursor:pointer; font-weight:500;"
                          onclick="previewFile(${file.id}, '${file.fileName}')">
                        ${file.fileName}
                    </span>
                </div>
            </td>
            <td>tôi</td>
            <td>${new Date(file.uploadDate).toLocaleDateString('vi-VN')}</td>
            <td>${file.fileSizeReadable || '0 KB'}</td>
            <td>
                <button class="action-btn" onclick="downloadFile(${file.id}, '${file.fileName}')"><i class="fa-solid fa-download"></i></button>
                <button class="action-btn" onclick="deleteFile(${file.id})"><i class="fa-solid fa-trash-can"></i></button>
            </td>
        </tr>
    `).join('');
}

async function previewFile(fileId, fileName) {
    if (!fileName) {
        console.error("fileName is missing!");
        return;
    }

    const modal = document.getElementById('previewModal');
    const img = document.getElementById('previewImage');
    const pdf = document.getElementById('previewPDF');
    const other = document.getElementById('previewOther');

    // 1. Reset trạng thái cũ
    img.style.display = 'none';
    pdf.style.display = 'none';
    other.style.display = 'none';
    img.src = "";
    pdf.src = "";

    document.getElementById('previewFileName').innerText = fileName;
    const ext = fileName.split('.').pop().toLowerCase();
    const token = localStorage.getItem('token');

    try {
        const response = await fetch(`/api/files/preview/${fileId}`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });

        // 2. Nếu server trả về lỗi (400, 404, 500)
        if (!response.ok) {
            const errorData = await response.json().catch(() => ({}));
            throw new Error(errorData.message || `Server error: ${response.status}`);
        }

        const blob = await response.blob();
        const objectUrl = URL.createObjectURL(blob);

        // 3. Hiển thị dựa trên định dạng
        if (['jpg', 'jpeg', 'png', 'gif', 'jfif'].includes(ext)) {
            img.src = objectUrl;
            img.style.display = 'block';
        } else if (ext === 'pdf') {
            pdf.src = objectUrl;
            pdf.style.display = 'block';
        } else {
            other.style.display = 'block';
            other.innerText = "Định dạng này không hỗ trợ xem trước trực tiếp.";
        }

        modal.style.display = "block";

        // Gán sự kiện cho nút download trong modal nếu có
        const downloadBtn = document.getElementById('previewDownloadBtn');
        if (downloadBtn) {
            downloadBtn.onclick = () => downloadFile(fileId, fileName);
        }

    } catch (err) {
        console.error("Preview detail error:", err);
        alert("Lỗi xem trước: " + err.message);
    }
}

document.querySelector('.close-preview').onclick = () => {
    document.getElementById('previewModal').style.display = "none";
    document.getElementById('previewImage').src = "";
    document.getElementById('previewPDF').src = "";
};

window.addEventListener('keydown', (e) => {
    if (e.key === 'Escape') document.getElementById('previewModal').style.display = "none";
});


async function downloadFile(fileId, fileName) {
    try {
        const response = await fetch(`/api/files/download/${fileId}`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('token')}`
            }
        });

        if (response.ok) {
            const blob = await response.blob();
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = fileName; // Đặt tên file khi tải về
            document.body.appendChild(a);
            a.click();
            window.URL.revokeObjectURL(url);
            a.remove();
        } else {
            alert("cannot download file. Error: " + response.status);
        }
    } catch (err) {
        console.error("Download error:", err);
    }
}

// 2. Hàm Xóa file
async function deleteFile(fileId) {
    if (!confirm("Do you want to delete this file")) return;

    try {
        const response = await fetch(`/api/files/delete/${fileId}`, {
            method: 'DELETE',
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('token')}`
            }
        });

        if (response.ok) {
            alert("Delete success!");
            fetchFiles(); // Reload lại bảng
        } else {
            alert("Error when delete file.");
        }
    } catch (err) {
        console.error("Delete error:", err);
    }
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

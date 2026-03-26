const API_BASE_URL = 'http://localhost:8080/api';
const Toast = Swal.mixin({ toast: true, position: 'top-end', showConfirmButton: false, timer: 3000 });
let chartsInstance = {};
let currentStudentPage = 0;
let currentSClassPage = 0;

// Các biến lưu trữ trạng thái Khoa đang được chọn để hiển thị Ngành
let currentFacultyId = null;
let currentFacultyName = '';

// Lưu trữ danh sách để load form/filter
let globalMajors = [];
let globalClasses = [];
let globalFaculties = [];
// ==========================================
// 1. CORE & XÁC THỰC
// ==========================================
window.onload = () => {
    const token = localStorage.getItem('accessToken');
    if (!token) window.location.href = '/login';
    else {
        loadUserInfo();
        loadDashboardData();
        // Tải sẵn dữ liệu Major và Class cho các bộ lọc
        preloadCommonData();
    }
};

$(document).ready(function() {
    // Hiển thị tên file khi chọn upload Excel
    $('#excelFile').on('change',function(){
        var fileName = $(this).val().split('\\').pop();
        $(this).next('.custom-file-label').html(fileName);
    });
});

function loadUserInfo() {
    const userStr = localStorage.getItem('user');
    if (userStr) {
        const user = JSON.parse(userStr);
        document.getElementById('display-username').textContent = user.username;
        const roleName = user.role.replace('ROLE_', '');
        document.getElementById('display-role').textContent = roleName;
        document.getElementById('user-avatar').src = `https://ui-avatars.com/api/?name=${user.username}&background=random`;

        if(roleName === 'ADMIN') {
            document.querySelectorAll('.admin-only').forEach(el => el.style.display = 'block');
        }
    }
}

async function handleLogout() {
    const refreshToken = localStorage.getItem('refreshToken');
    if (refreshToken) {
        try {
            await fetch(`${API_BASE_URL}/auth/logout`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ refreshToken: refreshToken })
            });
        } catch(e) {}
    }
    localStorage.clear();
    window.location.href = '/login';
}

async function fetchWithAuth(endpoint, options = {}) {
    const token = localStorage.getItem('accessToken');
    const headers = { 'Authorization': `Bearer ${token}` };
    if (!(options.body instanceof FormData)) headers['Content-Type'] = 'application/json';
    options.headers = { ...options.headers, ...headers };

    const res = await fetch(`${API_BASE_URL}${endpoint}`, options);
    if (res.status === 401 || res.status === 403) handleLogout();
    const data = await res.json();
    if (!res.ok) throw new Error(data.message || 'Lỗi hệ thống');
    return data;
}

// Load danh sách Ngành và Lớp dùng chung cho Filter và Modal
async function preloadCommonData() {
    try {
        const majorRes = await fetchWithAuth('/departments/majors');
        globalMajors = majorRes.data || [];

        // Cập nhật bộ lọc ngành trong view Quản lý Sinh viên
        const filterMajor = document.getElementById('filter-stu-major');
        if(filterMajor) {
            filterMajor.innerHTML = '<option value="">-- Tất cả Ngành học --</option>' +
                globalMajors.map(m => `<option value="${m.id}">${m.name}</option>`).join('');
        }

        // Tải danh sách tất cả lớp học để điền vào dropdown Modal Sinh viên
        const classRes = await fetchWithAuth('/student-classes/all');
        globalClasses = classRes.data || [];
    } catch(e) { console.error("Không thể preload data", e); }
}

// ==========================================
// 2. SPA ROUTING
// ==========================================
function switchView(viewId, element) {
    document.querySelectorAll('.nav-menu').forEach(el => el.classList.remove('active'));
    if(element) element.classList.add('active');
    document.querySelectorAll('.view-section').forEach(el => el.classList.remove('active'));

    const target = document.getElementById(viewId);
    if(target) target.classList.add('active');

    if (viewId === 'view-dashboard') loadDashboardData();
    if (viewId === 'view-reports') {
        // Đảm bảo select box báo cáo luôn có data khi chuyển view
        const reportFacSelect = document.getElementById('report-faculty-select');
        if(reportFacSelect && reportFacSelect.options.length <= 1 && globalFaculties.length > 0) {
            reportFacSelect.innerHTML = '<option value="">-- Chọn khoa để xem số lượng --</option>' +
                globalFaculties.map(f => `<option value="${f.id}">${f.name}</option>`).join('');
        }
        loadReportsData();
    }
    if (viewId === 'view-audit-logs') loadAuditLogs();
    if (viewId === 'view-departments') loadDepartmentsData();
    if (viewId === 'view-students') loadStudents(0);
    if (viewId === 'view-student-classes') loadStudentClasses(0);
}

// ==========================================
// 3. LOGIC DEPARTMENTS (KHOA / NGÀNH)
// ==========================================
async function loadDepartmentsData() {
    currentFacultyId = null;
    currentFacultyName = '';
    document.getElementById('majors-card-title').innerText = 'Danh sách Ngành học';
    document.getElementById('majors-tbody').innerHTML = '<tr><td colspan="3" class="text-center">Vui lòng chọn Khoa để xem</td></tr>';
    loadFaculties();
}

async function loadFaculties() {
    try {
        const res = await fetchWithAuth('/departments/faculties');
        const tbody = document.getElementById('faculties-tbody');
        const facSelect = document.getElementById('maj-faculty');

        if (!res.data || res.data.length === 0) {
            tbody.innerHTML = '<tr><td colspan="4" class="text-center">Chưa có Khoa nào</td></tr>';
        } else {
            tbody.innerHTML = res.data.map(f => `
                <tr>
                    <td><b>${f.facultyCode}</b></td>
                    <td>${f.name}</td>
                    <td>${f.contactEmail || ''}</td>
                    <td>
                        <button class="btn btn-sm btn-info" onclick="loadMajorsByFaculty('${f.id}', '${f.name}')">
                            <i class="fas fa-list"></i> Xem Ngành
                        </button>
                    </td>
                </tr>`).join('');
        }
        facSelect.innerHTML = '<option value="">-- Chọn Khoa --</option>' + res.data.map(f => `<option value="${f.id}">${f.name}</option>`).join('');
    } catch (e) { console.error(e); }
}

async function loadMajorsByFaculty(facultyId, facultyName) {
    currentFacultyId = facultyId;
    currentFacultyName = facultyName;
    document.getElementById('majors-card-title').innerText = `Ngành học - Khoa ${facultyName}`;

    try {
        document.getElementById('majors-tbody').innerHTML = '<tr><td colspan="3" class="text-center">Đang tải...</td></tr>';
        const res = await fetchWithAuth(`/departments/faculties/${facultyId}/majors`);
        const tbody = document.getElementById('majors-tbody');

        if (!res.data || res.data.length === 0) {
            tbody.innerHTML = `<tr><td colspan="3" class="text-center">Chưa có Ngành nào thuộc Khoa này</td></tr>`;
        } else {
            tbody.innerHTML = res.data.map(m => `
                <tr>
                    <td><b>${m.majorCode}</b></td>
                    <td>${m.name}</td>
                    <td>${facultyName}</td>
                </tr>`).join('');
        }
    } catch (e) {
        console.error(e);
        document.getElementById('majors-tbody').innerHTML = '<tr><td colspan="3" class="text-center text-danger">Lỗi tải dữ liệu</td></tr>';
    }
}

async function handleCreateFaculty(e) {
    e.preventDefault();
    const payload = {
        facultyCode: document.getElementById('fac-code').value,
        name: document.getElementById('fac-name').value,
        contactEmail: document.getElementById('fac-email').value,
        description: document.getElementById('fac-desc').value
    };
    try {
        const res = await fetchWithAuth('/departments/faculties', { method: 'POST', body: JSON.stringify(payload) });
        Toast.fire({ icon: 'success', title: res.message });
        $('#modal-faculty').modal('hide');
        document.getElementById('fac-code').value = '';
        document.getElementById('fac-name').value = '';
        loadFaculties();
    } catch (err) { Swal.fire('Lỗi', err.message, 'error'); }
}

async function handleCreateMajor(e) {
    e.preventDefault();
    const payload = {
        majorCode: document.getElementById('maj-code').value,
        name: document.getElementById('maj-name').value,
        facultyId: document.getElementById('maj-faculty').value
    };
    try {
        const res = await fetchWithAuth('/departments/majors', { method: 'POST', body: JSON.stringify(payload) });
        Toast.fire({ icon: 'success', title: res.message });
        $('#modal-major').modal('hide');
        document.getElementById('maj-code').value = '';
        document.getElementById('maj-name').value = '';
        preloadCommonData(); // Update global data

        if (currentFacultyId === payload.facultyId) {
            loadMajorsByFaculty(currentFacultyId, currentFacultyName);
        }
    } catch (err) { Swal.fire('Lỗi', err.message, 'error'); }
}

// ==========================================
// 4. LOGIC STUDENTS (SINH VIÊN)
// ==========================================
function resetStudentFilter() {
    document.getElementById('filter-stu-keyword').value = '';
    document.getElementById('filter-stu-major').value = '';
    loadStudents(0);
}

// ==========================================
// 3. QUẢN LÝ SINH VIÊN (STUDENTS) - FIX LỖI N/A & ĐỒNG BỘ ID
// ==========================================
async function loadStudents(page = 0) {
    currentStudentPage = page;
    const keyword = document.getElementById('filter-stu-keyword').value.trim();
    const majorId = document.getElementById('filter-stu-major').value;

    try {
        document.getElementById('students-tbody').innerHTML = '<tr><td colspan="6" class="text-center">Đang tải...</td></tr>';
        let url = `/students?page=${page}&size=10&keyword=${encodeURIComponent(keyword)}&majorId=${majorId}`;

        const res = await fetchWithAuth(url);
        const students = res.data.content || [];
        const tbody = document.getElementById('students-tbody');

        if (students.length === 0) {
            tbody.innerHTML = '<tr><td colspan="6" class="text-center">Trống</td></tr>';
        } else {
            tbody.innerHTML = students.map(s => {
                // ✅ SỬA LỖI N/A: Dùng trường dữ liệu phẳng từ DTO
                const majorDisplay = s.majorName || 'N/A';
                const classDisplay = s.className || 'N/A';
                const statusBadge = s.academicStatus === 'BINH_THUONG' ? 'badge-success' : 'badge-warning';

                return `
                <tr>
                    <td><b>${s.studentCode}</b></td>
                    <td>${s.fullName}</td>
                    <td>${majorDisplay}</td>
                    <td>${classDisplay}</td>
                    <td><span class="badge ${statusBadge}">${s.academicStatus || 'BINH_THUONG'}</span></td>
                    <td class="action-btns">
                        <button class="btn btn-info" onclick='editStudent("${s.id}")'><i class="fas fa-edit"></i></button>
                        <button class="btn btn-danger" onclick="deleteStudent('${s.id}')"><i class="fas fa-trash"></i></button>
                    </td>
                </tr>`;
            }).join('');
        }
        renderPagination('student-pagination', res.data.totalPages, page, 'loadStudents');
    } catch (e) { console.error(e); }
}

async function editStudent(id) {
    try {
        const res = await fetchWithAuth(`/students/${id}`);
        const s = res.data;

        // Populate dropdown
        document.getElementById('stu-major').innerHTML =
            '<option value="">-- Chọn Ngành --</option>' +
            globalMajors.map(m => `<option value="${m.id}">${m.name}</option>`).join('');

        document.getElementById('stu-class').innerHTML =
            '<option value="">-- Chọn Lớp --</option>' +
            globalClasses.map(c => `<option value="${c.id}">${c.name || c.className}</option>`).join('');

        // Set value (DTO phẳng giống loadStudents)
        document.getElementById('stu-id').value = s.id;
        document.getElementById('stu-code').value = s.studentCode;
        document.getElementById('stu-name').value = s.fullName;
        document.getElementById('stu-major').value = s.majorId || '';
        document.getElementById('stu-class').value = s.studentClassId || '';
        document.getElementById('stu-year').value = s.enrollmentYear;
        document.getElementById('stu-status').value = s.academicStatus || 'BINH_THUONG';

        document.getElementById('modal-student-title').innerText = "Cập nhật Sinh viên";
        $('#modal-student').modal('show');

    } catch (e) {
        Toast.fire({ icon: 'error', title: 'Lỗi tải chi tiết sinh viên' });
    }
}


function openStudentModal(student = null) {
    const majorSelect = document.getElementById('stu-major');
    majorSelect.innerHTML =
        '<option value="">-- Chọn Ngành học --</option>' +
        globalMajors.map(m => `<option value="${m.id}">${m.name}</option>`).join('');

    const classSelect = document.getElementById('stu-class');
    classSelect.innerHTML =
        '<option value="">-- Chọn Lớp sinh hoạt --</option>' +
        globalClasses.map(c => `<option value="${c.id}">${c.name || c.className}</option>`).join('');

    if (student) {
        document.getElementById('modal-student-title').innerText = "Cập nhật Sinh viên";

        document.getElementById('stu-id').value = student.id;
        document.getElementById('stu-code').value = student.studentCode;
        document.getElementById('stu-code').disabled = false;
        document.getElementById('stu-name').value = student.fullName;

        // ✅ DTO phẳng
        document.getElementById('stu-major').value = student.majorId || '';
        document.getElementById('stu-class').value = student.studentClassId || '';

        document.getElementById('stu-year').value =
            student.enrollmentYear || new Date().getFullYear();

        document.getElementById('stu-status').value =
            student.academicStatus || 'BINH_THUONG';

    } else {
        document.getElementById('modal-student-title').innerText = "Thêm Sinh viên";

        document.getElementById('stu-id').value = '';
        document.getElementById('stu-code').value = '';
        document.getElementById('stu-code').disabled = false;
        document.getElementById('stu-name').value = '';

        document.getElementById('stu-major').value = '';
        document.getElementById('stu-class').value = '';
        document.getElementById('stu-year').value = new Date().getFullYear();
        document.getElementById('stu-status').value = 'BINH_THUONG';
    }

    $('#modal-student').modal('show');
}

async function handleSaveStudent(e) {
    e.preventDefault();
    const id = document.getElementById('stu-id').value;

    const payload = {
        studentCode: document.getElementById('stu-code').value.trim(),
        fullName: document.getElementById('stu-name').value.trim(),
        majorId: document.getElementById('stu-major').value,
        studentClassId: document.getElementById('stu-class').value,
        enrollmentYear: parseInt(document.getElementById('stu-year').value),
        academicStatus: document.getElementById('stu-status').value,
        avatarUrl: null
    };

    // ✅ Validate nhanh
    if (!payload.studentCode || !payload.fullName) {
        return Swal.fire('Lỗi', 'Vui lòng nhập đầy đủ thông tin', 'warning');
    }

    try {
        if (id) {
            await fetchWithAuth(`/students/${id}`, {
                method: 'PUT',
                body: JSON.stringify(payload)
            });
            Toast.fire({ icon: 'success', title: 'Cập nhật thành công' });
        } else {
            await fetchWithAuth(`/students`, {
                method: 'POST',
                body: JSON.stringify(payload)
            });
            Toast.fire({ icon: 'success', title: 'Thêm sinh viên thành công' });
        }

        $('#modal-student').modal('hide');
        loadStudents(currentStudentPage);

    } catch (err) {
        Swal.fire('Lỗi', err.message, 'error');
    }
}

function deleteStudent(id) {
    Swal.fire({
        title: 'Xóa sinh viên?',
        text: "Hành động này không thể hoàn tác!",
        icon: 'warning',
        showCancelButton: true,
        confirmButtonText: 'Có, xóa đi',
        cancelButtonText: 'Hủy'
    }).then(async (result) => {
        if (result.isConfirmed) {
            try {
                await fetchWithAuth(`/students/${id}`, { method: 'DELETE' });
                Toast.fire({ icon: 'success', title: 'Đã xóa sinh viên' });

                // Nếu trang hiện tại chỉ còn 1 record thì lùi trang
                const rows = document.getElementById('students-tbody').querySelectorAll('tr');
                if(rows.length === 1 && currentStudentPage > 0) currentStudentPage--;

                loadStudents(currentStudentPage);
            } catch (err) { Swal.fire('Lỗi', err.message, 'error'); }
        }
    });
}


// ==========================================
// 5. MODULE: QUẢN LÝ LỚP SINH HOẠT
// ==========================================
async function loadStudentClasses(page = 0) {
    currentSClassPage = page;
    const keyword = document.getElementById('filter-sclass-keyword').value.trim();

    try {
        document.getElementById('student-classes-tbody').innerHTML = '<tr><td colspan="4" class="text-center">Đang tải...</td></tr>';

        let url = `/student-classes?page=${page}&size=10`;
        if (keyword) url += `&keyword=${encodeURIComponent(keyword)}`;

        const res = await fetchWithAuth(url);
        // Tương thích với cả định dạng res.data hoặc res.result
        const pageData = res.data ? res.data : (res.result ? res.result : {});
        const classes = pageData.content || [];
        const tbody = document.getElementById('student-classes-tbody');

        if (classes.length === 0) {
            tbody.innerHTML = '<tr><td colspan="4" class="text-center">Không có bản ghi phù hợp</td></tr>';
        } else {
            tbody.innerHTML = classes.map(c => `
                <tr>
                    <td><strong>${c.classCode}</strong></td>
                    <td>${c.name}</td>
                    <td><span class="badge badge-info">${c.majorName || (c.major ? c.major.name : '')}</span></td>
                    <td>
                        <div class="action-btns">
                            <button class="btn btn-warning btn-sm" onclick="openStudentClassModal('${c.id}')" title="Sửa">
                                <i class="fas fa-edit"></i>
                            </button>
                            <button class="btn btn-danger btn-sm" onclick="deleteStudentClass('${c.id}')" title="Xóa">
                                <i class="fas fa-trash"></i>
                            </button>
                        </div>
                    </td>
                </tr>
            `).join('');
        }

        const totalPages = pageData.totalPages || 1;
        let paginationHtml = '';
        for (let i = 0; i < totalPages; i++) {
            paginationHtml += `<li class="page-item ${i === page ? 'active' : ''}">
                <a class="page-link" href="#" onclick="loadStudentClasses(${i}); return false;">${i + 1}</a>
             </li>`;
        }
        document.getElementById('student-class-pagination').innerHTML = paginationHtml;

    } catch (e) {
        console.error(e);
        Toast.fire({ icon: 'error', title: 'Lỗi lấy danh sách lớp sinh hoạt' });
    }
}

async function openStudentClassModal(id = null) {
    document.getElementById('form-student-class').reset();
    document.getElementById('sclass-id').value = '';
    document.getElementById('modal-student-class-title').innerText = id ? "Cập nhật Lớp sinh hoạt" : "Thêm Lớp sinh hoạt mới";

    try {
        // Tận dụng mảng globalMajors đã fetch sẵn từ hàm preloadCommonData thay vì gọi API liên tục
        const majorSelect = document.getElementById('sclass-major');
        majorSelect.innerHTML = '<option value="">-- Chọn Ngành học --</option>' +
            globalMajors.map(m => `<option value="${m.id}">${m.name}</option>`).join('');

        if (id) {
            const res = await fetchWithAuth(`/student-classes/${id}`);
            const data = res.data ? res.data : (res.result ? res.result : null);

            if (data) {
                document.getElementById('sclass-id').value = data.id;
                document.getElementById('sclass-code').value = data.classCode;
                document.getElementById('sclass-name').value = data.name;
                document.getElementById('sclass-major').value = data.majorId || (data.major ? data.major.id : '');
            }
        }
        $('#modal-student-class').modal('show');
    } catch (err) {
        console.error(err);
        Swal.fire('Lỗi', 'Không thể tải dữ liệu chuẩn bị cho Form.', 'error');
    }
}

async function handleSaveStudentClass(event) {
    event.preventDefault(); // Chặn tải lại trang

    const id = document.getElementById('sclass-id').value;
    const payload = {
        classCode: document.getElementById('sclass-code').value,
        name: document.getElementById('sclass-name').value,
        majorId: document.getElementById('sclass-major').value
    };

    const url = id ? `/student-classes/${id}` : '/student-classes';
    const method = id ? 'PUT' : 'POST';

    try {
        const res = await fetchWithAuth(url, {
            method: method,
            body: JSON.stringify(payload)
        });

        Swal.fire('Thành công', res.message || 'Lưu thông tin thành công', 'success');
        $('#modal-student-class').modal('hide');
        preloadCommonData(); // Tải lại cache globalClasses để form Sinh viên luôn nhận được data lớp mới
        loadStudentClasses(currentSClassPage); // Load lại bảng
    } catch (err) {
        console.error(err);
        Swal.fire('Lỗi', err.message || 'Có lỗi xảy ra khi lưu', 'error');
    }
}

function deleteStudentClass(id) {
    Swal.fire({
        title: 'Bạn có chắc chắn muốn xóa?',
        text: "Hành động này không thể hoàn tác!",
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#d33',
        cancelButtonColor: '#3085d6',
        confirmButtonText: 'Có, xóa ngay!',
        cancelButtonText: 'Hủy'
    }).then(async (result) => {
        if (result.isConfirmed) {
            try {
                const res = await fetchWithAuth(`/student-classes/${id}`, { method: 'DELETE' });
                Swal.fire('Đã xóa!', res.message || 'Xóa lớp sinh hoạt thành công.', 'success');
                preloadCommonData(); // Cập nhật lại cache globalClasses

                // Nếu trang hiện tại chỉ còn 1 record thì lùi trang
                const rows = document.getElementById('student-classes-tbody').querySelectorAll('tr');
                if(rows.length === 1 && currentSClassPage > 0) currentSClassPage--;

                loadStudentClasses(currentSClassPage); // Load lại bảng
            } catch (err) {
                console.error(err);
                Swal.fire('Lỗi', err.message || 'Lỗi kết nối khi xóa dữ liệu.', 'error');
            }
        }
    });
}


// ==========================================
// 6. XỬ LÝ DỮ LIỆU VIEW: DASHBOARD & BÁO CÁO
// ==========================================
async function loadDashboardData() {
    try {
        const res = await fetchWithAuth('/reports/dashboard');
        const data = res.data;

        // Cập nhật cho View Dashboard
        const statStudents = document.getElementById('stat-students');
        if(statStudents) statStudents.textContent = data.totalStudents || 0;

        // Cập nhật cho View Reports
        const statTotal = document.getElementById('stat-total-students');
        if(statTotal) statTotal.textContent = data.totalStudents || 0;

        const statActive = document.getElementById('stat-active-students');
        if(statActive) statActive.textContent = data.activeStudents || data.totalStudents || 0;

        if(document.getElementById('stat-lecturers')) document.getElementById('stat-lecturers').textContent = data.totalLecturers || 0;
        if(document.getElementById('stat-courses')) document.getElementById('stat-courses').textContent = data.totalCourses || 0;
        if(document.getElementById('stat-classes')) document.getElementById('stat-classes').textContent = data.totalClasses || 0;
    } catch (error) { console.error(error); }
}

async function onFacultyFilterChange() {
    const facultyId = document.getElementById('report-faculty-select').value;
    const countDisplay = document.getElementById('faculty-student-count');
    const scopeLabel = document.getElementById('top-students-scope');

    if (!facultyId) {
        countDisplay.textContent = "0";
        scopeLabel.textContent = "Toàn trường";
        loadReportsData();
        return;
    }

    try {
        const countRes = await fetchWithAuth(`/students/count/faculty/${facultyId}`);
        countDisplay.textContent = countRes.data;

        const topRes = await fetchWithAuth(`/students/faculty/${facultyId}?page=0&size=10&sort=gpa,desc`);
        scopeLabel.textContent = "Tại khoa được chọn";
        renderTopStudents(topRes.data.content || []);
    } catch (error) {
        Toast.fire({ icon: 'error', title: 'Không thể lọc dữ liệu theo khoa' });
    }
}

async function loadReportsData() {
    try {
        const [facultyRes, passFailRes, topStudentsRes] = await Promise.all([
            fetchWithAuth('/reports/faculty-stats'),
            fetchWithAuth('/reports/pass-fail-ratio'),
            fetchWithAuth('/reports/top-students?limit=10')
        ]);

        renderFacultyChart(facultyRes.data);
        renderPassFailChart(passFailRes.data);
        renderTopStudents(topStudentsRes.data);
    } catch (error) { Toast.fire({ icon: 'error', title: 'Không thể tải dữ liệu báo cáo' }); }
}

function renderFacultyChart(facultyData) {
    if(!facultyData || !Array.isArray(facultyData)) return;

    // Khắc phục lỗi undefined bằng cách kiểm tra tên trường dự phòng
    const labels = facultyData.map(d => d.facultyName || d.name || 'N/A');
    const data = facultyData.map(d => d.studentCount || d.count || 0);

    if(chartsInstance.faculty) chartsInstance.faculty.destroy();
    const ctx = document.getElementById('facultyChart').getContext('2d');
    chartsInstance.faculty = new Chart(ctx, {
        type: 'doughnut',
        data: {
            labels: labels,
            datasets: [{ data: data, backgroundColor: ['#f56954', '#00a65a', '#f39c12', '#00c0ef', '#3c8dbc', '#d2d6de'] }]
        },
        options: { maintainAspectRatio: false }
    });
}

function renderPassFailChart(stat) {
    if(!stat) return;
    if(chartsInstance.passFail) chartsInstance.passFail.destroy();
    const ctx = document.getElementById('passFailChart').getContext('2d');
    chartsInstance.passFail = new Chart(ctx, {
        type: 'pie',
        data: {
            labels: [`Đậu (${stat.passRate || 0}%)`, `Rớt (${stat.failRate || 0}%)`],
            datasets: [{ data: [stat.passCount || 0, stat.failCount || 0], backgroundColor: ['#28a745', '#dc3545'] }]
        },
        options: { maintainAspectRatio: false }
    });
}

function renderTopStudents(students) {
    const tbody = document.getElementById('top-students-tbody');
    if (!students || students.length === 0) {
        tbody.innerHTML = '<tr><td colspan="4" class="text-center">Chưa có dữ liệu</td></tr>';
        return;
    }
    tbody.innerHTML = students.map((s, i) => `
        <tr>
            <td>${i + 1}</td>
            <td><b>${s.studentCode}</b></td>
            <td>${s.fullName}</td>
            <td><span class="badge badge-success">${s.gpa || 0}</span></td>
        </tr>
    `).join('');
}

// ==========================================
// 7. XỬ LÝ DỮ LIỆU VIEW: AUDIT LOGS
// ==========================================
async function loadAuditLogs() {
    try {
        document.getElementById('audit-logs-tbody').innerHTML = '<tr><td colspan="6" class="text-center">Đang tải...</td></tr>';
        const res = await fetchWithAuth('/audit-logs?page=0&size=50');
        const logs = res.data.content || [];

        const tbody = document.getElementById('audit-logs-tbody');
        if (logs.length === 0) {
            tbody.innerHTML = '<tr><td colspan="6" class="text-center">Không có bản ghi nào</td></tr>';
            return;
        }

        tbody.innerHTML = logs.map(log => {
            const statusBadge = log.status === 'SUCCESS' ? 'badge-success' : 'badge-danger';
            const dateStr = new Date(log.createdAt).toLocaleString('vi-VN');
            return `
            <tr>
                <td>${dateStr}</td>
                <td>${log.username || 'System'}</td>
                <td><b>${log.action}</b></td>
                <td>${log.entityName}</td>
                <td><span class="badge ${statusBadge}">${log.status}</span></td>
                <td>${log.ipAddress || 'N/A'}</td>
            </tr>
        `}).join('');
    } catch (error) { Toast.fire({ icon: 'error', title: 'Lỗi lấy nhật ký' }); }
}

// ==========================================
// 8. XỬ LÝ VIEW: NHẬP / XUẤT (IMPORT/EXPORT)
// ==========================================
async function handleImportStudents(e) {
    e.preventDefault();
    const fileInput = document.getElementById('excelFile');
    if (!fileInput.files[0]) return;

    const formData = new FormData();
    formData.append('file', fileInput.files[0]);

    Swal.fire({ title: 'Đang xử lý...', allowOutsideClick: false, didOpen: () => { Swal.showLoading(); }});

    try {
        const res = await fetchWithAuth('/data/import/students', {
            method: 'POST',
            body: formData
        });
        Swal.fire('Thành công!', res.message, 'success');
        fileInput.value = "";
        document.getElementById('excelFileLabel').innerText = "Chọn file...";
    } catch (error) {
        Swal.fire('Thất bại', error.message, 'error');
    }
}

async function downloadExcel(endpoint, filename) {
    try {
        Swal.fire({ title: 'Đang chuẩn bị file...', allowOutsideClick: false, didOpen: () => { Swal.showLoading(); }});

        const token = localStorage.getItem('accessToken');
        const res = await fetch(`${API_BASE_URL}${endpoint}`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });

        if (!res.ok) throw new Error("Không thể tải file, kiểm tra lại quyền hoặc dữ liệu.");

        const blob = await res.blob();
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.style.display = 'none';
        a.href = url;
        a.download = filename;
        document.body.appendChild(a);
        a.click();

        window.URL.revokeObjectURL(url);
        Swal.close();
    } catch (error) {
        Swal.fire('Lỗi', error.message, 'error');
    }
}

function exportStudentSpecific(type) {
    const studentId = document.getElementById('export-student-id').value.trim();
    if(!studentId) {
        return Toast.fire({ icon: 'warning', title: 'Vui lòng nhập ID/Mã Sinh viên' });
    }
    if(type === 'grades') {
        downloadExcel(`/data/export/grades/${studentId}`, `bang_diem_${studentId}.xlsx`);
    } else if (type === 'schedule') {
        downloadExcel(`/data/export/schedule/${studentId}`, `tkb_${studentId}.xlsx`);
    }
}
STATIC   = "/mnt/c/prathmesh_lsm/lims-dipak/backend/src/main/resources/static/index.html"
FRONTEND = "/mnt/c/prathmesh_lsm/lims-dipak/backend/src/main/resources/frontend/index.html"
TARGET   = "/mnt/c/prathmesh_lsm/lims-dipak/backend/target/classes/static/index.html"
import shutil, re

with open(STATIC, 'r', encoding='utf-8') as f:
    html = f.read()

print(f"Original size: {len(html)}")

def replace_func(html, search, replacement):
    idx = html.find(search)
    if idx == -1:
        print(f"NOT FOUND: {search[:50]}")
        return html
    depth, i, end = 0, idx, -1
    while i < len(html):
        if html[i] == '{': depth += 1
        elif html[i] == '}':
            depth -= 1
            if depth == 0: end = i + 1; break
        i += 1
    if end == -1:
        print(f"NO END: {search[:50]}")
        return html
    print(f"REPLACED: {search[:50]}")
    return html[:idx] + replacement + html[end:]

# 1. Clean doLogin
html = replace_func(html, 'async function doLogin()', '''async function doLogin() {
  const u = document.getElementById('login-user').value.trim();
  const p = document.getElementById('login-pass').value.trim();
  if (!u || !p) { showToast('Please enter your credentials', 'error'); return; }
  try {
    const res = await fetch('http://localhost:8081/api/auth/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username: u, password: p })
    });
    const data = await res.json();
    if (!res.ok || !data.success) {
      showToast(data?.message || 'Invalid username or password', 'error');
      return;
    }
    const d = data.data || {};
    authToken = d.token || '';
    if (authToken) localStorage.setItem('lis_token', authToken);
    currentUser = { username: d.username || u, fullName: d.fullName || u, roles: d.roles || ['ADMIN'] };
    showToast('Welcome back, ' + currentUser.fullName + '!', 'success');
    launchApp();
  } catch(e) {
    console.error('Login error:', e);
    showToast('Cannot reach server on port 8081. Is backend running?', 'error');
  }
}''')

# 2. Clean apiFetch
html = replace_func(html, 'async function apiFetch(', '''async function apiFetch(path, opts = {}) {
  const token = authToken || localStorage.getItem('lis_token') || '';
  const headers = {
    'Content-Type': 'application/json',
    ...(token ? { 'Authorization': 'Bearer ' + token } : {}),
    ...(opts.headers || {})
  };
  try {
    const res = await fetch('http://localhost:8081' + path, { ...opts, headers });
    if (res.status === 401) { clearAuth(); return null; }
    if (!res.ok) return null;
    return await res.json();
  } catch(e) {
    return null;
  }
}''')

# 3. Clean launchApp
html = replace_func(html, 'function launchApp()', '''function launchApp() {
  document.getElementById('login-page').style.display = 'none';
  document.getElementById('app').style.display = 'flex';
  document.getElementById('sb-uname').textContent = currentUser.fullName || 'Administrator';
  document.getElementById('sb-av').textContent = (currentUser.fullName || 'A').charAt(0).toUpperCase();
  document.getElementById('sb-urole').textContent = currentUser.roles?.[0] || 'ADMIN';
  buildSidebar();
  navigate('dashboard');
}''')

# 4. Remove duplicate loadPatients - keep last one
while html.count('async function loadPatients()') > 1:
    idx = html.find('async function loadPatients()')
    depth, i, end = 0, idx, -1
    while i < len(html):
        if html[i] == '{': depth += 1
        elif html[i] == '}':
            depth -= 1
            if depth == 0: end = i + 1; break
        i += 1
    html = html[:idx] + html[end:]
    print("Removed duplicate loadPatients")

# 5. Fix renderDashboard - ensure it returns HTML
idx = html.find('function renderDashboard()')
depth, i, end = 0, idx, -1
while i < len(html):
    if html[i] == '{': depth += 1
    elif html[i] == '}':
        depth -= 1
        if depth == 0: end = i + 1; break
    i += 1
func = html[idx:end]
if 'return ' not in func:
    print("renderDashboard has no return - fixing")
    html = replace_func(html, 'function renderDashboard()', '''function renderDashboard() {
  const completed = patients.filter(p => p.status === 'Printed' || p.status === 'Dispatch').length;
  const pending = patients.filter(p => p.status === 'Reg' || p.status === 'SamCollect').length;
  const totalColl = patients.reduce((s, p) => s + (parseFloat(p.paid) || 0), 0);
  const today = new Date().toDateString();
  const todayPats = patients.filter(p => p.createdAt && new Date(p.createdAt).toDateString() === today).length;
  const stats = [
    { icon: '👤', label: 'Total Patients', val: patients.length, color: '#2d6499', bg: '#daeaf7', delta: 'All time' },
    { icon: '📋', label: "Today Reg.", val: todayPats, color: '#7c3aed', bg: '#ede9fe', delta: 'Active today' },
    { icon: '⏳', label: 'Pending', val: pending, color: '#b45309', bg: '#fef3c7', delta: 'Awaiting results' },
    { icon: '✅', label: 'Completed', val: completed, color: '#15803d', bg: '#dcfce7', delta: 'Reports ready' },
    { icon: '💰', label: 'Collection', val: 'Rs.' + fmt(totalColl), color: '#0f766e', bg: '#ccfbf1', delta: 'Total collected' },
  ];
  const cards = stats.map(s =>
    '<div class="stat-card" style="border-left:3px solid ' + s.color + '">' +
    '<div class="stat-icon" style="background:' + s.bg + ';color:' + s.color + '">' + s.icon + '</div>' +
    '<div class="stat-body">' +
    '<div class="stat-val" style="color:' + s.color + '">' + s.val + '</div>' +
    '<div class="stat-lbl">' + s.label + '</div>' +
    '<div class="stat-delta" style="color:' + s.color + '">' + s.delta + '</div>' +
    '</div></div>'
  ).join('');
  const quickLinks = [
    ['📝','New Registration','new-registration','#daeaf7','#2d6499'],
    ['📋','Patient Status','patient-status','#dcfce7','#15803d'],
    ['🧪','Sample Mgmt','sample-accept','#fff7ed','#b45309'],
    ['🔬','Test Results','test-result-entry','#ede9fe','#7c3aed'],
    ['💳','Bill Desk','bill-desk','#ccfbf1','#0f766e'],
  ].map(q =>
    '<div class="qa-tile" onclick="navigate(\'' + q[2] + '\')">' +
    '<div class="qa-tile-icon" style="background:' + q[3] + ';color:' + q[4] + '">' + q[0] + '</div>' +
    '<div class="qa-tile-text"><div class="qa-tile-label">' + q[1] + '</div></div></div>'
  ).join('');
  const rows = patients.slice(0,6).map(p =>
    '<tr><td class="col-mono">' + p.regNo + '</td>' +
    '<td>' + p.name + '</td><td>' + p.center + '</td>' +
    '<td class="trunc">' + p.tests + '</td>' +
    '<td>' + badge(p.status) + '</td>' +
    '<td class="text-right mono">Rs.' + fmt(p.charges) + '</td></tr>'
  ).join('');
  requestAnimationFrame(function(){ initDashboardCharts(); });
  return ph('Dashboard Overview', 'Welcome back.') +
    '<div class="g5 mb3">' + cards + '</div>' +
    '<div class="card mb3"><div class="card-header">' +
      '<div><div class="card-title">Total patients daywise</div></div>' +
      '<div style="display:flex;gap:8px">' +
        '<input type="date" class="inp inp-sm" id="dash-from" style="width:140px"/>' +
        '<input type="date" class="inp inp-sm" id="dash-to" style="width:140px"/>' +
        '<button class="btn btn-primary btn-sm" onclick="refreshDailyChart()">Go</button>' +
      '</div></div>' +
      '<canvas id="dailyRegsChart" height="75"></canvas></div>' +
    '<div style="display:grid;grid-template-columns:1.4fr 1fr;gap:16px;margin-bottom:20px">' +
      '<div class="card"><div class="card-header">' +
        '<div><div class="card-title">Patients by department</div></div>' +
        '<div style="display:flex;gap:6px">' +
          '<select class="inp inp-sm" id="dept-month" style="width:110px" onchange="refreshDeptChart()">' +
            '<option value="1">Jan</option><option value="2">Feb</option><option value="3">Mar</option>' +
            '<option value="4">Apr</option><option value="5">May</option><option value="6">Jun</option>' +
            '<option value="7">Jul</option><option value="8">Aug</option><option value="9">Sep</option>' +
            '<option value="10">Oct</option><option value="11">Nov</option><option value="12">Dec</option>' +
          '</select>' +
          '<select class="inp inp-sm" id="dept-year" style="width:78px" onchange="refreshDeptChart()">' +
            '<option>2025</option><option selected>2026</option></select>' +
        '</div></div>' +
        '<canvas id="deptChart" height="160"></canvas></div>' +
      '<div class="card"><div class="card-header">' +
        '<div><div class="card-title">Top Referral Doctors</div></div>' +
        '<div style="display:flex;gap:6px">' +
          '<select class="inp inp-sm" id="ref-month" style="width:110px" onchange="refreshRefChart()">' +
            '<option value="1">Jan</option><option value="2">Feb</option><option value="3">Mar</option>' +
            '<option value="4">Apr</option><option value="5">May</option><option value="6">Jun</option>' +
            '<option value="7">Jul</option><option value="8">Aug</option><option value="9">Sep</option>' +
            '<option value="10">Oct</option><option value="11">Nov</option><option value="12">Dec</option>' +
          '</select>' +
          '<select class="inp inp-sm" id="ref-year" style="width:78px" onchange="refreshRefChart()">' +
            '<option>2025</option><option selected>2026</option></select>' +
        '</div></div>' +
        '<canvas id="statusChart" height="160"></canvas></div>' +
    '</div>' +
    '<div style="margin-bottom:20px"><div style="margin-bottom:12px;font-size:14px;font-weight:700">Quick Access</div>' +
    '<div style="display:grid;grid-template-columns:repeat(5,1fr);gap:10px">' + quickLinks + '</div></div>' +
    (patients.length ?
      '<div class="card"><div class="card-header">' +
      '<div><div class="card-title">Recent Registrations</div></div>' +
      '<button class="btn btn-ghost btn-sm" onclick="navigate(\'patient-status\')">View All</button>' +
      '</div><div class="tbl-wrap"><table>' +
      '<thead><tr><th>Reg.No</th><th>Name</th><th>Center</th><th>Tests</th><th>Status</th><th>Charges</th></tr></thead>' +
      '<tbody>' + rows + '</tbody></table></div></div>'
    : '<div class="card"><div class="empty-state">No patients yet. Use New Registration.</div></div>');
}''')
else:
    print(f"renderDashboard OK - returns: {func[func.rfind('return '):func.rfind('return ')+30]}")

# 6. Fix ref-doctors URL
html = html.replace("'/api/v1/ref-doctors'", "'/api/v1/referring-doctors?page=0&size=500'")
html = html.replace('"/api/v1/ref-doctors"', '"/api/v1/referring-doctors?page=0&size=500"')
print("Fixed ref-doctors URL")

# Save
for path in [STATIC, FRONTEND]:
    with open(path, 'w', encoding='utf-8') as f:
        f.write(html)
    print(f"Saved: {path}")
try:
    shutil.copy2(STATIC, TARGET)
    print(f"Saved: {TARGET}")
except Exception as e:
    print(f"Skip target: {e}")

print(f"Final size: {len(html)}")
print("DONE")

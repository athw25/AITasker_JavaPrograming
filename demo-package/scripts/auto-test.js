import axios from 'axios';

const BASE_URL = process.env.BASE_URL || 'http://localhost:8080/api';
const client = axios.create({ baseURL: BASE_URL, validateStatus: () => true });

const state = {};
let passed = 0;
let failed = 0;

function log(ok, name, res) {
  if (ok) {
    passed++;
    console.log(`  \x1b[32m✓\x1b[0m ${name} (${res.status})`);
  } else {
    failed++;
    console.log(`  \x1b[31m✗\x1b[0m ${name} (${res.status}) -> ${JSON.stringify(res.data).slice(0, 200)}`);
  }
}

function expect(name, res, expectedStatuses) {
  const ok = expectedStatuses.includes(res.status);
  log(ok, name, res);
  return ok;
}

function unwrap(res) {
  return res.data?.data !== undefined ? res.data.data : res.data;
}

function authHeader(token) {
  return { headers: { Authorization: `Bearer ${token}` } };
}

async function step(title, fn) {
  console.log(`\n\x1b[36m▶ ${title}\x1b[0m`);
  await fn();
}

async function run() {
  console.log(`AITasker Auto Test — target: ${BASE_URL}`);

  await step('1. Auth — Register & Login', async () => {
    const suffix = Date.now();
    state.clientEmail = `autotest.client.${suffix}@aitasker.com`;
    state.expertEmail = `autotest.expert.${suffix}@aitasker.com`;

    let res = await client.post('/auth/register', {
      fullName: 'Auto Test Client', email: state.clientEmail, password: 'Demo@123', role: 'CLIENT'
    });
    expect('Register Client', res, [200, 201]);

    res = await client.post('/auth/register', {
      fullName: 'Auto Test Expert', email: state.expertEmail, password: 'Demo@123', role: 'EXPERT'
    });
    expect('Register Expert', res, [200, 201]);

    res = await client.post('/auth/login', { email: state.clientEmail, password: 'Demo@123' });
    if (expect('Login Client', res, [200])) state.clientToken = res.data.token;

    res = await client.post('/auth/login', { email: state.expertEmail, password: 'Demo@123' });
    if (expect('Login Expert', res, [200])) state.expertToken = res.data.token;

    res = await client.post('/auth/login', { email: 'admin@aitasker.com', password: 'Admin@123' });
    if (expect('Login Admin', res, [200])) state.adminToken = res.data.token;

    res = await client.get('/users/me', authHeader(state.clientToken));
    if (expect('Get Current User (Client)', res, [200])) state.clientId = res.data.id;

    res = await client.get('/users/me', authHeader(state.expertToken));
    if (expect('Get Current User (Expert)', res, [200])) state.expertId = res.data.id;
  });

  await step('2. Job Marketplace', async () => {
    let res = await client.post('/jobs', {
      title: 'Auto Test Job - Chatbot AI',
      description: 'Job tao boi auto test script.',
      budget: 3000.0, deadline: '2026-12-31', requiredSkills: 'Python, LangChain'
    }, authHeader(state.clientToken));
    if (expect('Create Job', res, [200, 201])) state.jobId = res.data.id;

    res = await client.get('/jobs', authHeader(state.expertToken));
    expect('Get All Jobs', res, [200]);

    res = await client.get(`/jobs/${state.jobId}`, authHeader(state.clientToken));
    expect('Get Job By Id', res, [200]);

    res = await client.get('/jobs/search', { params: { keyword: 'chatbot' }, ...authHeader(state.expertToken) });
    expect('Search Jobs', res, [200]);
  });

  await step('3. Expert Profile', async () => {
    let res = await client.put('/experts/me', {
      fullName: 'Auto Test Expert', title: 'AI Engineer', skills: 'Python, PyTorch',
      experienceYears: 3, hourlyRate: 40.0
    }, authHeader(state.expertToken));
    expect('Update Expert Profile', res, [200]);

    res = await client.get('/experts/me', authHeader(state.expertToken));
    expect('Get Expert Profile', res, [200]);
  });

  await step('4. Proposal', async () => {
    let res = await client.post('/proposals', {
      jobId: state.jobId, bidAmount: 2800.0, duration: 14,
      coverLetter: 'Auto test proposal cover letter.'
    }, authHeader(state.expertToken));
    if (expect('Create Proposal', res, [200, 201])) state.proposalId = unwrap(res).id;

    res = await client.get(`/proposals/job/${state.jobId}`, authHeader(state.clientToken));
    expect('Get Proposals By Job', res, [200]);

    res = await client.get('/proposals/me', authHeader(state.expertToken));
    expect('Get My Proposals', res, [200]);

    res = await client.put(`/proposals/${state.proposalId}/accept`, null, authHeader(state.clientToken));
    expect('Accept Proposal', res, [200]);
  });

  await step('5. Project', async () => {
    let res = await client.post('/projects', {
      proposalId: state.proposalId, startDate: '2026-07-10', endDate: '2026-08-10'
    }, authHeader(state.clientToken));
    if (expect('Create Project', res, [200, 201])) state.projectId = unwrap(res).id;

    res = await client.get(`/projects/${state.projectId}`, authHeader(state.clientToken));
    expect('Get Project By Id', res, [200]);

    res = await client.get('/projects/client', authHeader(state.clientToken));
    expect('Get Client Projects', res, [200]);

    res = await client.get('/projects/expert', authHeader(state.expertToken));
    expect('Get Expert Projects', res, [200]);
  });

  await step('6. Milestone & Delivery', async () => {
    let res = await client.post('/milestones', {
      projectId: state.projectId, title: 'Auto Test Milestone', description: 'Mo ta milestone',
      amount: 1000.0, dueDate: '2026-07-20'
    }, authHeader(state.clientToken));
    if (expect('Create Milestone', res, [200, 201])) state.milestoneId = unwrap(res).id;

    res = await client.put(`/milestones/${state.milestoneId}/submit`, {
      fileUrl: 'https://drive.google.com/auto-test', note: 'Auto test delivery'
    }, authHeader(state.expertToken));
    expect('Submit Milestone Delivery', res, [200]);

    res = await client.get(`/deliveries/milestone/${state.milestoneId}`, authHeader(state.clientToken));
    expect('Get Delivery History', res, [200]);

    res = await client.put(`/milestones/${state.milestoneId}/approve`, null, authHeader(state.clientToken));
    expect('Approve Milestone', res, [200]);
  });

  await step('7. Payment & Escrow', async () => {
    let res = await client.post('/payments/deposit', {
      projectId: state.projectId, amount: 1000.0
    }, authHeader(state.clientToken));
    if (expect('Deposit Escrow', res, [200, 201])) state.paymentId = unwrap(res).id;

    res = await client.get(`/payments/transactions/${state.paymentId}`, authHeader(state.clientToken));
    expect('Get Transactions By Payment', res, [200]);

    res = await client.put(`/milestones/${state.milestoneId}/release-payment`, null, authHeader(state.clientToken));
    expect('Release Milestone Payment', res, [200]);
  });

  await step('8. Withdrawal', async () => {
    let res = await client.post('/withdrawals', { amount: 200.0 }, authHeader(state.expertToken));
    expect('Request Withdrawal', res, [200, 201]);

    res = await client.get('/withdrawals/me', authHeader(state.expertToken));
    expect('Get My Withdrawals', res, [200]);
  });

  await step('9. Review', async () => {
    let res = await client.post('/reviews', {
      revieweeId: state.expertId, projectId: state.projectId, rating: 5,
      comment: 'Auto test review', type: 'CLIENT_TO_EXPERT'
    }, authHeader(state.clientToken));
    expect('Create Review', res, [200, 201]);

    res = await client.get(`/reviews/user/${state.expertId}`, authHeader(state.clientToken));
    expect('Get Reviews By User', res, [200]);
  });

  await step('10. Notification & Message', async () => {
    let res = await client.get('/notifications', authHeader(state.clientToken));
    expect('Get Notifications', res, [200]);

    res = await client.get(`/messages/project/${state.projectId}`, authHeader(state.clientToken));
    expect('Get Project Messages', res, [200]);
  });

  await step('11. Dispute', async () => {
    let res = await client.post('/disputes', {
      projectId: state.projectId, reason: 'Auto test dispute reason'
    }, authHeader(state.clientToken));
    if (expect('Create Dispute', res, [200, 201])) state.disputeId = unwrap(res).id;

    res = await client.get('/disputes', { params: { status: 'OPEN' }, ...authHeader(state.adminToken) });
    expect('Admin Get Disputes', res, [200]);

    res = await client.put(`/disputes/${state.disputeId}/resolve`, {
      status: 'RESOLVED_REJECTED', resolution: 'Auto test resolution'
    }, authHeader(state.adminToken));
    expect('Admin Resolve Dispute', res, [200]);
  });

  await step('12. AI Modules', async () => {
    let res = await client.post('/ai/job-assistant', { prompt: 'Toi muon chatbot ban hang' }, authHeader(state.clientToken));
    expect('AI Job Assistant', res, [200]);

    res = await client.post('/ai/service-generator', { prompt: 'Toi lam AI resume parser' }, authHeader(state.expertToken));
    expect('AI Service Generator', res, [200]);

    res = await client.get(`/ai/recommend-experts/${state.jobId}`, authHeader(state.clientToken));
    expect('AI Recommend Experts', res, [200]);
  });

  await step('13. Admin', async () => {
    let res = await client.get('/admin/users', authHeader(state.adminToken));
    expect('Admin Get Users', res, [200]);

    res = await client.get('/admin/dashboard', authHeader(state.adminToken));
    expect('Admin Dashboard', res, [200]);

    res = await client.get('/admin/analytics', authHeader(state.adminToken));
    expect('Admin Analytics', res, [200]);

    res = await client.get('/admin/audit-logs', authHeader(state.adminToken));
    expect('Admin Audit Logs', res, [200]);

    res = await client.get('/admin/test', authHeader(state.adminToken));
    expect('Admin Test Endpoint', res, [200]);
  });

  console.log(`\n${'='.repeat(50)}`);
  console.log(`\x1b[1mKET QUA: ${passed} passed, ${failed} failed\x1b[0m`);
  console.log('='.repeat(50));
  process.exit(failed > 0 ? 1 : 0);
}

run().catch((err) => {
  console.error('\nAuto test crashed:', err.message);
  process.exit(1);
});

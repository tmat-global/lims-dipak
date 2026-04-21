const puppeteer = require('puppeteer');

(async () => {
  const browser = await puppeteer.launch({ headless: 'new', args: ['--no-sandbox'] });
  const page = await browser.newPage();

  page.on('console', msg => console.log('BROWSER CONSOLE:', msg.type(), msg.text()));
  page.on('requestfailed', request => console.log('REQUEST FAILED:', request.url(), request.failure().errorText));
  page.on('response', response => console.log('RESPONSE:', response.url(), response.status()));

  await page.goto('http://localhost:8081/api/', { waitUntil: 'networkidle2' });

  console.log('Typing credentials...');
  await page.type('#login-user', 'admin');
  await page.type('#login-pass', 'admin123');

  console.log('Clicking login...');
  await page.click('button[onclick="doLogin()"]');

  await page.waitForTimeout(2000); // Wait for fetch

  console.log('Checking if app is visible...');
  const appDisplay = await page.evaluate(() => document.getElementById('app').style.display);
  console.log('App display:', appDisplay);

  await browser.close();
})();

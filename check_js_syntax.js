const fs = require('fs');
const path = 'frontend/index.html';
const content = fs.readFileSync(path, 'utf8');
const start = content.indexOf('<script>');
const end = content.indexOf('</script>', start);
if (start < 0 || end < 0) {
  console.error('script tags not found');
  process.exit(1);
}
const script = content.slice(start + '<script>'.length, end);
fs.writeFileSync('frontend/index_script_temp.js', script, 'utf8');
try {
  require('child_process').execSync('node --check frontend/index_script_temp.js', {stdio: 'inherit'});
  console.log('CHECK OK');
} catch (e) {
  console.error('SYNTAX CHECK FAILED');
  process.exit(1);
}
// Test if nested template literals work
const refDoctors = [{id:1, name:'Dr. Smith', city:'Mumbai', mobile:'999'}];
const testCatalog = [{code:'CBC', name:'CBC', rate:600}];
const rateLists = [];
const fmt = n => Number(n).toLocaleString();

// Simulate the problematic renderRateList pattern
function renderRateList() {
  const drWithRates = refDoctors.map(doc => {
    const linkedRL = rateLists.find(r => r.refDrName === doc.name);
    return { doc, linkedRL };
  });
  
  return `
    <div>
      ${drWithRates.map(({doc, linkedRL}) => `
        <div>
          ${linkedRL ? `<span>Linked: ${linkedRL.name}</span>` : `<span>No list</span>`}
          ${testCatalog.map(t => {
            const rl = rateLists.find(r => r.refDrName === doc.name);
            const rate = rl?.testRates?.[t.code];
            return `<td>${rate !== null && rate !== undefined ? `<span>₹${fmt(rate)}</span>` : `<span>MRP</span>`}</td>`;
          }).join('')}
        </div>
      `).join('')}
    </div>
  `;
}

console.log("Template literals work:", renderRateList().includes('No list'));

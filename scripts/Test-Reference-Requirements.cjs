const fs=require('fs'),path=require('path'),assert=require('assert'),crypto=require('crypto');
const root=path.resolve(__dirname,'..');
const r=JSON.parse(fs.readFileSync(path.join(root,'docs/REFERENCE_FUNCTIONAL_REQUIREMENTS.json'),'utf8'));
const original=fs.readFileSync(path.join(root,r.source));
let checks=0;function check(fn,label){fn();checks++;console.log('PASS: '+label);}
check(()=>assert.equal(r.pages.length,203),'All reference routes included');
check(()=>assert.equal(new Set(r.pages.map(x=>x.id)).size,203),'No duplicate routes');
check(()=>assert.equal(r.pages.filter(x=>x.group==='Reference tools').length,8),'Prototype tools distinguished');
check(()=>assert.equal(r.handlerCatalog.filter(x=>x.key.startsWith('action:')).length,92),'Late-added action handlers included');
check(()=>assert.equal(r.handlerCatalog.filter(x=>x.key.startsWith('form:')).length,72),'Late-added form handlers included');
check(()=>assert(r.pages.every(p=>p.requirements.handlerLinks.every(h=>h.resolved))),'Static named handler links resolve');
check(()=>assert(r.handlerCatalog.find(x=>x.key==='form:setup-apartment').priorDefinitions.length>0),'Wrapped earlier form preserved');
check(()=>assert(r.pages.find(x=>x.id==='event-review').requirements.validation.length>0),'Event submission validations traced');
check(()=>assert(r.pages.find(x=>x.id==='notice-preview').requirements.handlerLinks.some(x=>x.key==='action:publishAnnouncement')),'Extended notice handler linked');
check(()=>assert(r.pages.find(x=>x.id==='referral-add').requirements.validation.length>0),'Referral validation traced');
check(()=>assert(r.globalGuards.some(x=>x.source.includes('requireOnline'))),'Global online decorator captured');
check(()=>assert.equal(r.sha256,crypto.createHash('sha256').update(original).digest('hex').toUpperCase()),'Reference remains byte-identical to extraction');
check(()=>{const lines=original.toString('utf8').split('\n');for(const p of r.pages)assert(lines[p.referenceLine-1].includes("R('"+p.id+"'"));},'Every page source link points to its registration');
console.log(`Completed ${checks} inventory checks. These are extraction checks, not functional or UI QA.`);

/* Extract the design contract without running demo handlers or changing the reference. */
const fs=require('fs'),path=require('path'),crypto=require('crypto');
const root=path.resolve(__dirname,'..');
const {babelParse}=require(process.env.AP_BABEL_BUNDLE||'C:/Users/rapol/.cache/codex-runtimes/codex-primary-runtime/dependencies/node/node_modules/playwright/lib/transform/babelBundle.js');
const reference='reference/ApartmentCare_Final_Clickable_UI_v2_1.html';
const bytes=fs.readFileSync(path.join(root,reference)),html=bytes.toString('utf8');
const match=/<script>([\s\S]*?)<\/script>/.exec(html),js=match[1],offset=html.slice(0,match.index).split('\n').length-1;
const ast=babelParse(js,'ApartmentCare-reference.js');
const manifest=JSON.parse(fs.readFileSync(path.join(root,'reference/screen-manifest.json'),'utf8'));
const functions=new Map(),handlers=new Map(),routes=new Map(),versions=new Map(),globalGuards=[];
const source=n=>n?js.slice(n.start,n.end):'';
const value=n=>n?.type==='StringLiteral'?n.value:null;
const line=n=>offset+n.loc.start.line;
function walk(n,visit){if(!n||typeof n!=='object')return;if(n.type)visit(n);for(const [k,v] of Object.entries(n)){if(['loc','start','end','extra','leadingComments','trailingComments'].includes(k))continue;if(Array.isArray(v))v.forEach(x=>walk(x,visit));else if(v&&typeof v==='object')walk(v,visit);}}
function handlerKey(member){if(member?.type!=='MemberExpression'||!['ACTIONS','FORMS'].includes(member.object?.name))return null;const key=member.computed?value(member.property):member.property.name;return key?(member.object.name==='FORMS'?'form:':'action:')+key:null;}
function register(key,node){handlers.set(key,node);if(!versions.has(key))versions.set(key,[]);versions.get(key).push(node);}
walk(ast,n=>{
 if(n.type==='FunctionDeclaration')functions.set(n.id.name,n);
 if(n.type==='CallExpression'&&n.callee.name==='R'&&value(n.arguments[0]))routes.set(value(n.arguments[0]),n.arguments[4]);
 if(n.type==='VariableDeclarator'&&['ACTIONS','FORMS'].includes(n.id?.name)&&n.init?.type==='ObjectExpression')for(const p of n.init.properties)register((n.id.name==='FORMS'?'form:':'action:')+(p.key.name||p.key.value),p.value||p);
 if(n.type==='CallExpression'&&n.callee.type==='MemberExpression'&&n.callee.object.name==='Object'&&n.callee.property.name==='assign'&&['ACTIONS','FORMS'].includes(n.arguments[0]?.name)&&n.arguments[1]?.type==='ObjectExpression')for(const p of n.arguments[1].properties)register((n.arguments[0].name==='FORMS'?'form:':'action:')+(p.key.name||p.key.value),p.value||p);
 if(n.type==='AssignmentExpression'&&handlerKey(n.left))register(handlerKey(n.left),n.right);
 if(n.type==='VariableDeclarator'&&n.id?.type==='Identifier'&&handlerKey(n.init)&&handlers.has(handlerKey(n.init)))functions.set(n.id.name,handlers.get(handlerKey(n.init)));
 if(n.type==='VariableDeclarator'&&['ADMIN_FORMS','STAFF_FORMS'].includes(n.id?.name))globalGuards.push({line:line(n),source:source(n)});
 if(n.type==='ForOfStatement'&&/ACTIONS\[|FORMS\[/.test(source(n)))globalGuards.push({line:line(n),source:source(n)});
});
const controls=new Set(['btn','actBtn','form','tile','listRow','section','ib','tabs','monthPicker','toggle']);
const fields=new Set(['field','amountField','selectField','checkField','fileField','search']);
const neutral=new Set(['I','esc','iconBox','kv','INR','dateStr','dateTime','monthName','safeText','stats','flat','bill','pay','matches','getFilter','isStaff','isAdmin','save','persist','audit','notify','go','render']);
function template(n){let out='';for(let i=0;i<n.quasis.length;i++){out+=n.quasis[i].value.raw;if(i<n.expressions.length)out+='${'+source(n.expressions[i])+'}';}return out;}
function bound(n,bindings={}){return n?.type==='Identifier'&&bindings[n.name]?bound(bindings[n.name]):source(n);}
function boundValue(n,bindings={}){return n?.type==='Identifier'&&bindings[n.name]?boundValue(bindings[n.name]):value(n);}
function scan(render){
 const result={controls:[],inputs:[],conditions:[],messages:[],validation:[],navigation:[],referenceCollections:[],handlerLinks:[],helpers:[]},visited=new Set();
 function inspect(node,bindings={},depth=0){
  if(!node||depth>7)return;
  walk(node,n=>{
   if(n.type==='ConditionalExpression'||n.type==='IfStatement')result.conditions.push({condition:bound(n.test,bindings),line:line(n)});
   if(n.type==='MemberExpression'&&n.object?.name==='S')result.referenceCollections.push(n.computed?source(n.property):n.property.name);
   if(n.type==='TemplateLiteral'){
    const raw=template(n);
    for(const m of raw.matchAll(/<button\b([^>]*)>([\s\S]*?)<\/button>/g)){
     const attrs=m[1],label=m[2].replace(/<[^>]*>/g,' ').replace(/\s+/g,' ').trim();
     const action=(attrs.match(/data-act=["']([^"']*)/)||[])[1],destination=(attrs.match(/data-go=["']([^"']*)/)||[])[1];
     result.controls.push({label:label||'Icon/dynamic label',kind:'inline button',target:action||destination||'dynamic target',line:line(n)});
     if(action&&!action.includes('${'))linkHandler('action:'+action,depth);
     if(destination)result.navigation.push(destination);
    }
    for(const m of raw.matchAll(/<(?:input|textarea|select)\b([^>]*)>/g))result.inputs.push({definition:m[0],line:line(n)});
   }
   if(n.type!=='CallExpression'||n.callee.type!=='Identifier')return;
   const name=n.callee.name,a=n.arguments;
   if(controls.has(name)){
    let label='',kind='',target='';
    if(name==='btn'){label=bound(a[0],bindings);kind='navigation/custom';target=bound(a[1],bindings)||bound(a[4],bindings);if(boundValue(a[1],bindings))result.navigation.push(boundValue(a[1],bindings));}
    if(name==='actBtn'){label=bound(a[0],bindings);kind='action';target=bound(a[1],bindings);const id=boundValue(a[1],bindings);if(id)linkHandler('action:'+id,depth);}
    if(name==='form'){label=bound(a[2],bindings)||"'Save'";kind='submit';target=bound(a[0],bindings);const id=boundValue(a[0],bindings);if(id)linkHandler('form:'+id,depth);}
    if(name==='tile'){label=bound(a[1],bindings);kind='navigation';target=bound(a[2],bindings);}
    if(name==='listRow'){label=bound(a[1],bindings);kind='record/navigation row';target=bound(a[3],bindings);}
    if(name==='section'&&a[1]){label=bound(a[2],bindings)||"'View all'";kind='section navigation';target=bound(a[1],bindings);}
    if(name==='ib'){label=bound(a[1],bindings);kind='icon control';target=bound(a[2],bindings);}
    if(name==='tabs'){label=bound(a[0],bindings);kind='filter tabs';target=bound(a[2],bindings)||"'filter'";}
    if(name==='monthPicker'){label='Select month';kind='month selection';target='month';linkHandler('action:month',depth);}
    if(name==='toggle'){label=bound(a[1],bindings);kind='preference toggle';target=bound(a[0],bindings);}
    if(kind)result.controls.push({label,kind,target,line:line(n)});
   }
   if(fields.has(name))result.inputs.push({definition:source(n),line:line(n)});
   if(['fail','requiredText','requireStaff','requireAdmin','requireOnline'].includes(name))result.validation.push({rule:source(n),line:line(n)});
   if(['empty','note','toast','stateHero','confirm','sheet'].includes(name))result.messages.push({kind:name,definition:source(n),line:line(n)});
   if(name==='go')result.navigation.push(bound(a[0],bindings));
   if(name==='confirm'&&boundValue(a[3],bindings))linkHandler('action:'+boundValue(a[3],bindings),depth);
   if(functions.has(name)&&!controls.has(name)&&!fields.has(name)&&!neutral.has(name)){
    const key='function:'+name+':'+a.map(x=>bound(x,bindings)).join('|');
    if(!visited.has(key)){
     visited.add(key);const fn=functions.get(name),mapped={};fn.params.forEach((p,i)=>{if(p.type==='Identifier'&&a[i])mapped[p.name]=a[i].type==='Identifier'&&bindings[a[i].name]?bindings[a[i].name]:a[i];});
     result.helpers.push({name,line:line(fn)});inspect(fn.body,mapped,depth+1);
    }
   }
  });
 }
 function linkHandler(key,depth){if(visited.has(key))return;visited.add(key);const h=handlers.get(key);result.handlerLinks.push({key,line:h?line(h):null,resolved:!!h});if(h)inspect(h,{},depth+1);}
 inspect(render);
 for(const key of ['controls','inputs','conditions','messages','validation','handlerLinks','helpers']){const seen=new Set();result[key]=result[key].filter(x=>{const k=JSON.stringify(x);if(seen.has(k))return false;seen.add(k);return true;});}
 result.referenceCollections=[...new Set(result.referenceCollections)].sort();result.navigation=[...new Set(result.navigation)];
 return result;
}
const pages=manifest.map(p=>({...p,referenceLine:line(routes.get(p.id)),renderSource:source(routes.get(p.id)),requirements:scan(routes.get(p.id))}));
const catalog=[...handlers].map(([key,node])=>({key,line:line(node),source:source(node),priorDefinitions:(versions.get(key)||[]).slice(0,-1).map(n=>({line:line(n),source:source(n)})),details:scan(node)}));
const digest=crypto.createHash('sha256').update(bytes).digest('hex').toUpperCase();
if(pages.length!==203||routes.size!==203)throw Error('Page inventory mismatch');
const escape=s=>String(s||'').replace(/\|/g,'\\|').replace(/\r?\n/g,' ').replace(/`/g,"'");
const link=(n,label='source')=>`[${label}](/${root.replaceAll('\\','/')}/${reference}:${n})`;
let md='# Functional requirements collected from the approved reference UI\n\n';
md+='This document describes **what the approved design requires**, independently of whether native/backend code already implements it. All 203 page render definitions and every action/form handler in the reference are preserved in the companion JSON. The reference was parsed, not executed or modified.\n\n';
md+='Prototype data, demo sign-in, browser storage, fake clock, reset/role tools and simulated provider success are **not production implementations**. Production authorization, persistence, secrets, payment verification and integrations must follow the project rules. Requirements implied by UI are separated from test acceptance and existing-source coverage.\n\n';
md+=`Reference SHA256: ${digest}. Pages: ${pages.length}. Action handlers: ${catalog.filter(x=>x.key.startsWith('action:')).length}. Form handlers: ${catalog.filter(x=>x.key.startsWith('form:')).length}. These are handler-definition counts, not simultaneous button counts.\n\n`;
md+='## Shared requirements on applicable pages\n\n- Back and Android Back return through the correct navigation flow.\n- Theme supports light/dark; page content and controls remain usable on phones/tablets in both orientations.\n- Role-sensitive navigation never grants permissions; backend must independently enforce actor, tenant and parent-record access.\n- Preserve the shared footer Powered By @Rapolu`s, including dialogs and error states.\n- Loading, empty, forbidden, missing-record, offline, session-expired and validation states must not fabricate successful saves.\n- Filters/search/month selection affect visible records, not recorded financial history.\n- Dialog actions include Cancel/Close and a distinct confirmation; confirmation does not bypass validation.\n\n';
md+='Reference-wide form/online decorators and role guard registrations (apply in addition to per-handler checks):\n\n';for(const guard of globalGuards)md+=`- ${escape(guard.source)} — ${link(guard.line)}\n`;
md+='## Page index\n\n| # | Group | Page | Roles | Linked handlers |\n|---:|---|---|---|---:|\n';
for(const p of pages)md+=`| ${p.number} | ${escape(p.group)} | [${escape(p.title)} (${p.id})](#requirement-${p.number}) | ${p.roles.join('/')} | ${p.requirements.handlerLinks.length} |\n`;
for(const p of pages){const r=p.requirements;
 md+=`\n<a id="requirement-${p.number}"></a>\n\n## ${p.number}. ${p.title} — ${p.id}\n\nGroup: ${p.group}. Reference roles: ${p.roles.join(', ')}. ${link(p.referenceLine,'page definition')}. ${p.group==='Reference tools'?'REFERENCE ONLY: do not ship as production behavior.':''}\n\n`;
 md+='### Controls and function targets\n\n| Control / label expression | Function | Target | Definition |\n|---|---|---|---|\n';
 for(const x of r.controls)md+=`| ${escape(x.label)} | ${x.kind} | ${escape(x.target)} | ${link(x.line)} |\n`;
 if(!r.controls.length)md+='| Shared or dynamic controls only | Inspect page definition | No direct control definition | Page link above |\n';
 const sections=[['Inputs and selectable options',r.inputs,'definition'],['Conditional states / visibility',r.conditions,'condition'],['Required validation and role guards',r.validation,'rule'],['Empty states, confirmations, feedback and dialogs',r.messages,'definition']];
 for(const [title,rows,key] of sections){md+=`\n### ${title}\n\n`;if(!rows.length)md+='No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.\n';for(const x of rows)md+=`- ${escape(x[key])} — ${link(x.line)}\n`;}
 md+='\n### Navigation and data effects\n\n';
 md+='Navigation targets/expressions: '+(r.navigation.length?r.navigation.map(escape).join('; '):'Shared navigation only / dynamic target in page source')+'.\n\n';
 md+='Reference state collections accessed: '+(r.referenceCollections.length?r.referenceCollections.join(', '):'No direct reference collection extracted')+'. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.\n\n';
 md+='Linked action/form definitions: '+(r.handlerLinks.length?r.handlerLinks.map(x=>`${escape(x.key)} (${x.resolved?link(x.line):'dynamic/shared handler; manual resolution required'})`).join(', '):'No static handler link extracted')+'. The complete handler catalog follows; no handler source is truncated.\n\n';
 md+='Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.\n';
}
md+='\n## Complete reference action and form behavior catalog\n\nThe following is prototype behavior/design intent. It must not be copied as production security, financial verification or provider success. All handlers are included, even when reached only through a dynamically generated control.\n';
for(const h of catalog){md+=`\n### ${h.key}\n\n${link(h.line,'current handler definition')}\n\n\`\`\`javascript\n${h.source}\n\`\`\`\n`;if(h.priorDefinitions.length){md+='\nEarlier definitions retained for wrapper/delegation tracing, not additional active handlers:\n';for(const prior of h.priorDefinitions)md+=`\n${link(prior.line,'earlier definition')}\n\n\`\`\`javascript\n${prior.source}\n\`\`\`\n`;}}
fs.writeFileSync(path.join(root,'docs/REFERENCE_FUNCTIONAL_REQUIREMENTS.md'),md);
fs.writeFileSync(path.join(root,'docs/REFERENCE_FUNCTIONAL_REQUIREMENTS.json'),JSON.stringify({source:reference,sha256:digest,pageCount:pages.length,handlerCount:catalog.length,limitations:'Static source extraction, including conditional/shared-helper branches. Dynamic targets require manual linkage. Reference state is not database schema; no runtime or production acceptance claimed.',globalGuards,pages,handlerCatalog:catalog,functionCatalog:[...functions].map(([name,node])=>({name,line:line(node),source:source(node)}))},null,2));
console.log(JSON.stringify({pages:pages.length,actions:catalog.filter(x=>x.key.startsWith('action:')).length,forms:catalog.filter(x=>x.key.startsWith('form:')).length,unresolvedLinks:pages.reduce((n,p)=>n+p.requirements.handlerLinks.filter(x=>!x.resolved).length,0),markdownBytes:Buffer.byteLength(md),referenceSha256:digest}));

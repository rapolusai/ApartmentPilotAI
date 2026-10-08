/* Read-only source analysis; generated reports never execute reference/demo code. */
const fs=require('fs'),path=require('path');
const root=path.resolve(__dirname,'..');
const {babelParse}=require(process.env.AP_BABEL_BUNDLE||'C:/Users/rapol/.cache/codex-runtimes/codex-primary-runtime/dependencies/node/node_modules/playwright/lib/transform/babelBundle.js');
const read=f=>fs.readFileSync(path.join(root,f),'utf8');
const relative=f=>path.relative(root,f).replaceAll('\\','/');
const manifest=JSON.parse(read('reference/screen-manifest.json'));
const status=JSON.parse(read('docs/SCREEN_IMPLEMENTATION_MAP.json'));
const html=read('reference/ApartmentCare_Final_Clickable_UI_v2_1.html');
const scriptMatch=/<script>([\s\S]*?)<\/script>/.exec(html);
if(!scriptMatch)throw Error('Reference script not found');
const js=scriptMatch[1],offset=html.slice(0,scriptMatch.index).split('\n').length-1;
const ast=babelParse(js,'ApartmentCare-reference.js');
const text=n=>n?js.slice(n.start,n.end):'';
const literal=n=>n&&n.type==='StringLiteral'?n.value:null;
function walk(n,visit){if(!n||typeof n!=='object')return;if(n.type)visit(n);for(const [k,v] of Object.entries(n)){if(['loc','start','end','extra','leadingComments','trailingComments'].includes(k))continue;if(Array.isArray(v))v.forEach(x=>walk(x,visit));else if(v&&typeof v==='object')walk(v,visit);}}
const functions=new Map(),routes=new Map(),handlers={};
walk(ast,n=>{
 if(n.type==='FunctionDeclaration'&&n.id)functions.set(n.id.name,n);
 if(n.type==='VariableDeclarator'&&['ACTIONS','FORMS'].includes(n.id?.name)&&n.init?.type==='ObjectExpression'){
  for(const p of n.init.properties){handlers[(n.id.name==='FORMS'?'form:':'action:')+(p.key.name||p.key.value)]=p.value||p;}
 }
 if(n.type==='CallExpression'&&n.callee.name==='R'&&literal(n.arguments[0]))routes.set(literal(n.arguments[0]),n.arguments[4]);
});
const controls=new Set(['btn','actBtn','form','tile','listRow','section','ib','tabs','monthPicker','toggle']);
const skipHelpers=new Set(['I','esc','safeText','matches','kv','note','iconBox','dateStr','INR','field','amountField','selectField','checkField','fileField','pill','search','timeline','stateHero','getFilter','isStaff','isAdmin','requireStaff','requireAdmin','requireOnline','stats','flat','bill','pay','notify','audit','save','go']);
function referenceActions(render){
 const result=[],seen=new Set();
 function add(label,kind,target,source,line){result.push({label,kind,target,source,line});}
 function visit(node,stack=[]){
  if(!node)return;
  walk(node,n=>{
   if(n.type==='CallExpression'&&n.callee.type==='Identifier'){
    const name=n.callee.name,a=n.arguments;
    if(controls.has(name)){
     let label='',kind='',target='';
     if(name==='btn'){label=text(a[0]);kind='navigate / custom';target=text(a[1])||text(a[4]);}
     if(name==='actBtn'){label=text(a[0]);kind='action';target=literal(a[1])||text(a[1]);}
     if(name==='form'){label=text(a[2])||"'Save'";kind='submit';target=literal(a[0])||text(a[0]);}
     if(name==='tile'){label=text(a[1]);kind='navigate';target=text(a[2]);}
     if(name==='listRow'){label=text(a[1]);kind='navigate / row';target=text(a[3]);}
     if(name==='section'&&a[1]){label=text(a[2])||"'View all'";kind='navigate / section';target=text(a[1]);}
     if(name==='ib'){label=text(a[1]);kind='icon action';target=text(a[2]);}
     if(name==='tabs'){label=text(a[0]);kind='filter tabs';target=text(a[2])||"'filter'";}
     if(name==='monthPicker'){label='Month picker';kind='action';target='month';}
     if(name==='toggle'){label=text(a[1]);kind='toggle';target=text(a[0]);}
     if(kind)add(label,kind,target,'reference/ApartmentCare_Final_Clickable_UI_v2_1.html',offset+n.loc.start.line);
    } else if(functions.has(name)&&!skipHelpers.has(name)&&!stack.includes(name)&&stack.length<3)visit(functions.get(name).body,[...stack,name]);
   }
   if(n.type==='TemplateElement'){
    const raw=n.value.raw;
    for(const m of raw.matchAll(/<button\b([^>]*)>([\s\S]*?)<\/button>/g)){
     const label=m[2].replace(/<[^>]*>/g,' ').replace(/\s+/g,' ').trim()||'Dynamic/icon label';
     add(label,'inline button',(m[1].match(/data-(?:act|go)=["']([^"']*)/)||[])[1]||'Dynamic target / inspect source','reference/ApartmentCare_Final_Clickable_UI_v2_1.html',offset+n.loc.start.line);
    }
   }
  });
 }
 visit(render);
 return result.filter(x=>{const key=[x.label,x.kind,x.target,x.line].join('|');if(seen.has(key))return false;seen.add(key);return true;});
}
function files(dir){return fs.readdirSync(dir,{withFileTypes:true}).flatMap(x=>x.isDirectory()?files(path.join(dir,x.name)):[path.join(dir,x.name)]);}
// Balanced Java source scanner skips strings/comments. It is intentionally not a Java compiler.
function close(src,start,open,shut){let depth=0,quote='',comment='';for(let i=start;i<src.length;i++){const c=src[i],next=src[i+1];if(comment==='line'){if(c==='\n')comment='';continue;}if(comment==='block'){if(c==='*'&&next==='/'){comment='';i++;}continue;}if(quote){if(c==='\\'){i++;continue;}if(c===quote)quote='';continue;}if(c==='/'&&next==='/'){comment='line';i++;continue;}if(c==='/'&&next==='*'){comment='block';i++;continue;}if(c==='"'||c==="'"){quote=c;continue;}if(c===open)depth++;if(c===shut&&!--depth)return i;}return -1;}
function args(src,start){const end=close(src,start,'(',')');if(end<0)return [];let at=start+1,depth=0,quote='',result=[];for(let i=at;i<end;i++){const c=src[i];if(quote){if(c==='\\')i++;else if(c===quote)quote='';continue;}if(c==='"'||c==="'"){quote=c;continue;}if('([{'.includes(c))depth++;if(')]}'.includes(c))depth--;if(c===','&&depth===0){result.push(src.slice(at,i).trim());at=i+1;}}result.push(src.slice(at,end).trim());return result;}
const classes=new Map();
for(const f of [...files(path.join(root,'android/app/src/main/java')), ...files(path.join(root,'backend/src/main/java'))].filter(x=>x.endsWith('.java'))){
 const src=fs.readFileSync(f,'utf8'),className=path.basename(f,'.java'),methods=[];
 const rx=/^[ \t]*(?:@\w+(?:\([^\n]*?\))?\s+)*(?:(?:public|private|protected|static|final|synchronized)\s+)*(?:[A-Za-z][\w.]*(?:<[^\n;{}]*?>)?(?:\[\])?)\s+(\w+)\s*\(/gm;
 for(const match of src.matchAll(rx)){
  if(['if','for','while','switch','return','new','catch','throw'].includes(match[1]))continue;
  const p=match.index+match[0].length-1,end=close(src,p,'(',')');if(end<0)continue;
  const brace=src.indexOf('{',end),semi=src.indexOf(';',end);if(brace<0||(semi>=0&&semi<brace)||brace-end>150)continue;
  const last=close(src,brace,'{','}');if(last<0)continue;
  let parameters=src.slice(p+1,end);while(/<[^<>]*>/.test(parameters))parameters=parameters.replace(/<[^<>]*>/g,'');
  methods.push({name:match[1],arity:args('('+parameters+')',0).filter(x=>x).length,body:src.slice(brace+1,last),signature:src.slice(match.index,brace),start:match.index,line:src.slice(0,match.index).split('\n').length,file:relative(f),className});
 }
 const fields={};for(const m of src.matchAll(/(?:private|public|protected)\s+(?:final\s+)?([\w.]+)\s+(\w+)\s*[;=]/g))fields[m[2]]=m[1].split('.').pop();
 classes.set(className,{src,methods,fields,file:relative(f)});
}
const endpoints=[];
for(const [className,c] of classes){if(!className.endsWith('Controller'))continue;const prefix=(c.src.match(/@RequestMapping\("([^"]+)"\)/)||[])[1]||'';
 for(const m of c.methods){const match=m.signature.match(/@(Get|Post|Put|Delete|Patch)Mapping\("([^"]+)"\)/);if(match)endpoints.push({method:match[1].toUpperCase(),path:prefix+match[2],owner:className,name:m.name,entry:m});}
}
function dependencies(m,visited=new Set(),depth=0){const key=m.className+'.'+m.name+':'+m.line;if(visited.has(key)||depth>7)return [];visited.add(key);let out=[m];const c=classes.get(m.className);
 for(const hit of m.body.matchAll(/\b(?:(\w+)\.)?(\w+)\s*\(/g)){
  const variable=hit[1],method=hit[2];let owner=variable?c.fields[variable]:m.className;
  if(['go','openPage','render','refreshPage','request','requestApi','get','write','setButtons','problem','showError'].includes(method))continue;
  if(variable==='db')owner='Db';if(variable==='h'&&['chooseAttachment','openSharedFile','previewAttachment','saveAttachment','exportPdf','exportCsv'].includes(method))owner='MainActivity';
  const arity=args(m.body,m.body.indexOf('(',hit.index)).filter(x=>x).length;
  if(owner&&classes.has(owner))for(const target of classes.get(owner).methods.filter(x=>x.name===method&&x.arity===arity))out.push(...dependencies(target,visited,depth+1));
 }
 return out;
}
const nativeRoutes=new Map();
for(const name of ['PaymentScreens','FinanceReportScreens','FeatureScreens','MainActivity']){
 const c=classes.get(name);if(!c)continue;
 for(const match of c.src.matchAll(/((?:case\s+"[^"]+"\s*:\s*)+)([\w]+)\s*\(/g))for(const r of match[1].matchAll(/case\s+"([^"]+)"/g)){
  if(nativeRoutes.has(r[1]))continue;const ms=c.methods.filter(x=>x.name===match[2]);if(ms.length)nativeRoutes.set(r[1],ms);
 }
}
for(const name of ['PaymentScreens','FinanceReportScreens','FeatureScreens','MainActivity']){
 const c=classes.get(name);
 for(const match of c.src.matchAll(/((?:case\s+"[^"]+"\s*:\s*)+)([\s\S]*?)(?:break;|(?=case\s+"))/g)){
  const calls=[...match[2].matchAll(/\b(\w+)\s*\(/g)].map(x=>x[1]);const entry=c.methods.find(x=>calls.includes(x.name));
  if(entry)for(const r of match[1].matchAll(/case\s+"([^"]+)"/g))if(!nativeRoutes.has(r[1]))nativeRoutes.set(r[1],[entry]);
 }
}
nativeRoutes.set('expense-proof',classes.get('AttachmentScreens').methods.filter(x=>x.name==='render'));
function apiCalls(methods){const out=[];for(const m of methods)for(const match of m.body.matchAll(/\b(get|write|request|requestApi)\s*\(/g)){
 const a=args(m.body,m.body.indexOf('(',match.index)),name=match[1];let verb=name==='get'?'GET':name==='write'?'POST':(a[0]||'').replaceAll('"',''),expr=name==='get'||name==='write'?a[0]:a[1];
 if(!expr||!/^"\//.test(expr)||!['GET','POST','PUT','DELETE','PATCH'].includes(verb))continue;
 let url='';let quote=false,depth=0,start=0;
 const pieces=[];for(let i=0;i<expr.length;i++){const c=expr[i];if(c==='"'&&expr[i-1]!=='\\')quote=!quote;if(!quote){if(c==='(')depth++;if(c===')')depth--;if(c==='+'&&depth===0){pieces.push(expr.slice(start,i).trim());start=i+1;}}}pieces.push(expr.slice(start).trim());
 for(const piece of pieces)url+=/^"[^"\\]*"$/.test(piece)?piece.slice(1,-1):'{value}';
 out.push({method:verb,path:'/api/v1'+url.split('?')[0],expression:expr,source:m.file,line:m.line});
 }
 const seen=new Set();return out.filter(x=>{const k=x.method+' '+x.path;if(seen.has(k))return false;seen.add(k);return true;});}
function endpointMatches(api,endpoint){if(api.method!==endpoint.method)return false;const norm=s=>s.replace(/\{[^}]+\}/g,'*');return norm(api.path)===norm(endpoint.path);}
function storage(methods){const reads=new Set(),writes=new Set(),rules=new Set(),sources=[];
 for(const m of methods){for(const x of m.body.matchAll(/\b(?:from|join)\s+(ap_\w+)/gi))reads.add(x[1]);for(const x of m.body.matchAll(/\b(?:insert\s+into|update|delete\s+from)\s+(ap_\w+)/gi))writes.add(x[1]);
  for(const dto of m.signature.matchAll(/Requests\.(\w+)/g)){
   const source=classes.get('Requests');const match=new RegExp('public record '+dto[1]+'\\s*\\(').exec(source.src);
   if(match){const start=source.src.indexOf('(',match.index),end=close(source.src,start,'(',')');rules.add('Request DTO '+source.src.slice(match.index,end+1).replace(/\s+/g,' '));sources.push({file:source.file,line:source.src.slice(0,match.index).split('\n').length,method:dto[1]});}
  }
  for(const x of m.body.matchAll(/\bV\.(text|opt|money|date|id|optionalId|bool|choice)\([^\n;]{0,220}/g))rules.add(x[0].split(/\)\s*[;,]/)[0]+')');
  for(const x of m.body.matchAll(/(?:throw\s+(?:new\s+)?[\w.]+|ApiError\.(?:conflict|forbidden|missing))\([^\n;]{0,260}/g))rules.add(x[0]);
  if(/a\.requireAdmin\(/.test(m.body))rules.add('Server Admin role required');else if(/a\.requireStaff\(/.test(m.body))rules.add('Server Admin/Treasurer role required');
  sources.push({file:m.file,line:m.line,method:m.name});
 }
 return {reads:[...reads].sort(),writes:[...writes].sort(),rules:[...rules],sources};
}
const result=manifest.map(p=>{
 const s=status.find(x=>x.id===p.id),ref=routes.get(p.id),actions=referenceActions(ref);
 const methods=(nativeRoutes.get(p.id)||nativeRoutes.get(s?.native_route)||[]).flatMap(x=>dependencies(x));
 const apis=apiCalls(methods),backend=[];
 for(const api of apis)for(const endpoint of endpoints.filter(e=>endpointMatches(api,e)))backend.push(...dependencies(endpoint.entry));
 const st=storage(backend);
 const fields=[];walk(ref,n=>{if(n.type==='CallExpression'&&['field','amountField','selectField','checkField','fileField'].includes(n.callee.name))fields.push(text(n));});
 const behavior=[];for(const action of actions){const handler=handlers[(action.kind==='submit'?'form:':'action:')+action.target];if(handler)behavior.push({target:action.target,source:offset+handler.loc.start.line,snippet:text(handler).slice(0,650)});}
 return {...p,status:s?.implementation_status||'UNMAPPED',nativeRoute:s?.native_route||'',nativeSource:methods.slice(0,1).map(m=>({file:m.file,line:m.line,method:m.name})),actions,fields,referenceBehavior:behavior,apis,storage:st,backendMapped:backend.length>0};
});
if(result.length!==203||routes.size!==203)throw Error('Route count mismatch');
const escape=s=>String(s||'').replace(/\|/g,'\\|').replace(/\r?\n/g,' ').replace(/`/g,"'");
const link=(f,line,label)=>`[${label||path.basename(f)}](/${root.replaceAll('\\','/')}/${f}${line?':'+line:''})`;
const distinct=(xs)=>[...new Set(xs)];
let md='# Page-wise functionality and data traceability\n\nGenerated 27 September 2026 from the frozen 203-route reference and current working source. This is a source-audit inventory, **not QA approval or a completion percentage**.\n\n';
md+='For the complete **design requirements**, including late-added/overridden handlers, full validations and conditional states, use [REFERENCE_FUNCTIONAL_REQUIREMENTS.md](REFERENCE_FUNCTIONAL_REQUIREMENTS.md). The API/SQL traces here are a separate implementation audit and do not define the required behavior.\n\n';
md+='## How to read this list\n\n- Every reference route is listed, including 8 reference-only tools.\n- Actions are source definitions, including conditional branches and repeated-row templates. They are not an exact count of buttons simultaneously rendered. Filters can produce several buttons; data lists can produce any number of row buttons.\n- Shared Back, theme, navigation, notifications and development-only role controls are described separately, not counted on every page.\n- Reference form/actions describe design intent. They do not prove the Android implementation has every control.\n- APIs and database tables below are conservative static call traces from current native destinations to controllers/services. Dynamic API expressions, polymorphic calls and branches require manual review. Read/write lists are **potential tables**, not a per-button transaction proof. Helper methods may include side effects.\n- No endpoint match, no database trace, or no direct native route is explicitly marked unresolved. A merged destination is not a completed reference page.\n- Each action still needs a manually confirmed native-control → API → validation → SQL → test link before acceptance.\n\n';
const actionTotal=result.reduce((n,p)=>n+p.actions.length,0);
md+=`## Inventory totals\n\n203 routes; ${actionTotal} extracted action/control definitions (not a runtime button total). ${result.filter(p=>p.backendMapped).length} pages have at least one source-resolved backend call; others require tracing or are local-only.\n\n`;
md+='## Shared controls\n\n| Control | Function | Persistence / validation |\n|---|---|---|\n| Back | Previous native destination; Android Back must match | Navigation state; no direct business-table write |\n| Theme | Light/dark appearance | Local preference; no business-table write |\n| Bottom navigation | Home, Money/Dues, Issues, Community, More | Server-derived role governs accessible APIs; selection is not permission |\n| Notifications | Open authenticated inbox | ap_inbox, ap_user; recipient/tenant scope enforced by backend |\n| Reference role switch/reset/fake clock | Prototype tools only | Must not become production authorization or reset API |\n\n';
md+='## Page index\n\n| # | Group | Page | Source status | Action definitions |\n|---:|---|---|---|---:|\n';
for(const p of result)md+=`| ${p.number} | ${escape(p.group)} | [${escape(p.title)} (${p.id})](#page-${p.number}) | ${p.status.replaceAll('_',' ')} | ${p.actions.length} |\n`;
for(const p of result){
 md+=`\n<a id="page-${p.number}"></a>\n\n## ${p.number}. ${p.title} — ${p.id}\n\n`;
 md+=`Group: ${p.group}. Reference roles: ${p.roles.join(', ')}. Native destination: ${p.nativeRoute||'unresolved'}. Recorded status: ${p.status}.\n\n`;
 const ref=routes.get(p.id);md+=`Reference: ${link('reference/ApartmentCare_Final_Clickable_UI_v2_1.html',offset+ref.loc.start.line,'page definition')}. `;
 md+=p.nativeSource.length?`Native entry: ${link(p.nativeSource[0].file,p.nativeSource[0].line,p.nativeSource[0].method)}.\n\n`:'Native entry trace: unresolved / no dedicated implementation found.\n\n';
 md+='### Buttons and actions in the reference\n\n| Label / definition | Type | Function target | Source |\n|---|---|---|---|\n';
 if(!p.actions.length)md+='| No direct definition extracted | May use shared/dynamic controls | Manual inspection required | Page source above |\n';
 for(const a of p.actions)md+=`| ${escape(a.label).slice(0,200)} | ${a.kind} | ${escape(a.target).slice(0,230)} | ${link(a.source,a.line,'source')} |\n`;
 if(p.fields.length){md+='\nReference input/form definitions (attributes and allowed choices shown literally):\n\n';for(const f of distinct(p.fields))md+=`- ${escape(f).slice(0,550)}\n`;}
 if(p.referenceBehavior.length){md+='\nReference action/form behavior (prototype intent; never backend security proof):\n\n';for(const x of p.referenceBehavior)md+=`- **${escape(x.target)}**: ${escape(x.snippet)} — ${link('reference/ApartmentCare_Final_Clickable_UI_v2_1.html',x.source,'handler')}\n`;}
 md+='\n### Current native API and database trace\n\n';
 if(p.apis.length){for(const api of p.apis)md+=`- ${api.method} ${escape(api.path)} — expression ${escape(api.expression)} — ${link(api.source,api.line,'native method')}\n`;}else md+='No literal native API path resolved. Local-only page or tracing gap; **unverified**, not assumed implemented.\n';
 md+=`\nPotential reads: ${p.storage.reads.length?p.storage.reads.join(', '):'None resolved / requires manual tracing'}.\n\nPotential writes: ${p.storage.writes.length?p.storage.writes.join(', '):'None resolved / requires manual tracing'}.\n\n`;
 if(p.storage.rules.length){md+='Backend validation/guard expressions found in the call trace (includes helper branches):\n\n';for(const rule of p.storage.rules.slice(0,35))md+=`- ${escape(rule)}\n`;if(p.storage.rules.length>35)md+='- Additional expressions are retained in PAGEWISE_TRACEABILITY.json.\n';}
 else md+='Backend validation trace: unresolved or no server mutation. Do not infer that validation is unnecessary.\n';
 if(p.storage.sources.length){md+='\nBackend source pointers: '+distinct(p.storage.sources.map(x=>link(x.file,x.line,x.method))).slice(0,16).join(', ')+'.\n';}
 md+='\nAcceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.\n';
}
fs.writeFileSync(path.join(root,'docs/PAGEWISE_TRACEABILITY.md'),md);
fs.writeFileSync(path.join(root,'docs/PAGEWISE_TRACEABILITY.json'),JSON.stringify({generated:'2026-09-27',limitations:'Static conservative source traces, not independently verified button-to-transaction mappings; action definitions are not runtime button counts.',pageCount:result.length,actionDefinitionCount:actionTotal,pages:result},null,2));
console.log(JSON.stringify({pages:result.length,actionDefinitions:actionTotal,backendTracePages:result.filter(x=>x.backendMapped).length,markdownBytes:Buffer.byteLength(md)}));

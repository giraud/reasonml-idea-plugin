"use strict";(self.webpackChunk_reasonml_idea_plugin_website=self.webpackChunk_reasonml_idea_plugin_website||[]).push([[22],{7568:(e,t,n)=>{n.r(t),n.d(t,{assets:()=>l,contentTitle:()=>i,default:()=>d,frontMatter:()=>s,metadata:()=>c,toc:()=>a});var o=n(5893),r=n(1151);const s={sidebar_position:2},i="Project Detection",c={id:"project-types",title:"project-types",description:"**Preview** - This document needs review and is subject to change",source:"@site/docs/project-types.md",sourceDirName:".",slug:"/project-types",permalink:"/reasonml-idea-plugin/docs/project-types",draft:!1,unlisted:!1,editUrl:"https://github.com/giraud/reasonml-idea-plugin/edit/master/website/docs/project-types.md",tags:[],version:"current",sidebarPosition:2,frontMatter:{sidebar_position:2},sidebar:"tutorialSidebar",previous:{title:"Overview",permalink:"/reasonml-idea-plugin/docs/intro"},next:{title:"Language support",permalink:"/reasonml-idea-plugin/docs/category/language-support"}},l={},a=[{value:"Dune Projects",id:"dune-projects",level:2},{value:"BuckleScript Projects",id:"bucklescript-projects",level:2},{value:"Esy Projects",id:"esy-projects",level:2}];function p(e){const t={code:"code",em:"em",h1:"h1",h2:"h2",li:"li",ol:"ol",p:"p",strong:"strong",...(0,r.a)(),...e.components};return(0,o.jsxs)(o.Fragment,{children:[(0,o.jsx)(t.p,{children:(0,o.jsxs)(t.em,{children:[(0,o.jsx)(t.strong,{children:"Preview"})," - This document needs review and is subject to change"]})}),"\n",(0,o.jsx)(t.p,{children:"Currently, three project types are supported:"}),"\n",(0,o.jsxs)(t.ol,{children:["\n",(0,o.jsx)(t.li,{children:"Dune"}),"\n",(0,o.jsx)(t.li,{children:"BuckleScript"}),"\n",(0,o.jsx)(t.li,{children:"Esy (Beta)"}),"\n"]}),"\n",(0,o.jsx)(t.p,{children:"Project types are auto-detected, and a single IDEA project may contain multiple project types. An IDEA project can even contain multiple of the same type of project (mono-repo)."}),"\n",(0,o.jsx)(t.h1,{id:"project-detection",children:"Project Detection"}),"\n",(0,o.jsx)(t.p,{children:"Projects are auto-detected but may require additional setup. Detection is based on the presence of certain project configuration files. These are outlined below."}),"\n",(0,o.jsx)(t.h2,{id:"dune-projects",children:"Dune Projects"}),"\n",(0,o.jsxs)(t.p,{children:["Dune projects currently require the most setup. If a ",(0,o.jsx)(t.code,{children:"dune-project"})," or ",(0,o.jsx)(t.code,{children:"dune"})," file is present in your project then you should be prompted to create a Dune Facet. This Facet allows you to supply additional project information such as the OCaml SDK location on your system."]}),"\n",(0,o.jsx)(t.h2,{id:"bucklescript-projects",children:"BuckleScript Projects"}),"\n",(0,o.jsxs)(t.p,{children:["BuckleScript projects are detected based on the presence of a ",(0,o.jsx)(t.code,{children:"bsconfig.json"})," configuration file. If a BuckleScript configuration file is present, BuckleScript support will be enabled. This can be verified by the presence of a BuckleScript tool window icon in IDEA."]}),"\n",(0,o.jsx)(t.h2,{id:"esy-projects",children:"Esy Projects"}),"\n",(0,o.jsxs)(t.p,{children:["Esy projects are detected based on the presence of ",(0,o.jsx)(t.code,{children:"package.json"})," file with an ",(0,o.jsx)(t.code,{children:'"esy": {...}'})," property. If an Esy configuration file is present, Esy support will be enabled. This can be verified by the presence of an Esy tool window icon in IDEA."]})]})}function d(e={}){const{wrapper:t}={...(0,r.a)(),...e.components};return t?(0,o.jsx)(t,{...e,children:(0,o.jsx)(p,{...e})}):p(e)}},1151:(e,t,n)=>{n.d(t,{Z:()=>c,a:()=>i});var o=n(7294);const r={},s=o.createContext(r);function i(e){const t=o.useContext(s);return o.useMemo((function(){return"function"==typeof e?e(t):{...t,...e}}),[t,e])}function c(e){let t;return t=e.disableParentContext?"function"==typeof e.components?e.components(r):e.components||r:i(e.components),o.createElement(s.Provider,{value:t},e.children)}}}]);
(window.webpackJsonp=window.webpackJsonp||[]).push([[8],{61:function(e,t,r){"use strict";r.r(t),r.d(t,"frontMatter",(function(){return i})),r.d(t,"metadata",(function(){return p})),r.d(t,"rightToc",(function(){return a})),r.d(t,"default",(function(){return l}));var n=r(2),o=r(6),c=(r(0),r(89)),i={id:"project-types",title:"Supported Project Types",sidebar_label:"Supported Project Types",slug:"/support-project-types"},p={unversionedId:"project-types",id:"project-types",isDocsHomePage:!1,title:"Supported Project Types",description:"**Preview** - This document needs review and is subject to change",source:"@site/docs/project-types.md",slug:"/support-project-types",permalink:"/reasonml-idea-plugin/docs/support-project-types",editUrl:"https://github.com/facebook/docusaurus/edit/master/website/docs/project-types.md",version:"current",sidebar_label:"Supported Project Types",sidebar:"someSidebar",previous:{title:"Get Started",permalink:"/reasonml-idea-plugin/docs/"},next:{title:"BuckleScript Support",permalink:"/reasonml-idea-plugin/docs/bucklescript"}},a=[{value:"Dune Projects",id:"dune-projects",children:[]},{value:"BuckleScript Projects",id:"bucklescript-projects",children:[]},{value:"Esy Projects",id:"esy-projects",children:[]}],s={rightToc:a};function l(e){var t=e.components,r=Object(o.a)(e,["components"]);return Object(c.b)("wrapper",Object(n.a)({},s,r,{components:t,mdxType:"MDXLayout"}),Object(c.b)("p",null,Object(c.b)("em",{parentName:"p"},Object(c.b)("strong",{parentName:"em"},"Preview")," - This document needs review and is subject to change")),Object(c.b)("p",null,"Currently, three project types are supported:"),Object(c.b)("ol",null,Object(c.b)("li",{parentName:"ol"},"Dune"),Object(c.b)("li",{parentName:"ol"},"BuckleScript"),Object(c.b)("li",{parentName:"ol"},"Esy (Beta)")),Object(c.b)("p",null,"Project types are auto-detected and a single IDEA project may contain multiple project types. An IDEA project can even contain multiple of the same type of project (mono-repo)."),Object(c.b)("h1",{id:"project-detection"},"Project Detection"),Object(c.b)("p",null,"Projects are auto-detected but may require additional setup. Detection is based on the presence of certain project configuration files. These are outlined below."),Object(c.b)("h2",{id:"dune-projects"},"Dune Projects"),Object(c.b)("p",null,"Dune projects currently require the most setup. If a ",Object(c.b)("inlineCode",{parentName:"p"},"dune-project")," or ",Object(c.b)("inlineCode",{parentName:"p"},"dune")," file is present in your project then you should be prompted to create a Dune Facet. This Facet allows you to supply additional project information such as the OCaml SDK location on your system."),Object(c.b)("h2",{id:"bucklescript-projects"},"BuckleScript Projects"),Object(c.b)("p",null,"BuckleScript projects are detected based on the presence of a ",Object(c.b)("inlineCode",{parentName:"p"},"bsconfig.json")," configuration file. If a BuckleScript configuration file is present, BuckleScript support will be enabled. This can be verified by the presence of a BuckleScript tool window icon in IDEA."),Object(c.b)("h2",{id:"esy-projects"},"Esy Projects"),Object(c.b)("p",null,"Esy projects are detected based on the presence of ",Object(c.b)("inlineCode",{parentName:"p"},"package.json")," file with an ",Object(c.b)("inlineCode",{parentName:"p"},'"esy": {...}')," property. If an Esy configuration file is present, Esy support will be enabled. This can be verified by the presence of an Esy tool window icon in IDEA."))}l.isMDXComponent=!0},89:function(e,t,r){"use strict";r.d(t,"a",(function(){return u})),r.d(t,"b",(function(){return j}));var n=r(0),o=r.n(n);function c(e,t,r){return t in e?Object.defineProperty(e,t,{value:r,enumerable:!0,configurable:!0,writable:!0}):e[t]=r,e}function i(e,t){var r=Object.keys(e);if(Object.getOwnPropertySymbols){var n=Object.getOwnPropertySymbols(e);t&&(n=n.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),r.push.apply(r,n)}return r}function p(e){for(var t=1;t<arguments.length;t++){var r=null!=arguments[t]?arguments[t]:{};t%2?i(Object(r),!0).forEach((function(t){c(e,t,r[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(r)):i(Object(r)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(r,t))}))}return e}function a(e,t){if(null==e)return{};var r,n,o=function(e,t){if(null==e)return{};var r,n,o={},c=Object.keys(e);for(n=0;n<c.length;n++)r=c[n],t.indexOf(r)>=0||(o[r]=e[r]);return o}(e,t);if(Object.getOwnPropertySymbols){var c=Object.getOwnPropertySymbols(e);for(n=0;n<c.length;n++)r=c[n],t.indexOf(r)>=0||Object.prototype.propertyIsEnumerable.call(e,r)&&(o[r]=e[r])}return o}var s=o.a.createContext({}),l=function(e){var t=o.a.useContext(s),r=t;return e&&(r="function"==typeof e?e(t):p(p({},t),e)),r},u=function(e){var t=l(e.components);return o.a.createElement(s.Provider,{value:t},e.children)},d={inlineCode:"code",wrapper:function(e){var t=e.children;return o.a.createElement(o.a.Fragment,{},t)}},b=o.a.forwardRef((function(e,t){var r=e.components,n=e.mdxType,c=e.originalType,i=e.parentName,s=a(e,["components","mdxType","originalType","parentName"]),u=l(r),b=n,j=u["".concat(i,".").concat(b)]||u[b]||d[b]||c;return r?o.a.createElement(j,p(p({ref:t},s),{},{components:r})):o.a.createElement(j,p({ref:t},s))}));function j(e,t){var r=arguments,n=t&&t.mdxType;if("string"==typeof e||n){var c=r.length,i=new Array(c);i[0]=b;var p={};for(var a in t)hasOwnProperty.call(t,a)&&(p[a]=t[a]);p.originalType=e,p.mdxType="string"==typeof e?e:n,i[1]=p;for(var s=2;s<c;s++)i[s]=r[s];return o.a.createElement.apply(null,i)}return o.a.createElement.apply(null,r)}b.displayName="MDXCreateElement"}}]);
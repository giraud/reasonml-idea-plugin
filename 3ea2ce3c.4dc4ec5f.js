(window.webpackJsonp=window.webpackJsonp||[]).push([[13],{150:function(e,t,n){"use strict";n.r(t),t.default=n.p+"assets/images/run_ide-16ae610e9fcaeec46e330232b03afed4.png"},151:function(e,t,n){"use strict";n.r(t),t.default=n.p+"assets/images/psiviewer_01-ce5dba988e0fc4e0f8bf2a768c766fd6.png"},152:function(e,t,n){"use strict";n.r(t),t.default=n.p+"assets/images/indicesviewer_01-9f220b589145e12ad963efbcdd6193e8.png"},153:function(e,t,n){"use strict";n.r(t),t.default=n.p+"assets/images/enable-logging-178c1dfc6490befc765c8f7c56ff3c62.png"},67:function(e,t,n){"use strict";n.r(t),n.d(t,"frontMatter",(function(){return i})),n.d(t,"metadata",(function(){return a})),n.d(t,"rightToc",(function(){return c})),n.d(t,"default",(function(){return b}));var r=n(2),o=n(6),l=(n(0),n(93)),i={id:"plugin-development",title:"Plugin Development",sidebar_label:"Plugin Development",slug:"/contributing/plugin-development"},a={unversionedId:"contributing/plugin-development",id:"contributing/plugin-development",isDocsHomePage:!1,title:"Plugin Development",description:"Local Development",source:"@site/docs/contributing/plugin-development.md",slug:"/contributing/plugin-development",permalink:"/reasonml-idea-plugin/docs/contributing/plugin-development",editUrl:"https://github.com/reasonml-editor/reasonml-idea-plugin/edit/master/website/docs/contributing/plugin-development.md",version:"current",sidebar_label:"Plugin Development",sidebar:"someSidebar",previous:{title:"How to Contribute",permalink:"/reasonml-idea-plugin/docs/contributing"},next:{title:"Plugin Architecture",permalink:"/reasonml-idea-plugin/docs/contributing/plugin-architecture"}},c=[{value:"Local Development",id:"local-development",children:[{value:"Prepare your Environment",id:"prepare-your-environment",children:[]},{value:"Run the Plugin",id:"run-the-plugin",children:[]},{value:"Troubleshooting",id:"troubleshooting",children:[]}]},{value:"Tools",id:"tools",children:[]},{value:"Enable Debug Logs",id:"enable-debug-logs",children:[]}],u={rightToc:c};function b(e){var t=e.components,i=Object(o.a)(e,["components"]);return Object(l.b)("wrapper",Object(r.a)({},u,i,{components:t,mdxType:"MDXLayout"}),Object(l.b)("h2",{id:"local-development"},"Local Development"),Object(l.b)("p",null,"Follow the steps below to get the plugin running locally for development."),Object(l.b)("h3",{id:"prepare-your-environment"},"Prepare your Environment"),Object(l.b)("ol",null,Object(l.b)("li",{parentName:"ol"},"Install the ",Object(l.b)("a",Object(r.a)({parentName:"li"},{href:"http://www.jetbrains.org/intellij/sdk/docs/tutorials/custom_language_support/prerequisites.html"}),"plugin prerequisites"),", ensuring that the Plugin DevKit plugin is enabled and ",Object(l.b)("a",Object(r.a)({parentName:"li"},{href:"https://plugins.jetbrains.com/plugin/227-psiviewer"}),"PsiViewer")," is installed and enabled.  "),Object(l.b)("li",{parentName:"ol"},"Clone the project ",Object(l.b)("inlineCode",{parentName:"li"},"git clone https://github.com/giraud/reasonml-idea-plugin.git")),Object(l.b)("li",{parentName:"ol"},"Import the project into Intellij as a Gradle project. This should auto-configure everything else needed to run the plugin."),Object(l.b)("li",{parentName:"ol"},"If prompted, download the Intellij SDK source files. This is useful for debugging the Intellij platform code.")),Object(l.b)("h3",{id:"run-the-plugin"},"Run the Plugin"),Object(l.b)("p",null,"You can launch a new IDEA instance with your development version of the plugin installed with the ",Object(l.b)("inlineCode",{parentName:"p"},"runIde")," gradle task."),Object(l.b)("p",null,Object(l.b)("img",{src:n(150).default})),Object(l.b)("h3",{id:"troubleshooting"},"Troubleshooting"),Object(l.b)("ul",null,Object(l.b)("li",{parentName:"ul"},"Verifying that your project is configured to use the ",Object(l.b)("strong",{parentName:"li"},"gradle-wrapper.properties")," file under ",Object(l.b)("strong",{parentName:"li"},"File > Settings > Gradle"),"."),Object(l.b)("li",{parentName:"ul"},"Ensure that the ",Object(l.b)("strong",{parentName:"li"},"Project SDK")," and ",Object(l.b)("strong",{parentName:"li"},"Project Language Level")," are both set to ",Object(l.b)("strong",{parentName:"li"},"Java 11 (Corretto - 11.0.9)")," via ",Object(l.b)("strong",{parentName:"li"},"File > Project Structure > Project Settings > Project"),".")),Object(l.b)("hr",null),Object(l.b)("h2",{id:"tools"},"Tools"),Object(l.b)("p",null,"Two tools are very important for development: the PSIViewer and the Indices viewer."),Object(l.b)("p",null,"When you start an IntelliJ instance with gradle for debugging,\nthey are automatically downloaded for you and immediately available."),Object(l.b)("p",null,Object(l.b)("img",{alt:"PSIViewer tool",src:n(151).default})),Object(l.b)("blockquote",null,Object(l.b)("p",{parentName:"blockquote"},"The PsiViewer tool to inspect PsiElements of the generated tree from parser")),Object(l.b)("p",null,Object(l.b)("img",{alt:"\xcendices viewer",src:n(152).default})),Object(l.b)("blockquote",null,Object(l.b)("p",{parentName:"blockquote"},"The indices viewer tool let you verify the correctness of the indexing")),Object(l.b)("hr",null),Object(l.b)("h2",{id:"enable-debug-logs"},"Enable Debug Logs"),Object(l.b)("p",null,"Debug statements can be found throughout the plugin codebase."),Object(l.b)("p",null,"Here's an example:"),Object(l.b)("pre",null,Object(l.b)("code",Object(r.a)({parentName:"pre"},{className:"language-java"}),'private final static Log LOG = Log.create("my-logging-category");\n\n...\n\nif (LOG.isDebugEnabled()) {\n  LOG.debug("Log some useful debug information here...");\n}\n')),Object(l.b)("p",null,"These statements are disabled by default. To enable debug logs, do the following:"),Object(l.b)("ol",null,Object(l.b)("li",{parentName:"ol"},Object(l.b)("p",{parentName:"li"},"Launch an instance of IntelliJ + the plugin via the gradle task as described above.")),Object(l.b)("li",{parentName:"ol"},Object(l.b)("p",{parentName:"li"},"In the newly launched instance (not your development instance) click on ",Object(l.b)("strong",{parentName:"p"},"Help > Diagnostic Tools > Debug Log Settings..."))),Object(l.b)("li",{parentName:"ol"},Object(l.b)("p",{parentName:"li"},"Enter the following, replacing ",Object(l.b)("inlineCode",{parentName:"p"},"my-logging-category")," with the value provided by the ",Object(l.b)("inlineCode",{parentName:"p"},"Log.create(...)")," instantiator:"),Object(l.b)("p",{parentName:"li"},Object(l.b)("img",{alt:"Log Configuration",src:n(153).default}))),Object(l.b)("li",{parentName:"ol"},Object(l.b)("p",{parentName:"li"},"Debug logs should now be enabled for that logging category. To view the logs, run ",Object(l.b)("strong",{parentName:"p"},"Help > Show Log in Files")),Object(l.b)("blockquote",{parentName:"li"},Object(l.b)("p",{parentName:"blockquote"},"Note: you can run ",Object(l.b)("inlineCode",{parentName:"p"},"tail -f idea.log")," from a terminal to follow along with the log output.")))))}b.isMDXComponent=!0},93:function(e,t,n){"use strict";n.d(t,"a",(function(){return p})),n.d(t,"b",(function(){return d}));var r=n(0),o=n.n(r);function l(e,t,n){return t in e?Object.defineProperty(e,t,{value:n,enumerable:!0,configurable:!0,writable:!0}):e[t]=n,e}function i(e,t){var n=Object.keys(e);if(Object.getOwnPropertySymbols){var r=Object.getOwnPropertySymbols(e);t&&(r=r.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),n.push.apply(n,r)}return n}function a(e){for(var t=1;t<arguments.length;t++){var n=null!=arguments[t]?arguments[t]:{};t%2?i(Object(n),!0).forEach((function(t){l(e,t,n[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(n)):i(Object(n)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(n,t))}))}return e}function c(e,t){if(null==e)return{};var n,r,o=function(e,t){if(null==e)return{};var n,r,o={},l=Object.keys(e);for(r=0;r<l.length;r++)n=l[r],t.indexOf(n)>=0||(o[n]=e[n]);return o}(e,t);if(Object.getOwnPropertySymbols){var l=Object.getOwnPropertySymbols(e);for(r=0;r<l.length;r++)n=l[r],t.indexOf(n)>=0||Object.prototype.propertyIsEnumerable.call(e,n)&&(o[n]=e[n])}return o}var u=o.a.createContext({}),b=function(e){var t=o.a.useContext(u),n=t;return e&&(n="function"==typeof e?e(t):a(a({},t),e)),n},p=function(e){var t=b(e.components);return o.a.createElement(u.Provider,{value:t},e.children)},s={inlineCode:"code",wrapper:function(e){var t=e.children;return o.a.createElement(o.a.Fragment,{},t)}},g=o.a.forwardRef((function(e,t){var n=e.components,r=e.mdxType,l=e.originalType,i=e.parentName,u=c(e,["components","mdxType","originalType","parentName"]),p=b(n),g=r,d=p["".concat(i,".").concat(g)]||p[g]||s[g]||l;return n?o.a.createElement(d,a(a({ref:t},u),{},{components:n})):o.a.createElement(d,a({ref:t},u))}));function d(e,t){var n=arguments,r=t&&t.mdxType;if("string"==typeof e||r){var l=n.length,i=new Array(l);i[0]=g;var a={};for(var c in t)hasOwnProperty.call(t,c)&&(a[c]=t[c]);a.originalType=e,a.mdxType="string"==typeof e?e:r,i[1]=a;for(var u=2;u<l;u++)i[u]=n[u];return o.a.createElement.apply(null,i)}return o.a.createElement.apply(null,n)}g.displayName="MDXCreateElement"}}]);
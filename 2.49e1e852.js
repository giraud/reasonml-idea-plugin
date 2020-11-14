/*! For license information please see 2.49e1e852.js.LICENSE.txt */
(window.webpackJsonp=window.webpackJsonp||[]).push([[2],{100:function(e,t,n){"use strict";var a=n(0),r=n.n(a),c=n(87),o=n(92),i=n(120),l=n(53),u=n.n(l);var s=function(){var e,t=Object(i.a)(),n=t.isAnnouncementBarClosed,a=t.closeAnnouncementBar,l=Object(o.a)().announcementBar;if(!l)return null;var s=l.content,d=l.backgroundColor,f=l.textColor,m=l.isCloseable;return!s||m&&n?null:r.a.createElement("div",{className:u.a.announcementBar,style:{backgroundColor:d,color:f},role:"banner"},r.a.createElement("div",{className:Object(c.a)(u.a.announcementBarContent,(e={},e[u.a.announcementBarCloseable]=m,e)),dangerouslySetInnerHTML:{__html:s}}),m?r.a.createElement("button",{type:"button",className:u.a.announcementBarClose,onClick:a,"aria-label":"Close"},r.a.createElement("span",{"aria-hidden":"true"},"\xd7")):null)},d=n(101),f=n(104),m=n(10),h="light",v="dark",b=function(e){return e===v?v:h},p=function(){return m.a.canUseDOM?b(document.documentElement.getAttribute("data-theme")):h},g=function(e){try{localStorage.setItem("theme",b(e))}catch(t){console.error(t)}},O=function(){var e=Object(o.a)().colorMode.disableSwitch,t=void 0!==e&&e,n=Object(a.useState)(p),r=n[0],c=n[1],i=Object(a.useCallback)((function(){c(h),g(h)}),[]),l=Object(a.useCallback)((function(){c(v),g(v)}),[]);return Object(a.useEffect)((function(){document.documentElement.setAttribute("data-theme",b(r))}),[r]),Object(a.useEffect)((function(){if(!t)try{var e=localStorage.getItem("theme");null!==e&&c(b(e))}catch(n){console.error(n)}}),[c]),Object(a.useEffect)((function(){t||window.matchMedia("(prefers-color-scheme: dark)").addListener((function(e){var t=e.matches;c(t?v:h)}))}),[]),{isDarkTheme:r===v,setLightTheme:i,setDarkTheme:l}},k=n(122);var j=function(e){var t=O(),n=t.isDarkTheme,a=t.setLightTheme,c=t.setDarkTheme;return r.a.createElement(k.a.Provider,{value:{isDarkTheme:n,setLightTheme:a,setDarkTheme:c}},e.children)},E="docusaurus.tab.",y=function(){var e=Object(a.useState)({}),t=e[0],n=e[1],r=Object(a.useCallback)((function(e,t){try{localStorage.setItem("docusaurus.tab."+e,t)}catch(n){console.error(n)}}),[]);return Object(a.useEffect)((function(){try{for(var e={},t=0;t<localStorage.length;t+=1){var a=localStorage.key(t);if(a.startsWith(E))e[a.substring(E.length)]=localStorage.getItem(a)}n(e)}catch(r){console.error(r)}}),[]),{tabGroupChoices:t,setTabGroupChoices:function(e,t){n((function(n){var a;return Object.assign({},n,((a={})[e]=t,a))})),r(e,t)}}},w="docusaurus.announcement.dismiss",C="docusaurus.announcement.id",_=function(){var e=Object(o.a)().announcementBar,t=Object(a.useState)(!0),n=t[0],r=t[1],c=Object(a.useCallback)((function(){localStorage.setItem(w,"true"),r(!0)}),[]);return Object(a.useEffect)((function(){if(e){var t=e.id,n=localStorage.getItem(C);"annoucement-bar"===n&&(n="announcement-bar");var a=t!==n;localStorage.setItem(C,t),a&&localStorage.setItem(w,"false"),(a||"false"===localStorage.getItem(w))&&r(!1)}}),[]),{isAnnouncementBarClosed:n,closeAnnouncementBar:c}},N=n(121);var S=function(e){var t=y(),n=t.tabGroupChoices,a=t.setTabGroupChoices,c=_(),o=c.isAnnouncementBarClosed,i=c.closeAnnouncementBar;return r.a.createElement(N.a.Provider,{value:{tabGroupChoices:n,setTabGroupChoices:a,isAnnouncementBarClosed:o,closeAnnouncementBar:i}},e.children)},T=n(126);function I(e){var t=e.children;return r.a.createElement(j,null,r.a.createElement(S,null,r.a.createElement(T.a,null,t)))}var B=n(2),x=n(111),D=n(90),L=n(91);function P(e){var t=e.language,n=e.version,a=e.tag;return r.a.createElement(x.a,null,t&&r.a.createElement("meta",{name:"docusaurus_language",content:""+t}),n&&r.a.createElement("meta",{name:"docusaurus_version",content:n}),a&&r.a.createElement("meta",{name:"docusaurus_tag",content:a}))}var A=n(125);function M(e){var t=Object(D.a)().siteConfig,n=t.favicon,a=t.title,c=t.themeConfig,o=c.image,i=c.metadatas,l=t.url,u=t.titleDelimiter,s=e.title,d=e.description,f=e.image,m=e.keywords,h=e.permalink,v=e.searchMetadatas,b=s?s+" "+u+" "+a:a,p=f||o,g=Object(L.a)(p,{absolute:!0}),O=Object(L.a)(n);return r.a.createElement(r.a.Fragment,null,r.a.createElement(x.a,null,r.a.createElement("html",{lang:"en"}),b&&r.a.createElement("title",null,b),b&&r.a.createElement("meta",{property:"og:title",content:b}),n&&r.a.createElement("link",{rel:"shortcut icon",href:O}),d&&r.a.createElement("meta",{name:"description",content:d}),d&&r.a.createElement("meta",{property:"og:description",content:d}),m&&m.length&&r.a.createElement("meta",{name:"keywords",content:m.join(",")}),p&&r.a.createElement("meta",{property:"og:image",content:g}),p&&r.a.createElement("meta",{property:"twitter:image",content:g}),p&&r.a.createElement("meta",{name:"twitter:image:alt",content:"Image for "+b}),h&&r.a.createElement("meta",{property:"og:url",content:l+h}),h&&r.a.createElement("link",{rel:"canonical",href:l+h}),r.a.createElement("meta",{name:"twitter:card",content:"summary_large_image"})),r.a.createElement(P,Object(B.a)({tag:A.a,language:"en"},v)),r.a.createElement(x.a,null,i.map((function(e,t){return r.a.createElement("meta",Object(B.a)({key:"metadata_"+t},e))}))))}n(55);t.a=function(e){var t=e.children,n=e.noFooter,a=e.wrapperClassName;return r.a.createElement(I,null,r.a.createElement(M,e),r.a.createElement(s,null),r.a.createElement(d.a,null),r.a.createElement("div",{className:Object(c.a)("main-wrapper",a)},t),!n&&r.a.createElement(f.a,null))}},102:function(e,t,n){"use strict";var a=n(2),r=n(0),c=n.n(r),o=n(127),i=n.n(o),l=n(92),u=n(90),s=n(87),d=n(54),f=n.n(d),m=function(e){var t=e.icon,n=e.style;return c.a.createElement("span",{className:Object(s.a)(f.a.toggle,f.a.dark),style:n},t)},h=function(e){var t=e.icon,n=e.style;return c.a.createElement("span",{className:Object(s.a)(f.a.toggle,f.a.light),style:n},t)};t.a=function(e){var t=Object(l.a)().colorMode.switchConfig,n=t.darkIcon,r=t.darkIconStyle,o=t.lightIcon,s=t.lightIconStyle,d=Object(u.a)().isClient;return c.a.createElement(i.a,Object(a.a)({disabled:!d,icons:{checked:c.a.createElement(m,{icon:n,style:r}),unchecked:c.a.createElement(h,{icon:o,style:s})}},e))}},103:function(e,t,n){"use strict";n.d(t,"a",(function(){return l}));var a=n(6),r=n(0),c=n.n(r),o=n(110),i={default:function(){return o.a},docsVersion:function(){return n(132).default},docsVersionDropdown:function(){return n(136).default},doc:function(){return n(137).default}};function l(e){var t=e.type,n=Object(a.a)(e,["type"]),r=function(e){void 0===e&&(e="default");var t=i[e];if(!t)throw new Error("No NavbarItem component found for type="+e+".");return t()}(t);return c.a.createElement(r,n)}},105:function(e,t,n){"use strict";n.d(t,"a",(function(){return a}));var a=function(){return null}},106:function(e,t,n){"use strict";var a=n(0),r=n(107);var c=function(e){var t=Object(a.useState)(e),n=t[0],r=t[1];return Object(a.useEffect)((function(){var e=function(){return r(window.location.hash)};return window.addEventListener("hashchange",e),function(){return window.removeEventListener("hashchange",e)}}),[]),[n,r]},o=n(123);t.a=function(e){var t=Object(a.useState)(!0),n=t[0],i=t[1],l=Object(a.useState)(!1),u=l[0],s=l[1],d=Object(a.useState)(0),f=d[0],m=d[1],h=Object(a.useState)(0),v=h[0],b=h[1],p=Object(a.useCallback)((function(e){null!==e&&b(e.getBoundingClientRect().height)}),[]),g=Object(r.useLocation)(),O=c(g.hash),k=O[0],j=O[1];return Object(o.a)((function(t){var n=t.scrollY;if(e&&(0===n&&i(!0),!(n<v))){if(u)return s(!1),i(!1),void m(n);var a=document.documentElement.scrollHeight-v,r=window.innerHeight;f&&n>=f?i(!1):n+r<a&&i(!0),m(n)}}),[f,v]),Object(a.useEffect)((function(){e&&(i(!0),j(g.hash))}),[g]),Object(a.useEffect)((function(){e&&k&&s(!0)}),[k]),{navbarRef:p,isNavbarVisible:n}}},110:function(e,t,n){"use strict";var a=n(2),r=n(6),c=n(0),o=n.n(c),i=n(87),l=n(88),u=n(91),s=n(107),d=n(124);function f(e){var t=e.activeBasePath,n=e.activeBaseRegex,c=e.to,i=e.href,s=e.label,d=e.activeClassName,f=void 0===d?"navbar__link--active":d,m=e.prependBaseUrlToHref,h=Object(r.a)(e,["activeBasePath","activeBaseRegex","to","href","label","activeClassName","prependBaseUrlToHref"]),v=Object(u.a)(c),b=Object(u.a)(t),p=Object(u.a)(i,{forcePrependBaseUrl:!0});return o.a.createElement(l.a,Object(a.a)({},i?{target:"_blank",rel:"noopener noreferrer",href:m?p:i}:Object.assign({isNavLink:!0,activeClassName:f,to:v},t||n?{isActive:function(e,t){return n?new RegExp(n).test(t.pathname):t.pathname.startsWith(b)}}:null),h),s)}function m(e){var t=e.items,n=e.position,l=e.className,u=Object(r.a)(e,["items","position","className"]),s=Object(c.useRef)(null),d=Object(c.useRef)(null),m=Object(c.useState)(!1),h=m[0],v=m[1];Object(c.useEffect)((function(){var e=function(e){s.current&&!s.current.contains(e.target)&&v(!1)};return document.addEventListener("mousedown",e),document.addEventListener("touchstart",e),function(){document.removeEventListener("mousedown",e),document.removeEventListener("touchstart",e)}}),[s]);var b=function(e,t){return void 0===t&&(t=!1),Object(i.a)({"navbar__item navbar__link":!t,dropdown__link:t},e)};return t?o.a.createElement("div",{ref:s,className:Object(i.a)("navbar__item","dropdown","dropdown--hoverable",{"dropdown--left":"left"===n,"dropdown--right":"right"===n,"dropdown--show":h})},o.a.createElement(f,Object(a.a)({className:b(l)},u,{onClick:u.to?void 0:function(e){return e.preventDefault()},onKeyDown:function(e){"Enter"===e.key&&(e.preventDefault(),v(!h))}}),u.label),o.a.createElement("ul",{ref:d,className:"dropdown__menu"},t.map((function(e,n){var c=e.className,i=Object(r.a)(e,["className"]);return o.a.createElement("li",{key:n},o.a.createElement(f,Object(a.a)({onKeyDown:function(e){if(n===t.length-1&&"Tab"===e.key){e.preventDefault(),v(!1);var a=s.current.nextElementSibling;a&&a.focus()}},activeClassName:"dropdown__link--active",className:b(c,!0)},i)))})))):o.a.createElement(f,Object(a.a)({className:b(l)},u))}function h(e){var t=e.items,n=e.className,l=(e.position,Object(r.a)(e,["items","className","position"])),u=Object(s.useLocation)().pathname,m=Object(c.useState)((function(){var e;return null===(e=!(null==t?void 0:t.some((function(e){return Object(d.a)(e.to,u)}))))||void 0===e||e})),h=m[0],v=m[1],b=function(e,t){return void 0===t&&(t=!1),Object(i.a)("menu__link",{"menu__link--sublist":t},e)};return t?o.a.createElement("li",{className:Object(i.a)("menu__list-item",{"menu__list-item--collapsed":h})},o.a.createElement(f,Object(a.a)({role:"button",className:b(n,!0)},l,{onClick:function(){v((function(e){return!e}))}}),l.label),o.a.createElement("ul",{className:"menu__list"},t.map((function(e,t){var n=e.className,c=Object(r.a)(e,["className"]);return o.a.createElement("li",{className:"menu__list-item",key:t},o.a.createElement(f,Object(a.a)({activeClassName:"menu__link--active",className:b(n)},c,{onClick:l.onClick})))})))):o.a.createElement("li",{className:"menu__list-item"},o.a.createElement(f,Object(a.a)({className:b(n)},l)))}t.a=function(e){var t=e.mobile,n=void 0!==t&&t,a=Object(r.a)(e,["mobile"]),c=n?h:m;return o.a.createElement(c,a)}},120:function(e,t,n){"use strict";var a=n(0),r=n(121);t.a=function(){var e=Object(a.useContext)(r.a);if(null==e)throw new Error("`useUserPreferencesContext` is used outside of `Layout` Component.");return e}},121:function(e,t,n){"use strict";var a=n(0),r=Object(a.createContext)(void 0);t.a=r},122:function(e,t,n){"use strict";var a=n(0),r=n.n(a).a.createContext(void 0);t.a=r},123:function(e,t,n){"use strict";var a=n(0),r=n(10),c=function(){return{scrollX:r.a.canUseDOM?window.pageXOffset:0,scrollY:r.a.canUseDOM?window.pageYOffset:0}};t.a=function(e,t){void 0===t&&(t=[]);var n=Object(a.useState)(c()),r=n[0],o=n[1],i=function(){var t=c();o(t),e&&e(t)};return Object(a.useEffect)((function(){return window.addEventListener("scroll",i),function(){return window.removeEventListener("scroll",i,{passive:!0})}}),t),r}},124:function(e,t,n){"use strict";n.d(t,"a",(function(){return a}));var a=function(e,t){var n=function(e){return e.endsWith("/")?e:e+"/"};return n(e)===n(t)}},125:function(e,t,n){"use strict";n.d(t,"a",(function(){return a})),n.d(t,"b",(function(){return r}));var a="default";function r(e,t){return"docs-"+e+"-"+t}},127:function(e,t,n){"use strict";Object.defineProperty(t,"__esModule",{value:!0});var a=Object.assign||function(e){for(var t=1;t<arguments.length;t++){var n=arguments[t];for(var a in n)Object.prototype.hasOwnProperty.call(n,a)&&(e[a]=n[a])}return e},r=function(){function e(e,t){for(var n=0;n<t.length;n++){var a=t[n];a.enumerable=a.enumerable||!1,a.configurable=!0,"value"in a&&(a.writable=!0),Object.defineProperty(e,a.key,a)}}return function(t,n,a){return n&&e(t.prototype,n),a&&e(t,a),t}}(),c=n(0),o=f(c),i=f(n(128)),l=f(n(7)),u=f(n(129)),s=f(n(130)),d=n(131);function f(e){return e&&e.__esModule?e:{default:e}}var m=function(e){function t(e){!function(e,t){if(!(e instanceof t))throw new TypeError("Cannot call a class as a function")}(this,t);var n=function(e,t){if(!e)throw new ReferenceError("this hasn't been initialised - super() hasn't been called");return!t||"object"!=typeof t&&"function"!=typeof t?e:t}(this,(t.__proto__||Object.getPrototypeOf(t)).call(this,e));return n.handleClick=n.handleClick.bind(n),n.handleTouchStart=n.handleTouchStart.bind(n),n.handleTouchMove=n.handleTouchMove.bind(n),n.handleTouchEnd=n.handleTouchEnd.bind(n),n.handleFocus=n.handleFocus.bind(n),n.handleBlur=n.handleBlur.bind(n),n.previouslyChecked=!(!e.checked&&!e.defaultChecked),n.state={checked:!(!e.checked&&!e.defaultChecked),hasFocus:!1},n}return function(e,t){if("function"!=typeof t&&null!==t)throw new TypeError("Super expression must either be null or a function, not "+typeof t);e.prototype=Object.create(t&&t.prototype,{constructor:{value:e,enumerable:!1,writable:!0,configurable:!0}}),t&&(Object.setPrototypeOf?Object.setPrototypeOf(e,t):e.__proto__=t)}(t,e),r(t,[{key:"componentDidUpdate",value:function(e){e.checked!==this.props.checked&&this.setState({checked:!!this.props.checked})}},{key:"handleClick",value:function(e){var t=this.input;if(e.target!==t&&!this.moved)return this.previouslyChecked=t.checked,e.preventDefault(),t.focus(),void t.click();var n=this.props.hasOwnProperty("checked")?this.props.checked:t.checked;this.setState({checked:n})}},{key:"handleTouchStart",value:function(e){this.startX=(0,d.pointerCoord)(e).x,this.activated=!0}},{key:"handleTouchMove",value:function(e){if(this.activated&&(this.moved=!0,this.startX)){var t=(0,d.pointerCoord)(e).x;this.state.checked&&t+15<this.startX?(this.setState({checked:!1}),this.startX=t,this.activated=!0):t-15>this.startX&&(this.setState({checked:!0}),this.startX=t,this.activated=t<this.startX+5)}}},{key:"handleTouchEnd",value:function(e){if(this.moved){var t=this.input;if(e.preventDefault(),this.startX){var n=(0,d.pointerCoord)(e).x;!0===this.previouslyChecked&&this.startX+4>n?this.previouslyChecked!==this.state.checked&&(this.setState({checked:!1}),this.previouslyChecked=this.state.checked,t.click()):this.startX-4<n&&this.previouslyChecked!==this.state.checked&&(this.setState({checked:!0}),this.previouslyChecked=this.state.checked,t.click()),this.activated=!1,this.startX=null,this.moved=!1}}}},{key:"handleFocus",value:function(e){var t=this.props.onFocus;t&&t(e),this.setState({hasFocus:!0})}},{key:"handleBlur",value:function(e){var t=this.props.onBlur;t&&t(e),this.setState({hasFocus:!1})}},{key:"getIcon",value:function(e){var n=this.props.icons;return n?void 0===n[e]?t.defaultProps.icons[e]:n[e]:null}},{key:"render",value:function(){var e=this,t=this.props,n=t.className,r=(t.icons,function(e,t){var n={};for(var a in e)t.indexOf(a)>=0||Object.prototype.hasOwnProperty.call(e,a)&&(n[a]=e[a]);return n}(t,["className","icons"])),c=(0,i.default)("react-toggle",{"react-toggle--checked":this.state.checked,"react-toggle--focus":this.state.hasFocus,"react-toggle--disabled":this.props.disabled},n);return o.default.createElement("div",{className:c,onClick:this.handleClick,onTouchStart:this.handleTouchStart,onTouchMove:this.handleTouchMove,onTouchEnd:this.handleTouchEnd},o.default.createElement("div",{className:"react-toggle-track"},o.default.createElement("div",{className:"react-toggle-track-check"},this.getIcon("checked")),o.default.createElement("div",{className:"react-toggle-track-x"},this.getIcon("unchecked"))),o.default.createElement("div",{className:"react-toggle-thumb"}),o.default.createElement("input",a({},r,{ref:function(t){e.input=t},onFocus:this.handleFocus,onBlur:this.handleBlur,className:"react-toggle-screenreader-only",type:"checkbox"})))}}]),t}(c.PureComponent);t.default=m,m.displayName="Toggle",m.defaultProps={icons:{checked:o.default.createElement(u.default,null),unchecked:o.default.createElement(s.default,null)}},m.propTypes={checked:l.default.bool,disabled:l.default.bool,defaultChecked:l.default.bool,onChange:l.default.func,onFocus:l.default.func,onBlur:l.default.func,className:l.default.string,name:l.default.string,value:l.default.string,id:l.default.string,"aria-labelledby":l.default.string,"aria-label":l.default.string,icons:l.default.oneOfType([l.default.bool,l.default.shape({checked:l.default.node,unchecked:l.default.node})])}},128:function(e,t,n){var a;!function(){"use strict";var n={}.hasOwnProperty;function r(){for(var e=[],t=0;t<arguments.length;t++){var a=arguments[t];if(a){var c=typeof a;if("string"===c||"number"===c)e.push(a);else if(Array.isArray(a)&&a.length){var o=r.apply(null,a);o&&e.push(o)}else if("object"===c)for(var i in a)n.call(a,i)&&a[i]&&e.push(i)}}return e.join(" ")}e.exports?(r.default=r,e.exports=r):void 0===(a=function(){return r}.apply(t,[]))||(e.exports=a)}()},129:function(e,t,n){"use strict";Object.defineProperty(t,"__esModule",{value:!0});var a,r=n(0),c=(a=r)&&a.__esModule?a:{default:a};t.default=function(){return c.default.createElement("svg",{width:"14",height:"11",viewBox:"0 0 14 11"},c.default.createElement("title",null,"switch-check"),c.default.createElement("path",{d:"M11.264 0L5.26 6.004 2.103 2.847 0 4.95l5.26 5.26 8.108-8.107L11.264 0",fill:"#fff",fillRule:"evenodd"}))}},130:function(e,t,n){"use strict";Object.defineProperty(t,"__esModule",{value:!0});var a,r=n(0),c=(a=r)&&a.__esModule?a:{default:a};t.default=function(){return c.default.createElement("svg",{width:"10",height:"10",viewBox:"0 0 10 10"},c.default.createElement("title",null,"switch-x"),c.default.createElement("path",{d:"M9.9 2.12L7.78 0 4.95 2.828 2.12 0 0 2.12l2.83 2.83L0 7.776 2.123 9.9 4.95 7.07 7.78 9.9 9.9 7.776 7.072 4.95 9.9 2.12",fill:"#fff",fillRule:"evenodd"}))}},131:function(e,t,n){"use strict";Object.defineProperty(t,"__esModule",{value:!0}),t.pointerCoord=function(e){if(e){var t=e.changedTouches;if(t&&t.length>0){var n=t[0];return{x:n.clientX,y:n.clientY}}var a=e.pageX;if(void 0!==a)return{x:a,y:e.pageY}}return{x:0,y:0}}},132:function(e,t,n){"use strict";n.r(t),n.d(t,"default",(function(){return s}));var a=n(2),r=n(6),c=n(0),o=n.n(c),i=n(110),l=n(99),u=n(112);function s(e){var t,n=e.label,c=e.to,s=e.docsPluginId,d=Object(r.a)(e,["label","to","docsPluginId"]),f=Object(l.useActiveVersion)(s),m=Object(u.a)(s).preferredVersion,h=Object(l.useLatestVersion)(s),v=null!==(t=null!=f?f:m)&&void 0!==t?t:h,b=null!=n?n:v.label,p=null!=c?c:function(e){return e.docs.find((function(t){return t.id===e.mainDocId}))}(v).path;return o.a.createElement(i.a,Object(a.a)({},d,{label:b,to:p}))}},136:function(e,t,n){"use strict";n.r(t),n.d(t,"default",(function(){return d}));var a=n(2),r=n(6),c=n(0),o=n.n(c),i=n(110),l=n(99),u=n(112),s=function(e){return e.docs.find((function(t){return t.id===e.mainDocId}))};function d(e){var t,n,c=e.mobile,d=e.docsPluginId,f=e.dropdownActiveClassDisabled,m=e.dropdownItemsBefore,h=e.dropdownItemsAfter,v=Object(r.a)(e,["mobile","docsPluginId","dropdownActiveClassDisabled","dropdownItemsBefore","dropdownItemsAfter"]),b=Object(l.useActiveDocContext)(d),p=Object(l.useVersions)(d),g=Object(l.useLatestVersion)(d),O=Object(u.a)(d),k=O.preferredVersion,j=O.savePreferredVersionName;var E=null!==(t=null!==(n=b.activeVersion)&&void 0!==n?n:k)&&void 0!==t?t:g,y=c?"Versions":E.label,w=c?void 0:s(E).path;return o.a.createElement(i.a,Object(a.a)({},v,{mobile:c,label:y,to:w,items:function(){var e=p.map((function(e){var t=(null==b?void 0:b.alternateDocVersions[e.name])||s(e);return{isNavLink:!0,label:e.label,to:t.path,isActive:function(){return e===(null==b?void 0:b.activeVersion)},onClick:function(){j(e.name)}}})),t=[].concat(m,e,h);if(!(t.length<=1))return t}(),isActive:f?function(){return!1}:void 0}))}},137:function(e,t,n){"use strict";n.r(t),n.d(t,"default",(function(){return d}));var a=n(2),r=n(6),c=n(0),o=n.n(c),i=n(110),l=n(99),u=n(87),s=n(112);function d(e){var t,n,c=e.docId,d=e.activeSidebarClassName,f=e.label,m=e.docsPluginId,h=Object(r.a)(e,["docId","activeSidebarClassName","label","docsPluginId"]),v=Object(l.useActiveDocContext)(m),b=v.activeVersion,p=v.activeDoc,g=Object(s.a)(m).preferredVersion,O=Object(l.useLatestVersion)(m),k=null!==(t=null!=b?b:g)&&void 0!==t?t:O,j=k.docs.find((function(e){return e.id===c}));if(!j)throw new Error("DocNavbarItem: couldn't find any doc with id="+c+" in version "+k.name+".\nAvailable docIds=\n- "+k.docs.join("\n- "));return o.a.createElement(i.a,Object(a.a)({exact:!0},h,{className:Object(u.a)(h.className,(n={},n[d]=p&&p.sidebar===j.sidebar,n)),label:null!=f?f:j.id,to:j.path}))}},95:function(e,t,n){"use strict";var a=n(0),r=n(122);t.a=function(){var e=Object(a.useContext)(r.a);if(null==e)throw new Error("`useThemeContext` is used outside of `Layout` Component. See https://v2.docusaurus.io/docs/theme-classic#usethemecontext.");return e}},96:function(e,t,n){"use strict";var a=n(0);t.a=function(e){void 0===e&&(e=!0),Object(a.useEffect)((function(){return document.body.style.overflow=e?"hidden":"visible",function(){document.body.style.overflow="visible"}}),[e])}},97:function(e,t,n){"use strict";n.d(t,"b",(function(){return r}));var a=n(0),r={desktop:"desktop",mobile:"mobile"};t.a=function(){var e="undefined"!=typeof window;function t(){if(e)return window.innerWidth>996?r.desktop:r.mobile}var n=Object(a.useState)(t),c=n[0],o=n[1];return Object(a.useEffect)((function(){if(e)return window.addEventListener("resize",n),function(){return window.removeEventListener("resize",n)};function n(){o(t())}}),[]),c}},98:function(e,t,n){"use strict";var a=n(95),r=n(91),c=n(109),o=n(92);t.a=function(){var e=Object(o.a)().navbar.logo,t=Object(a.a)().isDarkTheme,n=Object(r.a)(e.href||"/"),i={};e.target?i={target:e.target}:Object(c.a)(n)||(i={rel:"noopener noreferrer",target:"_blank"});var l=e.srcDark&&t?e.srcDark:e.src;return{logoLink:n,logoLinkProps:i,logoImageUrl:Object(r.a)(l),logoAlt:e.alt}}}}]);
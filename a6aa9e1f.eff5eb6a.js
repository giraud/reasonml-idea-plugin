(window.webpackJsonp=window.webpackJsonp||[]).push([[27,8],{102:function(e,a,t){e.exports={displayOnlyInLargeViewport:"displayOnlyInLargeViewport_37ZF",hideLogoText:"hideLogoText_3e6b",navbarHideable:"navbarHideable_2ENK",navbarHidden:"navbarHidden_7Uo6"}},103:function(e,a,t){e.exports={copyright:"copyright_9pQs"}},110:function(e,a,t){"use strict";var n=t(2),r=t(0),l=t.n(r),i=t(97),c=t(98),o=t(99),s=t(115),m=t(111),d=t(104),b=t(116),v=t(105),u=t(106),g=t(107),f=t(102),_=t.n(f),E=t(112),p="right";a.a=function(){var e,a,t=Object(o.a)(),f=t.siteConfig.themeConfig,h=f.navbar,N=(h=void 0===h?{}:h).title,k=void 0===N?"":N,O=h.items,j=void 0===O?[]:O,y=h.hideOnScroll,w=void 0!==y&&y,C=h.style,L=void 0===C?void 0:C,x=f.colorMode,T=(x=void 0===x?{}:x).disableSwitch,I=void 0!==T&&T,H=t.isClient,M=Object(r.useState)(!1),S=M[0],B=M[1],D=Object(r.useState)(!1),U=D[0],P=D[1],V=Object(d.a)(),A=V.isDarkTheme,J=V.setLightTheme,K=V.setDarkTheme,F=Object(b.a)(w),Q=F.navbarRef,R=F.isNavbarVisible,W=Object(g.a)(),Z=W.logoLink,q=W.logoLinkProps,z=W.logoImageUrl,G=W.logoAlt;Object(v.a)(S);var X=Object(r.useCallback)((function(){B(!0)}),[B]),Y=Object(r.useCallback)((function(){B(!1)}),[B]),$=Object(r.useCallback)((function(e){return e.target.checked?K():J()}),[J,K]),ee=Object(u.a)();Object(r.useEffect)((function(){ee===u.b.desktop&&B(!1)}),[ee]);var ae=function(e){return{leftItems:e.filter((function(e){var a;return"left"===(null!==(a=e.position)&&void 0!==a?a:p)})),rightItems:e.filter((function(e){var a;return"right"===(null!==(a=e.position)&&void 0!==a?a:p)}))}}(j),te=ae.leftItems,ne=ae.rightItems;return l.a.createElement("nav",{ref:Q,className:Object(i.a)("navbar","navbar--fixed-top",(e={"navbar--dark":"dark"===L,"navbar--primary":"primary"===L,"navbar-sidebar--show":S},e[_.a.navbarHideable]=w,e[_.a.navbarHidden]=!R,e))},l.a.createElement("div",{className:"navbar__inner"},l.a.createElement("div",{className:"navbar__items"},null!=j&&0!==j.length&&l.a.createElement("div",{"aria-label":"Navigation bar toggle",className:"navbar__toggle",role:"button",tabIndex:0,onClick:X,onKeyDown:X},l.a.createElement("svg",{xmlns:"http://www.w3.org/2000/svg",width:"30",height:"30",viewBox:"0 0 30 30",role:"img",focusable:"false"},l.a.createElement("title",null,"Menu"),l.a.createElement("path",{stroke:"currentColor",strokeLinecap:"round",strokeMiterlimit:"10",strokeWidth:"2",d:"M4 7h22M4 15h22M4 23h22"}))),l.a.createElement(c.a,Object(n.a)({className:"navbar__brand",to:Z},q),null!=k&&l.a.createElement("strong",{className:Object(i.a)("navbar__title",(a={},a[_.a.hideLogoText]=U,a))},k)),te.map((function(e,a){return l.a.createElement(E.a,Object(n.a)({},e,{key:a}))}))),l.a.createElement("div",{className:"navbar__items navbar__items--right"},ne.map((function(e,a){return l.a.createElement(E.a,Object(n.a)({},e,{key:a}))})),!I&&l.a.createElement(m.a,{className:_.a.displayOnlyInLargeViewport,"aria-label":"Dark mode toggle",checked:A,onChange:$}),l.a.createElement(s.a,{handleSearchBarToggle:P,isSearchBarExpanded:U}))),l.a.createElement("div",{role:"presentation",className:"navbar-sidebar__backdrop",onClick:Y}),l.a.createElement("div",{className:"navbar-sidebar"},l.a.createElement("div",{className:"navbar-sidebar__brand"},l.a.createElement(c.a,Object(n.a)({className:"navbar__brand",onClick:Y,to:Z},q),null!=z&&l.a.createElement("img",{key:H,className:"navbar__logo",src:z,alt:G}),null!=k&&l.a.createElement("strong",{className:"navbar__title"},k)),!I&&S&&l.a.createElement(m.a,{"aria-label":"Dark mode toggle in sidebar",checked:A,onChange:$})),l.a.createElement("div",{className:"navbar-sidebar__items"},l.a.createElement("div",{className:"menu"},l.a.createElement("ul",{className:"menu__list"},j.map((function(e,a){return l.a.createElement(E.a,Object(n.a)({mobile:!0},e,{onClick:Y,key:a}))})))))))}},113:function(e,a,t){"use strict";var n=t(2),r=t(6),l=t(0),i=t.n(l),c=t(97),o=t(98),s=t(99),m=t(100),d=t(103),b=t.n(d);function v(e){var a=e.to,t=e.href,l=e.label,c=e.prependBaseUrlToHref,s=Object(r.a)(e,["to","href","label","prependBaseUrlToHref"]),d=Object(m.a)(a),b=Object(m.a)(t,{forcePrependBaseUrl:!0});return i.a.createElement(o.a,Object(n.a)({className:"footer__link-item"},t?{target:"_blank",rel:"noopener noreferrer",href:c?b:t}:{to:d},s),l)}var u=function(e){var a=e.url,t=e.alt;return i.a.createElement("img",{className:"footer__logo",alt:t,src:a})};a.a=function(){var e=Object(s.a)().siteConfig,a=(void 0===e?{}:e).themeConfig,t=(void 0===a?{}:a).footer,n=t||{},r=n.copyright,l=n.links,o=void 0===l?[]:l,d=n.logo,g=void 0===d?{}:d,f=Object(m.a)(g.src);return t?i.a.createElement("footer",{className:Object(c.a)("footer",b.a.footer)},i.a.createElement("div",{className:"container"},o&&o.length>0&&i.a.createElement("div",{className:"row footer__links"},o.map((function(e,a){return i.a.createElement("div",{key:a,className:"col footer__col"},null!=e.title?i.a.createElement("h4",{className:"footer__title"},e.title):null,null!=e.items&&Array.isArray(e.items)&&e.items.length>0?i.a.createElement("ul",{className:"footer__items"},e.items.map((function(e,a){return e.html?i.a.createElement("li",{key:a,className:"footer__item",dangerouslySetInnerHTML:{__html:e.html}}):i.a.createElement("li",{key:e.href||e.to,className:"footer__item"},i.a.createElement(v,e))}))):null)}))),(g||r)&&i.a.createElement("div",null,g&&g.src&&i.a.createElement("div",{className:"margin-bottom--sm"},g.href?i.a.createElement("a",{href:g.href,target:"_blank",rel:"noopener noreferrer",className:b.a.footerLogoLink},i.a.createElement(u,{alt:g.alt,url:f})):i.a.createElement(u,{alt:g.alt,url:f})),i.a.createElement("div",{className:Object(c.a)(b.a.copyright),dangerouslySetInnerHTML:{__html:r}})))):null}},93:function(e,a,t){"use strict";t.r(a);var n=t(0),r=t.n(n),l=t(99),i=t(109),c=t(125),o=t(98);var s=function(e){var a=e.metadata,t=a.previousPage,n=a.nextPage;return r.a.createElement("nav",{className:"pagination-nav","aria-label":"Blog list page navigation"},r.a.createElement("div",{className:"pagination-nav__item"},t&&r.a.createElement(o.a,{className:"pagination-nav__link",to:t},r.a.createElement("h4",{className:"pagination-nav__label"},"\xab Newer Entries"))),r.a.createElement("div",{className:"pagination-nav__item pagination-nav__item--next"},n&&r.a.createElement(o.a,{className:"pagination-nav__link",to:n},r.a.createElement("h4",{className:"pagination-nav__label"},"Older Entries \xbb"))))},m=t(118);a.default=function(e){var a=e.metadata,t=e.items,n=e.sidebar,o=Object(l.a)().siteConfig.title,d=a.blogDescription,b=a.blogTitle,v="/"===a.permalink?o:b;return r.a.createElement(i.a,{title:v,description:d},r.a.createElement("div",{className:"container margin-vert--lg"},r.a.createElement("div",{className:"row"},r.a.createElement("div",{className:"col col--2"},r.a.createElement(m.a,{sidebar:n})),r.a.createElement("main",{className:"col col--8"},t.map((function(e){var a=e.content;return r.a.createElement(c.a,{key:a.metadata.permalink,frontMatter:a.frontMatter,metadata:a.metadata,truncated:a.metadata.truncated},r.a.createElement(a,null))})),r.a.createElement(s,{metadata:a})))))}}}]);
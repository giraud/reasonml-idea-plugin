(window.webpackJsonp=window.webpackJsonp||[]).push([[8],{104:function(e,a,t){e.exports={displayOnlyInLargeViewport:"displayOnlyInLargeViewport_37ZF",hideLogoText:"hideLogoText_3e6b",navbarHideable:"navbarHideable_2ENK",navbarHidden:"navbarHidden_7Uo6"}},105:function(e,a,t){e.exports={copyright:"copyright_9pQs"}},112:function(e,a,t){"use strict";var r=t(2),n=t(0),l=t.n(n),o=t(99),i=t(100),c=t(101),s=t(117),m=t(113),b=t(106),d=t(118),v=t(107),u=t(108),f=t(109),g=t(104),_=t.n(g),h=t(114),E="right";a.a=function(){var e,a,t=Object(c.a)(),g=t.siteConfig.themeConfig,p=g.navbar,k=(p=void 0===p?{}:p).title,N=void 0===k?"":k,O=p.items,j=void 0===O?[]:O,y=p.hideOnScroll,w=void 0!==y&&y,C=p.style,L=void 0===C?void 0:C,I=g.colorMode,T=(I=void 0===I?{}:I).disableSwitch,x=void 0!==T&&T,H=t.isClient,M=Object(n.useState)(!1),S=M[0],B=M[1],D=Object(n.useState)(!1),U=D[0],V=D[1],A=Object(b.a)(),J=A.isDarkTheme,K=A.setLightTheme,P=A.setDarkTheme,F=Object(d.a)(w),Q=F.navbarRef,R=F.isNavbarVisible,W=Object(f.a)(),Z=W.logoLink,q=W.logoLinkProps,z=W.logoImageUrl,G=W.logoAlt;Object(v.a)(S);var X=Object(n.useCallback)((function(){B(!0)}),[B]),Y=Object(n.useCallback)((function(){B(!1)}),[B]),$=Object(n.useCallback)((function(e){return e.target.checked?P():K()}),[K,P]),ee=Object(u.a)();Object(n.useEffect)((function(){ee===u.b.desktop&&B(!1)}),[ee]);var ae=function(e){return{leftItems:e.filter((function(e){var a;return"left"===(null!==(a=e.position)&&void 0!==a?a:E)})),rightItems:e.filter((function(e){var a;return"right"===(null!==(a=e.position)&&void 0!==a?a:E)}))}}(j),te=ae.leftItems,re=ae.rightItems;return l.a.createElement("nav",{ref:Q,className:Object(o.a)("navbar","navbar--fixed-top",(e={"navbar--dark":"dark"===L,"navbar--primary":"primary"===L,"navbar-sidebar--show":S},e[_.a.navbarHideable]=w,e[_.a.navbarHidden]=!R,e))},l.a.createElement("div",{className:"navbar__inner"},l.a.createElement("div",{className:"navbar__items"},null!=j&&0!==j.length&&l.a.createElement("div",{"aria-label":"Navigation bar toggle",className:"navbar__toggle",role:"button",tabIndex:0,onClick:X,onKeyDown:X},l.a.createElement("svg",{xmlns:"http://www.w3.org/2000/svg",width:"30",height:"30",viewBox:"0 0 30 30",role:"img",focusable:"false"},l.a.createElement("title",null,"Menu"),l.a.createElement("path",{stroke:"currentColor",strokeLinecap:"round",strokeMiterlimit:"10",strokeWidth:"2",d:"M4 7h22M4 15h22M4 23h22"}))),l.a.createElement(i.a,Object(r.a)({className:"navbar__brand",to:Z},q),null!=N&&l.a.createElement("strong",{className:Object(o.a)("navbar__title",(a={},a[_.a.hideLogoText]=U,a))},N)),te.map((function(e,a){return l.a.createElement(h.a,Object(r.a)({},e,{key:a}))}))),l.a.createElement("div",{className:"navbar__items navbar__items--right"},re.map((function(e,a){return l.a.createElement(h.a,Object(r.a)({},e,{key:a}))})),!x&&l.a.createElement(m.a,{className:_.a.displayOnlyInLargeViewport,"aria-label":"Dark mode toggle",checked:J,onChange:$}),l.a.createElement(s.a,{handleSearchBarToggle:V,isSearchBarExpanded:U}))),l.a.createElement("div",{role:"presentation",className:"navbar-sidebar__backdrop",onClick:Y}),l.a.createElement("div",{className:"navbar-sidebar"},l.a.createElement("div",{className:"navbar-sidebar__brand"},l.a.createElement(i.a,Object(r.a)({className:"navbar__brand",onClick:Y,to:Z},q),null!=z&&l.a.createElement("img",{key:H,className:"navbar__logo",src:z,alt:G}),null!=N&&l.a.createElement("strong",{className:"navbar__title"},N)),!x&&S&&l.a.createElement(m.a,{"aria-label":"Dark mode toggle in sidebar",checked:J,onChange:$})),l.a.createElement("div",{className:"navbar-sidebar__items"},l.a.createElement("div",{className:"menu"},l.a.createElement("ul",{className:"menu__list"},j.map((function(e,a){return l.a.createElement(h.a,Object(r.a)({mobile:!0},e,{onClick:Y,key:a}))})))))))}},115:function(e,a,t){"use strict";var r=t(2),n=t(6),l=t(0),o=t.n(l),i=t(99),c=t(100),s=t(101),m=t(102),b=t(105),d=t.n(b);function v(e){var a=e.to,t=e.href,l=e.label,i=e.prependBaseUrlToHref,s=Object(n.a)(e,["to","href","label","prependBaseUrlToHref"]),b=Object(m.a)(a),d=Object(m.a)(t,{forcePrependBaseUrl:!0});return o.a.createElement(c.a,Object(r.a)({className:"footer__link-item"},t?{target:"_blank",rel:"noopener noreferrer",href:i?d:t}:{to:b},s),l)}var u=function(e){var a=e.url,t=e.alt;return o.a.createElement("img",{className:"footer__logo",alt:t,src:a})};a.a=function(){var e=Object(s.a)().siteConfig,a=(void 0===e?{}:e).themeConfig,t=(void 0===a?{}:a).footer,r=t||{},n=r.copyright,l=r.links,c=void 0===l?[]:l,b=r.logo,f=void 0===b?{}:b,g=Object(m.a)(f.src);return t?o.a.createElement("footer",{className:Object(i.a)("footer",d.a.footer)},o.a.createElement("div",{className:"container"},c&&c.length>0&&o.a.createElement("div",{className:"row footer__links"},c.map((function(e,a){return o.a.createElement("div",{key:a,className:"col footer__col"},null!=e.title?o.a.createElement("h4",{className:"footer__title"},e.title):null,null!=e.items&&Array.isArray(e.items)&&e.items.length>0?o.a.createElement("ul",{className:"footer__items"},e.items.map((function(e,a){return e.html?o.a.createElement("li",{key:a,className:"footer__item",dangerouslySetInnerHTML:{__html:e.html}}):o.a.createElement("li",{key:e.href||e.to,className:"footer__item"},o.a.createElement(v,e))}))):null)}))),(f||n)&&o.a.createElement("div",null,f&&f.src&&o.a.createElement("div",{className:"margin-bottom--sm"},f.href?o.a.createElement("a",{href:f.href,target:"_blank",rel:"noopener noreferrer",className:d.a.footerLogoLink},o.a.createElement(u,{alt:f.alt,url:g})):o.a.createElement(u,{alt:f.alt,url:g})),o.a.createElement("div",{className:Object(i.a)(d.a.copyright),dangerouslySetInnerHTML:{__html:n}})))):null}}}]);
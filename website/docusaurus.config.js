// @ts-check
// Note: type annotations allow type checking and IDEs autocompletion

const {themes} = require('prism-react-renderer');
const lightCodeTheme = themes.github;
const darkCodeTheme = themes.dracula;

/** @type {import('@docusaurus/types').Config} */
const config = {
    title: 'Reason IDEA Plugin',
    tagline: 'Language Plugin for <code>OCaml</code>, <code>Reason</code> and <code>ReScript</code>',
    url: 'https://reasonml-idea-plugin.github.io/',
    baseUrl: '/reasonml-idea-plugin/',
    onBrokenLinks: 'throw',
    onBrokenMarkdownLinks: 'warn',
    favicon: 'img/favicon.svg',

    // GitHub pages deployment config.
    organizationName: 'giraud',
    projectName: 'reasonml-idea-plugin',

    // Even if you don't use internalization, you can use this field to set useful metadata like html lang.
    i18n: {
        defaultLocale: 'en',
        locales: ['en'],
    },

    presets: [
        [
            'classic', /** @type {import('@docusaurus/preset-classic').Options} */
            ({
                docs: {
                    sidebarPath: require.resolve('./sidebars.js'),
                    editUrl: 'https://github.com/giraud/reasonml-idea-plugin/edit/master/website/',
                },
                theme: {
                    customCss: require.resolve('./src/css/custom.css'),
                },
                gtag: {
                    trackingID: 'G-HN9MJXSN5F',
                    anonymizeIP: true,
                },
            }),
        ],
    ],

    themeConfig: /** @type {import('@docusaurus/preset-classic').ThemeConfig}  */
        ({
            navbar: {
                title: 'Reason IDEA Plugin',
                logo: {
                    alt: 'Reason IDEA Plugin Logo',
                    src: 'img/logo.svg',
                },
                items: [
                    {
                        type: 'doc',
                        docId: 'intro',
                        position: 'left',
                        label: 'Documentation',
                    }, {
                        href: 'https://github.com/giraud/reasonml-idea-plugin',
                        label: 'GitHub',
                        position: 'right',
                    }
                ],
            },
            footer: {
                style: 'dark',
                links: [
                    {
                        title: 'Docs',
                        items: [
                            {
                                label: 'Get Started',
                                to: 'docs/intro',
                            }, {
                                label: 'How to Contribute',
                                to: 'docs/contributing/how-to-contribute',
                            },
                        ],
                    }, {
                        title: 'Community',
                        items: [
                            {
                                label: 'Our Discord',
                                href: 'https://discord.gg/65fz5jb',
                            }, {
                                label: 'OCaml',
                                href: 'https://ocaml.org/',
                            }, {
                                label: 'ReasonML',
                                href: 'https://reasonml.github.io/',
                            }, {
                                label: 'ReScript',
                                href: 'https://rescript-lang.org/',
                            },
                        ],
                    }, {
                        title: 'More',
                        items: [
                            {
                                label: 'GitHub',
                                href: 'https://github.com/giraud/reasonml-idea-plugin',
                            },
                        ],
                    },
                ],
                copyright: `Copyright © ${new Date().getFullYear()} Reason IDEA Plugin`,
            },
            prism: {
                theme: lightCodeTheme,
                darkTheme: darkCodeTheme,
            },
        }),
};

module.exports = config;

module.exports = {
  title: 'Reason IDEA Plugin',
  tagline: 'Language Plugin for <code>OCaml</code>, <code>Reason</code> and' +
    ' <code>ReScript</code>',
  url: 'https://reasonml-idea-plugin.github.io',
  baseUrl: '/reasonml-idea-plugin/',
  onBrokenLinks: 'throw',
  favicon: 'img/favicon.svg',
  organizationName: 'reasonml-editor',
  projectName: 'reasonml-idea-plugin',
  themeConfig: {
    colorMode: {
      defaultMode: 'dark',
      respectPrefersColorScheme: true,
    },
    navbar: {
      title: 'Reason IDEA Plugin',
      logo: {
        alt: 'Reason IDEA Plugin Logo',
        src: 'img/logo.svg',
      },
      items: [
        {
          to: 'docs/',
          label: 'Documentation',
          activeBaseRegex: 'docs/?$',
          position: 'left',
        },
        {
          to: 'docs/contribute',
          activeBasePath: 'docs/contribute',
          label: 'Contribute',
          position: 'left',
        },
        {
          to: 'blog',
          label: 'Latest Updates',
          position: 'left'
        },
        {
          href: 'https://github.com/reasonml-editor/reasonml-idea-plugin',
          label: 'GitHub',
          position: 'right',
        },
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
              to: 'docs/',
            },
            {
              label: 'Contribute',
              to: 'docs/',
            },
          ],
        },
        {
          title: 'Community',
          items: [
            {
              label: 'Our Discord',
              href: 'https://discordapp.com/invite/5dzVqJAW',
            },
            {
              label: 'OCaml',
              href: 'https://ocaml.org/',
            },
            {
              label: 'ReasonML',
              href: 'https://reasonml.github.io/',
            },
            {
              label: 'ReScript',
              href: 'https://rescript-lang.org/',
            },
          ],
        },
        {
          title: 'More',
          items: [
            {
              label: 'Blog',
              to: 'blog',
            },
            {
              label: 'GitHub',
              href: 'https://github.com/facebook/docusaurus',
            },
          ],
        },
      ],
      copyright: `Copyright © ${new Date().getFullYear()} Reason IDEA Plugin`,
    },
  },
  plugins: ["docusaurus-plugin-sass", "@docusaurus/plugin-ideal-image"],
  presets: [
    [
      '@docusaurus/preset-classic',
      {
        docs: {
          sidebarPath: require.resolve('./sidebars.js'),
          // Please change this to your repo.
          editUrl:
            'https://github.com/facebook/docusaurus/edit/master/website/',
        },
        blog: {
          showReadingTime: true,
          // Please change this to your repo.
          editUrl:
            'https://github.com/facebook/docusaurus/edit/master/website/blog/',
        },
        theme: {
          customCss: require.resolve('./src/css/custom.scss'),
        },
      },
    ],
  ],
};

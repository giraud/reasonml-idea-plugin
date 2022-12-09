import React from 'react';
import clsx from 'clsx';
import Link from '@docusaurus/Link';
import useBaseUrl from "@docusaurus/useBaseUrl";
import useDocusaurusContext from '@docusaurus/useDocusaurusContext';
import Layout from '@theme/Layout';
import HomepageFeatures from '@site/src/components/HomepageFeatures';

import styles from './index.module.css';

function HomepageHeader() {
    const {siteConfig} = useDocusaurusContext();
    return (<header className={clsx('hero hero--primary', styles.heroBanner)}>
            <div className="container">
                <img src={useBaseUrl("/img/logo.svg")} alt="Logo" className={styles.logo}/>
                <h1 className="hero__title">{siteConfig.title}</h1>
                <p className={clsx("hero__subtitle", styles.subtitle)} dangerouslySetInnerHTML={{__html: siteConfig.tagline}}/>

                <div className={styles.badges}>
                    <a href=""><img alt="Build Status" src="https://github.com/giraud/reasonml-idea-plugin/workflows/Build%20Status/badge.svg"/></a>
                    <a href="https://plugins.jetbrains.com/plugin/9440-reasonml">
                        <img alt="JetBrains IntelliJ plugins" src="https://img.shields.io/jetbrains/plugin/d/9440-reasonml.svg"/>
                    </a>
                    <a href="https://discord.gg/65fz5jb">
                        <img alt="Discord" src="https://img.shields.io/discord/713777184996589580"/>
                    </a>
                    <a href="https://opensource.org/licenses/MIT">
                        <img alt="License: MIT" src="https://img.shields.io/badge/License-MIT-yellow.svg"/>
                    </a>
                </div>

                <div className={styles.buttons}>
                    <Link className="button button--secondary button--lg" to="https://plugins.jetbrains.com/plugin/9440-reasonml">Download</Link>
                    <Link className="button button--secondary button--lg" to="/docs/intro">Get Started</Link>
                </div>

            </div>
        </header>);
}

export default function Home() {
    return (//
        <Layout description="Description will go into a meta tag in <head />">
            <HomepageHeader/>
            <main>
                <HomepageFeatures/>
            </main>
        </Layout>);
}

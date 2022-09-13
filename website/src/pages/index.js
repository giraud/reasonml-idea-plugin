import React from 'react';
import clsx from 'clsx';
import Layout from '@theme/Layout';
import useDocusaurusContext from '@docusaurus/useDocusaurusContext';
import useBaseUrl from '@docusaurus/useBaseUrl';
import styles from './styles.module.scss';

import Hero from '@theme/Hero';

const features = [
  {
    title: 'Language Support',
    imageUrl: 'img/syntax-all.png',
    placement: 'left',
    description: (
      <>
        The Reason IDEA Plugin adds full language support for <code>OCaml</code>, <code>Reason</code>, and <code>ReScript</code>.
        Language support features include <b>code formatting</b>, <b>type annotations</b>, <b>structured code views</b>, and more.
        Minimal support for <b>Dune</b> and <b>JSX</b> is included as well.
      </>
    ),
  },
  {
    title: 'Build Tools',
    imageUrl: 'img/build-tools.png',
    placement: 'right',
    description: (
      <>
        The plugin integrates with common build tools such as <b>Dune</b>, <b>Esy</b>, and <b>BuckleScript</b>.
        These are useful for installing dependencies and building your project without leaving your IDE.
      </>
    ),
  },
];

function Feature({imageUrl, title, placement, description}) {
  const imgUrl = useBaseUrl(imageUrl);
  return (
    <div className={clsx(styles.feature, {[styles.reverse]: placement === 'right'})}>
      {imgUrl && (
        <div>
          <img className={styles.featureImage} src={imgUrl} alt={title} />
        </div>
      )}
      <div>
        <h3>{title}</h3>
        <p>{description}</p>
      </div>
    </div>
  );
}

function Home() {
  return (
    <Layout
      description="Description will go into a meta tag in <head />">
      <Hero />
      <main>
        {features && features.length > 0 && (
          <section className={styles.features}>
            <div className="container">
              <div className="row">
                {features.map((props, idx) => (
                  <Feature key={idx} {...props} />
                ))}
              </div>
            </div>
          </section>
        )}
      </main>
    </Layout>
  );
}

export default Home;

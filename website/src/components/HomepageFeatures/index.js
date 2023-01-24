import React from 'react';
import clsx from 'clsx';
import useBaseUrl from '@docusaurus/useBaseUrl';

import styles from './styles.module.css';

const FeatureList = [
    {
        title:       'Language Support',
        imageURL:    'img/syntax-all.png',
        description: (<>
            The Reason IDEA Plugin adds full language support for <code>OCaml</code>, <code>Reason</code>, and <code>ReScript</code>.
            Language support features include <b>code formatting</b>, <b>type annotations</b>, <b>structured code views</b>, and more.
            Minimal support for <b>Dune</b> and <b>JSX</b> is included as well.
        </>),
    }, {
        title:       'Build Tools',
        imageURL:    'img/build-tools.png',
        description: (<>
            The plugin integrates with common build tools such as <b>Dune</b>, <b>Esy</b>, and <b>BuckleScript</b>.
            These are useful for installing dependencies and building your project without leaving your IDE.
        </>),
    },{
        title:       'Built on idea',
        imageURL:    'img/structure.png',
        description: (<>
            The plugin is built on Idea and is not using LSP so you can get all the functionalities you can expect
            for any IntelliJ product. It is compatible with a large number of editors (Idea, Webstorm, CLion, ...).
        </>),
    },
];

function Feature({
                     imageURL,
                     title,
                     description
                 }) {
    const imgUrl = useBaseUrl(imageURL);
    return (//
        <div className={clsx('col col--4')}>
            <div className="text--center">
                <img className={styles.featureImage} src={imgUrl} alt={title}/>
            </div>
            <div className="text--center padding-horiz--md">
                <h3>{title}</h3>
                <p>{description}</p>
            </div>
        </div>);
}

export default function HomepageFeatures() {
    return (//
        <section className={styles.features}>
            <div className="container">
                <div className="row">
                    {FeatureList.map((props, idx) => (<Feature key={idx} {...props} />))}
                </div>
            </div>
        </section>);
}

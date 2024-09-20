/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';
import {createRoot} from 'react-dom/client';
import {BrowserRouter, Link, Route, Routes} from "react-router-dom";

import api from './common/services/liferay/api.js';
import {Liferay} from './common/services/liferay/liferay.js';
import HelloBar from './routes/hello-bar/pages/HelloBar.js';
import HelloFoo from './routes/hello-foo/pages/HelloFoo.js';
import HelloWorld from './routes/hello-world/pages/HelloWorld.js';

import './common/styles/index.scss';

const App = () => {
	return (
		<div>
			<HelloWorld />

			{Liferay.ThemeDisplay.isSignedIn() && (
				<div>
					<h2>Logged In!</h2>
				</div>
			)}
		</div>
	);
};

class WebComponent extends HTMLElement {
	connectedCallback() {
		this.root = createRoot(this);

		const basename = window.location.pathname == '/' ? window.location.pathname : Liferay.ThemeDisplay.getLayoutRelativeURL();
		const layoutRelativeURL = Liferay.ThemeDisplay.getLayoutRelativeURL();

		this.root.render(
			<BrowserRouter basename={basename}>
				<nav>
					<ul>
						<li>
							<Link to={`${layoutRelativeURL}`}>Main</Link>
						</li>
						<li>
							<Link to={`${layoutRelativeURL}/-/${ELEMENT_ID}/bar`}>Bar</Link>
						</li>
						<li>
							<Link to={`${layoutRelativeURL}/-/${ELEMENT_ID}/foo`}>Foo</Link>
						</li>
					</ul>
				</nav>

				<Routes>
					<Route path={`${layoutRelativeURL}/-/${ELEMENT_ID}/bar`} element={<HelloBar />} />
					<Route path={`${layoutRelativeURL}/-/${ELEMENT_ID}/foo`} element={<HelloFoo />} />
					<Route path={`${layoutRelativeURL}`} element={<App />} />
					<Route path='/' element={<App />} />
				</Routes>
			</BrowserRouter>
			, this);

		if (Liferay.ThemeDisplay.isSignedIn()) {
			api('o/headless-admin-user/v1.0/my-user-account')
				.then((response) => response.json())
				.then((response) => {
					if (response.givenName) {
						const nameElements =
							document.getElementsByClassName('hello-world-name');

						if (nameElements.length) {
							nameElements[0].innerHTML = response.givenName;
						}
					}
				})
				.catch((error) => {

					// eslint-disable-next-line no-console
					console.log(error);
				});
		}
	}

	disconnectedCallback() {

		//
		// Unmount React tree to prevent memory leaks.
		//
		// See React documentation at
		//
		//     https://react.dev/reference/react-dom/client/createRoot#root-unmount
		//
		// for more information.
		//

		this.root.unmount();
		delete this.root;
	}
}

const ELEMENT_ID = 'liferay-sample-custom-element-2';

if (!customElements.get(ELEMENT_ID)) {
	customElements.define(ELEMENT_ID, WebComponent);
}

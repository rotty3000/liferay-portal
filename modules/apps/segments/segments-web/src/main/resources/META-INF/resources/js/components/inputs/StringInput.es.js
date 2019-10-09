/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

import {ClaySelectWithOption} from '@clayui/form';
import propTypes from 'prop-types';
import React from 'react';

class StringInput extends React.Component {
	static propTypes = {
		disabled: propTypes.bool,
		onChange: propTypes.func.isRequired,
		options: propTypes.array,
		selectEntity: propTypes.shape({
			id: propTypes.string,
			multiple: propTypes.bool,
			title: propTypes.string,
			uri: propTypes.string
		}),
		value: propTypes.oneOfType([propTypes.string, propTypes.number])
	};

	static defaultProps = {
		options: []
	};

	_handleChange = event => {
		this.props.onChange({value: event.target.value});
	};

	/**
	 * Opens a modal for selecting entities. Uses different methods for
	 * selecting multiple entities versus single because of the way the event
	 * and data is submitted.
	 */
	_handleSelectEntity = () => {
		const {
			onChange,
			selectEntity: {id, multiple, title, uri}
		} = this.props;

		if (multiple) {
			AUI().use('liferay-item-selector-dialog', A => {
				const itemSelectorDialog = new A.LiferayItemSelectorDialog({
					eventName: id,
					on: {
						selectedItemChange: event => {
							const newVal = event.newVal;

							if (newVal) {
								const selectedValues = event.newVal.map(
									item => ({
										displayValue: item.name,
										value: item.id
									})
								);

								onChange(selectedValues);
							}
						}
					},
					strings: {
						add: Liferay.Language.get('select'),
						cancel: Liferay.Language.get('cancel')
					},
					title,
					url: uri
				});

				itemSelectorDialog.open();
			});
		} else {
			Liferay.Util.selectEntity(
				{
					dialog: {
						constrain: true,
						destroyOnHide: true,
						modal: true
					},
					id,
					title,
					uri
				},
				event => {
					onChange({
						displayValue: event.entityname,
						value: event.entityid
					});
				}
			);
		}
	};

	render() {
		const {disabled, options, value} = this.props;

		return options.length === 0 ? (
			<div className="criterion-input input-group">
				<input
					className="form-control"
					data-testid="simple-string"
					disabled={disabled}
					onChange={this._handleChange}
					placeholder={Liferay.Language.get('value')}
					type="text"
					value={value}
				/>
				<div className="input-group-append">
					<button
						className="btn btn-secondary"
						id="button-addon1"
						onClick={this._handleSelectEntity}
						type="button"
					>
						{Liferay.Language.get('select')}
					</button>
				</div>
			</div>
		) : (
			<ClaySelectWithOption
				className="criterion-input form-control"
				data-testid="options-string"
				disabled={disabled}
				onChange={this._handleChange}
				options={options.map(o => ({
					label: o.label,
					value: o.value
				}))}
				value={value}
			/>
		);
	}
}

export default StringInput;

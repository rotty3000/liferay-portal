define(['exports'], function (exports) {
	'use strict';

	Liferay.Charlie = {
		getCountries: function getCountries(callback) {
			Liferay.Service('/country/get-countries', {
				active: true
			}, callback);
		},

		getRegions: function getRegions(callback, selectKey) {
			Liferay.Service('/region/get-regions', {
				active: true,
				countryId: Number(selectKey)
			}, callback);
		}
	};
});
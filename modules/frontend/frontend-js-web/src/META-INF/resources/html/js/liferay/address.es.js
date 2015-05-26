define('liferay/html/js/liferay/address.es', ['exports'], function(exports) {
	exports.Address = {
		getCountries: function(callback) {
			Liferay.Service(
				'/country/get-countries',
				{
					active: true
				},
				callback
			);
		},

		getRegions: function(callback, selectKey) {
			Liferay.Service(
				'/region/get-regions',
				{
					active: true,
					countryId: Number(selectKey)
				},
				callback
			);
		}
	};
});

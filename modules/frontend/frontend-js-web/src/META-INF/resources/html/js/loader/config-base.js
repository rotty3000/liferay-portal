(function() {
    var LiferayAUI = Liferay.AUI;

    var COMBINE = LiferayAUI.getCombine();

    window.__CONFIG__ = {
        basePath: '/o/frontend-js-web/',
        combine: COMBINE,
        url: LiferayAUI.getComboPath()
    };
}());
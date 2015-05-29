var LiferayAUI = Liferay.AUI;

var COMBINE = LiferayAUI.getCombine();

var __CONFIG__ = {
    url: LiferayAUI.getComboPath(),
    basePath: '',
    combine: COMBINE
};
__CONFIG__.modules = {
    "/desktop/nate-cavanaugh": {
        "dependencies": ["dep1", "dep2"],
        "path": "/desktop/NATE_CAVANAUGH.es.js"
    }
};
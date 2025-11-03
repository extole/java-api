(function (global) {
    "use strict";

    var ModuleManager = function () {

        var loadingModules = [];
        var definedModules = {};

        var getAnonymousModuleUri = (function () {
            var unique = 0;
            return function () {
                var currentlyLoadingDependency = loadingModules[loadingModules.length - 1];
                if (currentlyLoadingDependency) {
                    return currentlyLoadingDependency;
                }
                return "anonymous_" + unique++;
            };
        })();

        this.requireSync = function (uri) {
            uri = global.ExtoleJavascriptLibraryLoader.getNormalizedUri(uri);
            if (loadingModules.indexOf(uri) > -1 ) {
                throw new Error("Circular reference in dependency stack: " + loadingModules);
            }

            loadingModules.push(uri);

            var name = global.ExtoleJavascriptLibraryLoader.getNormalizedName(uri);
            if (!definedModules[name]) {
                loadAsset(uri);
            }

            loadingModules.pop(uri);

            if (!definedModules[name]) {
                throw new Error("Failed to resolve dependency: " + uri + " (" + name + "), resolved dependencies: " +
                    Object.keys(definedModules) + ", dependency stack: " + loadingModules);
            }

            return definedModules[name];
        };

        this.defineSync = function (uri, module) {
            if (!uri) {
                uri = getAnonymousModuleUri();
            }
            uri = global.ExtoleJavascriptLibraryLoader.getNormalizedUri(uri);
            var name = global.ExtoleJavascriptLibraryLoader.getNormalizedName(uri);
            definedModules[name] = module;
            return module;
        };

        function loadAsset(asset) {
            global.load({
                script: global.ExtoleJavascriptLibraryLoader.get(asset),
                name: asset
            });
        }

    };

    var moduleManager = new ModuleManager();

    var require = function (dependencyUris, callback) {
        var dependencies = dependencyUris.map(function (uri) {
            return moduleManager.requireSync(uri);
        });
        // Returning the result is a convenience for Java interactions, not meant to be used by creatives.
        return callback.apply(global, dependencies);
    };

    var define = function (moduleUri, dependencyUris, defineModule) {
        if (Array.isArray(moduleUri)) {
            defineModule = dependencyUris;
            dependencyUris = moduleUri;
            moduleUri = null;
        }

        var dependencies = dependencyUris.map(function (uri) {
            return moduleManager.requireSync(uri);
        });
        // Returning the result is a convenience for Java interactions, not meant to be used by creatives.
        return moduleManager.defineSync(moduleUri, defineModule.apply(global, dependencies));
    };

    global.extole = global.extole || {};
    global.extole.require = require;
    global.extole.define = define;

})(this);

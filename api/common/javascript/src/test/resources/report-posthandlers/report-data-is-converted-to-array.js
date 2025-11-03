(function () {
    function nashornToJs(nashornCollection) {
        var jsObject;
        var isArray = Array.isArray(nashornCollection) || nashornCollection instanceof Java.type("java.util.ArrayList");
        if (typeof nashornCollection === 'string' ||
            typeof nashornCollection === 'number' ||
            typeof nashornCollection === 'boolean' ||
            typeof nashornCollection === 'undefined' ||
            nashornCollection === null) {
            return nashornCollection;
        } else if (isArray) {
            jsObject = [];
            each(nashornCollection, function (item, idx) {
                jsObject[idx] = nashornToJs(item);
            });
        } else {
            jsObject = {};
            for (var key in nashornCollection) {
                jsObject[key] = nashornToJs(nashornCollection[key]);
            }
        }
        return jsObject;
    }

    function each(collection, iteratee) {
        var response;
        var i;
        var key;
        var object;
        var isArray = Array.isArray(collection) || collection instanceof Java.type("java.util.ArrayList");
        if (isArray) {
            for (i = 0; i < collection.length; i++) {
                response = iteratee(collection[i], i);
                if (response === false) {
                    return false;
                }
            }
        } else {
            object = collection;
            for (key in object) {
                response = iteratee(object[key], key);
                if (response === false) {
                    return false;
                }
            }
        }
    }

    var reportData = context.getReport();
    var data = Java.from(reportData);
    return nashornToJs(data);
})();

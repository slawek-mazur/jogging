(function () {
    'use strict';
    angular
        .module('jogging')
        .factory('Run', Run);

    Run.$inject = ['$resource', 'DateUtils'];

    function Run($resource, DateUtils) {
        var resourceUrl = 'runs/:id';

        return $resource(resourceUrl, {}, {
            'query': {method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.day = DateUtils.convertDateTimeFromServer(data.day);
                    }
                    return data;
                }
            },
            'update': {method: 'PUT'}
        });
    }
})();

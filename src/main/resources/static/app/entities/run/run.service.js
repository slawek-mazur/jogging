(function() {
    'use strict';
    angular
        .module('myJoggingApp')
        .factory('Run', Run);

    Run.$inject = ['$resource', 'DateUtils'];

    function Run ($resource, DateUtils) {
        var resourceUrl =  'api/runs/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
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
            'update': { method:'PUT' }
        });
    }
})();

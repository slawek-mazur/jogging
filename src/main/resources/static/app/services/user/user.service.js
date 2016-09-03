(function () {
    'use strict';

    angular
        .module('jogging')
        .factory('User', User);

    User.$inject = ['$resource'];

    function User($resource) {
        var resourceUrl = 'users/:id';

        return $resource(resourceUrl, {}, {
            'query': {method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    return data;
                }
            },
            'save': {method: 'POST'},
            'update': {method: 'PUT'},
            'delete': {method: 'DELETE'}
        });
    }
})();

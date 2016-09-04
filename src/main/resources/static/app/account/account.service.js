(function () {
    'use strict';

    angular
        .module('jogging')
        .factory('Account', Account);

    Account.$inject = ['$resource'];

    function Account($resource) {
        return $resource('account/info', {}, {
            'get': {
                method: 'GET', params: {}, isArray: false,
                interceptor: {
                    response: function (response) {
                        return response;
                    }
                }
            }
        });
    }
})();

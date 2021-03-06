(function () {
    'use strict';

    angular
        .module('jogging')
        .factory('errorHandlerInterceptor', errorHandlerInterceptor);

    errorHandlerInterceptor.$inject = ['$q', '$rootScope'];

    function errorHandlerInterceptor($q, $rootScope) {
        return {
            responseError: responseError
        };

        function responseError(response) {
            if (!(response.status === 401 && (response.data === '' || (response.data.path && response.data.path.indexOf('/api/account') === 0 )))) {
                $rootScope.$emit('jogging.httpError', response);
            }
            return $q.reject(response);
        }
    }
})();

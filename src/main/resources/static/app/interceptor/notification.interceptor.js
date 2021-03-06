(function () {
    'use strict';

    angular
        .module('jogging')
        .factory('notificationInterceptor', notificationInterceptor);

    notificationInterceptor.$inject = ['$q', 'AlertService'];

    function notificationInterceptor($q, AlertService) {
        return {
            response: response
        };

        function response(response) {
            var alertKey = response.headers('X-jogging-alert');
            if (angular.isString(alertKey)) {
                AlertService.success(alertKey, {param: response.headers('X-jogging-params')});
            }
            return response;
        }
    }
})();

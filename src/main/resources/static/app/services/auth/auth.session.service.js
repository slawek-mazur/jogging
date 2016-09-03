(function () {
    'use strict';

    angular
        .module('jogging')
        .factory('AuthServerProvider', AuthServerProvider);

    AuthServerProvider.$inject = ['$http', '$localStorage'];

    function AuthServerProvider($http, $localStorage) {
        return {
            getToken: getToken,
            hasValidToken: hasValidToken,
            login: login,
            logout: logout
        };

        function getToken() {
            return $localStorage.authenticationToken;
        }

        function hasValidToken() {
            var token = this.getToken();
            return !!token;
        }

        function login(credentials) {
            var data =
                'username=' + encodeURIComponent(credentials.email) + '&' +
                'password=' + encodeURIComponent(credentials.password);

            return $http.post('account/authentication', data, {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                }
            }).success(function (response) {
                return response;
            });
        }

        function logout() {
            $http.post('api/logout').success(function (response) {
                delete $localStorage.authenticationToken;
                $http.get('api/account');
                return response;
            });

        }
    }
})();

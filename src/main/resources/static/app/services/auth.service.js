(function () {
    'use strict';

    angular
        .module('jogging')
        .factory('Auth', Auth);

    Auth.$inject = ['$rootScope', '$state', '$sessionStorage', '$q', 'Principal', 'AuthServerProvider', 'Account', 'Register'];

    function Auth($rootScope, $state, $sessionStorage, $q, Principal, AuthServerProvider, Account, Register) {
        return {
            authorize: authorize,
            createAccount: createAccount,
            getPreviousState: getPreviousState,
            login: login,
            logout: logout,
            resetPreviousState: resetPreviousState,
            storePreviousState: storePreviousState,
            updateAccount: updateAccount
        };

        function authorize(force) {
            return Principal.identity(force).then(authThen);

            function authThen() {
                var isAuthenticated = Principal.isAuthenticated();

                // an authenticated user can't access to login and register pages
                if (isAuthenticated &&
                    $rootScope.toState.parent === 'account' &&
                    ($rootScope.toState.name === 'login' || $rootScope.toState.name === 'register')) {
                    $state.go('home');
                }

                // recover and clear previousState after external login redirect (e.g. oauth2)
                if (isAuthenticated && !$rootScope.fromState.name && getPreviousState()) {
                    var previousState = getPreviousState();
                    resetPreviousState();
                    $state.go(previousState.name, previousState.params);
                }

                if ($rootScope.toState.data.authorities &&
                    $rootScope.toState.data.authorities.length > 0 && !Principal.hasAnyAuthority($rootScope.toState.data.authorities)) {

                    if (isAuthenticated) {
                        // user is signed in but not authorized for desired state
                        $state.go('accessdenied');
                    }
                    else {
                        // user is not authenticated. stow the state they wanted before you
                        // send them to the login service, so you can return them when you're done
                        storePreviousState($rootScope.toState.name, $rootScope.toStateParams);

                        // now, send them to the sign in state so they can log in
                        $state.go('accessdenied');
                    }
                }
            }
        }

        function createAccount(account, callback) {
            var cb = callback || angular.noop;

            return Register.save(account,
                function () {
                    return cb(account);
                },
                function (err) {
                    this.logout();
                    return cb(err);
                }.bind(this)).$promise;
        }

        function login(credentials, callback) {
            var cb = callback || angular.noop;
            var deferred = $q.defer();

            AuthServerProvider.login(credentials)
                .then(loginThen)
                .catch(function (err) {
                    this.logout();
                    deferred.reject(err);
                    return cb(err);
                }.bind(this));

            function loginThen(data) {
                Principal.identity(true).then(function () {
                    deferred.resolve(data);
                });
                return cb();
            }

            return deferred.promise;
        }


        function logout() {
            AuthServerProvider.logout();
            Principal.authenticate(null);
        }

        function updateAccount(account, callback) {
            var cb = callback || angular.noop;

            return Account.save(account,
                function () {
                    return cb(account);
                },
                function (err) {
                    return cb(err);
                }.bind(this)).$promise;
        }

        function getPreviousState() {
            return $sessionStorage.previousState;
        }

        function resetPreviousState() {
            delete $sessionStorage.previousState;
        }

        function storePreviousState(previousStateName, previousStateParams) {
            $sessionStorage.previousState = {"name": previousStateName, "params": previousStateParams};
        }
    }
})();

(function () {
    'use strict';

    angular
        .module('jogging')
        .controller('LoginController', LoginController);

    LoginController.$inject = ['$rootScope', '$state', '$timeout', 'Auth'];

    function LoginController($rootScope, $state, $timeout, Auth) {
        var vm = this;

        vm.authenticationError = false;
        vm.cancel = cancel;
        vm.credentials = {};
        vm.login = login;
        vm.password = null;
        vm.register = register;
        vm.email = null;

        $timeout(function () {
            angular.element('#email').focus();
        });

        function cancel() {
            vm.credentials = {
                email: null,
                password: null
            };
            vm.authenticationError = false;
        }

        function login(event) {
            event.preventDefault();
            Auth.login({
                email: vm.email,
                password: vm.password
            }).then(function () {
                vm.authenticationError = false;
                if ($state.current.name === 'register' || $state.current.name === 'activate' ||
                    $state.current.name === 'finishReset' || $state.current.name === 'requestReset') {
                    $state.go('home');
                }

                $rootScope.$broadcast('authenticationSuccess');

                // previousState was set in the authExpiredInterceptor before being redirected to login modal.
                // since login is successful, go to stored previousState and clear previousState
                if (Auth.getPreviousState()) {
                    var previousState = Auth.getPreviousState();
                    Auth.resetPreviousState();
                    $state.go(previousState.name, previousState.params);
                }
            }).catch(function () {
                vm.authenticationError = true;
            });
        }

        function register() {
            $state.go('register');
        }
    }
})();

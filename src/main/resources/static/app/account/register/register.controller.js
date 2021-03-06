(function () {
    'use strict';

    angular
        .module('jogging')
        .controller('RegisterController', RegisterController);

    RegisterController.$inject = ['$timeout', 'Auth'];

    function RegisterController($timeout, Auth) {
        var vm = this;

        vm.doNotMatch = null;
        vm.error = null;
        vm.errorUserExists = null;
        vm.login = function () {
            alert('o ho 2');
        };
        vm.register = register;
        vm.registerAccount = {};
        vm.success = null;

        $timeout(function () {
            angular.element('#login').focus();
        });

        function register() {
            vm.doNotMatch = null;
            vm.error = null;
            vm.errorEmailExists = null;

            Auth.createAccount(vm.registerAccount).then(function () {
                vm.success = 'OK';
            }).catch(function (response) {
                vm.success = null;
                if (response.status === 400 && response.data === 'e-mail address already in use') {
                    vm.errorEmailExists = 'ERROR';
                } else {
                    vm.error = 'ERROR';
                }
            });
        }
    }
})();

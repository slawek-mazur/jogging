(function () {
    'use strict';

    angular
        .module('jogging')
        .controller('HomeController', HomeController);

    HomeController.$inject = ['$scope', 'Principal', '$state'];

    function HomeController($scope, Principal, $state) {
        var vm = this;

        vm.account = null;
        vm.isAuthenticated = null;
        vm.login = login;
        vm.register = register;
        $scope.$on('authenticationSuccess', function () {
            getAccount();
        });

        getAccount();

        function getAccount() {
            Principal.identity().then(function (account) {
                vm.account = account;
                vm.isAuthenticated = Principal.isAuthenticated;
            });
        }

        function login() {
            $state.go('login');
        }

        function register() {
            $state.go('register');
        }
    }
})();

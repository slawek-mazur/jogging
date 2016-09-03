(function () {
    'use strict';

    angular
        .module('jogging')
        .controller('UserManagementDetailController', UserManagementDetailController);

    UserManagementDetailController.$inject = ['$stateParams', 'User'];

    function UserManagementDetailController($stateParams, User) {
        var vm = this;

        vm.load = load;
        vm.user = {};

        vm.load($stateParams.id);

        function load(id) {
            User.get({id: id}, function (result) {
                vm.user = result;
            });
        }
    }
})();

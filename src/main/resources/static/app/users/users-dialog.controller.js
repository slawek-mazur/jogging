(function () {
    'use strict';

    angular
        .module('jogging')
        .controller('UserManagementDialogController', UserManagementDialogController);

    UserManagementDialogController.$inject = ['$uibModalInstance', 'entity', 'User'];

    function UserManagementDialogController($uibModalInstance, entity, User) {
        var vm = this;

        vm.authorities = ['ROLE_USER', 'ROLE_MANAGER', 'ROLE_ADMIN'];
        vm.clear = clear;
        vm.save = save;
        vm.user = entity;

        function clear() {
            $uibModalInstance.dismiss('cancel');
        }

        function onSaveSuccess(result) {
            vm.isSaving = false;
            $uibModalInstance.close(result);
        }

        function onSaveError() {
            vm.isSaving = false;
        }

        function save() {
            vm.isSaving = true;
            if (vm.user.id !== null) {
                User.update(vm.user, onSaveSuccess, onSaveError);
            } else {
                vm.user.langKey = 'en';
                User.save(vm.user, onSaveSuccess, onSaveError);
            }
        }
    }
})();

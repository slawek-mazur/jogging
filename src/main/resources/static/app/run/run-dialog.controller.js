(function () {
    'use strict';

    angular
        .module('jogging')
        .controller('RunDialogController', RunDialogController);

    RunDialogController.$inject = ['$timeout', '$scope', '$uibModalInstance', 'entity', 'Run'];

    function RunDialogController($timeout, $scope, $uibModalInstance, entity, Run) {
        var vm = this;

        vm.run = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;

        $timeout(function () {
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear() {
            $uibModalInstance.dismiss('cancel');
        }

        function save() {
            vm.isSaving = true;
            if (vm.run.id !== null) {
                Run.update(vm.run, onSaveSuccess, onSaveError);
            } else {
                Run.save(vm.run, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess(result) {
            $scope.$emit('jogging:runUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError() {
            vm.isSaving = false;
        }

        vm.datePickerOpenStatus.day = false;

        function openCalendar(date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();

(function () {
    'use strict';

    angular
        .module('jogging')
        .controller('RunDeleteController', RunDeleteController);

    RunDeleteController.$inject = ['$uibModalInstance', 'entity', 'Run'];

    function RunDeleteController($uibModalInstance, entity, Run) {
        var vm = this;

        vm.run = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear() {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete(id) {
            Run.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();

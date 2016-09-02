(function() {
    'use strict';

    angular
        .module('myJoggingApp')
        .controller('RunDetailController', RunDetailController);

    RunDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Run'];

    function RunDetailController($scope, $rootScope, $stateParams, previousState, entity, Run) {
        var vm = this;

        vm.run = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('myJoggingApp:runUpdate', function(event, result) {
            vm.run = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();

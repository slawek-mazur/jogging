(function () {
    'use strict';

    var alert = {
        template: '<div class="alerts" ng-cloak="">' +
        '<div ng-repeat="alert in $ctrl.alerts" ng-class="[alert.position]">' +
        '<uib-alert ng-cloak="" type="{{alert.type}}" close="alert.close($ctrl.alerts)"><pre ng-bind-html="alert.msg"></pre></uib-alert>' +
        '</div>' +
        '</div>',
        controller: alertController
    };

    angular
        .module('jogging')
        .component('alert', alert);

    alertController.$inject = ['$scope', 'AlertService'];

    function alertController($scope, AlertService) {
        var vm = this;

        vm.alerts = AlertService.get();
        $scope.$on('$destroy', function () {
            vm.alerts = [];
        });
    }
})();

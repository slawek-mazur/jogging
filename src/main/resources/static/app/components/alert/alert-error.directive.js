(function () {
    'use strict';

    var alertError = {
        template: '<div class="alerts" ng-cloak="">' +
        '<div ng-repeat="alert in $ctrl.alerts" ng-class="[alert.position]">' +
        '<uib-alert ng-cloak="" type="{{alert.type}}" close="alert.close($ctrl.alerts)"><pre>{{ alert.msg }}</pre></uib-alert>' +
        '</div>' +
        '</div>',
        controller: alertErrorController
    };

    angular
        .module('jogging')
        .component('alertError', alertError);

    alertErrorController.$inject = ['$scope', 'AlertService', '$rootScope'];

    function alertErrorController($scope, AlertService, $rootScope) {
        var vm = this;

        vm.alerts = [];

        function addErrorAlert(message) {
            vm.alerts.push(
                AlertService.add(
                    {
                        type: 'danger',
                        msg: message,
                        timeout: 5000,
                        scoped: true
                    },
                    vm.alerts
                )
            );
        }

        var cleanHttpErrorListener = $rootScope.$on('jogging.httpError', function (event, httpResponse) {
            var i;
            event.stopPropagation();
            switch (httpResponse.status) {
                // connection refused, server not reachable
                case 0:
                    addErrorAlert('Server not reachable');
                    break;

                case 400:
                    var errorHeader = httpResponse.headers('X-jogging-error');
                    var entityKey = httpResponse.headers('X-jogging-params');
                    if (errorHeader) {
                        addErrorAlert(errorHeader);
                    } else if (httpResponse.data && httpResponse.data.fieldErrors) {
                        for (i = 0; i < httpResponse.data.fieldErrors.length; i++) {
                            var fieldError = httpResponse.data.fieldErrors[i];
                            // convert 'something[14].other[4].id' to 'something[].other[].id' so translations can be written to it
                            var convertedField = fieldError.field.replace(/\[\d*\]/g, '[]');
                            var fieldName = convertedField.charAt(0).toUpperCase() + convertedField.slice(1);
                            addErrorAlert('Field ' + fieldName + ' cannot be empty');
                        }
                    } else if (httpResponse.data && httpResponse.data.message) {
                        addErrorAlert(httpResponse.data.message);
                    } else {
                        addErrorAlert(httpResponse.data);
                    }
                    break;

                case 404:
                    addErrorAlert('Not found');
                    break;

                default:
                    if (httpResponse.data && httpResponse.data.message) {
                        addErrorAlert(httpResponse.data.message);
                    } else {
                        addErrorAlert(angular.toJson(httpResponse));
                    }
            }
        });

        $scope.$on('$destroy', function () {
            if (angular.isDefined(cleanHttpErrorListener) && cleanHttpErrorListener !== null) {
                cleanHttpErrorListener();
                vm.alerts = [];
            }
        });
    }
})();

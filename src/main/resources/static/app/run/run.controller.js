(function () {
    'use strict';

    angular
        .module('jogging')
        .controller('RunController', RunController);

    RunController.$inject = ['Run', 'AlertService', 'pagingParams', 'paginationConstants', 'NgTableParams'];

    function RunController(Run, AlertService, pagingParams, paginationConstants, NgTableParams) {
        var vm = this;

        vm.predicate = pagingParams.predicate;
        vm.reverse = pagingParams.ascending;
        vm.tableParams = new NgTableParams({}, {getData: loadAll});
        vm.itemsPerPage = paginationConstants.itemsPerPage;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.dateformat = dateformat;

        function loadAll(params) {

            console.log(params.filter());

            return Run.query({
                page: pagingParams.page - 1,
                size: vm.itemsPerPage,
                sort: sort()
            }, onSuccess, onError).$promise;
            function sort() {
                var result = [vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc')];
                if (vm.predicate !== 'id') {
                    result.push('id');
                }
                return result;
            }

            function onSuccess(data, headers) {
                params.total(headers('X-Total-Count'));
                return data;
            }

            function onError(error) {
                AlertService.error(error.data.message);
                params.total(0);
                return [];
            }
        }

        vm.datePickerOpenStatus.from = false;
        vm.datePickerOpenStatus.to = false;

        function openCalendar(date) {
            vm.datePickerOpenStatus[date] = true;
        }

        function dateformat() {
            return 'yyyy-MM-dd';
        }
    }
})();

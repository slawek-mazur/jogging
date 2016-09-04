(function () {
    'use strict';

    angular
        .module('jogging')
        .controller('RunController', RunController);

    RunController.$inject = ['Run', 'AlertService', 'paginationConstants', 'NgTableParams'];

    function RunController(Run, AlertService, pagingParams, NgTableParams) {
        var vm = this;

        vm.predicate = pagingParams.predicate;
        vm.reverse = pagingParams.ascending;
        vm.tableParams = new NgTableParams({}, {getData: loadAll});

        function loadAll(params) {
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
                //vm.links = ParseLinks.parse(headers('link'));

                params.total(headers('X-Total-Count'));
                return data;
            }

            function onError(error) {
                AlertService.error(error.data.message);
                params.total(0);
                return [];
            }
        }
    }
})();

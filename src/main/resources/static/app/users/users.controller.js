(function () {
    'use strict';

    angular
        .module('jogging')
        .controller('UserManagementController', UserManagementController);

    UserManagementController.$inject = ['Principal', 'User', 'AlertService', '$state',
        'pagingParams', 'paginationConstants', 'NgTableParams'];

    function UserManagementController(Principal, User, AlertService, $state,
                                      pagingParams, paginationConstants, NgTableParams) {
        var vm = this;

        vm.authorities = ['ROLE_MANAGER'];
        vm.currentAccount = null;
        vm.setActive = setActive;
        vm.users = [];
        vm.page = 1;
        vm.totalItems = null;
        vm.clear = clear;
        vm.links = null;
        vm.loadPage = loadPage;
        vm.predicate = pagingParams.predicate;
        vm.reverse = pagingParams.ascending;
        vm.itemsPerPage = paginationConstants.itemsPerPage;
        vm.transition = transition;
        vm.tableParams = new NgTableParams({}, {getData: loadAll});

        Principal.identity().then(function (account) {
            vm.currentAccount = account;
        });

        function setActive(user, isActivated) {
            user.activated = isActivated;
            User.update(user, function () {
                vm.loadAll();
                vm.clear();
            });
        }

        function loadAll(params) {
            return User.query({
                page: pagingParams.page - 1,
                size: vm.itemsPerPage,
                sort: sort()
            }, onSuccess, onError).$promise;

            function onSuccess(data, headers) {
                //hide anonymous user from user management: it's a required user for Spring Security
                for (var i in data) {
                    if (data[i]['login'] === 'anonymoususer') {
                        data.splice(i, 1);
                    }
                }

                params.total(headers('X-Total-Count'));
                return data;
            }

            function onError(error) {
                AlertService.error(error.data.message);
            }
        }

        function clear() {
            vm.user = {id: null, email: null, authorities: null};
        }

        function sort() {
            var result = [vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc')];
            if (vm.predicate !== 'id') {
                result.push('id');
            }
            return result;
        }

        function loadPage(page) {
            vm.page = page;
            vm.transition();
        }

        function transition() {
            $state.transitionTo($state.$current, {
                page: vm.page,
                sort: vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc'),
                search: vm.currentSearch
            });
        }
    }
})();

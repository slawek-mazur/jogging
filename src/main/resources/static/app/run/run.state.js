(function () {
    'use strict';

    angular
        .module('jogging')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
            .state('run', {
                parent: 'app',
                url: '/run?page&sort&search',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'Runs'
                },
                views: {
                    'content@': {
                        templateUrl: 'app/run/runs.html',
                        controller: 'RunController',
                        controllerAs: 'vm'
                    }
                },
                params: {
                    page: {
                        value: '1',
                        squash: true
                    },
                    sort: {
                        value: 'id,asc',
                        squash: true
                    },
                    search: null
                },
                resolve: {
                    pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                        return {
                            page: PaginationUtil.parsePage($stateParams.page),
                            sort: $stateParams.sort,
                            predicate: PaginationUtil.parsePredicate($stateParams.sort),
                            ascending: PaginationUtil.parseAscending($stateParams.sort),
                            search: $stateParams.search
                        };
                    }]
                }
            })
            .state('run-detail', {
                parent: 'run',
                url: '/run/{id}',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'Run'
                },
                views: {
                    'content@': {
                        templateUrl: 'app/run/run-detail.html',
                        controller: 'RunDetailController',
                        controllerAs: 'vm'
                    }
                },
                resolve: {
                    entity: ['$stateParams', 'Run', function ($stateParams, Run) {
                        return Run.get({id: $stateParams.id}).$promise;
                    }],
                    previousState: ["$state", function ($state) {
                        return {
                            name: $state.current.name || 'run',
                            params: $state.params,
                            url: $state.href($state.current.name, $state.params)
                        };
                    }]
                }
            })
            .state('run-detail.edit', {
                parent: 'run-detail',
                url: '/detail/edit',
                data: {
                    authorities: ['ROLE_USER']
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function ($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'app/run/run-dialog.html',
                        controller: 'RunDialogController',
                        controllerAs: 'vm',
                        backdrop: 'static',
                        size: 'lg',
                        resolve: {
                            entity: ['Run', function (Run) {
                                return Run.get({id: $stateParams.id}).$promise;
                            }]
                        }
                    }).result.then(function () {
                        $state.go('^', {}, {reload: false});
                    }, function () {
                        $state.go('^');
                    });
                }]
            })
            .state('run.new', {
                parent: 'run',
                url: '/new',
                data: {
                    authorities: ['ROLE_USER']
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function ($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'app/run/run-dialog.html',
                        controller: 'RunDialogController',
                        controllerAs: 'vm',
                        backdrop: 'static',
                        size: 'lg',
                        resolve: {
                            entity: function () {
                                return {
                                    day: null,
                                    distance: 0,
                                    duration: 0,
                                    averageSpeed: 0.0,
                                    id: null
                                };
                            }
                        }
                    }).result.then(function () {
                        $state.go('run', null, {reload: 'run'});
                    }, function () {
                        $state.go('run');
                    });
                }]
            })
            .state('run.edit', {
                parent: 'run',
                url: '/{id}/edit',
                data: {
                    authorities: ['ROLE_USER']
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function ($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'app/run/run-dialog.html',
                        controller: 'RunDialogController',
                        controllerAs: 'vm',
                        backdrop: 'static',
                        size: 'lg',
                        resolve: {
                            entity: ['Run', function (Run) {
                                return Run.get({id: $stateParams.id}).$promise;
                            }]
                        }
                    }).result.then(function () {
                        $state.go('run', null, {reload: 'run'});
                    }, function () {
                        $state.go('^');
                    });
                }]
            })
            .state('run.delete', {
                parent: 'run',
                url: '/{id}/delete',
                data: {
                    authorities: ['ROLE_USER']
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function ($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'app/run/run-delete-dialog.html',
                        controller: 'RunDeleteController',
                        controllerAs: 'vm',
                        size: 'md',
                        resolve: {
                            entity: ['Run', function (Run) {
                                return Run.get({id: $stateParams.id}).$promise;
                            }]
                        }
                    }).result.then(function () {
                        $state.go('run', null, {reload: 'run'});
                    }, function () {
                        $state.go('^');
                    });
                }]
            });
    }

})();

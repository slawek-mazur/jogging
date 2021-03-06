(function () {
    'use strict';

    angular
        .module('jogging')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
            .state('users', {
                parent: 'app',
                url: '/users?page&sort',
                data: {
                    authorities: ['ROLE_MANAGER'],
                    pageTitle: 'Users'
                },
                views: {
                    'content@': {
                        templateUrl: 'app/users/users.html',
                        controller: 'UserManagementController',
                        controllerAs: 'vm'
                    }
                }, params: {
                    page: {
                        value: '1',
                        squash: true
                    },
                    sort: {
                        value: 'id,asc',
                        squash: true
                    }
                },
                resolve: {
                    pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                        return {
                            page: PaginationUtil.parsePage($stateParams.page),
                            sort: $stateParams.sort,
                            predicate: PaginationUtil.parsePredicate($stateParams.sort),
                            ascending: PaginationUtil.parseAscending($stateParams.sort)
                        };
                    }]
                }
            })
            .state('users-detail', {
                parent: 'users',
                url: '/user/{id}',
                data: {
                    authorities: ['ROLE_MANAGER'],
                    pageTitle: 'jogging'
                },
                views: {
                    'content@': {
                        templateUrl: 'app/users/users-detail.html',
                        controller: 'UserManagementDetailController',
                        controllerAs: 'vm'
                    }
                }
            })
            .state('users.new', {
                parent: 'users',
                url: '/new',
                data: {
                    authorities: ['ROLE_MANAGER']
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function ($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'app/users/users-dialog.html',
                        controller: 'UserManagementDialogController',
                        controllerAs: 'vm',
                        backdrop: 'static',
                        size: 'lg',
                        resolve: {
                            entity: function () {
                                return {
                                    id: null, email: null, password: null, authorities: null
                                };
                            }
                        }
                    }).result.then(function () {
                        $state.go('users', null, {reload: true});
                    }, function () {
                        $state.go('users');
                    });
                }]
            })
            .state('users.edit', {
                parent: 'users',
                url: '/{id}/edit',
                data: {
                    authorities: ['ROLE_MANAGER']
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function ($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'app/users/users-dialog.html',
                        controller: 'UserManagementDialogController',
                        controllerAs: 'vm',
                        backdrop: 'static',
                        size: 'lg',
                        resolve: {
                            entity: ['User', function (User) {
                                return User.get({id: $stateParams.id});
                            }]
                        }
                    }).result.then(function () {
                        $state.go('users', null, {reload: true});
                    }, function () {
                        $state.go('^');
                    });
                }]
            })
            .state('users.delete', {
                parent: 'users',
                url: '/{id}/delete',
                data: {
                    authorities: ['ROLE_MANAGER']
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function ($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'app/users/users-delete-dialog.html',
                        controller: 'UserManagementDeleteController',
                        controllerAs: 'vm',
                        size: 'md',
                        resolve: {
                            entity: ['User', function (User) {
                                return User.get({id: $stateParams.id});
                            }]
                        }
                    }).result.then(function () {
                        $state.go('users', null, {reload: true});
                    }, function () {
                        $state.go('^');
                    });
                }]
            });
    }
})();

(function () {
    'use strict';

    angular
        .module('jogging')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider.state('login', {
            parent: 'account',
            url: '/login',
            data: {
                authorities: [],
                pageTitle: 'Login'
            },
            views: {
                'content@': {
                    templateUrl: 'app/account/login/login.html',
                    controller: 'LoginController',
                    controllerAs: 'vm'
                }
            }
        });
    }
})();

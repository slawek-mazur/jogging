(function () {
    'use strict';

    angular
        .module('jogging', [
            'ngStorage',
            'ngResource',
            'ngCookies',
            'ngAria',
            'ui.bootstrap',
            'ui.bootstrap.datetimepicker',
            'ui.router'
        ])
        .run(run);

    run.$inject = ['stateHandler'];

    function run(stateHandler) {
        stateHandler.initialize();
    }
})();

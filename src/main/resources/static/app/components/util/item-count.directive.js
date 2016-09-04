(function () {
    'use strict';

    var itemCount = {
        template: '<div class="info">' +
        'Showing {{($ctrl.page * $ctrl.itemsPerPage) < $ctrl.queryCount ? ($ctrl.page * $ctrl.itemsPerPage) : $ctrl.queryCount}} ' +
        'of {{$ctrl.queryCount}}' +
        '</div>',
        bindings: {
            page: '<',
            queryCount: '<total',
            itemsPerPage: '<'
        }
    };

    angular
        .module('jogging')
        .component('itemCount', itemCount);
})();

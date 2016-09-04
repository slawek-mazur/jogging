(function() {
    'use strict';

    angular
        .module('jogging')
        .config(localStorageConfig);

    localStorageConfig.$inject = ['$localStorageProvider', '$sessionStorageProvider'];

    function localStorageConfig($localStorageProvider, $sessionStorageProvider) {
        $localStorageProvider.setKeyPrefix('jogging-');
        $sessionStorageProvider.setKeyPrefix('jogging-');
    }
})();

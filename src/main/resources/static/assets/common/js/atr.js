'use strict';

angular.module('atr', ['ngRoute', 'bootstrap3-typeahead'])
    .config(function ($routeProvider, $httpProvider) {

        $routeProvider.when('/', {
            templateUrl: 'home.html',
            controller: 'home',
            controllerAs: 'controller'
        }).when('/login', {
            templateUrl: 'login.html',
            controller: 'auth',
            controllerAs: 'controller'
        }).when('/dates', {
            templateUrl: 'dates.html',
            controller: 'dates',
            controllerAs: 'controller'
        }).otherwise('/');

        $httpProvider.defaults.headers.common["X-Requested-With"] = 'XMLHttpRequest';

    })
    .controller('home', function ($rootScope, $http, $location) {
        var self = this;

        $http.get('/airports/departures').then(function (response) {
            $rootScope.departures = response.data;
        });

        $http.get('/airports/destinations').then(function (response) {
            $rootScope.destinations = response.data;
        });

        self.returning = 'return';
        self.oneway = function () {
            return self.returning === 'oneway';
        };

        self.departure = '';
        self.destination = '';

        $rootScope.airportName = function (item) {
            return item.name;
        };

        $rootScope.departureSelected = function (item) {
            self.departure = item.id;
        };

        $rootScope.destinationSelected = function (item) {
            self.destination = item.id;
        };

        self.continue = function () {
            //todo sprawdzic jakies warunki

            $location.path("/dates");
        };
    })
    .controller('dates', function () {

    })
    .controller('auth', function ($rootScope, $http, $location) {
        $rootScope.authenticating = true;

        var self = this;

        var authenticate = function (credentials, callback) {
            var headers = credentials ? {
                authorization: "Basic "
                + btoa(credentials.username + ":" + credentials.password)
            } : {};

            $http.get('user', {headers: headers}).then(function (response) {
                $rootScope.authenticated = !!response.data.name;
                callback && callback();
            }, function () {
                $rootScope.authenticated = false;
                callback && callback();
            });
        };

        authenticate();
        self.credentials = {};
        self.login = function () {
            authenticate(self.credentials, function () {
                if ($rootScope.authenticated) {
                    $location.path("/home");
                    self.error = false;
                } else {
                    $location.path("/login");
                    self.error = true;
                }
            });
        };

        self.logout = function () {
            $http.post('logout', {}).finally(function () {
                $rootScope.authenticated = false;
                $location.path("/");
            });
        }
    });
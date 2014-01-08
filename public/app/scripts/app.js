'use strict';

var OSCR = angular.module('OSCR', ['ngCookies', 'ui.bootstrap','md5', 'ui-gravatar', 'angularFileUpload']);

OSCR.config(
    function ($routeProvider) {
        $routeProvider
            .when('/dashboard', {
                templateUrl: 'views/dashboard.html',
                controller: 'DashboardController',
                title: 'OSCR Dashboard'
            })
            .when('/document/:schema', {
                templateUrl: 'views/document-list.html',
                controller: 'DocumentListController'
            })
            .when('/document/:schema/edit/:identifier', {
                templateUrl: 'views/document-edit.html',
                controller: 'DocumentEditController'
            })
            .when('/vocab/:vocab', {
                templateUrl: 'views/vocab.html',
                controller: 'VocabularyEditController'
            })
            .when('/lang/:lang', {
                templateUrl: 'views/lang.html',
                controller: 'LangEditController'
            })
            .when('/media', {
                templateUrl: 'views/media.html',
                controller: 'MediaUploadController'
            })
            .when('/people', {
                templateUrl: 'views/people.html',
                controller: 'PeopleController'
            })
            .when('/people/group/:identifier', {
                templateUrl: 'views/group.html',
                controller: 'GroupViewController'
            })
            .when('/people/user/:identifier', {
                templateUrl: 'views/user.html',
                controller: 'UserViewController'
            })
            .otherwise({
                templateUrl: 'views/login.html'
            });
    }
);


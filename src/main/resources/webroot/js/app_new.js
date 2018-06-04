angular.module('CrudApp', []).config(['$routeProvider', function ($routeProvider) {
    $routeProvider.
        when('/', {templateUrl: '/tpl/lists.html', controller: ListCtrl}).
        when('/add-discovery', {templateUrl: '/tpl/add-new.html', controller: AddCtrl}).
        when('/edit/:id', {templateUrl: '/tpl/edit.html', controller: EditCtrl}).
        otherwise({redirectTo: '/'});
}]);



function ListCtrl($scope, $http) {
    $http.get('/api/discovery').success(function (data) {
        $scope.users = data;
    });
}

function AddCtrl($scope, $http, $location) {
    $scope.master = {};
    $scope.activePath = null;
    $scope.ShowSpinnerStatus = false;
    $scope.loaderClass = "loader1";
				
	$scope.ShowSpinner = function(){
		$scope.ShowSpinnerStatus = true;
		$scope.loaderClass = "loader";
	};
	$scope.HideSpinner = function(){
		$scope.ShowSpinnerStatus = false;
		$scope.loaderClass = "loader1";
	};
 if ($scope.CrudApp.$valid) {
        alert('All good...'); //next step!

   }
   else {
        alert('Not all fields valid! Do something...');
    }
				    
   $scope.HideSpinner();
				        
    $scope.add_new = function (user, AddNewForm) {
    
    $scope.ShowSpinner();
        $http.post('/api/discovery', user).success(function () {    
            $scope.reset();
            $scope.HideSpinner();
            $scope.activePath = $location.path('/');
    
        });  

        $scope.reset = function () {
            $scope.user = angular.copy($scope.master);
        };

      // $scope.reset();

    };
    
            
}

function EditCtrl($scope, $http, $location, $routeParams) {
    var id = $routeParams.id;
    $scope.activePath = null;

    $http.get('/api/discovery/' + id).success(function (data) {
        $scope.user = data;
    });

    $scope.update = function (user) {
        $http.put('/api/discovery/' + id, user).success(function (data) {
            $scope.user = data;
            $scope.activePath = $location.path('/');
        });
    };

    $scope.delete = function (user) {
        var deleteUser = confirm('Are you absolutely sure you want to delete ?');
        if (deleteUser) {
            $http.delete('/api/discovery/' + id)
                .success(function(data, status, headers, config) {
                    $scope.activePath = $location.path('/');
                }).
                error(function(data, status, headers, config) {
                    console.log("error");
                    // custom handle error
                });
        }
    };
    
    
    angular.module('CrudApp', ['ngAnimate'])
    .service('CrudApp', function () {
        var tracker, add, remove, get;
        tracker = {};
        add = function (track_id) {
            if (undefined === tracker[track_id]) {
                tracker[track_id] = 0;
            }
            tracker[track_id]++;
        };
        remove = function (track_id) {
            if (tracker[track_id] === undefined) {
                tracker[track_id] = 0;
            } else {
                tracker[track_id]--;
            }
        };
        get = function (track_id) {
            return tracker[track_id] || 0;
        };
        return {
            add: add,
            remove: remove,
            get: get
        };
    })
    .directive('CrudApp', ['$compile', 'CrudApp', function ($compile, CrudApp) {
        return {
            restrict: 'A',
            scope: {
                CrudApp: '='
            },
            link: function (scope, element, attrs) {
                var track_var = attrs.CrudApp;

                // used in ng-show
                scope.isActive = function () {
                    return CrudApp.get(track_var) > 0;
                };

                // element's css `position` value needs to be relative-like
                var position = element.css('position');
                if (position === 'static' || position === '' || typeof position === 'undefined') {
                    element.css('position', 'relative');
                }

                // html to append: backdrop + message "please wait"
                var template = '<div class="busy-indicator busy-indicator-animation" ng-show="isActive()">' +
                    '<div class="busy-indicator busy-indicator-bg"></div>' +
                    '<div class="busy-indicator-message"><span>Please wait...</span></div>' +
                    '</div>';
                var templateElement = $compile(template)(scope);
                element.append(templateElement);

            }
        };
    }]);
    }

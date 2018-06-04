angular.module('CrudApp', []).config(['$routeProvider', function ($routeProvider) {
    $routeProvider.
        when('/', {templateUrl: '/tpl/welcome.html', controller: WelcomeCtrl}).
        when('/list',{templateUrl: '/tpl/lists.html', controller: ListCtrl} ).
        when('/listAll',{templateUrl: '/tpl/listsAll.html', controller:ListAllCtrl} ).
        when('/add-discovery', {templateUrl: '/tpl/add-new.html', controller: AddCtrl}).
        otherwise({redirectTo: '/'});
}]);

function ListAllCtrl($scope,$http,$location){
       $http.get('/api/discovery').success(function (data) {
        $scope.discoveries = data;
    });
  }

function WelcomeCtrl($scope,$location){
    $scope.activePath = null;
	$scope.add_discovery= function (){
    $scope.activePath = $location.path('/add-discovery');
	}
	
}



function ListCtrl($scope, $http ,$location) {
    $scope.activePath = $location.path('/list');   
    $http.get('/api/disco').success(function (data) {
        $scope.discoveries = data;
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

  $scope.MyFocus = function (field)  {
 
    var ipformat = /^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$/;
 if(field.match(ipformat))
    {
   
     
     return true;
 }
    else
   {
  alert("Please  enter valid IP address!");
  
        return false;
  }
 };
 
 
 // $scope.MyFocus();
   $scope.HideSpinner();
  // $scope.add_new = function (discovery, AddNewDiscoveryForm) {
    
    $scope.add_new = function (discovery) {
     if (false == $scope.MyFocus(discovery.startip))
     {
     $scope.discovery.startip.focus();
     	return;
     }
     if ( false == $scope.MyFocus(discovery.endip))
     {
       $scope.discovery.endip.focus();
     	return;
     }
     
      $scope.ShowSpinner();
      
    $http.post('/api/discovery', discovery).success(function () {
          $scope.reset();
          $scope.HideSpinner();
          $scope.activePath = $location.path('/list');
        });  

         $scope.reset = function () {
            $scope.discovery = angular.copy($scope.master);
        };

        $scope.reset();

    };
    
}

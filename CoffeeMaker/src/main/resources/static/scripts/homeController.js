var app = angular.module('myApp');

//register home controller
app.controller('homeController', function($scope, $http) {
	$http.get("/api/v1/users").then(
			function(success) {
				$scope.user = {
					username: success.data.username,
					role: success.data.role
				}
			},
			function(failure) {
				window.location.href = "/login";
			}
		);
	
	$scope.isCustomer = function() {
		return $scope.user != undefined && $scope.user.role == 'customer';
	}
	
	$scope.isEmployee = function() {
		return $scope.user != undefined && $scope.user.role == 'employee';
	}
});